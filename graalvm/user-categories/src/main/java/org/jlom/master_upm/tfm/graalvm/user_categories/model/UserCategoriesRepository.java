package org.jlom.master_upm.tfm.graalvm.user_categories.model;



import com.fasterxml.jackson.core.JsonProcessingException;
import io.lettuce.core.api.StatefulRedisConnection;
import io.lettuce.core.api.sync.RedisCommands;
import org.jlom.master_upm.tfm.graalvm.user_categories.model.api.IUserCategoriesRepository;
import org.jlom.master_upm.tfm.graalvm.user_categories.model.daos.ContentPackage;
import org.jlom.master_upm.tfm.graalvm.user_categories.model.daos.UserCategory;
import org.jlom.master_upm.tfm.graalvm.user_categories.model.daos.UserData;
import org.jlom.master_upm.tfm.graalvm.user_categories.utils.CollectionUtilities;
import org.jlom.master_upm.tfm.graalvm.user_categories.utils.JsonUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import javax.inject.Singleton;
import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import static org.jlom.master_upm.tfm.graalvm.user_categories.utils.JsonUtils.ObjectToJson;
import static org.jlom.master_upm.tfm.graalvm.user_categories.utils.JsonUtils.jsonToObject;

@Singleton
public class UserCategoriesRepository implements IUserCategoriesRepository {
  private static final Logger LOG = LoggerFactory.getLogger(UserCategoriesRepository.class);

  private static final String USERCATEGORY_DATA_KEY = "UserCategory";
  private static final String CATEGORY_COLLECTION = "Cat";
  private static final String PACKAGE_COLLECTION = "Pack";
  private static final String USERDATA_COLLECTION = "User";

  private StatefulRedisConnection<String, String> connection;

  public UserCategoriesRepository(StatefulRedisConnection<String, String> connection) {
    this.connection = connection;
  }


  private String buildCollectionKey(String collection, String id) {
    return String.format("%s:%s:%s",
            USERCATEGORY_DATA_KEY,
            collection,
            id
            );
  }
  private String buildCollectionKey(String collection, long id) {
    return buildCollectionKey(collection,String.valueOf(id));
  }

  @Override
  public void save(UserCategory category) throws IOException {
    LOG.info("UserCategoriesRepository::save: " + category);

    RedisCommands<String, String> redisApi = connection.sync();

    String jsonData = JsonUtils.ObjectToJson(category);
    redisApi.set(buildCollectionKey(CATEGORY_COLLECTION,category.getCategoryId()), jsonData);
  }

  @Override
  public Optional<UserCategory> findCategoryById(String categoryId) throws IOException{
    LOG.info("UserCategoriesRepository::findCategoryById: " + categoryId);
    RedisCommands<String, String> redisApi = connection.sync();

    String jsonData = redisApi.get(buildCollectionKey(CATEGORY_COLLECTION, categoryId));
    if(jsonData == null) {
      return Optional.empty();
    }
    UserCategory userCategory = jsonToObject(jsonData, UserCategory.class);
    return Optional.ofNullable(userCategory);
  }

  @Override
  public Optional<ContentPackage> findPackageById(String packageId) throws IOException {
    LOG.info("UserCategoriesRepository::findPackageById: " + packageId);
    RedisCommands<String, String> redisApi = connection.sync();

    String jsonData = redisApi.get(buildCollectionKey(PACKAGE_COLLECTION, packageId));
    if(jsonData == null) {
      return Optional.empty();
    }
    ContentPackage contentPackage = jsonToObject(jsonData, ContentPackage.class);
    return Optional.ofNullable(contentPackage);
  }

  @Override
  public Optional<UserData> findUserById(long userId) throws IOException {
    LOG.info("UserCategoriesRepository::findUserById: " + userId);
    RedisCommands<String, String> redisApi = connection.sync();

    String jsonData = redisApi.get(buildCollectionKey(USERDATA_COLLECTION, userId));
    if(jsonData == null) {
      return Optional.empty();
    }
    UserData userData = jsonToObject(jsonData, UserData.class);
    return Optional.ofNullable(userData);
  }

  @Override
  public void save(ContentPackage contentPackage) throws JsonProcessingException {
    LOG.info("UserCategoriesRepository::save: " + contentPackage);
    RedisCommands<String, String> redisApi = connection.sync();

    String jsonData = ObjectToJson(contentPackage);

    redisApi.set(buildCollectionKey(PACKAGE_COLLECTION, contentPackage.getPackageId()), jsonData);
  }

  @Override
  public ContentPackage addTags(String packageId, Set<String> tags) throws IOException {
    LOG.info("UserCategoriesRepository::addTags:  packageId:" + packageId + ",tags:" + tags);
    RedisCommands<String, String> redisApi = connection.sync();

    String jsonData = redisApi.get(buildCollectionKey(PACKAGE_COLLECTION, packageId));
    if (null == jsonData) {
      return null;
    }

    ContentPackage contentPackage = jsonToObject(jsonData, ContentPackage.class);

    Set<String> newTags = CollectionUtilities.union(contentPackage.getTagsFilter(), tags);
    contentPackage.setTagsFilter(newTags);

    redisApi.set(buildCollectionKey(PACKAGE_COLLECTION, contentPackage.getPackageId()),
            ObjectToJson(contentPackage));
    return contentPackage;
  }

  @Override
  public ContentPackage removeTags(String packageId, Set<String> tags) throws IOException {
    LOG.info("UserCategoriesRepository::removeTags:  packageId:" + packageId + ",tags:" + tags);
    RedisCommands<String, String> redisApi = connection.sync();

    String jsonData = redisApi.get(buildCollectionKey(PACKAGE_COLLECTION, packageId));
    if (null == jsonData) {
      return null;
    }
    ContentPackage contentPackage = jsonToObject(jsonData, ContentPackage.class);

    Set<String> newTags = CollectionUtilities.difference(contentPackage.getTagsFilter(), tags);
    contentPackage.setTagsFilter(newTags);

    redisApi.set(buildCollectionKey(PACKAGE_COLLECTION, contentPackage.getPackageId()),
            ObjectToJson(contentPackage));

    return contentPackage;
  }

  @Override
  public void save(UserData userData) throws IOException {
    LOG.info("UserCategoriesRepository::save: userData" + userData);
    RedisCommands<String, String> redisApi = connection.sync();

    redisApi.set(buildCollectionKey(USERDATA_COLLECTION,userData.getUserId()), ObjectToJson(userData));
  }

  @Override
  public boolean update(UserData userData) {
    return false;
  }

  @Override
  public UserData addPackageToUser(long userId, String packageId) throws IOException {
    LOG.info("UserCategoriesRepository::addPackageToUser: userId" + userId + ",packageId:" + packageId);
    RedisCommands<String, String> redisApi = connection.sync();

    String jsonData = redisApi.get(buildCollectionKey(USERDATA_COLLECTION, userId));
    if (null == jsonData) {
      return null;
    }
    UserData userData = jsonToObject(jsonData, UserData.class);

    Set<String> setOfPackId = new HashSet<>();
    setOfPackId.add(packageId);
    userData.setPackageIds(CollectionUtilities.union(userData.getPackageIds()
            , setOfPackId) );


    redisApi.set(buildCollectionKey(USERDATA_COLLECTION,userId),ObjectToJson(userData));

    return userData;
  }

  @Override
  public UserData removePackageFromUser(long userId, String packageId) throws IOException{
    LOG.info("UserCategoriesRepository::removePackageFromUser: userId" + userId + ",packageId:" + packageId);
    RedisCommands<String, String> redisApi = connection.sync();

    String jsonData = redisApi.get(buildCollectionKey(USERDATA_COLLECTION, userId));
    if (null == jsonData) {
      return null;
    }
    UserData userData = jsonToObject(jsonData, UserData.class);

    Set<String> setOfPackId = new HashSet<>();
    setOfPackId.add(packageId);

    userData.setPackageIds(
            CollectionUtilities.difference(userData.getPackageIds()
                    , setOfPackId)
    );

    redisApi.set(buildCollectionKey(USERDATA_COLLECTION,userId),ObjectToJson(userData));

    return userData;
  }

  @Override
  public List<ContentPackage> getPackages(long userId) throws IOException{
    LOG.info("UserCategoriesRepository::getPackages: userId" + userId );
    RedisCommands<String, String> redisApi = connection.sync();

    String jsonData = redisApi.get(buildCollectionKey(USERDATA_COLLECTION, userId));
    if (null == jsonData) {
      return null;
    }
    UserData userData = jsonToObject(jsonData, UserData.class);

    return userData.getPackageIds().stream()
            .map(id -> redisApi.get(buildCollectionKey(PACKAGE_COLLECTION, id)))
            .map(jsonObj -> {
              try {
                return jsonToObject(jsonObj, ContentPackage.class);
              } catch (IOException e) {
                e.printStackTrace();
                return null;
              }
            })
            .filter(Objects::nonNull)
            .collect(Collectors.toList());
  }

  @Override
  public UserCategory getCategory(long userId) throws IOException{
    LOG.info("UserCategoriesRepository::getCategory: userId" + userId );
    RedisCommands<String, String> redisApi = connection.sync();

    String jsonData = redisApi.get(buildCollectionKey(USERDATA_COLLECTION, userId));
    if (null == jsonData) {
      return null;
    }
    UserData userData = jsonToObject(jsonData, UserData.class);

    jsonData = redisApi.get(buildCollectionKey(CATEGORY_COLLECTION, userData.getCategoryId()));
    if (null == jsonData) {
      return null;
    }
    return jsonToObject(jsonData, UserCategory.class);
  }

  @Override
  public List<ContentPackage> listPackages() {
    LOG.info("UserCategoriesRepository::listPackages" );
    RedisCommands<String, String> redisApi = connection.sync();

    return redisApi.keys(buildCollectionKey(PACKAGE_COLLECTION,"*"))
            .stream()
            .map(redisApi::get)
            .filter(Objects::nonNull)
            .map( jsonData -> {
              try {
                return jsonToObject(jsonData, ContentPackage.class);
              } catch (IOException e) {
                e.printStackTrace();
                return null;
              }
            })
            .filter(Objects::nonNull)
            .collect(Collectors.toList());
  }

  @Override
  public List<UserCategory> listCategories() {
    LOG.info("UserCategoriesRepository::listPackages" );
    RedisCommands<String, String> redisApi = connection.sync();

    return redisApi.keys(buildCollectionKey(CATEGORY_COLLECTION,"*"))
            .stream()
            .map(redisApi::get)
            .filter(Objects::nonNull)
            .map( jsonData -> {
              try {
                return jsonToObject(jsonData, UserCategory.class);
              } catch (IOException e) {
                e.printStackTrace();
                return null;
              }
            })
            .filter(Objects::nonNull)
            .collect(Collectors.toList());
  }
}
