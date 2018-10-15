package com.tekops.system.service;


import com.amazonaws.services.codecommit.AWSCodeCommit;
import com.amazonaws.services.codecommit.AWSCodeCommitClientBuilder;
import com.amazonaws.services.codecommit.model.CreateRepositoryRequest;
import com.amazonaws.services.codecommit.model.CreateRepositoryResult;
import com.amazonaws.services.codecommit.model.ListRepositoriesRequest;
import com.amazonaws.services.codecommit.model.RepositoryNameIdPair;
import com.tekops.system.model.CodeCommitModel;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * Handles CodeCommit related AWS service calls.
 * @author rapaul
 */
@Service
@Slf4j
public class CodeCommitService {

  public boolean createRepository(final CodeCommitModel codeCommitModel) {
    try {
      final List<CodeCommitModel> codeCommitModels = getRepositoryList();
      for (final CodeCommitModel tempCodeCommitModel : codeCommitModels) {
        if (tempCodeCommitModel.getRepositoryName().equals(codeCommitModel.getRepositoryName())) {
          log.info(String.format("The repo with the name {0} already exists. "), codeCommitModel.getRepositoryName());
          return false;
        }
      }
      final CreateRepositoryRequest createRepositoryRequest = new CreateRepositoryRequest()
              .withRepositoryName(codeCommitModel.getRepositoryName())
              .withRepositoryDescription(codeCommitModel.getRepositoryDescription());

      final AWSCodeCommit awsCodeCommit = AWSCodeCommitClientBuilder.defaultClient();
      final CreateRepositoryResult createRepositoryResult = awsCodeCommit.createRepository(createRepositoryRequest);
      log.info("Created CodeCommit repository. Here is the response from AWS -> " + createRepositoryResult);
      return true;
    } catch (Exception e) {
      ExceptionUtils.printRootCauseStackTrace(e);
      log.error("An error occurred while creating the CodeCommit repository. Error details -> "
              + ExceptionUtils.getStackTrace(e));
      return false;
    }
  }

  public List<CodeCommitModel> getRepositoryList() {
    final List<CodeCommitModel> repositoryList = new ArrayList<>();
    final AWSCodeCommit awsCodeCommit = AWSCodeCommitClientBuilder.defaultClient();
    final List<RepositoryNameIdPair> repositoryNameIdPairList = awsCodeCommit.listRepositories(
            new ListRepositoriesRequest()).getRepositories();
    for (RepositoryNameIdPair temp : repositoryNameIdPairList) {
      repositoryList.add(new CodeCommitModel(temp.getRepositoryName(), null));
    }
    return repositoryList;
  }
}
