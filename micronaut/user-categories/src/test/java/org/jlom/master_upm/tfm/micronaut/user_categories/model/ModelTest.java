package org.jlom.master_upm.tfm.micronaut.user_categories.model;


import io.micronaut.test.annotation.MicronautTest;
import org.assertj.core.api.Assertions;
import org.jlom.master_upm.tfm.micronaut.user_categories.model.api.IUserCategoriesRepository;
import org.jlom.master_upm.tfm.micronaut.user_categories.model.daos.ContentPackage;
import org.jlom.master_upm.tfm.micronaut.user_categories.model.daos.UserCategory;
import org.jlom.master_upm.tfm.micronaut.user_categories.model.daos.UserData;
import org.jlom.master_upm.tfm.micronaut.user_categories.utils.CollectionUtilities;
import org.jlom.master_upm.tfm.micronaut.user_categories.utils.EmbeddedRedisServer;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@MicronautTest
public class ModelTest {

  private static final Logger LOG = LoggerFactory.getLogger(ModelTest.class);

  @Inject
  private IUserCategoriesRepository repository;

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
  public void save_a_new_category() throws IOException {

    UserCategory goldCategory = UserCategory.builder()
            .categoryId("gold")
            .name("Gold Category")
            .price(100.0)
            .tagId("cat_gold")
            .build();

    repository.save(goldCategory);

    Optional<UserCategory> categoryById = repository.findCategoryById(goldCategory.getCategoryId());
    Assertions.assertThat(categoryById).contains(goldCategory);

  }

  @Test
  public void save_a_new_package() throws IOException {

    ContentPackage pack_01 = ContentPackage.builder()
            .name("pack_01")
            .packageId("pck-01")
            .price(10.0)
            .tagsFilter(Set.of("pck-01:enable"))
            .build();


    repository.save(pack_01);


    Optional<ContentPackage> packageById = repository.findPackageById(pack_01.getPackageId());
    Assertions.assertThat(packageById).contains(pack_01);

  }

  @Test
  public void addtags_to_existing_package() throws IOException {

    ContentPackage pack_01 = ContentPackage.builder()
            .name("pack_01")
            .packageId("pck-01")
            .price(10.0)
            .tagsFilter(Set.of("pck-01:enable"))
            .build();
    final Set<String> newTags = Set.of("action", "drama");
    final Set<String> expectedTags = Set.of("action", "drama", "pck-01:enable");


    repository.save(pack_01);
    Assertions.assertThat(repository.findPackageById(pack_01.getPackageId())).contains(pack_01);


    ContentPackage pack_01_updated = repository.addTags(pack_01.getPackageId(), newTags);

    var expected_package = pack_01;
    expected_package.setTagsFilter(expectedTags);

    Assertions.assertThat(pack_01_updated).isEqualTo(expected_package);
  }

  @Test
  public void removetags_to_existing_package() throws IOException{

    ContentPackage pack_01 = ContentPackage.builder()
            .name("pack_01")
            .packageId("pck-01")
            .price(10.0)
            .tagsFilter(Set.of("action", "drama", "pck-01:enable"))
            .build();
    final Set<String> tags2remove = Set.of("action", "drama");
    final Set<String> expectedTags = Set.of("pck-01:enable");


    repository.save(pack_01);
    Assertions.assertThat(repository.findPackageById(pack_01.getPackageId())).contains(pack_01);


    ContentPackage pack_01_updated = repository.removeTags(pack_01.getPackageId(), tags2remove);

    var expected_package = pack_01;
    expected_package.setTagsFilter(expectedTags);

    Assertions.assertThat(pack_01_updated).isEqualTo(expected_package);
  }

  @Test
  public void save_new_UserData() throws IOException{

    final long userId = 1;
    final String categoryId = "cat_gold";
    final Set<String> packageIds = Set.of("pck-01");

    UserData userData = UserData.builder()
            .userId(userId)
            .categoryId(categoryId)
            .packageIds(packageIds)
            .build();

    repository.save(userData);
    Assertions.assertThat(repository.findUserById(userId)).contains(userData);
  }

  @Test
  public void addPackage_to_existing_user() throws IOException{

    final long userId = 1;
    final String categoryId = "cat_gold";
    final Set<String> packageIds = Set.of("pck-01");
    final String pack_02 = "pck-02";

    UserData userData = UserData.builder()
            .userId(userId)
            .categoryId(categoryId)
            .packageIds(packageIds)
            .build();
    repository.save(userData);
    Assertions.assertThat(repository.findUserById(userId)).contains(userData);


    UserData newUserData = repository.addPackageToUser(userId, pack_02);

    Set<String> union = CollectionUtilities.union(userData.getPackageIds(), Set.of(pack_02));
    userData.setPackageIds(union);

    Assertions.assertThat(newUserData).isEqualTo(userData);


  }

  @Test
  public void removePackage_to_existing_user() throws IOException{

    final long userId = 1;
    final String categoryId = "cat_gold";
    final Set<String> packageIds = Set.of("pck-01", "pck-02");
    final String pack_02 = "pck-02";

    UserData userData = UserData.builder()
            .userId(userId)
            .categoryId(categoryId)
            .packageIds(packageIds)
            .build();
    repository.save(userData);
    Assertions.assertThat(repository.findUserById(userId)).contains(userData);


    UserData newUserData = repository.removePackageFromUser(userId, pack_02);

    Set<String> union = CollectionUtilities.difference(userData.getPackageIds(), Set.of(pack_02));
    userData.setPackageIds(union);

    Assertions.assertThat(newUserData).isEqualTo(userData);
  }

  @Test
  public void get_packages_for_user() throws IOException{

    final long userId = 1;
    final String categoryId = "cat_gold";
    final String pck_01 = "pck-01";
    final Set<String> packageIds = Set.of(pck_01);

    UserData userData = UserData.builder()
            .userId(userId)
            .categoryId(categoryId)
            .packageIds(packageIds)
            .build();

    ContentPackage contentPackage = ContentPackage.builder()
            .packageId(pck_01)
            .price(10.0)
            .name("pack-01")
            .tagsFilter(Set.of("action", "drama"))
            .build();

    repository.save(contentPackage);
    Assertions.assertThat(repository.findPackageById(pck_01)).contains(contentPackage);

    repository.save(userData);
    Assertions.assertThat(repository.findUserById(userId)).contains(userData);


    List<ContentPackage> packages = repository.getPackages(userId);

    Assertions.assertThat(packages).containsOnly(contentPackage);


  }

  @Test
  public void get_category_for_user() throws IOException{

    final long userId = 1;
    final String categoryId = "cat_gold";
    final String pck_01 = "pck-01";
    final Set<String> packageIds = Set.of(pck_01);

    UserData userData = UserData.builder()
            .userId(userId)
            .categoryId(categoryId)
            .packageIds(packageIds)
            .build();

    UserCategory userCategory = UserCategory.builder()
            .categoryId(categoryId)
            .tagId("cat_gold")
            .price(1.0)
            .name("gold catergory")
            .build();

    repository.save(userData);
    Assertions.assertThat(repository.findUserById(userId)).contains(userData);

    repository.save(userCategory);
    Assertions.assertThat(repository.findCategoryById(categoryId)).contains(userCategory);


    UserCategory category = repository.getCategory(userId);
    Assertions.assertThat(category).isEqualTo(userCategory);
  }


  @Test
  public void listAllPackages() throws IOException{

    ContentPackage contentPackage_one = ContentPackage.builder()
            .packageId("pck-01")
            .price(10.0)
            .name("pack-01")
            .tagsFilter(Set.of("action", "drama"))
            .build();

    ContentPackage contentPackage_two = ContentPackage.builder()
            .packageId("pck-02")
            .price(10.0)
            .name("pack-01")
            .tagsFilter(Set.of("comedy"))
            .build();

    repository.save(contentPackage_one);
    Assertions.assertThat(repository.findPackageById(contentPackage_one.getPackageId())).contains(contentPackage_one);

    repository.save(contentPackage_two);
    Assertions.assertThat(repository.findPackageById(contentPackage_two.getPackageId())).contains(contentPackage_two);


    List<ContentPackage> contentPackages = repository.listPackages();
    Assertions.assertThat(contentPackages).containsOnly(contentPackage_one,contentPackage_two);
  }

  @Test
  public void listAllCategories() throws IOException{

    UserCategory userCategory_gold = UserCategory.builder()
            .categoryId("gold")
            .tagId("cat_gold")
            .price(1.0)
            .name("gold category")
            .build();

    UserCategory userCategory_silver = UserCategory.builder()
            .categoryId("silver")
            .tagId("cat_silver")
            .price(2.0)
            .name("silver category")
            .build();


    repository.save(userCategory_gold);
    repository.save(userCategory_silver);


    List<UserCategory> userCategories = repository.listCategories();
    Assertions.assertThat(userCategories).containsOnly(userCategory_gold,userCategory_silver);

  }
}
