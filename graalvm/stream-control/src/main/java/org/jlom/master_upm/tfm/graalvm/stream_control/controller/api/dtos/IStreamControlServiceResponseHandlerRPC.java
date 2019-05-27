package org.jlom.master_upm.tfm.graalvm.stream_control.controller.api.dtos;


import org.jlom.master_upm.tfm.graalvm.stream_control.view.api.dtos.StreamControlReturnValue;

public interface IStreamControlServiceResponseHandlerRPC {

  StreamControlReturnValue handle(StreamControlServiceResponseOK response);

  StreamControlReturnValue handle(StreamControlServiceResponseFailureException response);
  StreamControlReturnValue handle(StreamControlServiceResponseFailureInternalError response);
  StreamControlReturnValue handle(StreamControlServiceResponseFailureInvalidInputParameter response);
  StreamControlReturnValue handle(StreamControlServiceResponseFailureNotFound response);

}
