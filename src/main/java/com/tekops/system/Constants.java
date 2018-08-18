package com.tekops.system;

/**
 * @author rapaul
 */
public class Constants {
  public static final String S3_BUCKET_NAME_PREFIX = "tekops-";
  public static final String BUILD_SPEC_FILE_NAME = "buildspec.yml";
  public static final String CODE_BUILD_IMAGE_NAME = "aws/codebuild/java:openjdk-8";
  public static final String SEPERATOR_CHAR = "_";
//  public static final String CODE_BUILD_SERVICE_ROLE_ARN = "arn:aws:iam::880423837149:role/CodeBuildAccessPolicyRole";
  public static final String CODE_PIPELINE_SERVICE_ROLE_ARN = "arn:aws:iam::299024323439:role/service-role/AWS_Events_Invoke_CodePipeline_1573795732";
  public static final String CODE_BUILD_SERVICE_ROLE_ARN = "arn:aws:iam::299024323439:role/CodeBuildServiceRole-latest";
  public static final String S3_BUCKET_PATH = "MyArtifacts";
  public static final String CODE_PIPELINE_SERVICE_ROLE = "arn:aws:iam::299024323439:role/CodePipelineAccessPolicyRole";
//  public static final String EB_STACK_NAME = "64bit Amazon Linux 2018.03 v3.0.2 running Tomcat 8.5 Java 8";
  public static final String S3_BUCKET_NAME = "rpaul-test-bucket-1";
  public static final String WATCHED_CODE_COMMIT_BRANCH = "master";
  public static final String ACCOUNT_ID = "299024323439";
  private Constants(){}
}
