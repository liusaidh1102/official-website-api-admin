package weilai.team.officialWebSiteApi.config;
import io.minio.MinioClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import javax.annotation.Resource;
/**
 * @author lzw
 * @date 2024/11/11 10:15
 * @description  minio的配置类
 */
@Configuration
public class MinioConfig {

    @Resource
    private MinioConfigProperties minioConfigProperties;


    //单例的，没有线程安全问题
    @Bean
    public MinioClient buildMinioClient() {
        //链式编程
        return MinioClient
                .builder()
                .credentials(minioConfigProperties.getAccessKey(), minioConfigProperties.getSecretKey())
                .endpoint(minioConfigProperties.getEndpoint())
                .build();
    }



}
