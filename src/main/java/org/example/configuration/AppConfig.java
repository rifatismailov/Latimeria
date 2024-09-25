package org.example.configuration;

import io.minio.MinioClient;
import org.apache.http.HttpHost;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestClientBuilder;
import org.elasticsearch.client.RestHighLevelClient;
import org.example.manager.MinioManager;
import org.example.nlp.ClassifierModel;
import org.example.manager.ElasticSender;
import org.example.reader.Reader;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;
import java.util.logging.Logger;

@Configuration
public class AppConfig {
    private static final Logger LOGGER = Logger.getLogger(AppConfig.class.getName());

    @Value("${classifier.model}")
    private String classifier_model;

    @Value("${classifier.model_tip}")
    private String classifier_model_tip;

    @Value("${minio.url}")
    private String minioUrl;

    @Value("${minio.access-key}")
    private String minioAccessKey;

    @Value("${minio.secret-key}")
    private String minioSecretKey;

    @Value("${elc.host}")
    private String elc_host;

    @Value("${elc.port}")
    private String elc_port;

    @Value("${elc.scheme}")
    private String elc_scheme;

    @Bean
    public ClassifierModel classifierModel() throws IOException, ClassNotFoundException {
        LOGGER.info(classifier_model + " " + classifier_model_tip);
        return new ClassifierModel(classifier_model, classifier_model_tip);
    }

    @Bean
    public MinioManager minioConnector() {
        LOGGER.info(minioUrl + " " + minioAccessKey + " " + minioSecretKey);
        MinioClient minioClient = MinioManager.minio("http://192.168.51.131:9001", "admin", "27Zeynalov");
        String bucketName = "example-bucket";
        MinioManager minioManager = new MinioManager(minioClient, bucketName);
        minioManager.checkCreateBucket();
        minioManager.checkSaveDir("received_files/");
        return minioManager;
    }


    public RestHighLevelClient client() {
        LOGGER.info(elc_host + " " + elc_port + " " + elc_scheme);
        int port = Integer.parseInt(elc_port);
        RestClientBuilder builder = RestClient.builder(
                new HttpHost(elc_host, port, elc_scheme));
        return new RestHighLevelClient(builder);
    }

    @Bean
    public ElasticSender elasticSender() {
        return new ElasticSender(client());
    }
}

