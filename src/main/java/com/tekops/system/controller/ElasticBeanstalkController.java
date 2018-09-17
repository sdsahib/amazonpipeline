package com.tekops.system.controller;


import com.tekops.system.model.EBSModel;
import com.tekops.system.service.ElasticBeanstalkService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/EBS")
@CrossOrigin("*")
public class ElasticBeanstalkController {

    @Autowired
    private final ElasticBeanstalkService elasticBeanstalkService;

    @Autowired
    public ElasticBeanstalkController(ElasticBeanstalkService elasticBeanstalkService) {this.elasticBeanstalkService = elasticBeanstalkService;}

    @PostMapping(value = "/createEBS")
    public void ElasticBeanstalk(@RequestBody EBSModel ebsModel) {
            elasticBeanstalkService.createElasticBeanstalk(ebsModel);
    }
}
