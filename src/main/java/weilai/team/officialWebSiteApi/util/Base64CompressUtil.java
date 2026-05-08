package weilai.team.officialWebSiteApi.util;

import lombok.extern.slf4j.Slf4j;
import net.coobird.thumbnailator.Thumbnails;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Base64;

/**
 * ClassName:Base64Img
 * Description:
 *
 * @Author:独酌
 * @Create:2025/8/14 10:04
 */
@Slf4j
public class Base64CompressUtil {

    /**
     * 自定义压缩图片
     * @param commentTxt 评论内容
     * @return 压缩后的图片
     */
    public static String ownCompressImage(String commentTxt) {
        String res = commentTxt;
        if(commentTxt.length() > 80000) {
            log.info("评论长度超过 80000，开始压缩，原大小: " + commentTxt.length());
            // 当图片质量大于 80000 时，需要压缩，将图片质量压缩为 80000 之内
            String[] split = commentTxt.split("<!-- IMG_SPLIT -->");
            float quality;
            if(split.length == 1) {
                // 长度为 1 表示只有文字
                res = commentTxt;
            } else if(split.length == 2) {
                // 长度为 2 表示只有图片
                try {
                    quality = Math.min(80000.0f / split[1].length(), 1.0f);
                    quality = Math.round(quality * 100) / 100.0f;
                    // 这里进行图片压缩，但是最大压缩为原来的 0.05，如果到达0.05f还是大于80000，那末直接用0.05压缩的图片
                    String compressImage = split[1];
                    while(compressImage.length() > 80000 && quality >= 0.05f) {
                        compressImage = Base64CompressUtil.compressImage(split[1], quality);
                        quality -= 0.01f;
                    }
                    res = "<!-- IMG_SPLIT -->" + compressImage + "<!-- IMG_SPLIT -->";
                } catch (IOException e) {
                    log.error("图片压缩失败，使用原画质");
                    res = commentTxt;
                }
            } else {
                // 长度为 3 表示有文字 + 图片
                try {
                    quality = Math.min(80000.0f / split[1].length(), 1.0f);
                    quality = Math.round(quality * 100) / 100.0f;
                    // 这里进行图片压缩，但是最大压缩为原来的 0.05，如果到达0.05f还是大于80000，那末直接用0.05压缩的图片
                    String compressImage = split[1];
                    while(compressImage.length() > 80000 && quality > 0.05f) {
                        compressImage = Base64CompressUtil.compressImage(split[1], quality);
                        quality -= 0.01f;
                    }
                    String text  = split[2];
                    res = "<!-- IMG_SPLIT -->" + compressImage + "<!-- IMG_SPLIT -->" + text;
                } catch (IOException e) {
                    log.error("图片压缩失败，使用原画质");
                    res = commentTxt;
                }
            }
        }
        log.info("评论大小: {}", res.length());
        return res;
    }


    /**
     * 压缩前端传来的带分隔符的base64图片（保持尺寸不变，只调整质量）
     * @param input 前端传来的完整字符串
     * @param quality 质量(0.0-1.0)
     * @return 压缩后的base64字符串
     * @throws IOException 处理过程中的IO异常
     */
    public static String compressImage(String input, float quality) throws IOException {
        // 1. 提取有效base64数据
        String base64Data = extractBase64Data(input);
        if (base64Data == null || base64Data.isEmpty()) {
            throw new IllegalArgumentException("无效的图片数据");
        }

        // 2. 解码base64为字节数组
        byte[] imageBytes = Base64.getDecoder().decode(base64Data);

        // 3. 转换为BufferedImage
        try (ByteArrayInputStream bais = new ByteArrayInputStream(imageBytes)) {
            BufferedImage originalImage = ImageIO.read(bais);

            // 4. 压缩图片（保持尺寸不变，只调整质量）
            try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
                // 明确指定输出格式为JPEG
                Thumbnails.of(originalImage)
                        .size(originalImage.getWidth(), originalImage.getHeight()) // 保持原尺寸
                        .outputFormat("jpg") // 明确指定输出格式
                        .outputQuality(quality)
                        .toOutputStream(baos);

                // 5. 压缩后的字节数组转为base64
                byte[] compressedBytes = baos.toByteArray();
                return "data:image/jpeg;base64," + Base64.getEncoder().encodeToString(compressedBytes);
            }
        }
    }

    /**
     * 提取纯base64数据
     */
    private static String extractBase64Data(String input) {
        // 移除分隔符
        String withoutSplit = input.replace("<!-- IMG_SPLIT -->", "");

        // 提取base64部分
        int commaIndex = withoutSplit.indexOf(',');
        if (commaIndex != -1 && commaIndex < withoutSplit.length() - 1) {
            return withoutSplit.substring(commaIndex + 1);
        }
        return null;
    }
}
