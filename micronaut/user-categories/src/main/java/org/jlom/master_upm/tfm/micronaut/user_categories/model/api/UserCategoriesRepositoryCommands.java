package org.jlom.master_upm.tfm.micronaut.user_categories.model.api;




import com.fasterxml.jackson.core.JsonProcessingException;
import org.jlom.master_upm.tfm.micronaut.user_categories.model.daos.ContentPackage;
import org.jlom.master_upm.tfm.micronaut.user_categories.model.daos.UserCategory;
import org.jlom.master_upm.tfm.micronaut.user_categories.model.daos.UserData;

import java.io.IOException;
import java.util.Set;


public interface UserCategoriesRepositoryCommands {

  void  save(UserCategory category) throws IOException;

  void save(final ContentPackage contentPackage) throws JsonProcessingException;

  ContentPackage addTags(final String packageId, Set<String> tags) throws IOException;
  ContentPackage removeTags(final String packageId, Set<String> tags) throws IOException;

  void  save(UserData userData) throws IOException;
  boolean update(UserData userData);
  UserData addPackageToUser(final long userId, String packageId) throws IOException;
  UserData removePackageFromUser(final long userId, String packageId) throws IOException;
}
