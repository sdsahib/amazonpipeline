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
public class CodeBuildModel {
  private String projectName;
  private String codeCommitRepoLocation;
}
