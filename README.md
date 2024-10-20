
---

# AWS S3 and Lambda File Service

This repository contains two Spring Boot projects:
1. **File Storage Service**: A Spring Boot application to upload and retrieve files from AWS S3.
2. **Lambda Trigger Service**: A Spring Boot application that triggers an AWS Lambda function when a file is uploaded to S3.

## Project 1: File Storage Service

### Overview
The **File Storage Service** allows you to upload, retrieve, and manage files stored in an AWS S3 bucket.

### Features
- Upload files to AWS S3.
- Retrieve files from AWS S3 by filename.
- Delete files from S3 bucket.

### Technologies
- Spring Boot
- AWS S3 SDK
- MySQL Database (for storing file metadata)

### Configuration

Make sure the following properties are configured in your `application.yml`:
```yaml
aws:
  s3:
    bucket-name: your-bucket-name
    access-key: your-access-key
    secret-key: your-secret-key
    region: your-region
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/your-database
    username: your-username
    password: your-password
```

### Running the Application
1. Install dependencies:
```bash
mvn clean install
```
2. Start the application:
```bash
./mvnw spring-boot:run
```

### API Endpoints

- **Upload File**: `POST /files/upload`
- **Retrieve File**: `GET /files/{filename}`
- **Delete File**: `DELETE /files/{filename}`

---

## Project 2: Lambda Trigger Service

### Overview
The **Lambda Trigger Service** listens for file uploads in the AWS S3 bucket and triggers a specified AWS Lambda function to process these files.

### Features
- Monitors AWS S3 bucket for uploaded files.
- Triggers AWS Lambda function upon file upload.

### Technologies
- Spring Boot
- AWS S3 SDK
- AWS Lambda

### Configuration

Ensure the following properties are set in the `application.yml`:
```yaml
aws:
  s3:
    bucket-name: your-bucket-name
  lambda:
    function-name: your-lambda-function-name
    region: your-region
```

### Lambda Configuration
- Ensure the AWS Lambda function is correctly set up with necessary S3 event triggers and permissions.
- Update the Lambda's IAM role with permissions to access the S3 bucket.

### Running the Application
1. Install dependencies:
```bash
mvn clean install
```
2. Start the application:
```bash
./mvnw spring-boot:run
```

---

## Setup Instructions

### Prerequisites:
- Java 11+
- Maven
- AWS Account with S3 and Lambda services enabled

### Steps:
1. Clone the repository:
```bash
git clone https://github.com/your-username/aws-s3-lambda-file-service.git
```

2. Navigate into each project directory (File Storage Service and Lambda Trigger Service).
3. Update `application.yml` with your AWS credentials, S3 bucket name, Lambda function name, and other configuration settings.
4. Run each project using Maven as instructed above.

---

### Thank You.
