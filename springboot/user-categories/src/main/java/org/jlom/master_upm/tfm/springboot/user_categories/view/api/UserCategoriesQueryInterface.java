package org.jlom.master_upm.tfm.springboot.user_categories.view.api;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;

public interface UserCategoriesQueryInterface {

  String APPLICATION_JSON_PROBLEM_VALUE = "application/problem+json";

  @RequestMapping(
          value = "/user/{userId}/packages",
          method = RequestMethod.GET,
          produces = {MediaType.APPLICATION_JSON_VALUE, APPLICATION_JSON_PROBLEM_VALUE}
  )
  ResponseEntity<?> listPackagesForUser(HttpServletRequest request
          , @PathVariable(value = "userId") long userId);

  @RequestMapping(
          value = "/user/{userId}/category",
          method = RequestMethod.GET,
          produces = {MediaType.APPLICATION_JSON_VALUE, APPLICATION_JSON_PROBLEM_VALUE}
  )
  ResponseEntity<?> getCategoryForUser(HttpServletRequest request
          , @PathVariable(value = "userId") long userId);


  @RequestMapping(
          value = "/packages",
          method = RequestMethod.GET,
          produces = {MediaType.APPLICATION_JSON_VALUE, APPLICATION_JSON_PROBLEM_VALUE}
  )
  ResponseEntity<?> listAllPackages(HttpServletRequest request);

  @RequestMapping(
          value = "/categories",
          method = RequestMethod.GET,
          produces = {MediaType.APPLICATION_JSON_VALUE, APPLICATION_JSON_PROBLEM_VALUE}
  )
  ResponseEntity<?> listAllCategories(HttpServletRequest request);
}
