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
public class UserModel {
  private String id;
  private String name;
  private String arn;
}
