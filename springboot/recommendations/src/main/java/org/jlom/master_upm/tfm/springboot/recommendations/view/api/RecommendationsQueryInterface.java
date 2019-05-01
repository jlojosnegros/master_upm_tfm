package org.jlom.master_upm.tfm.springboot.recommendations.view.api;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;

public interface RecommendationsQueryInterface {

  String APPLICATION_JSON_PROBLEM_VALUE = "application/problem+json";

  @RequestMapping(
          value = "/users/{userId}/{top}",
          method = RequestMethod.GET,
          produces = {MediaType.APPLICATION_JSON_VALUE, APPLICATION_JSON_PROBLEM_VALUE}
  )
  ResponseEntity<?> getRecommendationsForUser(HttpServletRequest request,
                                              @PathVariable("userId") long userId,
                                              @PathVariable("top") long top);

  @RequestMapping(
          value = "/most-viewed/{top}",
          method = RequestMethod.GET,
          produces = {MediaType.APPLICATION_JSON_VALUE, APPLICATION_JSON_PROBLEM_VALUE}
  )
  ResponseEntity<?> getMostViewed(HttpServletRequest request,
                                              @PathVariable("top") long top);
}
