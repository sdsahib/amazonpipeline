package com.tekops.system.controller;

import com.tekops.system.model.CodeBuildModel;
import com.tekops.system.model.CommonServerResponseModel;
import com.tekops.system.service.CodeBuildService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author rapaul
 */
@RestController
@RequestMapping("/code-build")
public class CodeBuildController {
  private CodeBuildService codeBuildService;

  @Autowired
  public CodeBuildController(CodeBuildService codeBuildService) {
    this.codeBuildService = codeBuildService;
  }

  @PostMapping(value = "/create-project",
          consumes = MediaType.APPLICATION_JSON_VALUE,
          produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<CommonServerResponseModel> buildProject(@RequestBody CodeBuildModel codeBuildModel) {
    final boolean result = codeBuildService.createProject(codeBuildModel);
    final CommonServerResponseModel serverResponseModel = new CommonServerResponseModel();
    if (result) {
      serverResponseModel.setMessage("The CodeBuild project is successfully created.");
      serverResponseModel.setCode(HttpStatus.CREATED.value());
    } else {
      serverResponseModel.setMessage("An error occurred while creating the CodeBuild project.");
      serverResponseModel.setCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
    }
    return new ResponseEntity<>(serverResponseModel, HttpStatus.valueOf(serverResponseModel.getCode()));
  }
}
