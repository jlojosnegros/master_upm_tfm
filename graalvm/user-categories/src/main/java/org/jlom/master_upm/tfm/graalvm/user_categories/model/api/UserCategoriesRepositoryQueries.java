package org.jlom.master_upm.tfm.graalvm.user_categories.model.api;




import org.jlom.master_upm.tfm.graalvm.user_categories.model.daos.ContentPackage;
import org.jlom.master_upm.tfm.graalvm.user_categories.model.daos.UserCategory;
import org.jlom.master_upm.tfm.graalvm.user_categories.model.daos.UserData;

import java.io.IOException;
import java.util.List;
import java.util.Optional;


public interface UserCategoriesRepositoryQueries {

  List<ContentPackage> getPackages(final long userId) throws IOException;
  UserCategory getCategory(final long userId) throws IOException;

  List<ContentPackage> listPackages();
  List<UserCategory> listCategories();

  Optional<UserCategory> findCategoryById(String categoryId) throws IOException;
  Optional<ContentPackage> findPackageById(String packageId) throws IOException;
  Optional<UserData> findUserById(final long userId) throws IOException;
}
