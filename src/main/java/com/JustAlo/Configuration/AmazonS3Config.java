package com.JustAlo.Configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.S3Configuration;
import java.net.URI;

@Configuration
public class AmazonS3Config {

    @Bean
    public S3Client s3Client() {
        return S3Client.builder()
                .region(Region.US_EAST_1) // This is required but will be ignored by the custom endpoint
                .endpointOverride(URI.create("https://blr1.digitaloceanspaces.com"))
                .credentialsProvider(StaticCredentialsProvider.create(AwsBasicCredentials.create(
                        "DO00WXBHC4LZ2BXCWUWZ",
                        "NsGo7BivJXQbp6Yfh8fNpzUFfaXAEoAPo7FqqXZoNxY")))
                .serviceConfiguration(S3Configuration.builder().pathStyleAccessEnabled(true).build())
                .build();
    }
}

//import com.amazonaws.auth.AWSStaticCredentialsProvider;
//import com.amazonaws.auth.BasicAWSCredentials;
//import com.amazonaws.services.s3.AmazonS3;
//import com.amazonaws.services.s3.AmazonS3ClientBuilder;
//import com.amazonaws.services.s3.model.ObjectMetadata;
//import com.amazonaws.services.s3.model.PutObjectRequest;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.web.multipart.MultipartFile;
//
//import java.io.IOException;
//import java.io.InputStream;
//import java.net.URI;
//import java.net.URISyntaxException;
//import java.util.UUID;
//
//@Configuration
//public class AmazonS3Config {
//
//    @Value("${aws.accessKeyId}")
//    private String accessKey;
//
//    @Value("${aws.secretKey}")
//    private String sercetKey;
//
//    @Value("${region}")
//    private String region;
//
//    @Value("${aws.s3.bucketName}")
//    private  String bucketName;
//    @Autowired
//    private AmazonS3 amazonS3;
//
//    @Bean
//    public AmazonS3 amazonS3() {
//        BasicAWSCredentials awsCreds = new BasicAWSCredentials(accessKey, sercetKey);
//        return AmazonS3ClientBuilder.standard()
//                .withRegion(region)
//                .withCredentials(new AWSStaticCredentialsProvider(awsCreds))
//                .build();
//    }
//    public String uploadFile(MultipartFile file) throws IOException {
//        String key = UUID.randomUUID().toString() + "-" + file.getOriginalFilename();
//        try (InputStream inputStream = file.getInputStream()) {
//            ObjectMetadata metadata = new ObjectMetadata();
//            metadata.setContentLength(file.getSize());
//            amazonS3.putObject(new PutObjectRequest(bucketName, key, inputStream, metadata));
//        } catch (IOException e) {
//            throw new IOException("Error reading file input stream", e);
//        }
//
//        return generateFileUrl(key);
//    }
//    private String generateFileUrl(String key) {
//        try {
//            return new URI("https", String.format("%s.s3.%s.amazonaws.com", bucketName, region), "/" + key, null).toString();
//        } catch (URISyntaxException e) {
//            throw new RuntimeException("Error generating file URL", e);
//        }
//    }
//}