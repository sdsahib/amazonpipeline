package com.tekops.system.service;


import com.amazonaws.services.elasticbeanstalk.AWSElasticBeanstalk;
import com.amazonaws.services.elasticbeanstalk.AWSElasticBeanstalkClientBuilder;
import com.amazonaws.services.elasticbeanstalk.model.*;

import com.tekops.system.model.EBSModel;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

@Service
public class ElasticBeanstalkService {

    @Autowired
    private final AmazonS3Service amazonS3Service;

    @Autowired
    public ElasticBeanstalkService(AmazonS3Service amazonS3Service) {
        this.amazonS3Service = amazonS3Service;
    }

    public void createElasticBeanstalk(final EBSModel ebsModel) {
        try {
            final AWSElasticBeanstalk awsElasticBeanstalkClient = AWSElasticBeanstalkClientBuilder.defaultClient();
            DescribeApplicationsResult describeApplicationsResult = awsElasticBeanstalkClient.describeApplications();
            List<ApplicationDescription> applicationDescriptionList= describeApplicationsResult.getApplications();

            for(ApplicationDescription applicationDescription : applicationDescriptionList){
                if(!applicationDescription.getApplicationName().equals(ebsModel.getApplicationName())){
                    //          Creating the basic Application
                    final CreateApplicationRequest createApplicationRequest = new CreateApplicationRequest();
                    createApplicationRequest.setApplicationName(ebsModel.getApplicationName());
                    createApplicationRequest.setDescription(ebsModel.getDescription());
                    final CreateApplicationResult createApplicationResult = awsElasticBeanstalkClient.createApplication(createApplicationRequest);
                }
            }

            //          uploading to s3
            amazonS3Service.UploadObject();

            // Creating bucket data
            final S3Location s3Location = new S3Location().withS3Bucket(ebsModel.getS3Bucket())
                    .withS3Key(ebsModel.getS3Key());

            //creating object for fetching the Application of that type.
            ApplicationDescription applicationDescription = new ApplicationDescription();
            applicationDescription.setApplicationName(ebsModel.getApplicationName());
            applicationDescription.setDescription(ebsModel.getDescription());


            DescribeApplicationsResult describeApplicationsResult1 = awsElasticBeanstalkClient.describeApplications().withApplications(applicationDescription);

            List<ApplicationDescription> applicationDescriptionList1 = describeApplicationsResult1.getApplications();



            int highest =0;
            for(ApplicationDescription temp: applicationDescriptionList1){
                for(String version :temp.getVersions()) {
                    System.out.println(temp.getApplicationName());
                    System.out.println(version);

                    String [] versionString = version.split("v");
                    Integer versionInt = Integer.parseInt(String.valueOf(versionString[1]));
                    if(versionInt>highest)
                        highest = versionInt;
                }
            }



            ebsModel.setVersionLabel("v" + highest);
            System.out.println("current Version " + ebsModel.getVersionLabel());
            // checking whether the application environment is there
            // if there then will delete the repo
            // else normal flow
            DescribeEnvironmentsRequest describeEnvironmentsRequest1 = new DescribeEnvironmentsRequest();
            describeEnvironmentsRequest1.setApplicationName(ebsModel.getApplicationName());
            describeEnvironmentsRequest1.setEnvironmentNames(Collections.singleton(ebsModel.getEnvironmentName()));
            describeEnvironmentsRequest1.setVersionLabel(ebsModel.getVersionLabel());
            DescribeEnvironmentsResult describeEnvironmentsResult = awsElasticBeanstalkClient.describeEnvironments(describeEnvironmentsRequest1);


            if(describeEnvironmentsResult.getEnvironments().size()!=0) {
                System.out.println("deleting ");

                TerminateEnvironmentRequest terminateEnvironmentRequest = new TerminateEnvironmentRequest();
                terminateEnvironmentRequest.setEnvironmentName(ebsModel.getEnvironmentName());
                terminateEnvironmentRequest.setForceTerminate(true);
                awsElasticBeanstalkClient.terminateEnvironment(terminateEnvironmentRequest);

            }
            String environmentName = ebsModel.getEnvironmentName();
            String[] environmentNameArray = environmentName.split("my-env");
            Integer a ;
            String newenvironmentName;
            if(environmentNameArray.length==0) {
                a = 0;
                newenvironmentName = environmentName;
            }
            else{
                 a = Integer.parseInt( environmentNameArray[1]);
                newenvironmentName = environmentNameArray[0] + a++;

            }

            ebsModel.setVersionLabel("v" + (++highest));

            awsElasticBeanstalkClient.createApplicationVersion(new CreateApplicationVersionRequest().withApplicationName(ebsModel.getApplicationName())
                    .withDescription(ebsModel.getDescription())
                    .withSourceBundle(s3Location).withVersionLabel(ebsModel.getVersionLabel()));


//          configuring the Environment
             ConfigurationOptionSetting configurationOptionSetting = new ConfigurationOptionSetting();
            configurationOptionSetting.setNamespace(ebsModel.getConfigurationOptionNameSpace());
            configurationOptionSetting.setOptionName(ebsModel.getConfigurationOptionName());
            configurationOptionSetting.setValue(ebsModel.getConfigurationValue());

//          creating environment
            awsElasticBeanstalkClient.createEnvironment(new CreateEnvironmentRequest().withApplicationName(ebsModel.getApplicationName())
                    .withEnvironmentName(newenvironmentName).withVersionLabel(ebsModel.getVersionLabel())
                    .withSolutionStackName(ebsModel.getSolutionStackName())
                    .withOptionSettings(configurationOptionSetting));
//
//          Attaching the Elastic environment with the Instance
            final DescribeEnvironmentsRequest describeEnvironmentsRequest = new DescribeEnvironmentsRequest();
            describeEnvironmentsRequest.setApplicationName(ebsModel.getApplicationName());
            describeEnvironmentsRequest.setEnvironmentNames(Collections.singleton(ebsModel.getEnvironmentName()));
//
            final DescribeEnvironmentsResult describeEnvironmentsResult1 = awsElasticBeanstalkClient.describeEnvironments(describeEnvironmentsRequest);
//            System.out.println(describeEnvironmentsResult.getEnvironments().toString());


        } catch (Exception e) {
            ExceptionUtils.printRootCauseStackTrace(e);
//            log.error("An error occurred while creating the CodeBuild project. Error details -> "
//                    + ExceptionUtils.getStackTrace(e));
        }

    }
}
