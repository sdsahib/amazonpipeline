package com.tekops.system.service;

import com.amazonaws.services.codepipeline.AWSCodePipeline;
import com.amazonaws.services.codepipeline.AWSCodePipelineClientBuilder;
import com.amazonaws.services.codepipeline.model.*;
import com.amazonaws.services.elasticbeanstalk.model.ApplicationDescription;
import com.amazonaws.services.elasticbeanstalk.model.DescribeEnvironmentsResult;
import com.tekops.system.Constants;
import com.tekops.system.model.CodePipelineRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * Created by ravikumar on 18/08/18.
 */
@Service
public class CodePipelineService {

    private static final Logger logger = LoggerFactory.getLogger(CodePipelineService.class);

    @Autowired
    private ElasticBeanstalkService elasticBeanstalkService;

    public CreatePipelineResult createCodePipeline(CodePipelineRequest request) {
        try {
            AWSCodePipeline pipeline = AWSCodePipelineClientBuilder.defaultClient();

            final String outputArtifactForSource = request.getBuildProjectName() + Constants.SEPERATOR_CHAR + System.currentTimeMillis();
            final String outputArtifactForBuild = request.getBuildProjectName() + Constants.SEPERATOR_CHAR + System.currentTimeMillis() + "war";
            List<StageDeclaration> stageDeclarations = new ArrayList<>();
            stageDeclarations.add(createSourceStageDeclaration(Constants.WATCHED_CODE_COMMIT_BRANCH, request.getCodeCommitRepoName(), outputArtifactForSource));
            stageDeclarations.add(createBuildStageDeclaration(request.getBuildProjectName(), outputArtifactForSource, outputArtifactForBuild));

            String ebAppName = request.getCodeCommitRepoName() + System.currentTimeMillis();
            ApplicationDescription applicationDescription = elasticBeanstalkService.createApplication(ebAppName, ebAppName + " sample description");
            String envName =  elasticBeanstalkService.createEnvironment(ebAppName, ebAppName + " sample description");
            stageDeclarations.add(createStagingStageDeclaration(applicationDescription.getApplicationName(),
                    envName, outputArtifactForBuild));

            ArtifactStore artifactStore = new ArtifactStore();
            artifactStore.setLocation(Constants.S3_BUCKET_NAME);
            artifactStore.setType("S3");

            PipelineDeclaration pipelineDeclaration = new PipelineDeclaration();
            pipelineDeclaration.setName(request.getPipelineName());
            pipelineDeclaration.setRoleArn(Constants.CODE_PIPELINE_SERVICE_ROLE);
            pipelineDeclaration.setArtifactStore(artifactStore);
            pipelineDeclaration.setStages(stageDeclarations);
            pipelineDeclaration.setVersion(1);

            CreatePipelineRequest pipelineRequest = new CreatePipelineRequest();
            pipelineRequest.setPipeline(pipelineDeclaration);

            CreatePipelineResult result = pipeline.createPipeline(pipelineRequest);
            return result;
        }catch(Exception e) {
            logger.error(e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    private ActionTypeId createActionTypeId(String category, String owner, String provider, String version){
        ActionTypeId actionTypeId = new ActionTypeId();
        actionTypeId.setCategory(category);
        actionTypeId.setOwner(owner);
        actionTypeId.setProvider(provider);
        actionTypeId.setVersion(version);
        return actionTypeId;
    }

    private List<InputArtifact> createOneInputArtifact(String name) {
        List<InputArtifact> inputArtifacts = new ArrayList<>();
        InputArtifact inputArtifact = new InputArtifact();
        inputArtifact.setName(name);
        inputArtifacts.add(inputArtifact);
        return inputArtifacts;
    }

    private List<OutputArtifact> createOneOutputArtifact(String name) {
        List<OutputArtifact> outputArtifacts = new ArrayList<>();
        OutputArtifact outputArtifact = new OutputArtifact();
        outputArtifact.setName(name);
        outputArtifacts.add(outputArtifact);
        return outputArtifacts;
    }

    private List<ActionDeclaration> createOneActionDeclaration(
            List<InputArtifact> inputArtifacts,
            String name,
            ActionTypeId actionTypeId,
            List<OutputArtifact> outputArtifacts,
            int runOrder,
            Map<String, String> configuration
    ) {
        ActionDeclaration actionDeclaration = new ActionDeclaration();
        actionDeclaration.setInputArtifacts(inputArtifacts);
        actionDeclaration.setName(name);
        actionDeclaration.setActionTypeId(actionTypeId);
        actionDeclaration.setOutputArtifacts(outputArtifacts);
        actionDeclaration.setRunOrder(runOrder);
        actionDeclaration.setConfiguration(configuration);

        List<ActionDeclaration> actions = new ArrayList<>();
        actions.add(actionDeclaration);

        return actions;
    }


    private StageDeclaration createStagingStageDeclaration(String beanstalkAppName, String beanstalkAppEnv, String inputAppName){
        ActionTypeId actionTypeId = createActionTypeId("Deploy", "AWS", "ElasticBeanstalk", "1");

        Map<String, String> configuration = new HashMap<>();
        configuration.put("ApplicationName", beanstalkAppName);
        configuration.put("EnvironmentName", beanstalkAppEnv);

        List<InputArtifact> inputArtifacts = createOneInputArtifact(inputAppName);

        List<ActionDeclaration> actions = createOneActionDeclaration(
                inputArtifacts, beanstalkAppEnv, actionTypeId, Collections.emptyList(), 1, configuration);

        StageDeclaration stageDeclarationSource = new StageDeclaration();
        stageDeclarationSource.setName("Deploy");
        stageDeclarationSource.setActions(actions);

        return stageDeclarationSource;
    }

    private StageDeclaration createBuildStageDeclaration(String buildProjectName, String inputAppName, String outputAppName){


        ActionTypeId actionTypeId = createActionTypeId("Build", "AWS", "CodeBuild", "1");

        Map<String, String> configuration = new HashMap<>();
        configuration.put("ProjectName", buildProjectName);

        List<InputArtifact> inputArtifacts = createOneInputArtifact(inputAppName);
        List<OutputArtifact> outputArtifacts = createOneOutputArtifact(outputAppName);

        List<ActionDeclaration> actions = createOneActionDeclaration(
                inputArtifacts, "CodeBuild", actionTypeId, outputArtifacts, 1, configuration);

        StageDeclaration stageDeclarationSource = new StageDeclaration();
        stageDeclarationSource.setName("Build");
        stageDeclarationSource.setActions(actions);

        return stageDeclarationSource;
    }

    private StageDeclaration createSourceStageDeclaration(String codeCommitBranchName, String codeCommitRepoName, String outputAppName){

        ActionTypeId actionTypeId = createActionTypeId("Source", "AWS", "CodeCommit", "1");

        Map<String, String> configuration = new HashMap<>();
        configuration.put("BranchName", codeCommitBranchName);
        configuration.put("PollForSourceChanges","false");
        configuration.put("RepositoryName",codeCommitRepoName);

        List<OutputArtifact> outputArtifacts = createOneOutputArtifact(outputAppName);

        List<ActionDeclaration> actions = createOneActionDeclaration(
                Collections.emptyList(), "Source", actionTypeId, outputArtifacts, 1, configuration);

        StageDeclaration stageDeclarationSource = new StageDeclaration();
        stageDeclarationSource.setName("Source");
        stageDeclarationSource.setActions(actions);

        return stageDeclarationSource;
    }
}
