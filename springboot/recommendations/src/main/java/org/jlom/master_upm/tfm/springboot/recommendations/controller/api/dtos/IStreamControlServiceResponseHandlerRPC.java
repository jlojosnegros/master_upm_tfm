package org.jlom.master_upm.tfm.springboot.recommendations.controller.api.dtos;

import org.jlom.master_upm.tfm.springboot.recommendations.view.api.dtos.StreamControlReturnValue;

public interface IStreamControlServiceResponseHandlerRPC {

  StreamControlReturnValue handle(StreamControlServiceResponseOK response);

  StreamControlReturnValue handle(StreamControlServiceResponseFailureException response);
  StreamControlReturnValue handle(StreamControlServiceResponseFailureInternalError response);
  StreamControlReturnValue handle(StreamControlServiceResponseFailureInvalidInputParameter response);
  StreamControlReturnValue handle(StreamControlServiceResponseFailureNotFound response);

}
