package org.jlom.master_upm.tfm.micronaut.apigw.model;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.google.common.collect.Lists;
import io.micronaut.test.annotation.MicronautTest;
import org.jlom.master_upm.tfm.micronaut.apigw.utils.EmbeddedRedisServer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;

import static org.assertj.core.api.Assertions.assertThat;

@MicronautTest
public class ModelTest {

  private static final Logger LOG = LoggerFactory.getLogger(ModelTest.class);


  @Inject
  EmbeddedRedisServer embeddedRedisServer;

  @BeforeEach
  public void setup() {
    embeddedRedisServer.start();
  }
  @AfterEach
  public void tearDown() {
    embeddedRedisServer.stop();
  }


  @Inject
  private UsersRepository repository;



  @Test
  public void testingId() throws JsonProcessingException {

    UserModel userModel = new UserModel("one","one");
    UserModel userModel2= new UserModel("2","2");
    UserModel userModel3= new UserModel("3","3");
    UserModel userModel4= new UserModel("3","4");


    LOG.info("userModel:" + userModel);

    repository.save(userModel);
    repository.save(userModel2);
    repository.save(userModel3);
    repository.save(userModel4);


    Iterable<UserModel> all = repository.findAll();
    LOG.error("all" + Lists.newArrayList(all));
    assertThat(all).containsOnly(userModel,userModel2,userModel3,userModel4);

    var found = repository.findByUsername("one");

    LOG.error("oneByUsername:" + found);
    assertThat(found).isEqualTo(userModel);
  }

}
