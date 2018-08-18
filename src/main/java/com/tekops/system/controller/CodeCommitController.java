package com.tekops.system.controller;

import com.tekops.system.model.CodeCommitModel;
import com.tekops.system.model.CommonServerResponseModel;
import com.tekops.system.service.CodeCommitService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController("/code-commit")
public class CodeCommitController {

  @Autowired
  private final CodeCommitService codeCommitService;

  @Autowired
  public CodeCommitController(CodeCommitService codeCommitService) {
    this.codeCommitService = codeCommitService;
  }

  @PostMapping(value = "/create",
          produces = MediaType.APPLICATION_JSON_VALUE,
          consumes = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<CommonServerResponseModel> createRepository(
          @RequestBody CodeCommitModel codeCommitModel) {
    final boolean result = codeCommitService.createRepository(codeCommitModel);
    final CommonServerResponseModel serverResponseModel = new CommonServerResponseModel();
    if (result) {
      serverResponseModel.setMessage("The CodeCommit repo is successfully created.");
      serverResponseModel.setCode(HttpStatus.CREATED.value());
    } else {
      serverResponseModel.setMessage("An error occurred while creating the CodeCommit repo.");
      serverResponseModel.setCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
    }
    return new ResponseEntity<>(serverResponseModel, HttpStatus.valueOf(serverResponseModel.getCode()));
  }

  @GetMapping(value = "/list-repositories",
          produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<List<CodeCommitModel>> listRepositories() {
    final List<CodeCommitModel> codeCommitModels = codeCommitService.getRepositoryList();
    return new ResponseEntity<>(codeCommitModels, HttpStatus.OK);
  }
}
