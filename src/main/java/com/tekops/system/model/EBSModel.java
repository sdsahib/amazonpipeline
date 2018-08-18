package com.tekops.system.model;

//@AllArgsConstructor
//@NoArgsConstructor
//@Data
public class EBSModel {

        private String bucket;
        private String fileObjKeyName;
        private String filePath;
        private String s3Bucket;
        private String s3Key;
        private String applicationName;
        private String description ;
        private String versionLabel;
        private String securityGroupName;
        private String securityGroupDescription;
        private String configurationOptionNameSpace ;
        private String configurationOptionName;
        private String configurationValue;
        private String environmentName;
        private String solutionStackName;


    public String getBucket() {
        return bucket;
    }

    public void setBucket(String bucket) {
        this.bucket = bucket;
    }

    public String getFileObjKeyName() {
        return fileObjKeyName;
    }

    public void setFileObjKeyName(String fileObjKeyName) {
        this.fileObjKeyName = fileObjKeyName;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public String getS3Bucket() {
        return s3Bucket;
    }

    public void setS3Bucket(String s3Bucket) {
        this.s3Bucket = s3Bucket;
    }

    public String getS3Key() {
        return s3Key;
    }

    public void setS3Key(String s3Key) {
        this.s3Key = s3Key;
    }

    public String getApplicationName() {
        return applicationName;
    }

    public void setApplicationName(String applicationName) {
        this.applicationName = applicationName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getVersionLabel() {
        return versionLabel;
    }

    public void setVersionLabel(String versionLabel) {
        this.versionLabel = versionLabel;
    }

    public String getSecurityGroupName() {
        return securityGroupName;
    }

    public void setSecurityGroupName(String securityGroupName) {
        this.securityGroupName = securityGroupName;
    }

    public String getSecurityGroupDescription() {
        return securityGroupDescription;
    }

    public void setSecurityGroupDescription(String securityGroupDescription) {
        this.securityGroupDescription = securityGroupDescription;
    }

    public String getConfigurationOptionNameSpace() {
        return configurationOptionNameSpace;
    }

    public void setConfigurationOptionNameSpace(String configurationOptionNameSpace) {
        this.configurationOptionNameSpace = configurationOptionNameSpace;
    }

    public String getConfigurationOptionName() {
        return configurationOptionName;
    }

    public void setConfigurationOptionName(String configurationOptionName) {
        this.configurationOptionName = configurationOptionName;
    }

    public String getConfigurationValue() {
        return configurationValue;
    }

    public void setConfigurationValue(String configurationValue) {
        this.configurationValue = configurationValue;
    }

    public String getEnvironmentName() {
        return environmentName;
    }

    public void setEnvironmentName(String environmentName) {
        this.environmentName = environmentName;
    }

    public String getSolutionStackName() {
        return solutionStackName;
    }

    public void setSolutionStackName(String solutionStackName) {
        this.solutionStackName = solutionStackName;
    }
}
