package com.tekops.system.controller;

import com.amazonaws.services.cloudwatchevents.model.PutRuleResult;
import com.amazonaws.services.cloudwatchevents.model.PutTargetsResult;
import com.amazonaws.services.codepipeline.model.*;
import com.tekops.system.Constants;
import com.tekops.system.model.CodeBuildModel;
import com.tekops.system.model.CodeCommitModel;
import com.tekops.system.model.CodePipelineRequest;
import com.tekops.system.service.CloudWatchEventService;
import com.tekops.system.service.CodeBuildService;
import com.tekops.system.service.CodeCommitService;
import com.tekops.system.service.CodePipelineService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by ravikumar on 18/08/18.
 */
@RestController
public class CodePipelineController {

    private static final Logger logger = LoggerFactory.getLogger(CodePipelineController.class);

    private CodePipelineService codePipelineService;
    private CodeCommitService codeCommitService;
    private CodeBuildService codeBuildService;
    private CloudWatchEventService cloudWatchEventService;

    @Autowired
    public CodePipelineController(
            CodePipelineService codePipelineService,
            CodeCommitService codeCommitService,
            CodeBuildService codeBuildService,
            CloudWatchEventService cloudWatchEventService) {
        this.codePipelineService = codePipelineService;
        this.codeCommitService = codeCommitService;
        this.codeBuildService = codeBuildService;
        this.cloudWatchEventService = cloudWatchEventService;
    }

    @PostMapping("/one-click-setup")
    public CreatePipelineResult createCodeCommitBuildPipeline(@RequestBody(required = false) CodePipelineRequest request){

        final boolean codeCommitResult = codeCommitService.createRepository(new CodeCommitModel(request.getCodeCommitRepoName(), request.getCodeCommitRepoName()));
        if(codeCommitResult) {
            logger.info("Code commit created, name={}",request.getCodeCommitRepoName());

            return createPipeline(request);
        } else {
            logger.error("Failed to create code commit, name={}",request.getCodeCommitRepoName());
        }
        logger.error("Failed to create code pipeline, name={}",request.getPipelineName());
        return null;
    }

    @PostMapping("/create-code-pipeline")
    public CreatePipelineResult createCodePipeline(@RequestBody(required = false) CodePipelineRequest request) {
        return createPipeline(request);
    }

    private CreatePipelineResult createPipeline(CodePipelineRequest request){
        String repoURL= "https://git-codecommit.ap-south-1.amazonaws.com/v1/repos/"+request.getCodeCommitRepoName();
        final boolean codeBuildResult = codeBuildService.createProject(new CodeBuildModel(request.getBuildProjectName(),repoURL));
        if(codeBuildResult) {
            logger.info("Code build created, name={}",request.getBuildProjectName());
            CreatePipelineResult result = codePipelineService.createCodePipeline(request);
            logger.info(result.toString());
            if(result != null && result.getPipeline() != null){
                try {
                    //TODO: need to change the ARN accounts.
                    String repoArn = "arn:aws:codecommit:ap-south-1:" + Constants.ACCOUNT_ID + ":" + request.getCodeCommitRepoName();
                    String pipelineArn = "arn:aws:codepipeline:ap-south-1:" + Constants.ACCOUNT_ID + ":" + request.getPipelineName();
                    String ruleName = "codecommit_rule_" + request.getCodeCommitRepoName();
                    PutRuleResult putRuleResult = cloudWatchEventService.createEventRule(ruleName, repoArn);
                    logger.info("Event rule created, response={}",putRuleResult);
                    PutTargetsResult putTargetsResult =
                            cloudWatchEventService.addTarget(ruleName, Constants.CODE_PIPELINE_SERVICE_ROLE_ARN, pipelineArn);
                    logger.info("Target added to Event rule, response={}", putTargetsResult);
                }catch(Exception e){
                    logger.error("Exception while creating EventRule or Target" + e.getMessage());
                    e.printStackTrace();
                }
            }
            return result;
        } else {
            logger.error("Failed to create code build, name={}",request.getBuildProjectName());
        }
        logger.error("Failed to create code pipeline, name={}",request.getPipelineName());
        return null;
    }
}
