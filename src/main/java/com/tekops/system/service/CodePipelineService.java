package com.tekops.system.service;

import com.amazonaws.services.codepipeline.AWSCodePipeline;
import com.amazonaws.services.codepipeline.AWSCodePipelineClientBuilder;
import com.amazonaws.services.codepipeline.model.*;
import com.tekops.system.Constants;
import com.tekops.system.model.CodePipelineModel;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * Handles CodePipeline related AWS service calls.
 * Created by ravikumar on 18/08/18.
 * Updated by rapaul.
 */
@Service
@Slf4j
public class CodePipelineService {

    @Autowired
    private ElasticBeanstalkService elasticBeanstalkService;

    public CreatePipelineResult createCodePipeline(final CodePipelineModel request) {
        try {
            final ArtifactStore artifactStore = new ArtifactStore();
            artifactStore.setLocation(Constants.S3_BUCKET_NAME_FOR_CODE_PIPELINE);
            artifactStore.setType(ArtifactStoreType.S3);

            final PipelineDeclaration pipelineDeclaration = new PipelineDeclaration();
            pipelineDeclaration.setArtifactStore(artifactStore);
            pipelineDeclaration.setName(request.getPipelineName());
            pipelineDeclaration.setRoleArn(Constants.CODE_PIPELINE_SERVICE_ROLE);

            final List<StageDeclaration> stageDeclarations = new ArrayList<>();
            stageDeclarations.add(createSourceStageDeclaration(Constants.WATCHED_CODE_COMMIT_BRANCH,
                    request.getCodeCommitRepoName()));
            stageDeclarations.add(createBuildStageDeclaration(request.getBuildProjectName()));

            elasticBeanstalkService.createApplicationNew(
                    request.getCodeCommitRepoName() + Constants.ELASTICBEANSTALK_APP_SUFFIX);
            elasticBeanstalkService.createEnvironmentNew(
                    request.getCodeCommitRepoName() + Constants.ELASTICBEANSTALK_APP_SUFFIX ,
                    request.getCodeCommitRepoName() + Constants.ELASTICBEANSTALK_ENV_SUFFIX);
            stageDeclarations.add(createDeployStageDeclaration(
                    request.getCodeCommitRepoName() + Constants.ELASTICBEANSTALK_APP_SUFFIX ,
                    request.getCodeCommitRepoName() + Constants.ELASTICBEANSTALK_ENV_SUFFIX));

            pipelineDeclaration.setStages(stageDeclarations);
            pipelineDeclaration.setVersion(1);

            AWSCodePipeline pipeline = AWSCodePipelineClientBuilder.defaultClient();
            CreatePipelineResult createPipelineResult = pipeline.createPipeline(
                    new CreatePipelineRequest().withPipeline(pipelineDeclaration));
            log.info("Created CodePipeline. Here is the response from AWS -> " + createPipelineResult);
            return createPipelineResult;
        } catch(Exception e) {
            ExceptionUtils.printRootCauseStackTrace(e);
            log.error("An error occurred while creating the CodePipeline. Error details -> "
                    + ExceptionUtils.getStackTrace(e));
            return null;
        }
    }

    private StageDeclaration createSourceStageDeclaration(final String codeCommitBranchName,
                                                          final String codeCommitRepoName){
        final StageDeclaration stageDeclaration = new StageDeclaration();
        stageDeclaration.setName(Constants.SOURCE);

        final ActionTypeId actionTypeId
                = prepareActionTypeId(Constants.SOURCE, Constants.AWS, Constants.CODECOMMIT);

        final Map<String, String> configuration = new HashMap<>();
        configuration.put("BranchName", codeCommitBranchName);
        configuration.put("PollForSourceChanges","false");
        configuration.put("RepositoryName",codeCommitRepoName);

        final List<OutputArtifact> outputArtifacts = prepareOutputArtifacts(Constants.CODECOMMIT_ARTIFACT);

        final ActionDeclaration actionDeclaration = prepareActionDeclaration(Collections.emptyList(), Constants.SOURCE,
                actionTypeId, configuration, outputArtifacts);

        final List<ActionDeclaration> actions = new ArrayList<>();
        actions.add(actionDeclaration);

        stageDeclaration.setActions(actions);
        return stageDeclaration;
    }

    private StageDeclaration createBuildStageDeclaration(final String buildProjectName){
        final StageDeclaration stageDeclarationSource = new StageDeclaration();
        stageDeclarationSource.setName(Constants.BUILD);

        final ActionTypeId actionTypeId
                = prepareActionTypeId(Constants.BUILD, Constants.AWS, Constants.CODEBUILD);

        final Map<String, String> configuration = new HashMap<>();
        configuration.put("ProjectName", buildProjectName);

        final List<InputArtifact> inputArtifacts = prepareInputArtifacts(Constants.CODECOMMIT_ARTIFACT);

        final List<OutputArtifact> outputArtifacts = prepareOutputArtifacts(Constants.CODEBUILD_ARTIFACT);

        final ActionDeclaration actionDeclaration = prepareActionDeclaration(inputArtifacts, Constants.CODEBUILD,
                actionTypeId, configuration, outputArtifacts);

        final List<ActionDeclaration> actions = new ArrayList<>();
        actions.add(actionDeclaration);

        stageDeclarationSource.setActions(actions);
        return stageDeclarationSource;
    }

    private StageDeclaration createDeployStageDeclaration(final String beanstalkAppName, final String beanstalkAppEnv) {
        final StageDeclaration stageDeclarationSource = new StageDeclaration();
        stageDeclarationSource.setName(Constants.STAGING);

        final ActionTypeId actionTypeId
                = prepareActionTypeId(Constants.DEPLOY, Constants.AWS, Constants.ELASTICBEANSTALK);

        final Map<String, String> configuration = new HashMap<>();
        configuration.put("ApplicationName", beanstalkAppName);
        configuration.put("EnvironmentName", beanstalkAppEnv);

        final List<InputArtifact> inputArtifacts = prepareInputArtifacts(Constants.CODEBUILD_ARTIFACT);

        final ActionDeclaration actionDeclaration = prepareActionDeclaration(inputArtifacts, beanstalkAppEnv,
                actionTypeId, configuration, Collections.emptyList());

        final List<ActionDeclaration> actions = new ArrayList<>();
        actions.add(actionDeclaration);

        stageDeclarationSource.setActions(actions);
        return stageDeclarationSource;
    }

    private ActionTypeId prepareActionTypeId(final String category, final String owner, final String provider) {
        final ActionTypeId actionTypeId = new ActionTypeId();
        actionTypeId.setCategory(category);
        actionTypeId.setOwner(owner);
        actionTypeId.setProvider(provider);
        actionTypeId.setVersion(Constants.VERSION);
        return actionTypeId;
    }

    private List<InputArtifact> prepareInputArtifacts(final String artifactName) {
        final List<InputArtifact> inputArtifacts = new ArrayList<>();
        final InputArtifact inputArtifact = new InputArtifact();
        inputArtifact.setName(artifactName);
        inputArtifacts.add(inputArtifact);
        return inputArtifacts;
    }

    private List<OutputArtifact> prepareOutputArtifacts(final String artifactName) {
        final List<OutputArtifact> outputArtifacts = new ArrayList<>();
        OutputArtifact outputArtifact = new OutputArtifact();
        outputArtifact.setName(artifactName);
        outputArtifacts.add(outputArtifact);
        return outputArtifacts;
    }

    private ActionDeclaration prepareActionDeclaration(final List<InputArtifact> inputArtifacts,
                                                       final String name, final ActionTypeId actionTypeId,
                                                       final Map<String, String> configuration,
                                                       final List<OutputArtifact> outputArtifacts) {
        final ActionDeclaration actionDeclaration = new ActionDeclaration();
        actionDeclaration.setInputArtifacts(inputArtifacts);
        actionDeclaration.setName(name);
        actionDeclaration.setActionTypeId(actionTypeId);
        actionDeclaration.setConfiguration(configuration);
        actionDeclaration.setOutputArtifacts(outputArtifacts);
        actionDeclaration.setRunOrder(1);
        return actionDeclaration;
    }
}
