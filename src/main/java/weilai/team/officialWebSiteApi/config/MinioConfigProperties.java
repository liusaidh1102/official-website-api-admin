package weilai.team.officialWebSiteApi.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @author lzw
 * @date 2024/11/11 10:16
 * @description  将配置文件中的属性注入到配置类中
 */

@Component
@Data
@ConfigurationProperties(prefix = "minio")
public class MinioConfigProperties {

    private String accessKey;

    private String secretKey;

    private String endpoint;

    private String bucketName;

}
