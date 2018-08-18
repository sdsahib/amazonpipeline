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


@Service
@Slf4j
public class CodeCommitService {

  public boolean createRepository(CodeCommitModel codeCommitModel) {
    try {
      final List<CodeCommitModel> codeCommitModels = getRepositoryList();
      for (CodeCommitModel tempCodeCommitModel : codeCommitModels) {
        if (tempCodeCommitModel.getRepositoryName().equals(codeCommitModel.getRepositoryName())) {
          log.info("The repo already exists -> " + codeCommitModel.getRepositoryName());
          return false;
        }
      }
      final CreateRepositoryRequest createRepositoryRequest = new CreateRepositoryRequest()
              .withRepositoryName(codeCommitModel.getRepositoryName())
              .withRepositoryDescription(codeCommitModel.getRepositoryDescription());

      //will access the token from the credentials file in aws in user directory
      final AWSCodeCommit awsCodeCommit = AWSCodeCommitClientBuilder.defaultClient();
      final CreateRepositoryResult result = awsCodeCommit.createRepository(createRepositoryRequest);
      log.info("Created CodeCommit repo details -> " + result);
      return true;
    } catch (Exception e) {
      ExceptionUtils.printRootCauseStackTrace(e);
      log.error("An error occurred while creating the CodeCommit repo. Error details -> "
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
