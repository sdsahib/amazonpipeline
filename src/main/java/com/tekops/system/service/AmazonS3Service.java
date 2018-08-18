package com.tekops.system.service;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.SdkClientException;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import org.springframework.stereotype.Service;

import java.io.File;

@Service
public class AmazonS3Service {

    public void UploadObject(){

            String clientRegion = "ap-south-1";
            String bucketName = "codebuild-ap-south-1-312703586752-input-bucket";
            String fileObjKeyName = "demo-0.1.war";
            String filePath = "D:\\TEKOps_ashwin\\demo-0.1.war";

            try {
                AmazonS3 s3Client = AmazonS3ClientBuilder.defaultClient();

                // Upload a file as a new object with ContentType and title specified.
                PutObjectRequest request = new PutObjectRequest(bucketName, fileObjKeyName, new File(filePath));
                ObjectMetadata metadata = new ObjectMetadata();
                metadata.setContentType("application/war");
                metadata.addUserMetadata("x-amz-meta-title", "someTitle");
                request.setMetadata(metadata);
                request.setKey("demo-0.1.war");
                s3Client.putObject(request);
            }
            catch(AmazonServiceException e) {
                // The call was transmitted successfully, but Amazon S3 couldn't process
                // it, so it returned an error response.
                e.printStackTrace();
            }
            catch(SdkClientException e) {
                // Amazon S3 couldn't be contacted for a response, or the client
                // couldn't parse the response from Amazon S3.
                e.printStackTrace();
            }
        }

}
