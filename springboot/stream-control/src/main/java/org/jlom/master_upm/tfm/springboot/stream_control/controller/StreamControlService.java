package org.jlom.master_upm.tfm.springboot.stream_control.controller;


import org.jlom.master_upm.tfm.springboot.stream_control.controller.api.StreamControlServiceCommands;
import org.jlom.master_upm.tfm.springboot.stream_control.controller.api.StreamControlServiceQueries;
import org.jlom.master_upm.tfm.springboot.stream_control.model.api.IStreamControlRepository;
import org.springframework.stereotype.Service;

@Service
public class StreamControlService implements StreamControlServiceCommands, StreamControlServiceQueries {

  private final IStreamControlRepository repository;

  public StreamControlService(IStreamControlRepository repository) {
    this.repository = repository;
  }

  
}
