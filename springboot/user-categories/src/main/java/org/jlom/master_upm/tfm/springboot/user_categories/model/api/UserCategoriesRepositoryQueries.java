package org.jlom.master_upm.tfm.springboot.user_categories.model.api;


import org.jlom.master_upm.tfm.springboot.user_categories.model.daos.ContentPackage;
import org.jlom.master_upm.tfm.springboot.user_categories.model.daos.UserCategory;
import org.jlom.master_upm.tfm.springboot.user_categories.model.daos.UserData;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserCategoriesRepositoryQueries {

  List<ContentPackage> getPackages(final long userId);
  UserCategory getCategory(final long userId);

  List<ContentPackage> listPackages();
  List<UserCategory> listCategories();

  Optional<UserCategory> findCategoryById(String categoryId);
  Optional<ContentPackage> findPackageById(String packageId);
  Optional<UserData> findUserById(final long userId);
}
