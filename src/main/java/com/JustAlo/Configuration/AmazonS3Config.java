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

