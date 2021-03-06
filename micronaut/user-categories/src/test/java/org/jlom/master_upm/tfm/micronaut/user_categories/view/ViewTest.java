package org.jlom.master_upm.tfm.micronaut.user_categories.view;


import io.micronaut.http.HttpRequest;
import io.micronaut.http.client.HttpClient;
import io.micronaut.http.client.annotation.Client;
import io.micronaut.test.annotation.MicronautTest;
import org.apache.http.HttpResponse;
import org.assertj.core.api.Assertions;
import org.jlom.master_upm.tfm.micronaut.user_categories.controller.api.dtos.CatalogContent;
import org.jlom.master_upm.tfm.micronaut.user_categories.controller.api.dtos.ContentStatus;
import org.jlom.master_upm.tfm.micronaut.user_categories.model.api.IUserCategoriesRepository;
import org.jlom.master_upm.tfm.micronaut.user_categories.model.daos.ContentPackage;
import org.jlom.master_upm.tfm.micronaut.user_categories.model.daos.UserCategory;
import org.jlom.master_upm.tfm.micronaut.user_categories.model.daos.UserData;
import org.jlom.master_upm.tfm.micronaut.user_categories.utils.DtosTransformations;
import org.jlom.master_upm.tfm.micronaut.user_categories.utils.EmbeddedRedisServer;
import org.jlom.master_upm.tfm.micronaut.user_categories.view.api.dtos.InputCatalogContent;
import org.jlom.master_upm.tfm.micronaut.user_categories.view.api.dtos.InputContentPackage;
import org.jlom.master_upm.tfm.micronaut.user_categories.view.api.dtos.InputUserCategory;
import org.jlom.master_upm.tfm.micronaut.user_categories.view.api.dtos.InputUserCategoryData;
import org.jlom.master_upm.tfm.micronaut.user_categories.view.api.dtos.InputUserContentFiltered;
import org.jlom.master_upm.tfm.micronaut.user_categories.view.api.dtos.InputUserPackages;
import org.jlom.master_upm.tfm.micronaut.user_categories.view.api.dtos.InputUserWithCategory;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import java.io.IOException;
import java.time.Instant;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static org.jlom.master_upm.tfm.micronaut.user_categories.utils.JsonUtils.jsonToList;
import static org.jlom.master_upm.tfm.micronaut.user_categories.utils.JsonUtils.jsonToObject;

@MicronautTest
public class ViewTest {

  @Inject
  private IUserCategoriesRepository repository;

  private static final Logger LOG = LoggerFactory.getLogger(ViewTest.class);

  @Inject
  @Client("/")
  private HttpClient client;

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

    String body = client.toBlocking().retrieve(HttpRequest.POST("/categories/filter", input));


    LOG.error("jlom: response: " + body);

    List<InputCatalogContent> output = jsonToList(body, InputCatalogContent.class);

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
    final String sXX = "SXX";

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


    String body = client.toBlocking()
            .retrieve(HttpRequest.POST("/categories/user", input));

    LOG.error("jlom: response: " + body);

    InputUserCategoryData output = jsonToObject(body, InputUserCategoryData.class);

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
    final String sXX = "SXX";

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

    String body = client.toBlocking()
            .retrieve(HttpRequest.DELETE("/categories/user/" + userId));

    LOG.error("jlom: response: " + body);

    InputUserCategoryData output = jsonToObject(body, InputUserCategoryData.class);

    LOG.error("recv: " + output);

    Assertions.assertThat(output).isEqualTo(DtosTransformations.serviceToView(userData));
  }

  @Test
  public void change_category_for_user() throws IOException {

    final long userId = 1;

    final String gold_categoryId = "gold";
    final String gold_catTag ="tag_gold";
    final String silver_categoryId = "silver";
    final String silver_catTag ="tag_silver";


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

    UserCategory silver_category = UserCategory.builder()
            .categoryId(silver_categoryId)
            .price(100.0)
            .name("silver category")
            .tagId(silver_catTag)
            .build();
    repository.save(silver_category);

    InputUserCategoryData input = InputUserCategoryData.builder()
            .userId(String.valueOf(userId))
            .categoryId(silver_categoryId)
            .packageIds(packageIds)
            .build();

    String body = client.toBlocking()
            .retrieve(HttpRequest.POST("/categories/user/category", input));

    LOG.error("jlom: response: " + body);

    InputUserCategoryData output = jsonToObject(body, InputUserCategoryData.class);

    LOG.error("sent: " + input);
    LOG.error("recv: " + output);

    Assertions.assertThat(output).isEqualTo(input);
  }

  @Test
  public void add_packages_to_user() throws IOException {
    final long userId = 1;

    final String gold_categoryId = "gold";
    final String gold_catTag ="tag_gold";
    final String action = "action";
    final String drama = "drama";
    final String humor = "humor";
    final String sXX = "SXX";

    final String pck_01 = "pck-01";
    final String pck_02 = "pck-02";
    final String pck_03 = "pck-03";

    final Set<String> packageIds = Set.of(pck_01,pck_02);
    final Set<String> expected_packageIds = Set.of(pck_01,pck_02,pck_03);


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


    InputUserCategoryData input = InputUserCategoryData.builder()
            .userId(String.valueOf(userId))
            .packageIds(Set.of(pck_03))
            .categoryId(gold_categoryId)
            .build();

    String body = client.toBlocking().retrieve(HttpRequest.POST("/categories/user/add-packages",input));

    LOG.error("jlom: response: " + body);

    InputUserCategoryData output = jsonToObject(body, InputUserCategoryData.class);

    LOG.error("recv: " + output);

    userData.setPackageIds(expected_packageIds);
    Assertions.assertThat(output).isEqualTo(DtosTransformations.serviceToView(userData));
  }

  @Test
  public void remove_packages_to_user() throws IOException {
    final long userId = 1;

    final String gold_categoryId = "gold";
    final String gold_catTag ="tag_gold";
    final String action = "action";
    final String drama = "drama";
    final String humor = "humor";
    final String sXX = "SXX";

    final String pck_01 = "pck-01";
    final String pck_02 = "pck-02";
    final String pck_03 = "pck-03";

    final Set<String> expected_packageIds = Set.of(pck_01,pck_02);
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


    InputUserCategoryData input = InputUserCategoryData.builder()
            .userId(String.valueOf(userId))
            .packageIds(Set.of(pck_03))
            .categoryId(gold_categoryId)
            .build();

    String body = client.toBlocking()
            .retrieve(HttpRequest.POST("/categories/user/remove-packages",input));

    LOG.error("jlom: response: " + body);

    InputUserCategoryData output = jsonToObject(body, InputUserCategoryData.class);

    LOG.error("recv: " + output);


    userData.setPackageIds(expected_packageIds);
    Assertions.assertThat(output).isEqualTo(DtosTransformations.serviceToView(userData));
  }

  @Test
  public void get_packages_for_user() throws IOException {
    final long userId = 1;

    final String gold_categoryId = "gold";
    final String gold_catTag ="tag_gold";
    final String action = "action";
    final String drama = "drama";
    final String humor = "humor";
    final String sXX = "SXX";

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


    String body = client.toBlocking().retrieve(HttpRequest.GET("/categories/user/"+userId+"/packages"));
    LOG.error("jlom: response: " + body);

    InputUserPackages output = jsonToObject(body, InputUserPackages.class);

    LOG.error("recv: " + output.getPackages());

    List<InputContentPackage> expected = List.of(pack_one, pack_two, pack_three)
            .stream()
            .map(DtosTransformations::serviceToView)
            .collect(Collectors.toList());

    LOG.error("expected: " + expected);

    Assertions.assertThat(output.getPackages()).containsOnly(expected.toArray(new InputContentPackage[0]));
  }

  @Test
  public void get_category_for_user() throws IOException {
    final long userId = 1;

    final String gold_categoryId = "gold";
    final String gold_catTag ="tag_gold";

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


    String body = client.toBlocking().retrieve(HttpRequest.GET("/categories/user/"+userId+"/category"));
    LOG.error("jlom: response: " + body);

    InputUserWithCategory output = jsonToObject(body, InputUserWithCategory.class);
    LOG.error("recv: " + output);

    InputUserCategory expected_category= DtosTransformations.serviceToView(gold_category);
    InputUserWithCategory expected = InputUserWithCategory.builder()
            .userId(String.valueOf(userId))
            .userCategory(expected_category)
            .build();

    LOG.error("expected: " + expected);

    Assertions.assertThat(output).isEqualTo(expected);
  }

  @Test
  public void list_all_packages() throws IOException {
    final long userId = 1;

    final String gold_categoryId = "gold";
    final String gold_catTag ="tag_gold";
    final String action = "action";
    final String drama = "drama";
    final String humor = "humor";
    final String sXX = "SXX";

    final String pck_01 = "pck-01";
    final String pck_02 = "pck-02";
    final String pck_03 = "pck-03";


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


    String body = client.toBlocking().retrieve(HttpRequest.GET("/categories/packages"));
    LOG.error("jlom: response: " + body);

    List<InputContentPackage> output = jsonToList(body, InputContentPackage.class);
    LOG.error("recv: " + output);


    List<InputContentPackage> expected = List.of(pack_one, pack_two, pack_three)
            .stream()
            .map(DtosTransformations::serviceToView)
            .collect(Collectors.toList());


    LOG.error("expected: " + expected);

    Assertions.assertThat(output).containsOnly(expected.toArray(new InputContentPackage[0]));
  }

  @Test
  public void list_all_categories() throws IOException {

    UserCategory gold_category = UserCategory.builder()
            .categoryId("gold")
            .price(100.0)
            .name("gold category")
            .tagId("cat_gold")
            .build();
    repository.save(gold_category);

    UserCategory silver_category = UserCategory.builder()
            .categoryId("silver")
            .price(100.0)
            .name("silver category")
            .tagId("cat_silver")
            .build();
    repository.save(silver_category);

    UserCategory bronze_category = UserCategory.builder()
            .categoryId("bronze")
            .price(100.0)
            .name("bronze category")
            .tagId("cat_bronze")
            .build();
    repository.save(bronze_category);


    String body = client.toBlocking().retrieve(HttpRequest.GET("/categories/categories"));
    LOG.error("jlom: response: " + body);

    List<InputUserCategory> output = jsonToList(body, InputUserCategory.class);
    LOG.error("recv: " + output);

    List<InputUserCategory> expected = List.of(gold_category, silver_category, bronze_category)
            .stream()
            .map(DtosTransformations::serviceToView)
            .collect(Collectors.toList());

    LOG.error("expected: " + expected);

    Assertions.assertThat(output).containsOnly(expected.toArray(new InputUserCategory[0]));
  }
}
