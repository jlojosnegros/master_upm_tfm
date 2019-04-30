package org.jlom.master_upm.tfm.springboot.recommendations.controller;

import org.jlom.master_upm.tfm.springboot.recommendations.controller.api.dtos.UserActivityOperation;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.Map;
import java.util.TreeMap;

@Component
public class WeighingScales {

  private Map<UserActivityOperation, Long> weighTable;

  public WeighingScales() {
    //@jlom esto podria leerse de configuracion

    Map<UserActivityOperation, Long> tmp = new TreeMap<>();

    tmp.put(UserActivityOperation.SEARCH,1L);
    tmp.put(UserActivityOperation.LIKE,50L);
    tmp.put(UserActivityOperation.WATCH,10L);

    this.weighTable = Collections.unmodifiableMap(tmp);
  }

  public long weigh(UserActivityOperation operation) {
    return weighTable.getOrDefault(operation,0L);
  }
}
