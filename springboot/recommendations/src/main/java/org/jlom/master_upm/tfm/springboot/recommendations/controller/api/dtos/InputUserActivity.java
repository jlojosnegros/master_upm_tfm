package org.jlom.master_upm.tfm.springboot.recommendations.controller.api.dtos;

import lombok.Builder;
import lombok.Data;

import java.util.Set;

@Builder
@Data
public class InputUserActivity {
  private final String userId;
  private final Set<String> tags;
  private final UserActivityOperation operation;
  private final String contentId;
}
