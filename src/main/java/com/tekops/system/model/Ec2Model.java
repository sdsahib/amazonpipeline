package com.tekops.system.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author rapaul
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
public class Ec2Model {
  private String id;
  private String subnet;
  private String type;
  private String publicIp;
}
