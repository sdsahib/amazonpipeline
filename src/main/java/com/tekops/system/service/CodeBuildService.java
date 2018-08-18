package com.tekops.system.service;

import com.amazonaws.services.codebuild.AWSCodeBuild;
import com.amazonaws.services.codebuild.AWSCodeBuildClientBuilder;
import com.amazonaws.services.codebuild.model.*;
import com.tekops.system.Constants;
import com.tekops.system.model.BucketModel;
import com.tekops.system.model.CodeBuildModel;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author rapaul
 */
@Service
@Slf4j
public class CodeBuildService {
  private final CommonItemsRetrieverService commonItemsRetrieverService;

  @Autowired
  public CodeBuildService(CommonItemsRetrieverService commonItemsRetrieverService) {
    this.commonItemsRetrieverService = commonItemsRetrieverService;
  }

  public boolean createProject(final CodeBuildModel codeBuildModel) {
    try {
      final AWSCodeBuild codeBuild = AWSCodeBuildClientBuilder.defaultClient();

//      final ProjectSource source = new ProjectSource()
//              .withType(SourceType.CODECOMMIT)
//              .withLocation(codeBuildModel.getCodeCommitRepoLocation())
//              .withGitCloneDepth(1)
//              .withBuildspec(Constants.BUILD_SPEC_FILE_NAME);
      final ProjectSource source = new ProjectSource()
              .withType(SourceType.CODEPIPELINE)
//              .withLocation(codeBuildModel.getCodeCommitRepoLocation())
              .withLocation(codeBuildModel.getCodeCommitRepoLocation())
              .withBuildspec(Constants.BUILD_SPEC_FILE_NAME);

      final ProjectEnvironment environment = new ProjectEnvironment()
              .withComputeType(ComputeType.BUILD_GENERAL1_SMALL)
              .withImage(Constants.CODE_BUILD_IMAGE_NAME) // Takes care of the language, and language version, platform
              .withType(EnvironmentType.LINUX_CONTAINER);

//      final ProjectArtifacts artifacts = new ProjectArtifacts()
//              .withType(ArtifactsType.S3)
//              .withPackaging(ArtifactPackaging.NONE)
//              .withName(codeBuildModel.getProjectName()) // the artifact name i.e. MyProject
//              .withPath(Constants.S3_BUCKET_PATH) // i.e. MyArtifacts
//              .withNamespaceType(ArtifactNamespace.BUILD_ID)
//              .withEncryptionDisabled(true);

      final ProjectArtifacts artifacts = new ProjectArtifacts()
              .withType(ArtifactsType.CODEPIPELINE)
//              .withPackaging(ArtifactPackaging.NONE)
//              .withName(codeBuildModel.getProjectName()) // the artifact name i.e. MyProject
//              .withPath(Constants.S3_BUCKET_PATH) // i.e. MyArtifacts
//              .withNamespaceType(ArtifactNamespace.BUILD_ID)
              .withEncryptionDisabled(true);

//      final List<BucketModel> bucketModels = commonItemsRetrieverService.fetchS3Buckets();
//      for (BucketModel bucketModel : bucketModels) {
//        if (bucketModel.getName().startsWith(Constants.S3_BUCKET_NAME_PREFIX)) {
//          artifacts.setLocation(bucketModel.getName()); // Bucket name
//          break;
//        }
//      }

      final CreateProjectRequest request = new CreateProjectRequest()
              .withName(codeBuildModel.getProjectName())
              .withSource(source)
              .withEnvironment(environment)
              .withArtifacts(artifacts)
              .withTimeoutInMinutes(60)
              .withServiceRole(Constants.CODE_BUILD_SERVICE_ROLE_ARN);

      final CreateProjectResult result = codeBuild.createProject(request);
      log.info("Created CodeBuild project details -> " + result);
      return true;
    } catch (Exception e) {
      ExceptionUtils.printRootCauseStackTrace(e);
      log.error("An error occurred while creating the CodeBuild project. Error details -> "
              + ExceptionUtils.getStackTrace(e));
      return false;
    }
  }
}
