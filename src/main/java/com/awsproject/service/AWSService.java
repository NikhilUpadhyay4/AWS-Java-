package com.awsproject.service;

import com.awsproject.model.AWSResource;
import com.awsproject.model.JobStatus;
import com.awsproject.repository.AWSRepository;
import com.awsproject.repository.JobStatusRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.ec2.Ec2Client;
import software.amazon.awssdk.services.ec2.model.DescribeInstancesResponse;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.ListBucketsResponse;
import software.amazon.awssdk.services.s3.model.ListObjectsV2Request;
import software.amazon.awssdk.services.s3.model.ListObjectsV2Response;

//import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;


@Service
public class AWSService {
    @Autowired
    private Ec2Client ec2Client;

    @Autowired
    private S3Client s3Client;

    @Autowired
    private AWSRepository awsResourceRepository;

    @Autowired
    private JobStatusRepository jobStatusRepository;

    /**
     * Discovers EC2 instances in the Mumbai region asynchronously and saves them in the database.
     * Updates the job status during the discovery process.
     *
     * @param jobId the ID of the job tracking this discovery process.
     * @return a CompletableFuture representing the asynchronous operation.
     */
    @Async
    public CompletableFuture<Void> discoverEc2Instances(String jobId) {
        try {
            jobStatusRepository.save(new JobStatus(jobId, "In Progress"));
            DescribeInstancesResponse response = ec2Client.describeInstances();
            response.reservations().forEach(reservation -> {
                reservation.instances().forEach(instance -> {
                    AWSResource resource = new AWSResource();
                    resource.setType("EC2");
                    resource.setDetails(instance.instanceId());
                    resource.setJobId(jobId);
                    awsResourceRepository.save(resource);
                });
            });
            jobStatusRepository.save(new JobStatus(jobId, "Success"));
        } catch (Exception e) {
            jobStatusRepository.save(new JobStatus(jobId, "Failed"));
        }
        return CompletableFuture.completedFuture(null);
    }

    /**
     * Discovers S3 buckets asynchronously and saves them in the database.
     * Updates the job status during the discovery process.
     *
     * @param jobId the ID of the job tracking this discovery process.
     * @return a CompletableFuture representing the asynchronous operation.
     */
    @Async
    public CompletableFuture<Void> discoverS3Buckets(String jobId) {
        try {
            jobStatusRepository.save(new JobStatus(jobId, "In Progress"));
            ListBucketsResponse response = s3Client.listBuckets();
            response.buckets().forEach(bucket -> {
                AWSResource resource = new AWSResource();
                resource.setType("S3");
                resource.setDetails(bucket.name());
                resource.setJobId(jobId);
                awsResourceRepository.save(resource);
            });
            jobStatusRepository.save(new JobStatus(jobId, "Success"));
        } catch (Exception e) {
            jobStatusRepository.save(new JobStatus(jobId, "Failed"));
        }
        return CompletableFuture.completedFuture(null);
    }

    /**
     * Discovers all objects in the specified S3 bucket asynchronously and saves them in the database.
     * Updates the job status during the discovery process.
     *
     * @param bucketName the name of the S3 bucket.
     * @param jobId the ID of the job tracking this discovery process.
     * @return a CompletableFuture representing the asynchronous operation.
     */
    @Async
    public CompletableFuture<Void> discoverS3BucketObjects(String bucketName, String jobId) {
        try {
            jobStatusRepository.save(new JobStatus(jobId, "In Progress"));

            ListObjectsV2Request request = ListObjectsV2Request.builder()
                    .bucket(bucketName)
                    .build();

            ListObjectsV2Response response = s3Client.listObjectsV2(request);

            response.contents().forEach(s3Object -> {
                String fileName = s3Object.key().substring(s3Object.key().lastIndexOf('/') + 1); // Extract file name
                AWSResource resource = new AWSResource();
                resource.setType("S3Object");
                resource.setDetails(fileName); // Store file name only
                resource.setBucketName(bucketName); // Store bucket name
                resource.setJobId(jobId);
                awsResourceRepository.save(resource);
            });

            jobStatusRepository.save(new JobStatus(jobId, "Success"));
        } catch (Exception e) {
            jobStatusRepository.save(new JobStatus(jobId, "Failed"));
        }
        return CompletableFuture.completedFuture(null);
    }

    /**
     * Gets the status of a job.
     *
     * @param jobId the ID of the job.
     * @return the status of the job, or "Job not found" if the job does not exist.
     */
    public String getJobResult(String jobId) {
        Optional<JobStatus> jobStatus = jobStatusRepository.findById(jobId);
        if (jobStatus.isEmpty()) {
            return "Job not found";
        }
        return jobStatus.get().getStatus();
    }


    /**
     * Gets a list of EC2 instance IDs.
     *
     * @return a list of EC2 instance IDs.
     */
    public List<String> getEc2Instances() {
        return awsResourceRepository.findByType("EC2").stream()
                .map(AWSResource::getDetails)
                .collect(Collectors.toList());
    }

    /**
     * Gets a list of S3 bucket names.
     *
     * @return a list of S3 bucket names.
     */
    public List<String> getS3Buckets() {
        return awsResourceRepository.findByType("S3").stream()
                .map(AWSResource::getDetails)
                .collect(Collectors.toList());
    }

    /**
     * Gets a list of objects in the specified S3 bucket.
     *
     * @param bucketName the name of the S3 bucket.
     * @return a list of object names in the specified S3 bucket.
     */
    public List<String> getS3BucketObjects(String bucketName) {
        return awsResourceRepository.findByTypeAndDetails("S3Object", bucketName).stream()
                .map(AWSResource::getDetails)
                .collect(Collectors.toList());
    }


    /**
     * Gets the count of objects in the specified S3 bucket.
     *
     * @param bucketName the name of the S3 bucket.
     * @return the number of objects in the specified S3 bucket.
     */
    public long getS3BucketObjectCount(String bucketName) {
        return awsResourceRepository.findByBucketName(bucketName).size();
    }

    /**
     * Gets a list of object names in the specified S3 bucket that match the given pattern.
     *
     * @param bucketName the name of the S3 bucket.
     * @param pattern the pattern to match the object names.
     * @return a list of matching object names.
     */

    public List<String> getS3BucketObjectLike(String bucketName, String pattern) {
        return awsResourceRepository.findByBucketNameAndPattern(bucketName, pattern).stream()
                .map(AWSResource::getDetails)
                .collect(Collectors.toList());
    }


}
