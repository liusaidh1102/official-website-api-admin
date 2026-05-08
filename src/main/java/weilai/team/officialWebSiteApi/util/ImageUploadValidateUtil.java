package weilai.team.officialWebSiteApi.util;
import org.apache.tika.Tika;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * 企业级图片上传校验工具（Tika + 魔数 + 多层过滤）
 * 核心：Tika解决99%常规伪装，魔数兜底1%极端场景
 */
public class ImageUploadValidateUtil {
    // ========== 基础配置（不可变，防止篡改） ==========
    // 允许的图片MIME类型（兼容Java 8/17）
    private static final Set<String> ALLOWED_IMAGE_MIME_TYPES;
    // 允许的图片后缀
    private static final Set<String> ALLOWED_IMAGE_SUFFIXES;
    // 单例Tika（避免重复创建，提升性能）
    private static final Tika TIKA = new Tika();
    // 图片最大大小（20MB，可根据业务调整）
    private static final long MAX_IMAGE_SIZE = 20 * 1024 * 1024;

    private static final Logger log = LoggerFactory.getLogger(ImageUploadValidateUtil.class);

    static {
        // 初始化MIME类型（Java 8/17兼容写法）
        Set<String> mimeTypes = new HashSet<>();
        mimeTypes.add("image/jpeg");
        mimeTypes.add("image/png");
        mimeTypes.add("image/gif");
        mimeTypes.add("image/webp");
        mimeTypes.add("image/bmp");
        mimeTypes.add("image/x-ms-bmp");
        mimeTypes.add("image/x-windows-bmp");
        mimeTypes.add("image/tiff");
        ALLOWED_IMAGE_MIME_TYPES = Collections.unmodifiableSet(mimeTypes);

        // 初始化允许的后缀
        Set<String> suffixes = new HashSet<>();
        suffixes.add("jpg");
        suffixes.add("jpeg");
        suffixes.add("jpe");
        suffixes.add("jfif");
        suffixes.add("png");
        suffixes.add("gif");
        suffixes.add("webp");
        suffixes.add("bmp");
        suffixes.add("dib");
        suffixes.add("tif");
        suffixes.add("tiff");
        suffixes.add("img");
        ALLOWED_IMAGE_SUFFIXES = Collections.unmodifiableSet(suffixes);
    }

    // ========== 对外核心方法（适配上传场景） ==========
    /**
     * 完整图片校验：空值→大小→后缀→Tika检测→魔数兜底
     * @param file 上传的图片文件
     * @return true=合法，false=非法
     */
    public static boolean validateImage(MultipartFile file) {
        // 1. 空值校验
        if (file == null || file.isEmpty()) {
            LogUtil.info("图片校验失败：文件为空");
            return false;
        }

        // 2. 文件大小校验
        if (file.getSize() > MAX_IMAGE_SIZE) {
            LogUtil.info("图片校验失败：文件大小超过20MB，当前大小：" +  file.getSize() / 1024);
            return false;
        }

        // 3. 后缀基础过滤
        String filename = file.getOriginalFilename();
        if (filename == null || !filename.contains(".")) {
            LogUtil.info("图片校验失败：文件名非法");
            return false;
        }
        String suffix = filename.substring(filename.lastIndexOf(".") + 1).toLowerCase();
        if (!ALLOWED_IMAGE_SUFFIXES.contains(suffix)) {
            LogUtil.info("图片校验失败：后缀非法，当前后缀：" + suffix);
            return false;
        }

        // 4. Tika智能检测真实MIME类型（核心）
        String realMimeType;
        byte[] bytes;
        try {
            bytes = file.getBytes();
        } catch (IOException e) {
            LogUtil.Error("图片校验失败：读取文件异常", e);
            return false;
        }
        try {
            realMimeType = TIKA.detect(bytes, filename);
        } catch (Exception e) {
            LogUtil.Error("图片校验失败：Tika检测异常", e);
            return false;
        }
        if (!ALLOWED_IMAGE_MIME_TYPES.contains(realMimeType)) {
            LogUtil.info("图片校验失败：真实MIME类型非法，检测结果：" + realMimeType);
            return false;
        }

        // 5. 二进制魔数兜底校验（防极端伪装）
        if (!validateMagicNumber(bytes, realMimeType)) {
            LogUtil.info("图片校验失败：魔数与MIME类型不匹配");
            return false;
        }

        return true;
    }

    // ========== 内部辅助方法 ==========
    /**
     * 二进制魔数校验（兜底核心）
     */
    private static boolean validateMagicNumber(byte[] bytes, String mimeType) {
        if (bytes == null || bytes.length < 2) {
            return false;
        }
        int readLen = Math.min(bytes.length, 12);
        byte[] magicBytes = new byte[readLen];
        System.arraycopy(bytes, 0, magicBytes, 0, readLen);
        String magicHex = bytesToHex(magicBytes).toUpperCase();

        // 按MIME类型匹配对应魔数
        switch (mimeType) {
            case "image/jpeg":
            case "image/jpg":
            case "image/pjpeg":
                return magicHex.startsWith("FFD8FF");
            case "image/png":
            case "image/x-png":
                return magicHex.startsWith("89504E47");
            case "image/gif":
            case "image/x-gif":
                return magicHex.startsWith("47494638");
            case "image/webp":
                return magicHex.startsWith("52494646") && isWebpSignature(magicBytes, readLen);
            case "image/bmp":
            case "image/x-ms-bmp":
            case "image/x-windows-bmp":
                return magicHex.startsWith("424D");
            case "image/tiff":
            case "image/x-tiff":
                return magicHex.startsWith("49492A00") || magicHex.startsWith("4D4D002A");
            default:
                return false;
        }
    }

    private static boolean isWebpSignature(byte[] magicBytes, int readLen) {
        if (readLen < 12) {
            return false;
        }
        String webp = new String(magicBytes, 8, 4, StandardCharsets.US_ASCII);
        return "WEBP".equals(webp);
    }

    /**
     * 字节数组转十六进制（工具方法）
     */
    private static String bytesToHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            sb.append(String.format("%02X", b));
        }
        return sb.toString();
    }
}
