package org.jlom.master_upm.tfm.springboot.user_categories.view.api;

import org.jlom.master_upm.tfm.springboot.user_categories.view.api.dtos.StreamControlReturnValue;

public interface StreamControlInterface {

  StreamControlReturnValue play(final long streamId, final long deviceId);
  StreamControlReturnValue stop(final long deviceId);
  StreamControlReturnValue pause(final long deviceId);
}
