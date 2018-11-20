package launch;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.concurrent.TimeUnit;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.apache.http.HttpEntity;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.InputStreamBody;
import org.apache.http.impl.client.HttpClientBuilder;

import com.google.zxing.WriterException;

public class KeepInstanceUp {

    public static void main(String[] args) {
        try {
            while (true) {
                long start = System.currentTimeMillis();

                byte[] zip = generateZip();

                InputStreamBody body = new InputStreamBody(new ByteArrayInputStream(zip), "inputs.zip");

                MultipartEntityBuilder builder = MultipartEntityBuilder.create();
                builder.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);
                builder.addPart("file", body);
                HttpEntity entity = builder.build();

                HttpPost request = new HttpPost("https://en-code-optimisation-contest.herokuapp.com/stockExchange");
                request.setEntity(entity);

                HttpClient client = HttpClientBuilder.create().build();
                try {
                    client.execute(request);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                long end = System.currentTimeMillis();
                System.out.println("Request took: " + (end - start) + " MS.");
                TimeUnit.MINUTES.sleep(1);
            }
        } catch (Exception e) {
        }
    }

    private static byte[] generateZip() throws IOException, WriterException {
        ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
        ZipOutputStream out = new ZipOutputStream(byteOut);

        for (int i = 0; i < 2; ++i) {
            ZipEntry e = new ZipEntry(i + ".png");
            out.putNextEntry(e);
            StringBuilder sb = new StringBuilder();
            for (int j = 0; j < 1000; ++j) {
                double value = 5 + Math.random() * 90;
                sb.append(((int)(value * 100) / 100)).append(" ");
            }
            QRCodeGenerator.generateQRCodeImage(sb.toString(), out);
            out.closeEntry();
        }
        out.close();
        return byteOut.toByteArray();

    }
}
