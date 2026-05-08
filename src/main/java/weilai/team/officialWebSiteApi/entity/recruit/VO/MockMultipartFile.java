package weilai.team.officialWebSiteApi.entity.recruit.VO;
import org.springframework.web.multipart.MultipartFile;
import java.io.*;

public class MockMultipartFile implements MultipartFile {
    private final String name;
    private final byte[] content;
    private final String originalFilename;
    private final String contentType;

    public MockMultipartFile(String name, byte[] content, String originalFilename, String contentType) {
        this.name = name;
        this.content = content;
        this.originalFilename = originalFilename;
        this.contentType = contentType;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getOriginalFilename() {
        return originalFilename;
    }

    @Override
    public String getContentType() {
        return contentType;
    }

    @Override
    public boolean isEmpty() {
        return content == null || content.length == 0;
    }

    @Override
    public long getSize() {
        return content.length;
    }

    @Override
    public byte[] getBytes() throws IOException {
        return content;
    }

    @Override
    public InputStream getInputStream() throws IOException {
        return new ByteArrayInputStream(content);
    }

    @Override
    public void transferTo(File dest) throws IOException, IllegalStateException {
        try (OutputStream out = new FileOutputStream(dest)) {
            out.write(content);
        }
    }
}
