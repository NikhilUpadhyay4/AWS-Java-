package com.awsproject.repository;

import com.awsproject.model.AWSResource;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import java.util.List;


public interface AWSRepository extends MongoRepository<AWSResource, String> {
    /**
     * Finds all resources of the specified type.
     *
     * @param type the type of AWS resource (e.g., "EC2", "S3Object").
     * @return a list of resources of the specified type.
     */
    List<AWSResource> findByType(String type);

    /**
     * Finds all resources of the specified type and details.
     *
     * @param type the type of AWS resource (e.g., "EC2", "S3Object").
     * @param details the details of the resource (e.g., bucket name or instance ID).
     * @return a list of resources matching the type and details.
     */
    List<AWSResource> findByTypeAndDetails(String type, String details);


    /**
     * Finds all resources belonging to the specified bucket name.
     *
     * @param bucketName the name of the S3 bucket.
     * @return a list of resources belonging to the specified bucket name.
     */
    List<AWSResource> findByBucketName(String bucketName);

    /**
     * Finds all S3 objects in the specified bucket that match the given pattern.
     *
     * @param bucketName the name of the S3 bucket.
     * @param pattern the pattern to match the object names.
     * @return a list of matching S3 objects.
     */
    @Query("{ 'type': 'S3Object', 'details': { $regex: '^?0/.*?1.*' } }")
    List<AWSResource> findByBucketNameAndPattern(String bucketName, String pattern);
}
