package org.jlom.master_upm.tfm.springboot.user_categories.view;


import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.assertj.core.api.Assertions;
import org.jlom.master_upm.tfm.springboot.user_categories.controller.api.dtos.CatalogContent;
import org.jlom.master_upm.tfm.springboot.user_categories.controller.api.dtos.ContentStatus;
import org.jlom.master_upm.tfm.springboot.user_categories.model.api.IUserCategoriesRepository;
import org.jlom.master_upm.tfm.springboot.user_categories.model.daos.ContentPackage;
import org.jlom.master_upm.tfm.springboot.user_categories.model.daos.UserCategory;
import org.jlom.master_upm.tfm.springboot.user_categories.model.daos.UserData;
import org.jlom.master_upm.tfm.springboot.user_categories.utils.DtosTransformations;
import org.jlom.master_upm.tfm.springboot.user_categories.view.api.dtos.InputCatalogContent;
import org.jlom.master_upm.tfm.springboot.user_categories.view.api.dtos.InputUserCategoryData;
import org.jlom.master_upm.tfm.springboot.user_categories.view.api.dtos.InputUserContentFiltered;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import redis.embedded.RedisServer;

import java.io.IOException;
import java.time.Instant;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static org.jlom.master_upm.tfm.springboot.user_categories.utils.JsonUtils.ObjectToJson;
import static org.jlom.master_upm.tfm.springboot.user_categories.utils.JsonUtils.retrieveListOfResourcesFromResponse;
import static org.jlom.master_upm.tfm.springboot.user_categories.utils.JsonUtils.retrieveResourceFromResponse;
import static org.springframework.integration.graph.LinkNode.Type.output;


@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
public class ViewTest {

  @LocalServerPort
  private int port;

  @Autowired
  private IUserCategoriesRepository repository;

  private static final Logger LOG = LoggerFactory.getLogger(ViewTest.class);

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


  private HttpResponse getRestResponseTo(String resourceUri) throws IOException {
    HttpClient client = HttpClientBuilder.create().build();
    return client.execute(new HttpGet("http://localhost:"+port+resourceUri));
  }


  private HttpPost createPostRequest(String resourceUri) {
    HttpPost postRequest = new HttpPost("http://localhost:"+port+resourceUri);
    postRequest.setHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
    return postRequest;
  }

  private HttpDelete createDeleteRequest(String resourceUri) {
    HttpDelete deleteRequest = new HttpDelete("http://localhost:" + port + resourceUri);
    deleteRequest.setHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
    return deleteRequest;
  }

  private HttpResponse postMessageTo(String resourceUri) throws IOException {

    HttpClient httpClient = HttpClientBuilder.create().build();
    HttpPost postRequest = createPostRequest(resourceUri);
    return httpClient.execute(postRequest);
  }

  private HttpResponse postMessageTo(String resourceUri, Object body) throws IOException {

    HttpClient httpClient = HttpClientBuilder.create().build();
    HttpPost postRequest = createPostRequest(resourceUri);

    String jsonBody = ObjectToJson(body);
    StringEntity entityBody = new StringEntity(jsonBody);
    postRequest.setEntity(entityBody);

    return httpClient.execute(postRequest);
  }

  private HttpResponse deleteMessageTo(String resourceUri) throws IOException {

    HttpClient httpClient = HttpClientBuilder.create().build();
    HttpDelete deleteRequest = createDeleteRequest(resourceUri);
    return httpClient.execute(deleteRequest);
  }

//  final long userId = 1;
//  final Set<Long> devices = Set.of(1L,2L);
//
//  InputUserDevice inputUserDevice = InputUserDevice.builder()
//          .userId(String.valueOf(userId))
//          .devices(devices.stream().map(String::valueOf).collect(Collectors.toSet()))
//          .build();
//
//  HttpResponse httpResponse = postMessageTo("/dynamic-data/user-device/add-devices", inputUserDevice);
//
//
//  int statusCode = httpResponse.getStatusLine().getStatusCode();
//  assertThat(statusCode).isEqualTo(HttpStatus.OK.value());
//
//    LOG.error("jlom: response: " + httpResponse);
//
//  InputUserDevice actualInputUserDevice = retrieveResourceFromResponse(httpResponse, InputUserDevice.class);
//
//    LOG.debug("sent: " + inputUserDevice);
//    LOG.debug("recv: " + actualInputUserDevice);
//
//  assertThat(actualInputUserDevice).isEqualTo(inputUserDevice);

  @Test
  public void filtering() throws IOException {

    final long userId = 1;

    final String gold_categoryId = "gold";
    final String gold_catTag ="tag_gold";
    final String action = "action";
    final String drama = "drama";
    final String humor = "humor";
    final String silver_category = "cat_silver";
    final String sXX = "SXX";
    final String comedy = "comedy";

    final String pck_01 = "pck-01";
    final String pck_02 = "pck-02";
    final String pck_03 = "pck-03";

    final Set<String> packageIds = Set.of(pck_01,pck_02,pck_03);

    UserData userData = UserData.builder()
            .userId(userId)
            .categoryId(gold_categoryId)
            .packageIds(packageIds)
            .build();

    repository.save(userData);

    UserCategory gold_category = UserCategory.builder()
            .categoryId(gold_categoryId)
            .price(100.0)
            .name("gold category")
            .tagId(gold_catTag)
            .build();
    repository.save(gold_category);

    ContentPackage pack_one = ContentPackage.builder()
            .packageId(pck_01)
            .price(1.0)
            .name("pack_one")
            .tagsFilter(Set.of(action))
            .build();
    repository.save(pack_one);

    ContentPackage pack_two = ContentPackage.builder()
            .packageId(pck_02)
            .price(1.0)
            .name("pack_two")
            .tagsFilter(Set.of(drama))
            .build();
    repository.save(pack_two);

    ContentPackage pack_three = ContentPackage.builder()
            .packageId(pck_03)
            .price(1.0)
            .name("pack_three")
            .tagsFilter(Set.of(humor,sXX))
            .build();
    repository.save(pack_three);



    CatalogContent content_01 = CatalogContent.builder()
            .contentId(1)
            .available(Date.from(Instant.now()))
            .status(ContentStatus.AVAILABLE)
            .streamId(1)
            .title("content_01")
            .tags(Set.of(gold_catTag))
            .build();

    CatalogContent content_02 = CatalogContent.builder()
            .contentId(2)
            .available(Date.from(Instant.now()))
            .status(ContentStatus.AVAILABLE)
            .streamId(2)
            .title("content_02")
            .tags(Set.of(gold_catTag, action))
            .build();

    CatalogContent content_03 = CatalogContent.builder()
            .contentId(3)
            .available(Date.from(Instant.now()))
            .status(ContentStatus.AVAILABLE)
            .streamId(3)
            .title("content_03")
            .tags(Set.of(gold_catTag, drama, humor))
            .build();

    CatalogContent content_04 = CatalogContent.builder()
            .contentId(4)
            .available(Date.from(Instant.now()))
            .status(ContentStatus.AVAILABLE)
            .streamId(4)
            .title("content_04")
            .tags(Set.of(silver_category, action))
            .build();

    CatalogContent content_05 = CatalogContent.builder()
            .contentId(5)
            .available(Date.from(Instant.now()))
            .status(ContentStatus.AVAILABLE)
            .streamId(5)
            .title("content_05")
            .tags(Set.of(silver_category, drama))
            .build();

    CatalogContent content_06 = CatalogContent.builder()
            .contentId(6)
            .available(Date.from(Instant.now()))
            .status(ContentStatus.AVAILABLE)
            .streamId(6)
            .title("content_06")
            .tags(Set.of(silver_category, humor))
            .build();

    CatalogContent content_07 = CatalogContent.builder()
            .contentId(7)
            .available(Date.from(Instant.now()))
            .status(ContentStatus.AVAILABLE)
            .streamId(7)
            .title("content_07")
            .tags(Set.of(silver_category, humor, sXX))
            .build();

    CatalogContent content_08 = CatalogContent.builder()
            .contentId(8)
            .available(Date.from(Instant.now()))
            .status(ContentStatus.AVAILABLE)
            .streamId(8)
            .title("content_08")
            .tags(Set.of(silver_category, comedy, sXX))
            .build();

    List<CatalogContent> contents = List.of(content_01, content_02, content_03, content_04, content_05, content_06, content_07, content_08);
    List<CatalogContent> filteredContents = List.of(content_01, content_02, content_03, content_04, content_05, content_07);

    List<InputCatalogContent> inputCatalogContents = contents.stream()
            .map(DtosTransformations::serviceToView)
            .collect(Collectors.toList());


    InputUserContentFiltered input = InputUserContentFiltered.builder()
            .userId(String.valueOf(userId))
            .contents(inputCatalogContents)
            .build();

    HttpResponse httpResponse = postMessageTo("/stream-control/filter", input);

    int statusCode = httpResponse.getStatusLine().getStatusCode();
    Assertions.assertThat(statusCode).isEqualTo(HttpStatus.OK.value());

    LOG.error("jlom: response: " + httpResponse);

    List<InputCatalogContent> output = retrieveListOfResourcesFromResponse(httpResponse, InputCatalogContent.class);

    LOG.error("sent: " + input);
    LOG.error("recv: " + output);

    List<InputCatalogContent> outputFilteredContents = filteredContents.stream()
            .map(DtosTransformations::serviceToView)
            .collect(Collectors.toList());

    Assertions.assertThat(output).containsOnly(outputFilteredContents.toArray(new InputCatalogContent[0]));

  }


  @Test
  public void addUser() throws IOException {
    final long userId = 1;

    final String gold_categoryId = "gold";
    final String gold_catTag ="tag_gold";
    final String action = "action";
    final String drama = "drama";
    final String humor = "humor";
    final String silver_category = "cat_silver";
    final String sXX = "SXX";
    final String comedy = "comedy";

    final String pck_01 = "pck-01";
    final String pck_02 = "pck-02";
    final String pck_03 = "pck-03";

    final Set<String> packageIds = Set.of(pck_01,pck_02,pck_03);


    UserCategory gold_category = UserCategory.builder()
            .categoryId(gold_categoryId)
            .price(100.0)
            .name("gold category")
            .tagId(gold_catTag)
            .build();
    repository.save(gold_category);

    ContentPackage pack_one = ContentPackage.builder()
            .packageId(pck_01)
            .price(1.0)
            .name("pack_one")
            .tagsFilter(Set.of(action))
            .build();
    repository.save(pack_one);

    ContentPackage pack_two = ContentPackage.builder()
            .packageId(pck_02)
            .price(1.0)
            .name("pack_two")
            .tagsFilter(Set.of(drama))
            .build();
    repository.save(pack_two);

    ContentPackage pack_three = ContentPackage.builder()
            .packageId(pck_03)
            .price(1.0)
            .name("pack_three")
            .tagsFilter(Set.of(humor,sXX))
            .build();
    repository.save(pack_three);

    InputUserCategoryData input = InputUserCategoryData.builder()
            .userId(String.valueOf(userId))
            .categoryId(gold_categoryId)
            .packageIds(packageIds)
            .build();


    HttpResponse httpResponse = postMessageTo("/stream-control/user", input);

    int statusCode = httpResponse.getStatusLine().getStatusCode();
    Assertions.assertThat(statusCode).isEqualTo(HttpStatus.OK.value());

    LOG.error("jlom: response: " + httpResponse);

    InputUserCategoryData output = retrieveResourceFromResponse(httpResponse, InputUserCategoryData.class);

    LOG.error("sent: " + input);
    LOG.error("recv: " + output);


    Assertions.assertThat(output).isEqualTo(input);

  }

  @Test
  public void removeUser() throws IOException {
    final long userId = 1;

    final String gold_categoryId = "gold";
    final String gold_catTag ="tag_gold";
    final String action = "action";
    final String drama = "drama";
    final String humor = "humor";
    final String silver_category = "cat_silver";
    final String sXX = "SXX";
    final String comedy = "comedy";

    final String pck_01 = "pck-01";
    final String pck_02 = "pck-02";
    final String pck_03 = "pck-03";

    final Set<String> packageIds = Set.of(pck_01,pck_02,pck_03);


    UserData userData = UserData.builder()
            .userId(userId)
            .categoryId(gold_categoryId)
            .packageIds(packageIds)
            .build();

    repository.save(userData);

    UserCategory gold_category = UserCategory.builder()
            .categoryId(gold_categoryId)
            .price(100.0)
            .name("gold category")
            .tagId(gold_catTag)
            .build();
    repository.save(gold_category);

    ContentPackage pack_one = ContentPackage.builder()
            .packageId(pck_01)
            .price(1.0)
            .name("pack_one")
            .tagsFilter(Set.of(action))
            .build();
    repository.save(pack_one);

    ContentPackage pack_two = ContentPackage.builder()
            .packageId(pck_02)
            .price(1.0)
            .name("pack_two")
            .tagsFilter(Set.of(drama))
            .build();
    repository.save(pack_two);

    ContentPackage pack_three = ContentPackage.builder()
            .packageId(pck_03)
            .price(1.0)
            .name("pack_three")
            .tagsFilter(Set.of(humor,sXX))
            .build();
    repository.save(pack_three);


    HttpResponse httpResponse = deleteMessageTo("/stream-control/user/"+userId);

    int statusCode = httpResponse.getStatusLine().getStatusCode();
    Assertions.assertThat(statusCode).isEqualTo(HttpStatus.OK.value());

    LOG.error("jlom: response: " + httpResponse);

    InputUserCategoryData output = retrieveResourceFromResponse(httpResponse, InputUserCategoryData.class);

    LOG.error("recv: " + output);


    Assertions.assertThat(output).isEqualTo(DtosTransformations.serviceToView(userData));

  }
//======================================================================================================================
//  @RequestMapping(
//          value = "/user",
//          method = RequestMethod.DELETE,
//          produces = {MediaType.APPLICATION_JSON_VALUE, APPLICATION_JSON_PROBLEM_VALUE}
//  )
//  ResponseEntity<?> removeUser(HttpServletRequest request, @Valid @RequestBody InputUserCategoryData inputUserCategoryData);
//
//======================================================================================================================
//  @RequestMapping(
//          value = "/user/category",
//          method = RequestMethod.POST,
//          produces = {MediaType.APPLICATION_JSON_VALUE, APPLICATION_JSON_PROBLEM_VALUE}
//  )
//  ResponseEntity<?> changeCategory(HttpServletRequest request, @Valid @RequestBody InputUserCategoryData inputUserCategoryData);
//
//======================================================================================================================
//  @RequestMapping(
//          value = "/user/packages",
//          method = RequestMethod.POST,
//          produces = {MediaType.APPLICATION_JSON_VALUE, APPLICATION_JSON_PROBLEM_VALUE}
//  )
//  ResponseEntity<?> addPackages(HttpServletRequest request, @Valid @RequestBody InputUserCategoryData streamData);
//
// ======================================================================================================================
//  @RequestMapping(
//          value = "/user/packages",
//          method = RequestMethod.DELETE,
//          produces = {MediaType.APPLICATION_JSON_VALUE, APPLICATION_JSON_PROBLEM_VALUE}
//  )
//  ResponseEntity<?> removePackages(HttpServletRequest request, @Valid @RequestBody InputUserCategoryData streamData);
// ======================================================================================================================
//  @RequestMapping(
//          value = "/user/{userId}/packages",
//          method = RequestMethod.GET,
//          produces = {MediaType.APPLICATION_JSON_VALUE, APPLICATION_JSON_PROBLEM_VALUE}
//  )
//  ResponseEntity<?> listPackagesForUser(HttpServletRequest request
//          , @PathVariable(value = "userId") long userId);
//======================================================================================================================
//
//  @RequestMapping(
//          value = "/user/{userId}/category",
//          method = RequestMethod.GET,
//          produces = {MediaType.APPLICATION_JSON_VALUE, APPLICATION_JSON_PROBLEM_VALUE}
//  )
//  ResponseEntity<?> getCategoryForUser(HttpServletRequest request
//          , @PathVariable(value = "userId") long userId);
//
// ======================================================================================================================
//
//  @RequestMapping(
//          value = "/packages",
//          method = RequestMethod.GET,
//          produces = {MediaType.APPLICATION_JSON_VALUE, APPLICATION_JSON_PROBLEM_VALUE}
//  )
//  ResponseEntity<?> listAllPackages(HttpServletRequest request);
//
// ======================================================================================================================
//  @RequestMapping(
//          value = "/categories",
//          method = RequestMethod.GET,
//          produces = {MediaType.APPLICATION_JSON_VALUE, APPLICATION_JSON_PROBLEM_VALUE}
//  )
//  ResponseEntity<?> listAllCategories(HttpServletRequest request);
//======================================================================================================================
//======================================================================================================================
//======================================================================================================================
//======================================================================================================================
//  @Test
//  public void given_NoStreamingRunning_when_PlayANewStream_then_AllShouldWork() throws JsonProcessingException {
//
//    final long userId = 1;
//    final long streamId = 1;
//    final long deviceId = 1;
//
//    final String uri = String.format("/dynamic-data/user-device/device/%d",deviceId);
//
//
//    InputUserDevice userDevice = InputUserDevice.builder()
//            .userId(String.valueOf(userId))
//            .devices(Set.of(String.valueOf(deviceId)))
//            .build();
//
//    stubFor(get(urlEqualTo(uri))
//            //.withHeader("Accept", equalTo(MediaType.APPLICATION_JSON_VALUE))
//            .willReturn(aResponse()
//                    .withStatus(HttpStatus.OK.value())
//                    .withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
//                    .withBody(JsonUtils.ObjectToJson(userDevice))));
//
//
//    StreamControlReturnValue returnValue = view.play(streamId, deviceId);
//
//    assertThat(returnValue).isInstanceOf(StreamControlReturnValueOk.class);
//    StreamControlReturnValueOk returnValueOk = ((StreamControlReturnValueOk) returnValue);
//
//    assertThat(returnValueOk.getUserId()).isEqualTo(String.valueOf(userId));
//    assertThat(returnValueOk.getDeviceId()).isEqualTo(String.valueOf(deviceId));
//    assertThat(returnValueOk.getStreamId()).isEqualTo(String.valueOf(streamId));
//  }
//
//  @Test
//  public void given_AStreamingRunning_when_PlayANewStream_then_AllShould_NOT_Work() throws JsonProcessingException {
//
//    final long userId = 1;
//    final long streamId = 1;
//    final long anotherStreamId = 2;
//    final long deviceId = 1;
//    final String uri = String.format("/dynamic-data/user-device/device/%d",deviceId);
//
//    InputUserDevice userDevice = InputUserDevice.builder()
//            .userId(String.valueOf(userId))
//            .devices(Set.of(String.valueOf(deviceId)))
//            .build();
//
//    stubFor(get(urlEqualTo(uri))
//            //.withHeader("Accept", equalTo(MediaType.APPLICATION_JSON_VALUE))
//            .willReturn(aResponse()
//                    .withStatus(HttpStatus.OK.value())
//                    .withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
//                    .withBody(JsonUtils.ObjectToJson(userDevice))));
//
//    StreamControlData alreadyRunning = addCheckedStreamControlData(userId,
//            deviceId,
//            anotherStreamId,
//            StreamStatus.RUNNING,
//            false);
//
//
//    StreamControlReturnValue returnValue = view.play(streamId, deviceId);
//    LOG.error("returnValue: " + returnValue);
//
//    assertThat(returnValue).isInstanceOf(StreamControlReturnValueError.class);
//    StreamControlReturnValueError invalidResponse = (StreamControlReturnValueError) returnValue;
//    assertThat(invalidResponse.getMessage()).startsWith("Invalid");
//    assertThat(invalidResponse.getErrorCode()).isEqualTo(StreamControlReturnValueError.ErrorCode.INVALID_PARAMETER);
//
//  }
//
//  @Test
//  public void given_AStreamingRunning_when_StopAExistingStream_then_AllShouldWork() {
//
//    final long userId = 1;
//    final long streamId = 1;
//    final long anotherStreamId = 1;
//    final long deviceId = 1;
//
//    StreamControlData alreadyRunning = addCheckedStreamControlData(userId,
//            deviceId,
//            anotherStreamId,
//            StreamStatus.RUNNING,
//            false);
//
//    StreamControlReturnValue returnValue = view.stop(deviceId);
//
//    assertThat(returnValue).isInstanceOf(StreamControlReturnValueOk.class);
//    StreamControlReturnValueOk returnValueOk = (StreamControlReturnValueOk) returnValue;
//
//    assertThat(returnValueOk.getUserId()).isEqualTo(String.valueOf(userId));
//    assertThat(returnValueOk.getDeviceId()).isEqualTo(String.valueOf(deviceId));
//    assertThat(returnValueOk.getStreamId()).isEqualTo(String.valueOf(streamId));
//
//  }
//
//  @Test
//  public void given_NoStreamingRunning_when_StopANonExistingStream_then_AllShould_NOT_Work() throws JsonProcessingException {
//
//    final long deviceId = 1;
//
//    StreamControlReturnValue returnValue = view.stop(deviceId);
//    LOG.error("returnValue: " + returnValue);
//
//    assertThat(returnValue).isInstanceOf(StreamControlReturnValueError.class);
//    StreamControlReturnValueError invalidResponse = (StreamControlReturnValueError) returnValue;
//    assertThat(invalidResponse.getMessage()).startsWith("Invalid");
//    assertThat(invalidResponse.getErrorCode()).isEqualTo(StreamControlReturnValueError.ErrorCode.INVALID_PARAMETER);
//  }
//
//  @Test
//  public void given_AStreamingRunning_when_PauseAExistingStream_then_AllShouldWork() {
//
//    final long userId = 1;
//    final long deviceId = 1;
//    final long streamId = 1;
//
//    StreamControlData alreadyRunning = addCheckedStreamControlData(userId,
//            deviceId,
//            streamId,
//            StreamStatus.RUNNING,
//            false);
//
//    StreamControlReturnValue returnValue = view.pause(deviceId);
//
//    assertThat(returnValue).isInstanceOf(StreamControlReturnValueOk.class);
//    StreamControlReturnValueOk returnValueOk = (StreamControlReturnValueOk) returnValue;
//
//    assertThat(returnValueOk.getUserId()).isEqualTo(String.valueOf(userId));
//    assertThat(returnValueOk.getDeviceId()).isEqualTo(String.valueOf(deviceId));
//    assertThat(returnValueOk.getStreamId()).isEqualTo(String.valueOf(streamId));
//
//  }
//
//  @Test
//  public void given_NoStreamingRunning_when_PauseANonExistingStream_then_AllShould_NOT_Work() throws JsonProcessingException {
//
//    final long deviceId = 1;
//
//    StreamControlReturnValue returnValue = view.pause(deviceId);
//
//    assertThat(returnValue).isInstanceOf(StreamControlReturnValueError.class);
//    StreamControlReturnValueError invalidResponse = (StreamControlReturnValueError) returnValue;
//    assertThat(invalidResponse.getMessage()).startsWith("Invalid");
//    assertThat(invalidResponse.getErrorCode()).isEqualTo(StreamControlReturnValueError.ErrorCode.INVALID_PARAMETER);
//  }
}
