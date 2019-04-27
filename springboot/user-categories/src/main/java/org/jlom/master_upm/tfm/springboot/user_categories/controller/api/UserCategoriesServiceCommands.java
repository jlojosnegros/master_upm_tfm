package org.jlom.master_upm.tfm.springboot.user_categories.controller.api;


import org.jlom.master_upm.tfm.springboot.user_categories.controller.api.dtos.CatalogContent;
import org.jlom.master_upm.tfm.springboot.user_categories.controller.api.dtos.UserCategoriesServiceResponse;

import java.util.List;
import java.util.Set;

public interface UserCategoriesServiceCommands {
  UserCategoriesServiceResponse filter(final long userId, List<CatalogContent> contents);
  UserCategoriesServiceResponse addUser(final long userId,
                                        final String categoryId,
                                        Set<String> packageIds);
  UserCategoriesServiceResponse removeUser(final long userId);
  UserCategoriesServiceResponse changeCategoryForUser(final long userId, final String newCategoryId);
  UserCategoriesServiceResponse addPackageToUser(final long userId, final Set<String> packageIds);
  UserCategoriesServiceResponse removePackageFromUser(final long userId, final Set<String> packageIds);
}
