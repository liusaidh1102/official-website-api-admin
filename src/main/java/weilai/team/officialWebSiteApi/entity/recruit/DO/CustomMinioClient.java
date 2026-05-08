package weilai.team.officialWebSiteApi.entity.recruit.DO;
import com.google.common.collect.Multimap;
import io.minio.*;
import io.minio.errors.*;
import io.minio.messages.Part;
import org.springframework.stereotype.Component;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
/**
 * @author lzw
 * @date 2024/12/10 19:42
 * @description 作用：
 */
@Component
public class CustomMinioClient extends MinioClient {

    /**
     * 继承父类
     */
    public CustomMinioClient(MinioClient client) {
        super(client);
    }

    /**
     * 初始化分片上传即获取uploadId
     */
    public String initMultiPartUpload(String bucket, String region, String object,
                                      Multimap<String, String> headers, Multimap<String, String> extraQueryParams) throws Exception {
        CreateMultipartUploadResponse response = this.createMultipartUpload(bucket, region, object, headers, extraQueryParams);
        return response.result().uploadId();
    }

    /**
     * 上传单个分片
     */
    public UploadPartResponse uploadMultiPart(String bucket,
                                              String region,
                                              String object,
                                              Object data,
                                              long length,
                                              String uploadId,
                                              int partNumber,
                                              Multimap<String, String> headers,
                                              Multimap<String, String> extraQueryParams) throws IOException, InvalidKeyException, NoSuchAlgorithmException, InsufficientDataException, ServerException, InternalException, XmlParserException, InvalidResponseException, ErrorResponseException {
        return this.uploadPart(bucket, region, object, data, (int) length, uploadId, partNumber, headers, extraQueryParams);
    }

    /**
     * 合并分片
     */
    public ObjectWriteResponse mergeMultipartUpload(String bucketName, String region, String objectName, String uploadId, Part[] parts, Multimap<String, String> extraHeaders, Multimap<String, String> extraQueryParams) throws IOException, NoSuchAlgorithmException, InsufficientDataException, ServerException, InternalException, XmlParserException, InvalidResponseException, ErrorResponseException, ServerException, InvalidKeyException {
        return this.completeMultipartUpload(bucketName, region, objectName, uploadId, parts, extraHeaders, extraQueryParams);
    }

//    public void cancelMultipartUpload(String bucketName, String region, String objectName, String uploadId, Multimap<String, String> extraHeaders, Multimap<String, String> extraQueryParams) throws ServerException, InsufficientDataException, ErrorResponseException, NoSuchAlgorithmException, IOException, InvalidKeyException, XmlParserException, InvalidResponseException, InternalException {
//        this.abortMultipartUpload(bucketName, region, objectName, uploadId, extraHeaders, extraQueryParams);
//    }

    /**
     * 查询当前上传后的分片信息
     */
    public ListPartsResponse listMultipart(String bucketName, String region, String objectName, Integer maxParts, Integer partNumberMarker, String uploadId, Multimap<String, String> extraHeaders, Multimap<String, String> extraQueryParams) throws NoSuchAlgorithmException, InsufficientDataException, IOException, InvalidKeyException, ServerException, XmlParserException, ErrorResponseException, InternalException, InvalidResponseException {
        return this.listParts(bucketName, region, objectName, maxParts, partNumberMarker, uploadId, extraHeaders, extraQueryParams);
    }


}
