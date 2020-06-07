package il.wapp.app.configuration;
import io.minio.MinioClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MinioConfiguration {
    @Value("${minio.access.key}")
    String accessKey;
    @Value("${minio.access.secret}")
    String accessSecret;
    @Value("${minio.url}")
    String minioUrl;

    @Bean
    public MinioClient generateMinioClient() throws Exception{
        MinioClient client = new MinioClient(minioUrl, accessKey, accessSecret);
        return client;
    }
}
