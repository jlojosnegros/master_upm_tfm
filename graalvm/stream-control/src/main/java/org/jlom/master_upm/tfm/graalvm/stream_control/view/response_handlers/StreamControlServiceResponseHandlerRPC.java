package org.jlom.master_upm.tfm.graalvm.stream_control.view.response_handlers;

import org.jlom.master_upm.tfm.graalvm.stream_control.controller.api.dtos.IStreamControlServiceResponseHandlerRPC;
import org.jlom.master_upm.tfm.graalvm.stream_control.controller.api.dtos.StreamControlServiceResponseFailureException;
import org.jlom.master_upm.tfm.graalvm.stream_control.controller.api.dtos.StreamControlServiceResponseFailureInternalError;
import org.jlom.master_upm.tfm.graalvm.stream_control.controller.api.dtos.StreamControlServiceResponseFailureInvalidInputParameter;
import org.jlom.master_upm.tfm.graalvm.stream_control.controller.api.dtos.StreamControlServiceResponseFailureNotFound;
import org.jlom.master_upm.tfm.graalvm.stream_control.controller.api.dtos.StreamControlServiceResponseOK;
import org.jlom.master_upm.tfm.graalvm.stream_control.utils.DtosTransformations;
import org.jlom.master_upm.tfm.graalvm.stream_control.view.api.dtos.InputStreamData;
import org.jlom.master_upm.tfm.graalvm.stream_control.view.api.dtos.StreamControlReturnValue;
import org.jlom.master_upm.tfm.graalvm.stream_control.view.api.dtos.StreamControlReturnValueError;
import org.jlom.master_upm.tfm.graalvm.stream_control.view.api.dtos.StreamControlReturnValueOk;


public class StreamControlServiceResponseHandlerRPC implements IStreamControlServiceResponseHandlerRPC {
  @Override
  public StreamControlReturnValue handle(StreamControlServiceResponseOK response) {

    InputStreamData data = DtosTransformations.serviceToView(response.getStreamControlData());
    return StreamControlReturnValueOk.builder()
            .userId(data.getUserId())
            .deviceId(data.getDeviceId())
            .streamId(data.getStreamId())
            .build();
  }

  @Override
  public StreamControlReturnValue handle(StreamControlServiceResponseFailureException response) {

    Exception exception = response.getException();

    return StreamControlReturnValueError.builder()
            .message(exception.getMessage())
            .errorCode(StreamControlReturnValueError.ErrorCode.EXCEPTION)
            .build();
  }

  @Override
  public StreamControlReturnValue handle(StreamControlServiceResponseFailureInternalError response) {

    return StreamControlReturnValueError.builder()
            .message(response.getMessage())
            .errorCode(StreamControlReturnValueError.ErrorCode.INTERNAL_ERROR)
            .build();
  }

  @Override
  public StreamControlReturnValue handle(StreamControlServiceResponseFailureInvalidInputParameter response) {


    String responseMessage = String.format("InvalidParam: msg=%s; paramName=%s; paramValue=%s;"
            , response.getMessage()
            , response.getParamName()
            , response.getParamValue()
    );
    return StreamControlReturnValueError.builder()
            .message(responseMessage)
            .errorCode(StreamControlReturnValueError.ErrorCode.INVALID_PARAMETER)
            .build();
  }

  @Override
  public StreamControlReturnValue handle(StreamControlServiceResponseFailureNotFound response) {
    String responseMessage = String.format("NOT FOUND: msg=%s; paramName=%s; value=%s;"
            , response.getMessage()
            , response.getParamName()
            , response.getValue()
    );
    return StreamControlReturnValueError.builder()
            .message(responseMessage)
            .errorCode(StreamControlReturnValueError.ErrorCode.NOT_FOUND)
            .build();
  }
}
