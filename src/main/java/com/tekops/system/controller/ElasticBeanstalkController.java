package com.tekops.system.controller;


import com.tekops.system.model.EBSModel;
import com.tekops.system.service.ElasticBeanstalkService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/EBS")
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
