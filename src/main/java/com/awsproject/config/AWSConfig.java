package com.awsproject.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.ec2.Ec2Client;
import software.amazon.awssdk.services.s3.S3Client;


@Configuration
public class AWSConfig {

    @Value("${cloud.aws.credentials.access-key}")
    private String ACCESS_KEY;

    @Value("${cloud.aws.credentials.secret-key}")
    private String ACCESS_SECRET ;
    /**
     * Creates an EC2 client bean with the configured access key and secret key.
     *
     * @return an EC2Client instance.
     */
    @Bean
    public Ec2Client ec2Client() {
        AwsBasicCredentials credentials = AwsBasicCredentials.create(ACCESS_KEY, ACCESS_SECRET);
        return Ec2Client.builder()
                .region(Region.AP_SOUTH_1) // Mumbai region
                .credentialsProvider(StaticCredentialsProvider.create(credentials))
                .build();
    }

    /**
     * Creates an S3 client bean with the configured access key and secret key.
     *
     * @return an S3Client instance.
     */
    @Bean
    public S3Client s3Client() {
        AwsBasicCredentials credentials = AwsBasicCredentials.create(ACCESS_KEY, ACCESS_SECRET);
        return S3Client.builder()
                .region(Region.AP_SOUTH_1) // Mumbai region
                .credentialsProvider(StaticCredentialsProvider.create(credentials))
                .build();
    }
}
