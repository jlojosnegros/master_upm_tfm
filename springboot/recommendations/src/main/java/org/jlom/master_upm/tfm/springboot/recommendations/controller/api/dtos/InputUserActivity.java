package org.jlom.master_upm.tfm.springboot.recommendations.controller.api.dtos;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Builder
@Data
public class InputUserActivity {
  private final String userId;
  private final List<String> tags;
  private final UserActivityOperation operation;
}
