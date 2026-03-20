package weilai.team.officialWebSiteApi.util;

import com.itextpdf.io.image.ImageData;
import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Image;
import org.springframework.stereotype.Component;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

@Component
public class ImageToPdfConverter {

    /**
     * 将两张图片（通过 InputStream）转换为一个 PDF，并将 PDF 数据写入到 ByteArrayOutputStream 中
     *
     * @param imageStream1 第一张图片的输入流
     * @param imageStream2 第二张图片的输入流
     * @return 包含 PDF 数据的字节数组输出流
     */
    public ByteArrayOutputStream convertTwoImagesToPdf(InputStream imageStream1, InputStream imageStream2) throws IOException {
        // 创建一个字节数组输出流，用于存储生成的 PDF 数据
        ByteArrayOutputStream pdfOutputStream = new ByteArrayOutputStream();

        // 创建 PdfWriter 和 PdfDocument
        PdfWriter writer = new PdfWriter(pdfOutputStream);
        PdfDocument pdf = new PdfDocument(writer);
        Document document = new Document(pdf);

        // ======================
        // 处理第一张图片
        // ======================
        // 将 InputStream 转换为 byte[]
        byte[] imageBytes1 = readInputStreamToByteArray(imageStream1);
        // 使用 ImageDataFactory.create(byte[]) 创建 ImageData
        ImageData imageData1 = ImageDataFactory.create(imageBytes1);
        // 创建 PDF 图片对象并添加到文档
        Image pdfImage1 = new Image(imageData1);
        document.add(pdfImage1);

        // ======================
        // 处理第二张图片
        // ======================
        // 将 InputStream 转换为 byte[]
        byte[] imageBytes2 = readInputStreamToByteArray(imageStream2);
        // 使用 ImageDataFactory.create(byte[]) 创建 ImageData
        ImageData imageData2 = ImageDataFactory.create(imageBytes2);
        // 创建 PDF 图片对象并添加到文档
        Image pdfImage2 = new Image(imageData2);
        document.add(pdfImage2);

        // 关闭文档，完成 PDF 生成
        document.close();

        // 返回包含 PDF 数据的输出流
        return pdfOutputStream;
    }

    /**
     * 将 InputStream 中的数据读取为字节数组
     *
     * @param inputStream 输入流
     * @return 字节数组
     */
    private byte[] readInputStreamToByteArray(InputStream inputStream) throws IOException {
        // 创建一个字节数组输出流，用于临时存储输入流中的数据
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        // 缓冲区，用于读取输入流数据
        byte[] buffer = new byte[4096];
        int bytesRead;

        // 循环读取输入流数据，直到读取完毕
        while ((bytesRead = inputStream.read(buffer)) != -1) {
            // 将读取到的数据写入到字节数组输出流
            byteArrayOutputStream.write(buffer, 0, bytesRead);
        }
        // 将字节数组输出流中的数据转换为字节数组并返回
        return byteArrayOutputStream.toByteArray();
    }
}