package org.jlom.master_upm.tfm.springboot.user_categories.model.api;

import org.springframework.stereotype.Repository;

@Repository
public interface IUserCategoriesRepository extends UserCategoriesRepositoryCommands, UserCategoriesRepositoryQueries {
}
