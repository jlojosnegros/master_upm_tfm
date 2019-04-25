package org.jlom.master_upm.tfm.springboot.user_categories.controller.api.dtos;

import org.jlom.master_upm.tfm.springboot.user_categories.view.api.dtos.StreamControlReturnValue;

public interface IStreamControlServiceResponseHandlerRPC {

  StreamControlReturnValue handle(org.jlom.master_upm.tfm.springboot.user_categories.controller.api.dtos.StreamControlServiceResponseOK response);

  StreamControlReturnValue handle(StreamControlServiceResponseFailureException response);
  StreamControlReturnValue handle(StreamControlServiceResponseFailureInternalError response);
  StreamControlReturnValue handle(StreamControlServiceResponseFailureInvalidInputParameter response);
  StreamControlReturnValue handle(StreamControlServiceResponseFailureNotFound response);

}
