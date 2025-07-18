package com.example.s3demo;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.localstack.LocalStackContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.HeadBucketRequest;
import software.amazon.awssdk.services.s3.model.NoSuchBucketException;
import software.amazon.awssdk.services.s3control.S3ControlClient;
import software.amazon.awssdk.services.s3control.model.GetAccessPointRequest;


import static org.junit.jupiter.api.Assertions.*;

@Testcontainers
@SpringBootTest
class AwsS3ServiceIntegrationTest {

    @Container
    static LocalStackContainer localStack = new LocalStackContainer(DockerImageName.parse("localstack/localstack:latest").asCompatibleSubstituteFor("localstack/localstack:4.6.0"))
            .withServices(LocalStackContainer.Service.S3);

    @DynamicPropertySource
    static void overrideProperties(DynamicPropertyRegistry registry) {
        // This single endpoint override works for both S3Client and S3ControlClient
        registry.add("aws.region", localStack::getRegion);

        registry.add("aws.endpoint-url", () -> localStack.getEndpointOverride(LocalStackContainer.Service.S3).toString());

        //registry.add("aws.s3.endpoint-url", () -> localStack.getEndpointOverride(LocalStackContainer.Service.S3).toString());
    }

    @Autowired
    private AwsS3Service awsS3Service;

    @Autowired
    private S3Client s3Client; // For bucket assertions

    @Autowired
    private S3ControlClient s3ControlClient; // For access point assertions

    private static final String LOCALSTACK_ACCOUNT_ID = "000000000000";

    @Test
    void testCreateBucket() {
        String bucketName = "test-bucket-" + System.currentTimeMillis();

        assertThrows(NoSuchBucketException.class, () -> s3Client.headBucket(HeadBucketRequest.builder().bucket(bucketName).build()));

        boolean result = awsS3Service.createBucket(bucketName);
        assertTrue(result, "Service should return true on successful bucket creation");

        assertDoesNotThrow(() -> {
            s3Client.headBucket(HeadBucketRequest.builder().bucket(bucketName).build());
        }, "S3Client should be able to find the bucket after creation");
    }

    @Test
    void testCreateAccessPoint() {
        String bucketName = "ap-test-bucket-" + System.currentTimeMillis();
        String accessPointName = "my-test-access-point";

        awsS3Service.createBucket(bucketName);
        assertDoesNotThrow(() -> {
            s3Client.headBucket(HeadBucketRequest.builder().bucket(bucketName).build());
        }, "Pre-condition failed: Bucket for access point could not be created.");

        boolean result = awsS3Service.createAccessPoint(bucketName, accessPointName, LOCALSTACK_ACCOUNT_ID);
        assertTrue(result, "Service should return true on successful access point creation");

        assertDoesNotThrow(() -> {
            GetAccessPointRequest request = GetAccessPointRequest.builder()
                    .name(accessPointName)
                    .accountId(LOCALSTACK_ACCOUNT_ID)
                    .build();
            s3ControlClient.getAccessPoint(request);
        }, "S3ControlClient should be able to find the access point after creation");
    }
}
