package org.jlom.master_upm.tfm.springboot.stream_control.controller;

import org.jlom.master_upm.tfm.springboot.stream_control.model.daos.StreamControlData;
import org.junit.After;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import redis.embedded.RedisServer;

import java.util.Set;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@ActiveProfiles("test")
public class ServiceTest {

  private static final Logger LOG = LoggerFactory.getLogger(ServiceTest.class);

  @Autowired
  private StreamControlService service;

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

  private StreamControlData addCheckedUserDevice(long userId, Set<Long> deviceIds) {

//    StreamControlData userDevice = StreamControlData.builder()
//            .userId(userId)
//            .devices(deviceIds)
//            .build();

    //StreamControlServiceResponse response = service.createUser();
    //assertThat(response).isInstanceOf(StreamControlServiceResponseOK.class);

    //StreamControlData actualUserDevice = ((StreamControlServiceResponseOK) response).getStreamControlData();
    //assertThat(actualUserDevice).isEqualTo(userDevice);

    //return actualUserDevice;
    return null;
  }
  
}