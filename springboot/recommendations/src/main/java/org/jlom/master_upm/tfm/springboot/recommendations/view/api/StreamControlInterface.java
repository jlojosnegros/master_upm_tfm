package org.jlom.master_upm.tfm.springboot.recommendations.view.api;

public interface StreamControlInterface {

  StreamControlReturnValue play(final long streamId, final long deviceId);
  StreamControlReturnValue stop(final long deviceId);
  StreamControlReturnValue pause(final long deviceId);
}
