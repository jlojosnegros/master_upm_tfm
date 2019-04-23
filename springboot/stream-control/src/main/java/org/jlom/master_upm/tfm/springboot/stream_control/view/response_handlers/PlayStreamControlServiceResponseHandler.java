package org.jlom.master_upm.tfm.springboot.stream_control.view.response_handlers;

import org.jlom.master_upm.tfm.springboot.stream_control.controller.api.dtos.StreamControlServiceResponseFailureException;
import org.jlom.master_upm.tfm.springboot.stream_control.controller.api.dtos.StreamControlServiceResponseFailureInternalError;
import org.jlom.master_upm.tfm.springboot.stream_control.controller.api.dtos.StreamControlServiceResponseFailureInvalidInputParameter;
import org.jlom.master_upm.tfm.springboot.stream_control.controller.api.dtos.StreamControlServiceResponseFailureNotFound;
import org.jlom.master_upm.tfm.springboot.stream_control.controller.api.dtos.StreamControlServiceResponseHandlerRPC;
import org.jlom.master_upm.tfm.springboot.stream_control.controller.api.dtos.StreamControlServiceResponseOK;
import org.jlom.master_upm.tfm.springboot.stream_control.view.api.dtos.StreamControlReturnValue;

public class PlayStreamControlServiceResponseHandler implements StreamControlServiceResponseHandlerRPC {
  @Override
  public StreamControlReturnValue handle(StreamControlServiceResponseOK response) {
    return null;
  }

  @Override
  public StreamControlReturnValue handle(StreamControlServiceResponseFailureException response) {
    return null;
  }

  @Override
  public StreamControlReturnValue handle(StreamControlServiceResponseFailureInternalError response) {
    return null;
  }

  @Override
  public StreamControlReturnValue handle(StreamControlServiceResponseFailureInvalidInputParameter response) {
    return null;
  }

  @Override
  public StreamControlReturnValue handle(StreamControlServiceResponseFailureNotFound response) {
    return null;
  }
}
