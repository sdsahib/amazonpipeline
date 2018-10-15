package com.tekops.system.service;

import com.amazonaws.services.codebuild.AWSCodeBuild;
import com.amazonaws.services.codebuild.AWSCodeBuildClientBuilder;
import com.amazonaws.services.codebuild.model.*;
import com.tekops.system.Constants;
import com.tekops.system.model.CodeBuildModel;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.stereotype.Service;

/**
 * Handles CodeBuild related AWS service calls.
 * @author rapaul
 */
@Service
@Slf4j
public class CodeBuildService {

  public boolean createProject(final CodeBuildModel codeBuildModel) {
    try {
      final AWSCodeBuild codeBuild = AWSCodeBuildClientBuilder.defaultClient();

      final ProjectSource source = new ProjectSource()
              .withType(SourceType.CODEPIPELINE)
              .withLocation(codeBuildModel.getCodeCommitRepoLocation())
              .withBuildspec(Constants.BUILD_SPEC_FILE_NAME);

      final ProjectEnvironment environment = new ProjectEnvironment()
              .withComputeType(ComputeType.BUILD_GENERAL1_SMALL)
              .withImage(Constants.CODE_BUILD_IMAGE_NAME) // Takes care of the language, and language version, platform
              .withType(EnvironmentType.LINUX_CONTAINER);

      final ProjectArtifacts artifacts = new ProjectArtifacts()
              .withType(ArtifactsType.CODEPIPELINE)
              .withEncryptionDisabled(true);

      final CreateProjectRequest request = new CreateProjectRequest()
              .withName(codeBuildModel.getProjectName())
              .withSource(source)
              .withEnvironment(environment)
              .withArtifacts(artifacts)
              .withTimeoutInMinutes(60)
              .withServiceRole(Constants.CODE_BUILD_SERVICE_ROLE_ARN);

      final CreateProjectResult createProjectResult = codeBuild.createProject(request);
      log.info("Created CodeBuild project. Here is the response from AWS -> " + createProjectResult);
      return true;
    } catch (Exception e) {
      ExceptionUtils.printRootCauseStackTrace(e);
      log.error("An error occurred while creating the CodeBuild project. Error details -> "
              + ExceptionUtils.getStackTrace(e));
      return false;
    }
  }
}
