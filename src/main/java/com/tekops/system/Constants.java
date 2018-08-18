package com.tekops.system;

/**
 * @author rapaul
 */
public class Constants {
  public static final String S3_BUCKET_NAME_PREFIX = "tekops-";
  public static final String BUILD_SPEC_FILE_NAME = "buildspec.yml";
  public static final String CODE_BUILD_IMAGE_NAME = "aws/codebuild/java:openjdk-8";
  public static final String SEPERATOR_CHAR = "_";
  public static final String CODE_BUILD_SERVICE_ROLE_ARN = "arn:aws:iam::880423837149:role/CodeBuildAccessPolicyRole";
  public static final String CODE_PIPELINE_SERVICE_ROLE_ARN = "arn:aws:iam::880423837149:role/service-role/AWS_Events_Invoke_CodePipeline_709251510";
  private Constants(){}
}
