package org.jlom.master_upm.tfm.springboot.stream_control.controller.api.dtos;

import org.jlom.master_upm.tfm.springboot.stream_control.view.api.dtos.StreamControlReturnValue;

public interface StreamControlServiceResponseHandlerRPC {

  StreamControlReturnValue handle(StreamControlServiceResponseOK response);

  StreamControlReturnValue handle(StreamControlServiceResponseFailureException response);
  StreamControlReturnValue handle(StreamControlServiceResponseFailureInternalError response);
  StreamControlReturnValue handle(StreamControlServiceResponseFailureInvalidInputParameter response);
  StreamControlReturnValue handle(StreamControlServiceResponseFailureNotFound response);

}
