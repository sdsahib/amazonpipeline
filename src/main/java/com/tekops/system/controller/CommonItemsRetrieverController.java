package com.tekops.system.controller;

import com.tekops.system.model.BucketModel;
import com.tekops.system.model.Ec2Model;
import com.tekops.system.model.UserModel;
import com.tekops.system.model.VpcModel;
import com.tekops.system.service.CommonItemsRetrieverService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author rapaul
 */
@RestController
@RequestMapping("/common-items")
@CrossOrigin("*")
public class CommonItemsRetrieverController {
  private final CommonItemsRetrieverService commonItemsRetrieverService;

  @Autowired
  public CommonItemsRetrieverController(CommonItemsRetrieverService commonItemsRetrieverService) {
    this.commonItemsRetrieverService = commonItemsRetrieverService;
  }

  @GetMapping(value = "/vpc", produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<VpcModel> getVpc() {
    return new ResponseEntity<>(commonItemsRetrieverService.fetchVpc(), HttpStatus.OK);
  }

  @GetMapping(value = "/s3-buckets", produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<List<BucketModel>> getS3Buckets() {
    return new ResponseEntity<>(commonItemsRetrieverService.fetchS3Buckets(), HttpStatus.OK);
  }

  @GetMapping(value = "/ec2-instances", produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<List<Ec2Model>> getEc2Instances() {
    return new ResponseEntity<>(commonItemsRetrieverService.fetchEC2Instances(), HttpStatus.OK);
  }

  @GetMapping(value = "/user", produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<UserModel> getUser() {
    return new ResponseEntity<>(commonItemsRetrieverService.fetchIAMUser(), HttpStatus.OK);
  }
}
