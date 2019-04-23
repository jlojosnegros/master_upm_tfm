package org.jlom.master_upm.tfm.springboot.stream_control.controller;

import org.assertj.core.api.Assertions;
import org.jlom.master_upm.tfm.springboot.stream_control.controller.api.dtos.StreamControlServiceResponse;
import org.jlom.master_upm.tfm.springboot.stream_control.controller.api.dtos.StreamControlServiceResponseOK;
import org.jlom.master_upm.tfm.springboot.stream_control.model.api.IStreamControlRepository;
import org.jlom.master_upm.tfm.springboot.stream_control.model.daos.StreamControlData;
import org.jlom.master_upm.tfm.springboot.stream_control.model.daos.StreamStatus;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import redis.embedded.RedisServer;

import java.util.Set;

import static org.assertj.core.api.Assertions.*;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@ActiveProfiles("test")
public class ServiceTest {

  private static final Logger LOG = LoggerFactory.getLogger(ServiceTest.class);

  @Autowired
  private StreamControlService service;

  @Autowired
  private IStreamControlRepository repository;

  private static int redisEmbeddedServerPort = 6379;
  private RedisServer redisEmbeddedServer = new RedisServer(redisEmbeddedServerPort);



  @Before
  public void setup() {
    redisEmbeddedServer.start();
    String redisContainerIpAddress = "localhost";
    int redisFirstMappedPort = redisEmbeddedServerPort;
    LOG.info("-=* Redis Container running on: " + redisContainerIpAddress + ":" + redisFirstMappedPort);
  }

  @After
  public void tearDown() {
    redisEmbeddedServer.stop();
  }

  private StreamControlData addCheckedStreamControlData(long userId,
                                                        long deviceId,
                                                        long streamId,
                                                        StreamStatus status,
                                                        boolean tillTheEnd) {
    StreamControlData streamControlData = StreamControlData.builder()
            .userId(userId)
            .deviceId(deviceId)
            .streamId(streamId)
            .status(status)
            .tillTheEnd(tillTheEnd)
            .build();
    repository.save(streamControlData);

    return null;
  }

  @Test
  public void given_NoStreamingRunning_when_PlayANewStream_then_AllShouldWork() {


    final long streamId = 1;
    final long deviceId = 1;

    StreamControlServiceResponse play = service.play(streamId, deviceId);

    assertThat(play).isInstanceOf(StreamControlServiceResponseOK.class);
    StreamControlData streamControlData = ((StreamControlServiceResponseOK) play).getStreamControlData();

    assertThat(streamControlData.getDeviceId()).isEqualTo(deviceId);
    assertThat(streamControlData.getStatus()).isEqualTo(StreamStatus.RUNNING);
    assertThat(streamControlData.getStreamId()).isEqualTo(streamId);

  }
  
}