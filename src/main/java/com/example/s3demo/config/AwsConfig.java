package com.example.s3demo.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.S3ClientBuilder;
import software.amazon.awssdk.services.s3control.S3ControlClient;
import software.amazon.awssdk.services.s3control.S3ControlClientBuilder;

import java.net.URI;

@Configuration
public class AwsConfig {

    @Value("${aws.region}")
    private String awsRegion;

    @Value("${aws.endpoint-url:}") // Allow endpoint to be optional
    private String s3EndpointUrl;

    /**
     * Creates and configures the S3Client bean for data plane operations (e.g., buckets, objects).
     *
     * @return A configured S3Client.
     */
    @Bean
    public S3Client s3Client() {
        S3ClientBuilder builder = S3Client.builder();

        if (s3EndpointUrl != null && !s3EndpointUrl.isEmpty()) {
            builder.endpointOverride(URI.create(s3EndpointUrl));
            builder.credentialsProvider(StaticCredentialsProvider.create(
                    AwsBasicCredentials.create("test", "test")
            ));
        }

        return builder.region(Region.of(awsRegion)).build();
    }

    /**
     * Creates and configures the S3ControlClient bean for control plane operations (e.g., access points).
     *
     * @return A configured S3ControlClient.
     */
    @Bean
    public S3ControlClient s3ControlClient() {
        S3ControlClientBuilder builder = S3ControlClient.builder();

        if (s3EndpointUrl != null && !s3EndpointUrl.isEmpty()) {
            // For LocalStack, the S3 and S3Control services run on the same endpoint.
            builder.endpointOverride(URI.create(s3EndpointUrl));
            builder.credentialsProvider(StaticCredentialsProvider.create(
                    AwsBasicCredentials.create("test", "test")
            ));
        }

        return builder.region(Region.of(awsRegion)).build();
    }
}
