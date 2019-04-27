package org.jlom.master_upm.tfm.springboot.user_categories.model.api;


import org.jlom.master_upm.tfm.springboot.user_categories.model.daos.ContentPackage;
import org.jlom.master_upm.tfm.springboot.user_categories.model.daos.UserCategory;
import org.jlom.master_upm.tfm.springboot.user_categories.model.daos.UserData;
import org.springframework.stereotype.Repository;

import java.util.Set;

@Repository
public interface UserCategoriesRepositoryCommands {

  void  save(UserCategory category);

  void save(final ContentPackage contentPackage);

  ContentPackage addTags(final String packageId, Set<String> tags);
  ContentPackage removeTags(final String packageId, Set<String> tags);

  void  save(UserData userData);
  boolean update(UserData userData);
  UserData addPackageToUser(final long userId, String packageId);
  UserData removePackageFromUser(final long userId, String packageId);
}
