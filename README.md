
### README.md

```md
# AWS S3 and Lambda File Service

This repository contains two Spring Boot projects:
1. **File Storage Service**: A Spring Boot application to upload and retrieve files from AWS S3.
2. **Lambda Trigger Service**: A Spring Boot application that triggers an AWS Lambda function when a file is uploaded to S3.

## Project 1: File Storage Service

### Overview
This project allows you to upload files to AWS S3 and retrieve them. The files are stored in a designated S3 bucket.

### Features
- Upload files to AWS S3.
- Retrieve files from AWS S3.
- Delete files from S3 bucket.

### Technologies
- Spring Boot
- AWS S3 SDK
- MySQL Database (for file metadata)

### Configuration

Ensure the following properties are set in the `application.yml` file:
```yaml
aws:
  s3:
    bucket-name: your-bucket-name
    access-key: your-access-key
    secret-key: your-secret-key
    region: your-region
```

### Run the Application
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
This project listens for file uploads to an S3 bucket and triggers an AWS Lambda function for further processing.

### Features
- Monitors AWS S3 bucket for file uploads.
- Triggers a Lambda function upon file upload.

### Technologies
- Spring Boot
- AWS Lambda
- AWS S3 SDK

### Configuration

Ensure the following properties are set in the `application.yml` file:
```yaml
aws:
  s3:
    bucket-name: your-bucket-name
  lambda:
    function-name: your-lambda-function-name
    region: your-region
```

### Lambda Configuration
Ensure that the Lambda function has the required permissions and is set up to handle S3 events.

### Run the Application
```bash
./mvnw spring-boot:run
```

---

## How to Setup

1. Clone the repository:
```bash
git clone https://github.com/your-username/aws-s3-lambda-file-service.git
```

2. Navigate into each project directory and update the `application.yml` with your AWS credentials and configurations.

3. Run each project using Maven.

## Prerequisites

- Java 11+
- Maven
- AWS account with S3 and Lambda services configured.

