package com.tekops.system.controller;

import com.amazonaws.services.codepipeline.model.CreatePipelineResult;
import com.tekops.system.Constants;
import com.tekops.system.model.CodeBuildModel;
import com.tekops.system.model.CodeCommitModel;
import com.tekops.system.model.CodePipelineModel;
import com.tekops.system.model.CommonServerResponseModel;
import com.tekops.system.service.CloudWatchEventService;
import com.tekops.system.service.CodeBuildService;
import com.tekops.system.service.CodeCommitService;
import com.tekops.system.service.CodePipelineService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Objects;

/**
 * Main controller for TekOps.
 * @author rapaul
 */
@RestController
@RequestMapping("/tekops")
@Slf4j
public class TekOpsController {

    @Autowired
    private CodeCommitService codeCommitService;

    @Autowired
    private CodeBuildService codeBuildService;

    @Autowired
    private CodePipelineService codePipelineService;

    @Autowired
    private CloudWatchEventService cloudWatchEventService;

    @ApiOperation("Creates an AWS CodeCommit repository")
    @ApiResponses(
            value = {
                    @ApiResponse(code = 201, message = "When the repository is created"),
                    @ApiResponse(code = 500, message = "When repository creation fails")
            }
    )
    @PostMapping(value = "/code-commit/create",
            produces = MediaType.APPLICATION_JSON_VALUE,
            consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<CommonServerResponseModel> createRepository(
            @RequestBody CodeCommitModel codeCommitModel) {
        final boolean result = codeCommitService.createRepository(codeCommitModel);
        final CommonServerResponseModel serverResponseModel = new CommonServerResponseModel();
        if (result) {
            log.info("Created the CodeCommit repository with the following input -> " + codeCommitModel);
            serverResponseModel.setMessage("The CodeCommit repository has been successfully created.");
            serverResponseModel.setCode(HttpStatus.CREATED.value());
        } else {
            log.info("There was a problem while creating the CodeCommit repository with the following input -> "
                    + codeCommitModel);
            serverResponseModel.setMessage("An error occurred while creating the CodeCommit repository.");
            serverResponseModel.setCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
        }
        return new ResponseEntity<>(serverResponseModel, HttpStatus.valueOf(serverResponseModel.getCode()));
    }

    @ApiOperation("List AWS CodeCommit repositories available in the account")
    @ApiResponses(
            value = {
                    @ApiResponse(code = 200, message = "When retrieval is successful"),
                    @ApiResponse(code = 500, message = "When retrieval fails")
            }
    )
    @GetMapping(value = "/code-commit/list",
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<CodeCommitModel>> listRepositories() {
        final List<CodeCommitModel> codeCommitModels = codeCommitService.getRepositoryList();
        return new ResponseEntity<>(codeCommitModels, HttpStatus.OK);
    }

    @ApiOperation("Creates an AWS CodePipeline")
    @ApiResponses(
            value = {
                    @ApiResponse(code = 201, message = "When CodePipeline creation is successful"),
                    @ApiResponse(code = 500, message = "When CodePipeline creation fails")
            }
    )
    @PostMapping(value = "/code-pipeline/create",
            produces = MediaType.APPLICATION_JSON_VALUE,
            consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<CommonServerResponseModel> createCodePipeline(
            @RequestBody(required = false) CodePipelineModel request) {
        final String codeCommitRepoURL = new StringBuilder().append(Constants.CODE_COMMIT_LOCATION)
                .append(request.getCodeCommitRepoName()).toString();
        final CommonServerResponseModel serverResponseModel = new CommonServerResponseModel();

        // Create the CodeBuild project
        final boolean codeBuildResult = codeBuildService.createProject(
                new CodeBuildModel(request.getBuildProjectName(),codeCommitRepoURL));
        if (codeBuildResult) {

            // Create the CodePipeline project
            final CreatePipelineResult createPipelineResult = codePipelineService.createCodePipeline(request);
            if (Objects.nonNull(createPipelineResult) && Objects.nonNull(createPipelineResult.getPipeline())) {
                try {

                    // Create CloudWatch events rule for triggering CodePipeline when the source code change happens
                    final String repoArn = new StringBuilder().append(Constants.CODECOMMIT_ARN_PREFIX)
                            .append(Constants.ACCOUNT_ID).append(Constants.COLON)
                            .append(request.getCodeCommitRepoName()).toString();
                    final String pipelineArn = new StringBuilder().append(Constants.CODEPIPELINE_ARN_PREFIX)
                            .append(Constants.ACCOUNT_ID).append(Constants.COLON)
                            .append(request.getPipelineName()).toString();

                    final String ruleNameForCodePipelineTriggering =
                            new StringBuilder().append(Constants.TRIGGER_CODEPIPELINE_RULE_NAME_PREFIX)
                                    .append(request.getCodeCommitRepoName()).toString();
                    if (cloudWatchEventService.createEventRuleForCodePipelineTriggering(
                            ruleNameForCodePipelineTriggering, repoArn)) {
                        cloudWatchEventService.addTargetForCodePipelineTriggering(ruleNameForCodePipelineTriggering,
                                Constants.CODE_PIPELINE_TRIGGERING_SERVICE_ROLE_ARN, pipelineArn);
                    }

                    // Create CloudWatch events rule for SNS notification when the CodePipeline state changes
                    final String ruleNameForSnsNotification = new StringBuilder().append(Constants.SNS_RULE_NAME_PREFIX)
                            .append(request.getPipelineName()).toString();
                    if (cloudWatchEventService.createEventRuleForSnsNotification(
                            ruleNameForSnsNotification, request.getPipelineName())) {

                        cloudWatchEventService.addTargetForSnsNotification(ruleNameForSnsNotification,
                                Constants.SNS_TOPIC_ARN);
                    }

                    log.info("Created the CodePipeline with the following input -> " + request);
                    serverResponseModel.setMessage("The CodePipeline has been successfully created.");
                    serverResponseModel.setCode(HttpStatus.CREATED.value());

                } catch(Exception e){
                    log.error("Exception while creating CloudWatch event or rule. rror details -> "
                            + ExceptionUtils.getStackTrace(e));
                    e.printStackTrace();
                }
            }
        } else {
            log.info("There was a problem while creating the CodePipeline with the following input -> "
                    + request);
            serverResponseModel.setMessage("An error occurred while creating the CodePipeline.");
            serverResponseModel.setCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
        }
        return new ResponseEntity<>(serverResponseModel, HttpStatus.valueOf(serverResponseModel.getCode()));
    }
}
