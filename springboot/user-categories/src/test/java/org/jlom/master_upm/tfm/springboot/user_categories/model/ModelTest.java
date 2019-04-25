package org.jlom.master_upm.tfm.springboot.user_categories.model;


import org.jlom.master_upm.tfm.springboot.user_categories.model.api.IUserCategoriesRepository;
import org.jlom.master_upm.tfm.springboot.user_categories.model.daos.StreamControlData;
import org.jlom.master_upm.tfm.springboot.user_categories.model.daos.StreamStatus;
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

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@ActiveProfiles("test")
public class ModelTest {


  private static int redisEmbeddedServerPort = 6379;

  private RedisServer embeddedRedis = new RedisServer(redisEmbeddedServerPort);

 
  @Autowired
  private IUserCategoriesRepository streamControlRepository;

  private static final Logger LOG = LoggerFactory.getLogger(ModelTest.class);

  @Before
  public void setup() {
    embeddedRedis.start();
    String redisContainerIpAddress = "localhost";
    int redisFirstMappedPort = redisEmbeddedServerPort;
    LOG.info("-=* Redis Container running on: " + redisContainerIpAddress + ":" + redisFirstMappedPort);
  }

  @After
  public void tearDown() {
    embeddedRedis.stop();
  }


  @Test
  public void given_NoData_when_findRunning_then_ShouldNotFindAnything() {

    final long userId = 1;
    final long deviceId =1;

    StreamControlData streamingRunning = streamControlRepository.findStreamingRunning(userId, deviceId);

    assertThat(streamingRunning).isNull();

  }

  @Test
  public void given_OneData_when_findRunning_then_ShouldNotFindAnything() {

    StreamControlData OneRunning = StreamControlData.builder()
            .userId(1)
            .deviceId(1)
            .streamId(1)
            .status(StreamStatus.RUNNING)
            .build();
    streamControlRepository.save(OneRunning);

    StreamControlData streamingRunning = streamControlRepository.findStreamingRunning(OneRunning.getUserId()
            ,OneRunning.getDeviceId());

    assertThat(streamingRunning).isNotNull();
    assertThat(streamingRunning.getStatus()).isEqualTo(StreamStatus.RUNNING);
    assertThat(streamingRunning).isEqualTo(OneRunning);

  }

  @Test
  public void given_SomeData_when_findRunning_then_ShouldNotFindAnything() {

    StreamControlData OneRunning = StreamControlData.builder()
            .userId(1)
            .deviceId(1)
            .streamId(1)
            .status(StreamStatus.RUNNING)
            .build();
    streamControlRepository.save(OneRunning);

    StreamControlData streamingRunning = streamControlRepository.findStreamingRunning(OneRunning.getUserId()
            ,OneRunning.getDeviceId());

    assertThat(streamingRunning).isEqualTo(OneRunning);

    OneRunning.setStatus(StreamStatus.DONE);
    streamControlRepository.save(OneRunning);

    streamingRunning = streamControlRepository.findStreamingRunning(OneRunning.getUserId()
            ,OneRunning.getDeviceId());

    assertThat(streamingRunning).isNull();

  }
}
