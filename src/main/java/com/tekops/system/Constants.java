package com.tekops.system;

/**
 * @author rapaul
 */
public class Constants {
  public static final String CODE_COMMIT_LOCATION = "https://git-codecommit.us-east-1.amazonaws.com/v1/repos/";
  public static final String BUILD_SPEC_FILE_NAME = "buildspec.yml";
  public static final String CODE_BUILD_IMAGE_NAME = "aws/codebuild/java:openjdk-8";
  public static final String CODECOMMIT_ARN_PREFIX =  "arn:aws:codecommit:us-east-1:";
  public static final String CODEPIPELINE_ARN_PREFIX = "arn:aws:codepipeline:us-east-1:";
  public static final String COLON = ":";
  public static final String TRIGGER_CODEPIPELINE_RULE_NAME_PREFIX = "TriggerCodePipeline_rule_";
  public static final String SNS_RULE_NAME_PREFIX = "SnsNotification_rule_";
  public static final String CODECOMMIT_ARTIFACT = "MyApp";
  public static final String CODEBUILD_ARTIFACT = "MyAppBuild";
  public static final String ELASTICBEANSTALK_APP_SUFFIX = "-ebApp";
  public static final String ELASTICBEANSTALK_ENV_SUFFIX = "-ebEnv";
  public static final String AWS = "AWS";
  public static final String SOURCE = "Source";
  public static final String BUILD = "Build";
  public static final String DEPLOY = "Deploy";
  public static final String CODECOMMIT = "CodeCommit";
  public static final String CODEBUILD = "CodeBuild";
  public static final String ELASTICBEANSTALK = "ElasticBeanstalk";
  public static final String STAGING = "Staging";
  public static final String VERSION = "1";
  public static final String S3_BUCKET_NAME_FOR_CODE_PIPELINE = "tekops-codepipeline-artifacts";
  public static final String S3_BUCKET_NAME_FOR_DEMO_APP = "tekops-demo-app-artifacts";
  public static final String WATCHED_CODE_COMMIT_BRANCH = "master";
  public static final String WAR_DEFAULT_PATH = "demoapp.war";
  public static final String APP_VERSION_LEVEL = "EV-App-v1";
  public static final String EB_SOLUTION_STACK = "64bit Amazon Linux 2018.03 v3.0.4 running Tomcat 8.5 Java 8";
  public static final String EB_ENV_TIER_NAME = "WebServer";
  public static final String EB_ENV_TIER_TYPE = "Standard";

  public static final String ACCOUNT_ID = "243383093692";
  public static final String SNS_TOPIC_ARN = "arn:aws:sns:us-east-1:243383093692:TekOpsNotification";
  public static final String CODE_BUILD_SERVICE_ROLE_ARN =
          "arn:aws:iam::243383093692:role/TekOpsCodeBuildServiceRole";
  public static final String CODE_PIPELINE_TRIGGERING_SERVICE_ROLE_ARN =
          "arn:aws:iam::243383093692:role/TekOpsCloudWatchEventsTriggerCodePipelineServiceRole";
  public static final String CODE_PIPELINE_SERVICE_ROLE =
          "arn:aws:iam::243383093692:role/TekOpsCodePipelineServiceRole";

  private Constants(){}
}
