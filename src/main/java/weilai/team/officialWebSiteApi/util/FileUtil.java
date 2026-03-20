package weilai.team.officialWebSiteApi.util;
import org.apache.commons.lang3.StringUtils;
import org.apache.tika.exception.TikaException;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.metadata.TikaMetadataKeys;
import org.apache.tika.mime.MediaType;
import org.apache.tika.parser.AutoDetectParser;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.parser.Parser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.web.multipart.MultipartFile;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;
import weilai.team.officialWebSiteApi.entity.recruit.VO.MockMultipartFile;
import weilai.team.officialWebSiteApi.entity.recruit.enums.FileTypeEnum;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Objects;
import java.util.regex.Matcher;
/**
 * @author lzw
 * @date 2024/11/11 10:38
 * @description 支持上传的文件的类型
 */
public class FileUtil {

    private static final Logger log = LoggerFactory.getLogger(FileUtil.class);

    /**
     * 获取文件对应的mime类型
     *
     * @param file 文件
     * @return 返回mime类型
     */
    public static String getMimeType(File file) {
        //创建自动检测解析器
        AutoDetectParser parser = new AutoDetectParser();
        //设置解析器
        parser.setParsers(new HashMap<MediaType, Parser>());
        //创建元数据
        Metadata metadata = new Metadata();
        //添加文件名
        metadata.add(TikaMetadataKeys.RESOURCE_NAME_KEY, file.getName());
        //因为tika判断的是file，所以要将multipartFile转化成输入流
        try (InputStream stream = Files.newInputStream(file.toPath())) {
            // 解析文件流，获取文件的元数据信息
            parser.parse(stream, new DefaultHandler(), metadata, new ParseContext());
        } catch (IOException e) {
            // 捕获IO异常并记录错误日志
            LogUtil.Error("文件类型识别失败 - IO异常", e);
        } catch (TikaException e) {
            // 捕获Tika异常并记录错误日志，然后抛出运行时异常
            LogUtil.Error("文件类型识别失败 - Tika异常", e);
        } catch (SAXException e) {
            // 捕获SAX异常并记录错误日志，然后抛出运行时异常
            LogUtil.Error("文件类型识别失败 - SAX异常", e);
        }
        return metadata.get(HttpHeaders.CONTENT_TYPE);
    }

    //获取文件的大小
    public static long getFileSize(File file) {
        return file.length();
    }

    //获取multipartFile文件的大小
    public static long getFileSize(MultipartFile multipartFile) {
        return multipartFile.getSize();
    }

    //判断文件的大小是不是符合要求
    public static boolean isExpectedFileSize(MultipartFile multipartFile, long maxSize) {
        return getFileSize(multipartFile) <= maxSize;
    }




    /**
     * 判断是否是我们想要的文件类型
     *
     * @param multipartFile 文件
     * @param fileType      文件类型的枚举
     * @return true 是   false 否
     */
    public static boolean isExpectedFileType(MultipartFile multipartFile, FileTypeEnum fileType) {
        //将MultipartFile转化为File
        File file = new File(Objects.requireNonNull(multipartFile.getOriginalFilename()));
        try (FileOutputStream fos = new FileOutputStream(file);
             InputStream is = multipartFile.getInputStream()) {
            int len;
            byte[] buffer = new byte[1024];
            while ((len = is.read(buffer)) != -1) {
                fos.write(buffer, 0, len);
            }
        } catch (IOException e) {
            LogUtil.Error("文件从MultipartFile转化为File转换失败", e);
        }
        String type = getMimeType(file);
        LogUtil.info(FileUtil.class + "文件类型识别结果：" + type);
        //对比对应的文件类型的mime
        Matcher m = fileType.getPattern().matcher(type);
        //最后删除文件
        try {
            Files.deleteIfExists(file.toPath());
        } catch (IOException e) {
            LogUtil.Error("文件删除失败", e);
        }
        return m.matches();
    }

    /**
     * 获取文件后缀
     *
     * @param fileName 文件名
     * @return 返回后缀名称 jpg
     */
    public static String getFileSuffix(String fileName) {
        if (StringUtils.isEmpty(fileName)) {
            return "";
        }
        int dotIndex = fileName.lastIndexOf('.');
        if (dotIndex == -1 || dotIndex == fileName.length() - 1) {
            return "";
        }
        return fileName.substring(dotIndex + 1);
    }


    /**
     * 将文件转化为MultipartFile
     * @param file 文件
     * @return 返回结果 ,发生错误返回null
     */
    public static MultipartFile FileToMultipartFile(File file) {
        try {
            Path path = Paths.get(file.getAbsolutePath());
            String name = file.getName();
            String originalFileName = file.getName();
            String contentType = Files.probeContentType(path);
            LogUtil.info(FileUtil.class + "文件类型识别结果：" + contentType);
            byte[] content = null;
            try {
                content = Files.readAllBytes(path);
            } catch (final IOException e) {
                LogUtil.Error("文件读取失败", e);
                return null;
            }
            return new MockMultipartFile(name, content, originalFileName, contentType);
        } catch (IOException e) {
            LogUtil.Error("文件转化为MultipartFile失败", e);
            return null;
        }
    }


//    private static boolean getLicense() {
//        boolean result = false;
//        try {
//            InputStream license = FileUtil.class.getResourceAsStream("/license.xml.bak");
//            License aposeLic = new License();
//            aposeLic.setLicense(license);
//            result = true;
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        return result;
//    }
//
//    public static void converter(String resource, String target){
//        // 验证License
//        if (!getLicense()) {
//            return;
//        }
//        InputStream inputStream = null;
//        OutputStream outputStream = null;
//
//        try {
//            long old = System.currentTimeMillis();
//            File inputFile = new File(resource);
//            inputStream = Files.newInputStream(inputFile.toPath());
//            Document doc = new Document(inputStream);
//            File outPut = new File(target);
//            outputStream = Files.newOutputStream(outPut.toPath());
//            doc.save(outputStream, SaveFormat.PDF);
//        }catch (Exception e){
//            LogUtil.Error("异常", e);
//        }finally {
//            //关闭流，不然文件被占用，无法删除
//            try {
//                if (inputStream != null) {
//                    inputStream.close();
//                }
//                if (outputStream != null) {
//                    outputStream.close();
//                }
//            } catch (IOException e) {
//                LogUtil.Error("异常", e);
//            }
//        }
//    }

}
