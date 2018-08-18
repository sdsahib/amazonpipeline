package com.tekops.system.service;

import com.amazonaws.services.ec2.AmazonEC2;
import com.amazonaws.services.ec2.AmazonEC2ClientBuilder;
import com.amazonaws.services.ec2.model.Instance;
import com.amazonaws.services.ec2.model.Reservation;
import com.amazonaws.services.ec2.model.Vpc;
import com.amazonaws.services.identitymanagement.AmazonIdentityManagement;
import com.amazonaws.services.identitymanagement.AmazonIdentityManagementClientBuilder;
import com.amazonaws.services.identitymanagement.model.User;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.Bucket;
import com.tekops.system.model.BucketModel;
import com.tekops.system.model.Ec2Model;
import com.tekops.system.model.UserModel;
import com.tekops.system.model.VpcModel;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * @author rapaul
 */
@Service
public class CommonItemsRetrieverService {

  public VpcModel fetchVpc() {
    final AmazonEC2 ec2 = AmazonEC2ClientBuilder.defaultClient();
    Vpc vpc = ec2.describeVpcs().getVpcs().get(0);
    return new VpcModel(vpc.getVpcId(), vpc.getIsDefault());
  }

  public List<BucketModel> fetchS3Buckets() {
    final AmazonS3 s3 = AmazonS3ClientBuilder.defaultClient();
    final List<Bucket> buckets = s3.listBuckets();
    final List<BucketModel> bucketModels = new ArrayList<>();
    for (Bucket bucket : buckets ) {
      if (!bucket.getName().equals("appsync-lambda-rpaul")) {
        bucketModels.add(new BucketModel(bucket.getName()));
      }
    }
    return bucketModels;
  }

  public UserModel fetchIAMUser() {
    final AmazonIdentityManagement iam = AmazonIdentityManagementClientBuilder.defaultClient();
    User user =  iam.listUsers().getUsers().get(0);
    return new UserModel(user.getUserId(), user.getUserName(), user.getArn());
  }

  public List<Ec2Model> fetchEC2Instances() {
    final AmazonEC2 ec2 = AmazonEC2ClientBuilder.defaultClient();
    final List<Reservation> reservations = ec2.describeInstances().getReservations();
    final List<Ec2Model> ec2Models = new ArrayList<>();
    for (Reservation reservation : reservations) {
      final Instance instance = reservation.getInstances().get(0);
      ec2Models.add(new Ec2Model(
              instance.getInstanceId(),
              instance.getSubnetId(), instance.getInstanceType(), instance.getPublicIpAddress()));
    }
    return new ArrayList<>();
  }
}
