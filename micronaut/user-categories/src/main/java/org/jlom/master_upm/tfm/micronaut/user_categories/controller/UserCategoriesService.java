package org.jlom.master_upm.tfm.micronaut.user_categories.controller;



import org.jlom.master_upm.tfm.micronaut.user_categories.controller.api.UserCategoriesServiceCommands;
import org.jlom.master_upm.tfm.micronaut.user_categories.controller.api.UserCategoriesServiceQueries;
import org.jlom.master_upm.tfm.micronaut.user_categories.controller.api.dtos.CatalogContent;
import org.jlom.master_upm.tfm.micronaut.user_categories.controller.api.dtos.UserCategoriesServiceResponse;
import org.jlom.master_upm.tfm.micronaut.user_categories.controller.api.dtos.UserCategoriesServiceResponseFailureException;
import org.jlom.master_upm.tfm.micronaut.user_categories.controller.api.dtos.UserCategoriesServiceResponseFailureInternalError;
import org.jlom.master_upm.tfm.micronaut.user_categories.controller.api.dtos.UserCategoriesServiceResponseFailureInvalidInputParameter;
import org.jlom.master_upm.tfm.micronaut.user_categories.controller.api.dtos.UserCategoriesServiceResponseOKCatalogContent;
import org.jlom.master_upm.tfm.micronaut.user_categories.controller.api.dtos.UserCategoriesServiceResponseOKUserData;
import org.jlom.master_upm.tfm.micronaut.user_categories.model.api.IUserCategoriesRepository;
import org.jlom.master_upm.tfm.micronaut.user_categories.model.daos.ContentPackage;
import org.jlom.master_upm.tfm.micronaut.user_categories.model.daos.UserCategory;
import org.jlom.master_upm.tfm.micronaut.user_categories.model.daos.UserData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import javax.inject.Singleton;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Singleton
public class UserCategoriesService implements UserCategoriesServiceCommands, UserCategoriesServiceQueries {

  private final IUserCategoriesRepository repository;

  private static final Logger LOG = LoggerFactory.getLogger(UserCategoriesService.class);

  public UserCategoriesService(IUserCategoriesRepository repository) {
    this.repository = repository;
  }


  @Override
  public UserCategoriesServiceResponse filter(long userId, List<CatalogContent> contents) {
    Optional<UserData> userById = null;
    try {
      userById = repository.findUserById(userId);
    } catch (IOException e) {
      e.printStackTrace();
      return new UserCategoriesServiceResponseFailureException(e);
    }

    if (userById.isEmpty()) {
      return new UserCategoriesServiceResponseFailureInvalidInputParameter(
              "User does not exist ",
              "userId",
              userId
      );
    }


    UserCategory category = null;
    try {
      category = repository.getCategory(userId);
    } catch (IOException e) {
      e.printStackTrace();
      return new UserCategoriesServiceResponseFailureException(e);
    }

    UserCategory finalCategory = category;
    Set<CatalogContent> contentFilteredByCategory = contents.stream()
            .filter(c -> c.getTags().contains(finalCategory.getTagId()))
            .collect(Collectors.toSet());

    Set<ContentPackage> packages = userById.get().getPackageIds()
            .stream()
            .map(packageId -> {
              try {
                return repository.findPackageById(packageId);
              } catch (IOException e) {
                e.printStackTrace();
                return null;
              }
            })
            .filter(Objects::nonNull)
            .filter(Optional::isPresent)
            .map(Optional::get)
            .collect(Collectors.toSet());

    for (var content : contents) {
      for (var pack : packages) {
        if ( content.getTags().containsAll(pack.getTagsFilter()) ) {
          contentFilteredByCategory.add(content);
        }
      }
    }

    return new UserCategoriesServiceResponseOKCatalogContent(contentFilteredByCategory);
  }

  @Override
  public UserCategoriesServiceResponse addUser(long userId,
                                               String categoryId,
                                               Set<String> packageIds) {
    try {


      Optional<UserData> userById = repository.findUserById(userId);


      if (userById.isPresent()) {
        return new UserCategoriesServiceResponseFailureInvalidInputParameter(
                "User already exist " + userById.get(),
                "userId",
                userId
        );
      }

      Optional<UserCategory> categoryById = repository.findCategoryById(categoryId);

      if (categoryById.isEmpty()) {
        return new UserCategoriesServiceResponseFailureInvalidInputParameter(
                "Category not found ",
                "categoryId",
                categoryId
        );
      }

      long packages = packageIds.stream()
              .map(packageId -> {
                try {
                  return repository.findPackageById(packageId);
                } catch (IOException e) {
                  e.printStackTrace();
                  return null;
                }
              })
              .filter(Objects::nonNull)
              .count();

      if (packageIds.size() != packages) {
        return new UserCategoriesServiceResponseFailureInvalidInputParameter(
                "Some packages does not exist ",
                "packageIds",
                packageIds
        );
      }

      repository.save(
              UserData.builder()
                      .userId(userId)
                      .categoryId(categoryId)
                      .packageIds(packageIds)
                      .build()
      );

      Optional<UserData> userDataFound = repository.findUserById(userId);
      if (userDataFound.isEmpty()) {
        return new UserCategoriesServiceResponseFailureInternalError(
                "Unable to insert new userData"
        );
      }

      return new UserCategoriesServiceResponseOKUserData(userDataFound.get());
    } catch(IOException e) {
      return new UserCategoriesServiceResponseFailureException(e);
    }
  }

  @Override
  public UserCategoriesServiceResponse removeUser(long userId) {
    try {
      Optional<UserData> userById = repository.findUserById(userId);
      if (userById.isEmpty()) {
        return new UserCategoriesServiceResponseFailureInvalidInputParameter(
                "User does not exist ",
                "userId",
                userId
        );
      }

      return new UserCategoriesServiceResponseOKUserData(userById.get());
    } catch(IOException e) {
      return new UserCategoriesServiceResponseFailureException(e);
    }
  }

  @Override
  public UserCategoriesServiceResponse changeCategoryForUser(long userId, String newCategoryId) {
    try {
      Optional<UserData> userById = repository.findUserById(userId);
      if (userById.isEmpty()) {
        return new UserCategoriesServiceResponseFailureInvalidInputParameter(
                "User does not exist ",
                "userId",
                userId
        );
      }

      Optional<UserCategory> categoryById = repository.findCategoryById(newCategoryId);
      if (categoryById.isEmpty()) {
        return new UserCategoriesServiceResponseFailureInvalidInputParameter(
                "Category does not exist ",
                "newCategoryId",
                newCategoryId
        );
      }

      userById.get().setCategoryId(newCategoryId);
      repository.save(userById.get());

      Optional<UserData> savedUser = repository.findUserById(userId);
      if (savedUser.isEmpty()) {
        return new UserCategoriesServiceResponseFailureInternalError(
                "Unable to save " + userById.get()
        );
      }

      return new UserCategoriesServiceResponseOKUserData(userById.get());
    } catch(IOException e) {
      return new UserCategoriesServiceResponseFailureException(e);
    }
  }

  @Override
  public UserCategoriesServiceResponse addPackageToUser(long userId, Set<String> packageIds) {
    try {
      Optional<UserData> userById = repository.findUserById(userId);
      if (userById.isEmpty()) {
        return new UserCategoriesServiceResponseFailureInvalidInputParameter(
                "User does not exist ",
                "userId",
                userId
        );
      }

      for (var packageId : packageIds) {
        Optional<ContentPackage> packageById = repository.findPackageById(packageId);
        if (packageById.isEmpty()) {
          return new UserCategoriesServiceResponseFailureInvalidInputParameter(
                  "Package does not exist",
                  "packageId",
                  packageId
          );
        }
      }

      UserData userData = null;
      for (var packageId : packageIds) {
        userData = repository.addPackageToUser(userId, packageId);
      }
      return new UserCategoriesServiceResponseOKUserData(userData);
    } catch(IOException e) {
      return new UserCategoriesServiceResponseFailureException(e);
    }

  }

  @Override
  public UserCategoriesServiceResponse removePackageFromUser(long userId, Set<String> packageIds) {
    try {
      Optional<UserData> userById = repository.findUserById(userId);
      if (userById.isEmpty()) {
        return new UserCategoriesServiceResponseFailureInvalidInputParameter(
                "User does not exist ",
                "userId",
                userId
        );
      }

      UserData userData = null;
      for (var packageId : packageIds) {
        userData = repository.removePackageFromUser(userId, packageId);
      }

      return new UserCategoriesServiceResponseOKUserData(userData);
    } catch(IOException e) {
      return new UserCategoriesServiceResponseFailureException(e);
    }
  }

  @Override
  public List<ContentPackage> getPackagesForUser(long userId) {
    try {
      return repository.getPackages(userId);
    } catch(IOException e) {
    return new ArrayList<>();
  }
  }

  @Override
  public List<ContentPackage> listAllPackages() {
    return repository.listPackages();
  }

  @Override
  public UserCategory getCategoryForUser(long userId) {
    try {
      return repository.getCategory(userId);
    } catch (IOException e) {
      e.printStackTrace();
      return null;
    }
  }

  @Override
  public List<UserCategory> listAllCategories() {
    return repository.listCategories();
  }
}
