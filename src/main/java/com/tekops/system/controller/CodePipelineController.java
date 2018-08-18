package com.tekops.system.controller;

import com.amazonaws.services.codepipeline.model.*;
import com.tekops.system.model.CodePipelineRequest;
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
    @Autowired
    CodePipelineService codePipelineService;

    @PostMapping("/createCodePipeline")
    public CreatePipelineResult createCodePipeline(@RequestBody(required = false) CodePipelineRequest request){
        CreatePipelineResult result = codePipelineService.createCodePipeline(request);
        logger.info(result.toString());
        return result;
    }
}
