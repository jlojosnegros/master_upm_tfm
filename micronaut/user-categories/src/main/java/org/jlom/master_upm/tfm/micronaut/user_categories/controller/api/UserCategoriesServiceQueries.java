package org.jlom.master_upm.tfm.micronaut.user_categories.controller.api;





import org.jlom.master_upm.tfm.micronaut.user_categories.model.daos.ContentPackage;
import org.jlom.master_upm.tfm.micronaut.user_categories.model.daos.UserCategory;

import java.util.List;

public interface UserCategoriesServiceQueries {

  List<ContentPackage> getPackagesForUser(final long userId);
  List<ContentPackage> listAllPackages();
  UserCategory getCategoryForUser(final long userId);
  List<UserCategory> listAllCategories();

}
