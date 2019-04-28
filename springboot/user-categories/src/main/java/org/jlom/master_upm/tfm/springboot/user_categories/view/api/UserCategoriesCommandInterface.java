package org.jlom.master_upm.tfm.springboot.user_categories.view.api;

import org.jlom.master_upm.tfm.springboot.user_categories.view.api.dtos.InputUserCategoryData;
import org.jlom.master_upm.tfm.springboot.user_categories.view.api.dtos.InputUserContentFiltered;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

public interface UserCategoriesCommandInterface {

  String APPLICATION_JSON_PROBLEM_VALUE = "application/problem+json";

  @RequestMapping(
          value = "/filter",
          method = RequestMethod.POST,
          produces = {MediaType.APPLICATION_JSON_VALUE, APPLICATION_JSON_PROBLEM_VALUE}
  )
  ResponseEntity<?> filter(HttpServletRequest request, @Valid @RequestBody InputUserContentFiltered inputUserContentFiltered);

  @RequestMapping(
          value = "/user",
          method = RequestMethod.POST,
          produces = {MediaType.APPLICATION_JSON_VALUE, APPLICATION_JSON_PROBLEM_VALUE}
  )
  ResponseEntity<?> addUser(HttpServletRequest request, @Valid @RequestBody InputUserCategoryData inputUserCategoryData);

  @RequestMapping(
          value = "/user/{userId}",
          method = RequestMethod.DELETE,
          produces = {MediaType.APPLICATION_JSON_VALUE, APPLICATION_JSON_PROBLEM_VALUE}
  )
  ResponseEntity<?> removeUser(HttpServletRequest request, @Valid @PathVariable("userId") long userId);

  @RequestMapping(
          value = "/user/category",
          method = RequestMethod.POST,
          produces = {MediaType.APPLICATION_JSON_VALUE, APPLICATION_JSON_PROBLEM_VALUE}
  )
  ResponseEntity<?> changeCategory(HttpServletRequest request, @Valid @RequestBody InputUserCategoryData inputUserCategoryData);

  @RequestMapping(
          value = "/user/add-packages",
          method = RequestMethod.POST,
          produces = {MediaType.APPLICATION_JSON_VALUE, APPLICATION_JSON_PROBLEM_VALUE}
  )
  ResponseEntity<?> addPackages(HttpServletRequest request, @Valid @RequestBody InputUserCategoryData streamData);

  @RequestMapping(
          value = "/user/remove-packages",
          method = RequestMethod.POST,
          produces = {MediaType.APPLICATION_JSON_VALUE, APPLICATION_JSON_PROBLEM_VALUE}
  )
  ResponseEntity<?> removePackages(HttpServletRequest request, @Valid @RequestBody InputUserCategoryData streamData);

}
