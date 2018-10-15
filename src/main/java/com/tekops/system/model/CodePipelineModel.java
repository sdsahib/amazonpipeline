package com.tekops.system.model;

import lombok.Data;

/**
 * Created by ravikumar on 18/08/18.
 */
@Data
public class CodePipelineModel {
    private String pipelineName;
    private String codeCommitRepoName;
    private String buildProjectName;
}
