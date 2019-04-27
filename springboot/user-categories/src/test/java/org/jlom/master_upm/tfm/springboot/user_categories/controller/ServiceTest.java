package org.jlom.master_upm.tfm.springboot.user_categories.controller;

import org.assertj.core.api.Assertions;
import org.jlom.master_upm.tfm.springboot.user_categories.controller.api.dtos.CatalogContent;
import org.jlom.master_upm.tfm.springboot.user_categories.controller.api.dtos.ContentStatus;
import org.jlom.master_upm.tfm.springboot.user_categories.controller.api.dtos.UserCategoriesServiceResponse;
import org.jlom.master_upm.tfm.springboot.user_categories.controller.api.dtos.UserCategoriesServiceResponseOK;
import org.jlom.master_upm.tfm.springboot.user_categories.controller.api.dtos.UserCategoriesServiceResponseOKCatalogContent;
import org.jlom.master_upm.tfm.springboot.user_categories.controller.api.dtos.UserCategoriesServiceResponseOKUserData;
import org.jlom.master_upm.tfm.springboot.user_categories.model.api.IUserCategoriesRepository;
import org.jlom.master_upm.tfm.springboot.user_categories.model.daos.ContentPackage;
import org.jlom.master_upm.tfm.springboot.user_categories.model.daos.UserCategory;
import org.jlom.master_upm.tfm.springboot.user_categories.model.daos.UserData;
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

import java.sql.Date;
import java.time.Instant;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.*;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
public class ServiceTest {

  private static final Logger LOG = LoggerFactory.getLogger(ServiceTest.class);

  @Autowired
  private UserCategoriesService service;

  @Autowired
  private IUserCategoriesRepository repository;


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

  @Test
  public void add_a_new_user() {

    final long userId = 1;
    final String categoryId = "cat_gold";
    final String pck_01 = "pck-01";
    final String pck_02 = "pck-02";
    final Set<String> packageIds = Set.of(pck_01,pck_02);


    UserCategory userCategory = UserCategory.builder()
            .name("gold category")
            .price(1.0)
            .tagId("tag_gold")
            .categoryId(categoryId)
            .build();

    ContentPackage contentPackage_one = ContentPackage.builder()
            .name("pack 01")
            .price(1.0)
            .tagsFilter(Set.of("action", "drama"))
            .packageId(pck_01)
            .build();

    ContentPackage contentPackage_two = ContentPackage.builder()
            .name("pack 02")
            .price(1.0)
            .tagsFilter(Set.of("humor", "comedy"))
            .packageId(pck_02)
            .build();

    repository.save(userCategory);
    repository.save(contentPackage_one);
    repository.save(contentPackage_two);


    UserCategoriesServiceResponse response = service.addUser(userId, categoryId, packageIds);

    assertThat(response).isInstanceOf(UserCategoriesServiceResponseOKUserData.class);

    UserCategoriesServiceResponseOKUserData responseOKUserData = (UserCategoriesServiceResponseOKUserData) response;

    UserData expectedUserData = UserData.builder()
            .userId(userId)
            .packageIds(packageIds)
            .categoryId(categoryId)
            .build();

    assertThat(responseOKUserData.getUserData()).isEqualTo(expectedUserData);


  }


  @Test
  public void remove_existing_user() {

    final long userId = 1;
    final String categoryId = "cat_gold";
    final String pck_01 = "pck-01";
    final String pck_02 = "pck-02";
    final Set<String> packageIds = Set.of(pck_01,pck_02);


    UserData existingUserData = UserData.builder()
            .userId(userId)
            .packageIds(packageIds)
            .categoryId(categoryId)
            .build();

    repository.save(existingUserData);


    UserCategoriesServiceResponse response = service.removeUser(userId);
    assertThat(response).isInstanceOf(UserCategoriesServiceResponseOKUserData.class);

    UserCategoriesServiceResponseOKUserData responseOk = (UserCategoriesServiceResponseOKUserData) response;
    assertThat(responseOk.getUserData()).isEqualTo(existingUserData);
  }

  @Test
  public void change_user_category_from_existing_user() {

    final long userId = 1;
    final String categoryId = "cat_gold";
    final String categoryId_two = "cat_silver";
    final String pck_01 = "pck-01";
    final String pck_02 = "pck-02";
    final Set<String> packageIds = Set.of(pck_01,pck_02);


    UserData existingUserData = UserData.builder()
            .userId(userId)
            .packageIds(packageIds)
            .categoryId(categoryId)
            .build();

    repository.save(existingUserData);

    UserCategory goldCategory = UserCategory.builder()
            .name("gold category")
            .price(1.0)
            .tagId("tag_gold")
            .categoryId(categoryId)
            .build();

    UserCategory silverCategory = UserCategory.builder()
            .name("silver category")
            .price(1.0)
            .tagId("tag_silver")
            .categoryId(categoryId_two)
            .build();

    repository.save(goldCategory);
    repository.save(silverCategory);


    UserCategoriesServiceResponse response = service.changeCategoryForUser(userId, categoryId_two);
    assertThat(response).isInstanceOf(UserCategoriesServiceResponseOKUserData.class);

    final UserData modifiedUserData = existingUserData;
    modifiedUserData.setCategoryId(categoryId_two);

    UserCategoriesServiceResponseOKUserData responseOKUserData = (UserCategoriesServiceResponseOKUserData) response;
    assertThat(responseOKUserData.getUserData()).isEqualTo(modifiedUserData);
  }

  @Test
  public void add_package_to_existing_user() {
    final long userId = 1;
    final String categoryId = "cat_gold";
    final String pck_01 = "pck-01";
    final String pck_02 = "pck-02";
    final Set<String> packageIds = Set.of(pck_01);
    final Set<String> modified_packageIds = Set.of(pck_01,pck_02);

    UserData existingUserData = UserData.builder()
            .userId(userId)
            .packageIds(packageIds)
            .categoryId(categoryId)
            .build();

    ContentPackage contentPackage_one = ContentPackage.builder()
            .name("pack 01")
            .price(1.0)
            .tagsFilter(Set.of("action", "drama"))
            .packageId(pck_01)
            .build();

    ContentPackage contentPackage_two = ContentPackage.builder()
            .name("pack 02")
            .price(1.0)
            .tagsFilter(Set.of("humor", "comedy"))
            .packageId(pck_02)
            .build();

    repository.save(existingUserData);
    repository.save(contentPackage_one);
    repository.save(contentPackage_two);


    UserCategoriesServiceResponse response = service.addPackageToUser(userId, Set.of(pck_02));
    assertThat(response).isInstanceOf(UserCategoriesServiceResponseOKUserData.class);

    final UserData modifiedUserData = existingUserData;
    modifiedUserData.setPackageIds(modified_packageIds);
    UserCategoriesServiceResponseOKUserData responseOKUserData = (UserCategoriesServiceResponseOKUserData) response;
    assertThat(responseOKUserData.getUserData()).isEqualTo(modifiedUserData);
  }

  @Test
  public void remove_package_to_existing_user() {
    final long userId = 1;
    final String categoryId = "cat_gold";
    final String pck_01 = "pck-01";
    final String pck_02 = "pck-02";
    final Set<String> modified_packageIds = Set.of(pck_01);
    final Set<String> packageIds = Set.of(pck_01,pck_02);

    UserData existingUserData = UserData.builder()
            .userId(userId)
            .packageIds(packageIds)
            .categoryId(categoryId)
            .build();

    ContentPackage contentPackage_one = ContentPackage.builder()
            .name("pack 01")
            .price(1.0)
            .tagsFilter(Set.of("action", "drama"))
            .packageId(pck_01)
            .build();

    ContentPackage contentPackage_two = ContentPackage.builder()
            .name("pack 02")
            .price(1.0)
            .tagsFilter(Set.of("humor", "comedy"))
            .packageId(pck_02)
            .build();

    repository.save(existingUserData);
    repository.save(contentPackage_one);
    repository.save(contentPackage_two);


    UserCategoriesServiceResponse response = service.removePackageFromUser(userId, Set.of(pck_02));
    assertThat(response).isInstanceOf(UserCategoriesServiceResponseOKUserData.class);

    final UserData modifiedUserData = existingUserData;
    modifiedUserData.setPackageIds(modified_packageIds);
    UserCategoriesServiceResponseOKUserData responseOKUserData = (UserCategoriesServiceResponseOKUserData) response;
    assertThat(responseOKUserData.getUserData()).isEqualTo(modifiedUserData);
  }

  @Test
  public void filter_packages_for_user() {

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

    UserCategoriesServiceResponse response = service.filter(userId, contents);
    assertThat(response).isInstanceOf(UserCategoriesServiceResponseOKCatalogContent.class);

    UserCategoriesServiceResponseOKCatalogContent responseOKCatalogContent = (UserCategoriesServiceResponseOKCatalogContent) response;
    assertThat(responseOKCatalogContent.getFilteredContent()).containsOnly(filteredContents.toArray(new CatalogContent[0]));
  }


  @Test
  public void get_packages_for_user() {
    final long userId = 1;


    final String gold_categoryId = "gold";
    final String gold_catTag ="tag_gold";
    final String action = "action";
    final String drama = "drama";

    final String pck_01 = "pck-01";
    final String pck_02 = "pck-02";

    final Set<String> packageIds = Set.of(pck_01,pck_02);

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

    List<ContentPackage> packagesForUser = service.getPackagesForUser(userId);

    assertThat(packagesForUser).containsOnly(pack_one,pack_two);
  }


  @Test
  public void get_category_for_user() {
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


    UserCategory categoryForUser = service.getCategoryForUser(userId);
    assertThat(categoryForUser).isEqualTo(gold_category);
  }


  @Test
  public void list_all_packages() {

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

    List<ContentPackage> contentPackages = service.listAllPackages();
    assertThat(contentPackages).containsOnly(pack_one,pack_two,pack_three);
  }

  @Test
  public void list_all_categories() {




    UserCategory gold_category = UserCategory.builder()
            .categoryId("gold")
            .price(100.0)
            .name("gold category")
            .tagId("tag_gold")
            .build();
    repository.save(gold_category);

    UserCategory silver_category = UserCategory.builder()
            .categoryId("silver")
            .price(100.0)
            .name("silver category")
            .tagId("tag_silver")
            .build();
    repository.save(silver_category);

    UserCategory bronze_category = UserCategory.builder()
            .categoryId("bronze")
            .price(100.0)
            .name("bronze category")
            .tagId("tag_bronze")
            .build();
    repository.save(bronze_category);

    List<UserCategory> userCategories = service.listAllCategories();
    assertThat(userCategories).containsOnly(gold_category,silver_category,bronze_category);
  }
}