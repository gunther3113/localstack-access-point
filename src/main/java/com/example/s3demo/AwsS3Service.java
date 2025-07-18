package com.example.s3demo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.CreateBucketRequest;
import software.amazon.awssdk.services.s3.model.S3Exception;
import software.amazon.awssdk.services.s3control.S3ControlClient;
import software.amazon.awssdk.services.s3control.model.CreateAccessPointRequest;

@Service
public class AwsS3Service {

    private static final Logger logger = LoggerFactory.getLogger(AwsS3Service.class);
    private final S3Client s3Client;
    private final S3ControlClient s3ControlClient;

    public AwsS3Service(S3Client s3Client, S3ControlClient s3ControlClient) {
        this.s3Client = s3Client;
        this.s3ControlClient = s3ControlClient;
    }

    /**
     * Creates a new S3 bucket.
     *
     * @param bucketName The name of the bucket to create.
     * @return true if creation was successful, false otherwise.
     */
    public boolean createBucket(String bucketName) {
        try {
            logger.info("Attempting to create bucket: {}", bucketName);
            CreateBucketRequest createBucketRequest = CreateBucketRequest.builder()
                    .bucket(bucketName)
                    .build();
            s3Client.createBucket(createBucketRequest);
            logger.info("Successfully created bucket: {}", bucketName);
            return true;
        } catch (S3Exception e) {
            logger.error("Error creating bucket '{}': {}", bucketName, e.awsErrorDetails().errorMessage(), e);
            return false;
        }
    }

    /**
     * Creates a new S3 access point for a given bucket.
     *
     * @param bucketName      The name of the bucket.
     * @param accessPointName The name for the new access point.
     * @param accountId       The AWS account ID.
     * @return true if creation was successful, false otherwise.
     */
    public boolean createAccessPoint(String bucketName, String accessPointName, String accountId) {
        try {
            logger.info("Attempting to create access point '{}' for bucket '{}'", accessPointName, bucketName);
            CreateAccessPointRequest request = CreateAccessPointRequest.builder()
                    .bucket(bucketName)
                    .name(accessPointName)
                    .accountId(accountId)
                    .build();
            s3ControlClient.createAccessPoint(request);
            logger.info("Successfully created access point '{}'", accessPointName);
            return true;
        } catch (S3Exception e) {
            // S3ControlException also inherits from S3Exception
            logger.error("Error creating access point '{}': {}", accessPointName, e.awsErrorDetails().errorMessage(), e);
            return false;
        }
    }
}
