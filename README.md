# AWS Service Integration with Spring Boot

This Spring Boot project demonstrates how to integrate with AWS services (EC2 and S3) using the AWS SDK for Java. The project provides REST APIs to discover and interact with AWS resources asynchronously.

## Features

- **Discover Services**: Asynchronously discover EC2 instances and S3 buckets in the Mumbai region and persist the results in a MongoDB database.
- **Get Job Result**: Get the status of a job based on its ID.
- **Get Discovery Result**: Get the list of EC2 instances or S3 buckets discovered.
- **Get S3 Bucket Objects**: Discover all the file names in a specified S3 bucket and persist them in the database.
- **Get S3 Bucket Object Count**: Get the count of objects in a specified S3 bucket.
- **Get S3 Bucket Object Like**: Get a list of file names in a specified S3 bucket matching a given pattern.

## Pre-requisites

- Java 8 or higher
- Maven
- MongoDB
- AWS Account with access to EC2 and S3 services

## Setup

1. **Clone the repository**:

    ```bash
    git clone https://github.com/your-username/aws-spring-boot-integration.git
    ```

2. **Set AWS Credentials**:

    Set your AWS access key and secret key in the `application.properties` file:

    ```properties
    cloud.aws.credentials.access-key=YOUR_ACCESS_KEY
    cloud.aws.credentials.secret-key=YOUR_SECRET_KEY
    ```

3. **Set MongoDB Configuration**:

    Set your MongoDB connection details in the `application.properties` file:

    ```properties
    spring.data.mongodb.uri=mongodb://localhost:27017/aws-service
    ```

4. **Run the Application**:

    Run the Spring Boot application using Maven:

    ```bash
    mvn spring-boot:run
    ```

## Usage

You can interact with the REST APIs provided by the application to discover and retrieve information about AWS resources.

- **Discover Services**:

    ```http
    POST /api/aws/discoverServices
    ```

    Example request body:

    ```json
    ["EC2", "S3"]
    ```

- **Get Job Result**:

    ```http
    GET /api/aws/jobId/{jobId}
    ```

- **Get Discovery Result**:

    ```http
    GET /api/aws/service/{service}
    ```

    Example: `/api/aws/service/EC2`

- **Get S3 Bucket Objects**:

    ```http
    POST /api/aws/bucket?bucketName={bucketName}
    ```

- **Get S3 Bucket Object Count**:

    ```http
    GET /api/aws/bucket/{bucketName}
    ```

- **Get S3 Bucket Object Like**:

    ```http
    GET /api/aws/file/{bucketName}/{pattern}
    ```

## Contributing

Contributions are welcome! If you find any issues or have suggestions for improvement, feel free to open an issue or submit a pull request.
