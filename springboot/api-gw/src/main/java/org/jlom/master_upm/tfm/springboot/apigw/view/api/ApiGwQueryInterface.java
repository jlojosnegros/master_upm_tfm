package org.jlom.master_upm.tfm.springboot.apigw.view.api;


import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;

public interface ApiGwQueryInterface {
  String APPLICATION_JSON_PROBLEM_VALUE = "application/problem+json";

  @RequestMapping(
          value = "/content/soon",
          method = RequestMethod.GET,
          produces = {MediaType.APPLICATION_JSON_VALUE, APPLICATION_JSON_PROBLEM_VALUE}
  )
  ResponseEntity<?> listContentAvailableSoon(HttpServletRequest request);

  @RequestMapping(
          value = "/content/most-viewed/{top}",
          method = RequestMethod.GET,
          produces = {MediaType.APPLICATION_JSON_VALUE, APPLICATION_JSON_PROBLEM_VALUE}
  )
  ResponseEntity<?> listMostViewedContent(HttpServletRequest request,
                                                @PathVariable("top") long top);

  @RequestMapping(
          value = "/content/most-viewed",
          method = RequestMethod.GET,
          produces = {MediaType.APPLICATION_JSON_VALUE, APPLICATION_JSON_PROBLEM_VALUE}
  )
  ResponseEntity<?> listMostViewedContent(HttpServletRequest request);

  @RequestMapping(
          value = "/user/{userId}/recommended/{top}",
          method = RequestMethod.GET,
          produces = {MediaType.APPLICATION_JSON_VALUE, APPLICATION_JSON_PROBLEM_VALUE}
  )
  ResponseEntity<?> listRecommended(HttpServletRequest request,
                                          @PathVariable("userId") long userId,
                                          @PathVariable("top") long top);

  @RequestMapping(
          value = "/user/{userId}/liked/{top}",
          method = RequestMethod.GET,
          produces = {MediaType.APPLICATION_JSON_VALUE, APPLICATION_JSON_PROBLEM_VALUE}
  )
  ResponseEntity<?> listLiked(HttpServletRequest request,
                                    @PathVariable("userId") long userId,
                                    @PathVariable("top") long top);
}
