package org.jlom.master_upm.tfm.springboot.catalog.view.api;

import org.jlom.master_upm.tfm.springboot.catalog.model.ContentStatus;
import org.jlom.master_upm.tfm.springboot.catalog.view.api.dtos.InputCatalogContent;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

public interface CatalogCommandInterface {

  String APPLICATION_JSON_PROBLEM_VALUE = "application/problem+json";

  @RequestMapping(
          value = "/content/newContent",
          method = RequestMethod.POST,
          produces = {MediaType.APPLICATION_JSON_VALUE, APPLICATION_JSON_PROBLEM_VALUE}
  )
  ResponseEntity<?> createNewContent(HttpServletRequest request, @Valid @RequestBody InputCatalogContent content);

  @RequestMapping(
          value = "/content/delete/{contentId}",
          method = RequestMethod.POST,
          produces = {MediaType.APPLICATION_JSON_VALUE, APPLICATION_JSON_PROBLEM_VALUE}
  )
  ResponseEntity<?> deleteContent(HttpServletRequest request, @Valid @PathVariable(value = "contentId")long contentId);

  @RequestMapping(
          value = "/content/changeStatus/{contentId}",
          method = RequestMethod.POST,
          produces = {MediaType.APPLICATION_JSON_VALUE, APPLICATION_JSON_PROBLEM_VALUE}
  )
  ResponseEntity<?> changeStatus(HttpServletRequest request,
                                 @Valid @PathVariable(value = "contentId") long contentId,
                                 @Valid @RequestBody ContentStatus status);

  @RequestMapping(
          value = "/content/addTags/{contentId}",
          method = RequestMethod.POST,
          produces = {MediaType.APPLICATION_JSON_VALUE, APPLICATION_JSON_PROBLEM_VALUE}
  )
  ResponseEntity<?> addTags(HttpServletRequest request,
                                 @Valid @PathVariable(value = "contentId") long contentId,
                                 @Valid @RequestParam(value = "newTags") String[] tags);
}
