package com.tekops.system.service;


import com.amazonaws.services.elasticbeanstalk.AWSElasticBeanstalk;
import com.amazonaws.services.elasticbeanstalk.AWSElasticBeanstalkClientBuilder;
import com.amazonaws.services.elasticbeanstalk.model.*;

import com.tekops.system.Constants;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * Handles Elastic Beanstalk related AWS service calls.
 * @author rapaul
 */
@Service
@Slf4j
public class ElasticBeanstalkService {

    public String createApplicationNew(final String appName) {
        final AWSElasticBeanstalk beanstalkClient = AWSElasticBeanstalkClientBuilder.defaultClient();
        final CreateApplicationResult createApplicationResult
                = beanstalkClient.createApplication(new CreateApplicationRequest().withApplicationName(appName));
        log.info("Created EB Application. Here is the response from AWS -> " + createApplicationResult);
        return createApplicationResult.getApplication().getApplicationName();
    }

    public String createEnvironmentNew(final String appName, final String envName) {
        final AWSElasticBeanstalk beanstalkClient = AWSElasticBeanstalkClientBuilder.defaultClient();
        final CreateApplicationVersionResult createApplicationVersionResult
                = beanstalkClient.createApplicationVersion(new CreateApplicationVersionRequest()
                .withApplicationName(appName)
                .withAutoCreateApplication(false)
                .withSourceBundle(new S3Location(Constants.S3_BUCKET_NAME_FOR_DEMO_APP, Constants.WAR_DEFAULT_PATH))
                .withVersionLabel(Constants.APP_VERSION_LEVEL));
        log.info("Created EB ApplicationVersion. Here is the response from AWS -> " + createApplicationVersionResult);

        final CreateEnvironmentResult createEnvironmentResult = beanstalkClient.createEnvironment(
                new CreateEnvironmentRequest().withApplicationName(appName)
                .withEnvironmentName(envName)
                .withVersionLabel(Constants.APP_VERSION_LEVEL)
                .withOptionSettings(setEnvironmentProperties())
                .withSolutionStackName(Constants.EB_SOLUTION_STACK)
                .withTier(new EnvironmentTier().withName(Constants.EB_ENV_TIER_NAME)
                        .withType(Constants.EB_ENV_TIER_TYPE)));

        log.info("Created EB ApplicationEnvironment. Here is the response from AWS -> " + createEnvironmentResult);
        return createEnvironmentResult.getEnvironmentName();
    }

    private List<ConfigurationOptionSetting> setEnvironmentProperties() {
        final List<ConfigurationOptionSetting> configurationOptionSettings = new ArrayList<ConfigurationOptionSetting>();
        configurationOptionSettings.add(new ConfigurationOptionSetting(
                "aws:autoscaling:launchconfiguration", "InstanceType", "t2.micro"));
        configurationOptionSettings.add(new ConfigurationOptionSetting(
                "aws:autoscaling:launchconfiguration", "ImageId", "ami-a70eabda"));
        configurationOptionSettings.add(new ConfigurationOptionSetting(
                "aws:autoscaling:asg", "MaxSize", "1"));
        configurationOptionSettings.add(new ConfigurationOptionSetting(
                "aws:elasticbeanstalk:environment", "EnvironmentType", "SingleInstance"));
        configurationOptionSettings.add(new ConfigurationOptionSetting(
                "aws:elasticbeanstalk:xray", "XRayEnabled", "false"));
        return configurationOptionSettings;
    }
}
