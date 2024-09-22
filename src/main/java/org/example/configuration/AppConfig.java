package org.example.configuration;

import io.minio.MinioClient;
import org.apache.http.HttpHost;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestClientBuilder;
import org.elasticsearch.client.RestHighLevelClient;
import org.example.manager.MinioManager;
import org.example.nlp.ClassifierModel;
import org.example.manager.ELCSender;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;

@Configuration
public class AppConfig {

    @Bean
    public ClassifierModel classifierModel() throws IOException, ClassNotFoundException {
        return new ClassifierModel("classifier_model.dat", "classifier_model_tip.dat");
    }

    @Bean
    public MinioManager minioConnector() {
        MinioClient minioClient = MinioManager.minio("http://192.168.51.131:9001", "admin", "27Zeynalov");
        String bucketName = "example-bucket";
        String SAVE_DIR = "received_files/";
        MinioManager minioManager = new MinioManager(minioClient, bucketName);
        minioManager.checkCreateBucket();
        minioManager.checkSaveDir(SAVE_DIR);
        return minioManager;
    }


    public RestHighLevelClient client() {
        RestClientBuilder builder = RestClient.builder(
                new HttpHost("192.168.51.131", 9200, "http"));
        return new RestHighLevelClient(builder);
    }

    @Bean
    public ELCSender sender() {
        return new ELCSender(client());
    }
}

