package com.tekops.system.model;

import lombok.Data;

/**
 * Created by ravikumar on 18/08/18.
 */
@Data
public class CodePipelineRequest {

    private String pipelineName;
    private String codeCommitBranchName;
    private String codeCommitRepoName;
    private String codeCommitOutputAppName;
    private String buildProjectName;
    private String buildInputAppName;
    private String buildOutputAppName;
    private String beanstalkAppName;
    private String beanstalkAppEnv;
    private String stagingInputAppName;
    private String codePipelineS3Location;
    private String codePipelineRoleARN;
}
