package org.jlom.master_upm.tfm.springboot.stream_control.model;


import org.jlom.master_upm.tfm.springboot.stream_control.model.api.IStreamControlRepository;
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

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@ActiveProfiles("test")
public class ModelTest {


  private static int redisEmbeddedServerPort = 6379;

  private RedisServer embeddedRedis = new RedisServer(redisEmbeddedServerPort);

 
  @Autowired
  private IStreamControlRepository streamControlRepository;

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


  
}
