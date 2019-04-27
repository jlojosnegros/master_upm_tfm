package org.jlom.master_upm.tfm.springboot.user_categories.model;


import org.jlom.master_upm.tfm.springboot.user_categories.model.api.IUserCategoriesRepository;
import org.jlom.master_upm.tfm.springboot.user_categories.model.daos.ContentPackage;
import org.jlom.master_upm.tfm.springboot.user_categories.model.daos.UserCategory;
import org.jlom.master_upm.tfm.springboot.user_categories.model.daos.UserData;
import org.jlom.master_upm.tfm.springboot.user_categories.utils.CollectionUtilities;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Repository
public class UserCategoriesRepository implements IUserCategoriesRepository {

  private static final String USERCATEGORY_DATA_KEY = "UserCategory";
  private static final String CATEGORY_COLLECTION = "Cat";
  private static final String PACKAGE_COLLECTION = "Pack";
  private static final String USERDATA_COLLECTION = "User";


  @Resource(name="redisTemplate")
  private HashOperations<String, Long, UserData> userDataHashOperations;

  @Resource(name="redisTemplate")
  private HashOperations<String,String, UserCategory> userCategoryHashOperations;

  @Resource(name="redisTemplate")
  private HashOperations<String,String, ContentPackage> contentPackageHashOperations;


  private static final Logger LOG = LoggerFactory.getLogger(UserCategoriesRepository.class);

  private String buildCollectionKey(String collection) {
    return String.format("%s:%s",
            USERCATEGORY_DATA_KEY,
            collection
            );
  }

  @Override
  public void save(UserCategory category) {
    userCategoryHashOperations.put(buildCollectionKey(CATEGORY_COLLECTION)
            , category.getCategoryId()
            , category);
  }

  @Override
  public Optional<UserCategory> findCategoryById(String categoryId) {
    return Optional.ofNullable(userCategoryHashOperations.get(buildCollectionKey(CATEGORY_COLLECTION),
            categoryId));
  }

  @Override
  public Optional<ContentPackage> findPackageById(String packageId) {
    return Optional.ofNullable(
            contentPackageHashOperations.get(buildCollectionKey(PACKAGE_COLLECTION),packageId)
    );

  }

  @Override
  public Optional<UserData> findUserById(long userId) {
    return Optional.ofNullable(
            userDataHashOperations.get(buildCollectionKey(USERDATA_COLLECTION),userId)
    );
  }

  @Override
  public void save(ContentPackage contentPackage) {
    contentPackageHashOperations.put(buildCollectionKey(PACKAGE_COLLECTION),
            contentPackage.getPackageId(), contentPackage);
  }

  @Override
  public ContentPackage addTags(String packageId, Set<String> tags) {
    ContentPackage contentPackage = contentPackageHashOperations.get(buildCollectionKey(PACKAGE_COLLECTION), packageId);
    if (null == contentPackage) {
      return null;
    }

    Set<String> newTags = CollectionUtilities.union(contentPackage.getTagsFilter(), tags);
    contentPackage.setTagsFilter(newTags);

    contentPackageHashOperations.put(buildCollectionKey(PACKAGE_COLLECTION),
            contentPackage.getPackageId(),contentPackage);

    return contentPackage;
  }

  @Override
  public ContentPackage removeTags(String packageId, Set<String> tags) {
    ContentPackage contentPackage = contentPackageHashOperations.get(buildCollectionKey(PACKAGE_COLLECTION), packageId);
    if (null == contentPackage) {
      return null;
    }

    Set<String> newTags = CollectionUtilities.difference(contentPackage.getTagsFilter(), tags);
    contentPackage.setTagsFilter(newTags);

    contentPackageHashOperations.put(buildCollectionKey(PACKAGE_COLLECTION),
            contentPackage.getPackageId(),contentPackage);

    return contentPackage;
  }

  @Override
  public void save(UserData userData) {
    userDataHashOperations.put(buildCollectionKey(USERDATA_COLLECTION),userData.getUserId(), userData);
  }

  @Override
  public boolean update(UserData userData) {
    return false;
  }

  @Override
  public UserData addPackageToUser(long userId, String packageId) {

    UserData userData = userDataHashOperations.get(buildCollectionKey(USERDATA_COLLECTION), userId);
    if (null == userData) {
      return null;
    }

    userData.setPackageIds(CollectionUtilities.union(userData.getPackageIds(), Set.of(packageId)) );


    userDataHashOperations.put(buildCollectionKey(USERDATA_COLLECTION),userId,userData);

    return userData;
  }

  @Override
  public UserData removePackageFromUser(long userId, String packageId) {

    UserData userData = userDataHashOperations.get(buildCollectionKey(USERDATA_COLLECTION), userId);
    if (null == userData) {
      return null;
    }

    userData.setPackageIds(
            CollectionUtilities.difference(userData.getPackageIds(), Set.of(packageId))
    );


    userDataHashOperations.put(buildCollectionKey(USERDATA_COLLECTION),userId,userData);

    return userData;
  }

  @Override
  public List<ContentPackage> getPackages(long userId) {
    UserData userData = userDataHashOperations.get(buildCollectionKey(USERDATA_COLLECTION), userId);
    if (null == userData) {
      return null;
    }

    return userData.getPackageIds().stream()
            .map(id -> contentPackageHashOperations.get(buildCollectionKey(PACKAGE_COLLECTION), id))
            .filter(Objects::nonNull)
            .collect(Collectors.toList());
  }

  @Override
  public UserCategory getCategory(long userId) {
    UserData userData = userDataHashOperations.get(buildCollectionKey(USERDATA_COLLECTION), userId);
    if (null == userData) {
      return null;
    }
    return userCategoryHashOperations.get(buildCollectionKey(CATEGORY_COLLECTION), userData.getCategoryId());
  }

  @Override
  public List<ContentPackage> listPackages() {
    return contentPackageHashOperations.values(buildCollectionKey(PACKAGE_COLLECTION));
  }

  @Override
  public List<UserCategory> listCategories() {
    return userCategoryHashOperations.values(buildCollectionKey(CATEGORY_COLLECTION));
  }
}
