package org.jlom.master_upm.tfm.springboot.apigw.model;

import com.google.common.collect.Lists;
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

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@ActiveProfiles("test")
public class ModelTest {


  private static int redisEmbeddedServerPort = 6379;

  private RedisServer embeddedRedis = new RedisServer(redisEmbeddedServerPort);


  @Autowired
  private UsersRepository repository;

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
  public void testingId() {

    UserModel userModel = new UserModel("one","one");
    UserModel userModel2= new UserModel("2","2");
    UserModel userModel3= new UserModel("3","3");
    UserModel userModel4= new UserModel("3","4");


    LOG.error("userModel:" + userModel);


    repository.save(userModel);
    repository.save(userModel2);
    repository.save(userModel3);
    repository.save(userModel4);


    Iterable<UserModel> all = repository.findAll();
    LOG.error("all" + Lists.newArrayList(all));

    var found = repository.findByUsername("one");

    LOG.error("oneByUsername:" + found);

    assertThat(found).hasSize(1);
    assertThat(found.get(0)).isEqualTo(userModel);

  }

}
