package com.awsproject.controller;

import com.awsproject.service.AWSService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.GetMapping;

import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/aws")
public class AWSController {

    @GetMapping("/hello")
    public String getHello(){
        return "Working";
    }
    @Autowired
    private AWSService awsDiscoveryService;

    /**
     * Initiates the discovery of specified AWS services (EC2 and/or S3) asynchronously.
     *
     * @param services a list of AWS services to discover.
     * @return a job ID tracking this discovery process.
     */
    @PostMapping("/discoverServices")
    public String discoverServices(@RequestBody List<String> services) {
        String jobId = UUID.randomUUID().toString();
        if (services.contains("EC2")) {
            awsDiscoveryService.discoverEc2Instances(jobId);
        }
        if (services.contains("S3")) {
            awsDiscoveryService.discoverS3Buckets(jobId);
        }
        return jobId;
    }

    /**
     * Gets the status of a discovery job.
     *
     * @param jobId the ID of the job.
     * @return the status of the job.
     */
    @GetMapping("/jobId/{jobId}")
    public String getJobResult(@PathVariable String jobId) {
        return awsDiscoveryService.getJobResult(jobId);
    }

    /**
     * Gets the discovery results for a specified service (EC2 or S3).
     *
     * @param service the name of the AWS service.
     * @return a list of resource details for the specified service.
     */
    @GetMapping("/service/{service}")
    public List<String> getDiscoveryResult(@PathVariable String service) {
        if ("EC2".equalsIgnoreCase(service)) {
            return awsDiscoveryService.getEc2Instances();
        } else if ("S3".equalsIgnoreCase(service)) {
            return awsDiscoveryService.getS3Buckets();
        } else {
            throw new IllegalArgumentException("Unsupported service: " + service);
        }
    }

    /**
     * Initiates the discovery of objects in the specified S3 bucket asynchronously.
     *
     * @param bucketName the name of the S3 bucket.
     * @return a job ID tracking this discovery process.
     */
    @PostMapping("/bucket")
    public String getS3BucketObjects(@RequestParam String bucketName) {
        String jobId = UUID.randomUUID().toString();
        awsDiscoveryService.discoverS3BucketObjects(bucketName, jobId);
        return jobId;
    }

    /**
     * Gets the count of objects in the specified S3 bucket.
     *
     * @param bucketName the name of the S3 bucket.
     * @return the number of objects in the specified bucket.
     */
    @GetMapping("/bucket/{bucketName}")
    public long getS3BucketObjectCount(@PathVariable String bucketName) {
        return awsDiscoveryService.getS3BucketObjectCount(bucketName);
    }

    /**
     * Gets a list of object names in the specified S3 bucket that match the given pattern.
     *
     * @param bucketName the name of the S3 bucket.
     * @param pattern the pattern to match the object names.
     * @return a list of matching object names.
     */
    @GetMapping("/getS3BucketObjectLike/{bucketName}/{pattern}")
    public List<String> getS3BucketObjectLike(@PathVariable String bucketName, @PathVariable String pattern) {
        return awsDiscoveryService.getS3BucketObjectLike(bucketName, pattern);
    }
}
