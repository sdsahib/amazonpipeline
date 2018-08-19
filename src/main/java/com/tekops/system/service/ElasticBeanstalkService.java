package com.tekops.system.service;


import com.amazonaws.services.elasticbeanstalk.AWSElasticBeanstalk;
import com.amazonaws.services.elasticbeanstalk.AWSElasticBeanstalkClientBuilder;
import com.amazonaws.services.elasticbeanstalk.model.*;

import com.tekops.system.Constants;
import com.tekops.system.model.EBSModel;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

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
//            DescribeEnvironmentsRequest describeEnvironmentsRequest1 = new DescribeEnvironmentsRequest();
//            describeEnvironmentsRequest1.setApplicationName(ebsModel.getApplicationName());
//            describeEnvironmentsRequest1.setEnvironmentNames(Collections.singleton(ebsModel.getEnvironmentName()));
//            describeEnvironmentsRequest1.setVersionLabel(ebsModel.getVersionLabel());
//            DescribeEnvironmentsResult describeEnvironmentsResult = awsElasticBeanstalkClient.describeEnvironments(describeEnvironmentsRequest1);


//            if(describeEnvironmentsResult.getEnvironments().size()!=0) {
//                System.out.println("deleting ");
//
//                TerminateEnvironmentRequest terminateEnvironmentRequest = new TerminateEnvironmentRequest();
//                terminateEnvironmentRequest.setEnvironmentName(ebsModel.getEnvironmentName());
//                terminateEnvironmentRequest.setForceTerminate(true);
//                awsElasticBeanstalkClient.terminateEnvironment(terminateEnvironmentRequest);
//
//            }
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

//            awsElasticBeanstalkClient.createApplicationVersion(new CreateApplicationVersionRequest().withApplicationName(ebsModel.getApplicationName())
//                    .withDescription(ebsModel.getDescription())
//                    .withSourceBundle(s3Location).withVersionLabel(ebsModel.getVersionLabel()));


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

    public ApplicationDescription createApplication(String applicationName, String applicationDescription) {
        final AWSElasticBeanstalk awsElasticBeanstalkClient = AWSElasticBeanstalkClientBuilder.defaultClient();
        DescribeApplicationsResult describeApplicationsResult = awsElasticBeanstalkClient.describeApplications();
        List<ApplicationDescription> applicationDescriptionList= describeApplicationsResult.getApplications();

        ApplicationDescription resultModel = null;
        for(ApplicationDescription applicationDescriptionModel : applicationDescriptionList){
            if(applicationDescriptionModel.getApplicationName().equals(applicationName)){
                resultModel = applicationDescriptionModel;
            }
        }
        if (Objects.isNull(resultModel)) {
            final CreateApplicationRequest createApplicationRequest = new CreateApplicationRequest();
            createApplicationRequest.setApplicationName(applicationName);
            createApplicationRequest.setDescription(applicationDescription);
            CreateApplicationResult applicationResult = awsElasticBeanstalkClient.createApplication(createApplicationRequest);
            resultModel = applicationResult.getApplication();
        }
        return resultModel;
    }

    public String createEnvironment(String applicationName, String applicationDescription) {
        final AWSElasticBeanstalk awsElasticBeanstalkClient = AWSElasticBeanstalkClientBuilder.defaultClient();
        ConfigurationOptionSetting configurationOptionSetting = new ConfigurationOptionSetting();
        configurationOptionSetting.setNamespace("aws:autoscaling:launchconfiguration");
        configurationOptionSetting.setOptionName("IamInstanceProfile");
        configurationOptionSetting.setValue("aws-elasticbeanstalk-ec2-role");

        String envName = "my-env" + System.currentTimeMillis();
        String versionLabel = getVersionNumberToBeUsed(applicationName, applicationDescription);

        createApplicationVersion(applicationName, applicationDescription, versionLabel);

//          creating environment
        awsElasticBeanstalkClient.createEnvironment(new CreateEnvironmentRequest().withApplicationName(applicationName)
                .withEnvironmentName(envName)
                .withVersionLabel(versionLabel)
                .withSolutionStackName("64bit Amazon Linux 2018.03 v3.0.2 running Tomcat 8.5 Java 8")
                .withOptionSettings(configurationOptionSetting));
//
//          Attaching the Elastic environment with the Instance
        final DescribeEnvironmentsRequest describeEnvironmentsRequest = new DescribeEnvironmentsRequest();
        describeEnvironmentsRequest.setApplicationName(applicationName);
        describeEnvironmentsRequest.setEnvironmentNames(Collections.singleton(envName));
//
        awsElasticBeanstalkClient.describeEnvironments(describeEnvironmentsRequest);
        return envName;
    }

    private String getVersionNumberToBeUsed(String applicationName, String applicationDescription) {
        final AWSElasticBeanstalk awsElasticBeanstalkClient = AWSElasticBeanstalkClientBuilder.defaultClient();

        //creating object for fetching the Application of that type.
        ApplicationDescription applicationDescriptionModel = new ApplicationDescription();
        applicationDescriptionModel.setApplicationName(applicationName);
        applicationDescriptionModel.setDescription(applicationDescription);

        DescribeApplicationsResult describeApplicationsResult1
                = awsElasticBeanstalkClient.describeApplications().withApplications(applicationDescriptionModel);
        List<ApplicationDescription> applicationDescriptionList1 = describeApplicationsResult1.getApplications();

        int highest =0;
        for(ApplicationDescription temp: applicationDescriptionList1){
            if (temp.getApplicationName().equals(applicationName)) {
                List<String> appVersions = temp.getVersions();
                if (appVersions != null) {
                    highest = appVersions.size() + 1;
                }
                break;
            }
        }

        return "v" + highest;
    }

    private void createApplicationVersion(String applicationName, String applicationDescription, String versionLevel) {
        final S3Location s3Location = new S3Location().withS3Bucket(Constants.S3_BUCKET_NAME)
                .withS3Key(Constants.WAR_DEFAULT_PATH);
        final AWSElasticBeanstalk awsElasticBeanstalkClient = AWSElasticBeanstalkClientBuilder.defaultClient();
        awsElasticBeanstalkClient.createApplicationVersion(new CreateApplicationVersionRequest().withApplicationName(applicationName)
        .withDescription(applicationDescription)
        .withSourceBundle(s3Location).withVersionLabel(versionLevel));
    }
}
