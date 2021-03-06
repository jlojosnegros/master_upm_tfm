package org.jlom.master_upm.tfm.springboot.catalog.view.api;


import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;


public interface CatalogQueryInterface {
  String APPLICATION_JSON_PROBLEM_VALUE = "application/problem+json";

  @RequestMapping(
          value = "/content",
          method = RequestMethod.GET,
          produces = {MediaType.APPLICATION_JSON_VALUE, APPLICATION_JSON_PROBLEM_VALUE}
  )
  ResponseEntity<?> getAllContent();

  @RequestMapping(
          value = "/content/{contentId}",
          method = RequestMethod.GET,
          produces = {MediaType.APPLICATION_JSON_VALUE, APPLICATION_JSON_PROBLEM_VALUE}
  )
  ResponseEntity<?> getContentById(@PathVariable("contentId") long contentId);

  @RequestMapping(
          value = "/content/exactlyWithTags",
          method = RequestMethod.GET,
          produces = {MediaType.APPLICATION_JSON_VALUE, APPLICATION_JSON_PROBLEM_VALUE}
  )
  ResponseEntity<?> getContentExactlyWithTags(@RequestParam(value = "tags") String[] tags);

  @RequestMapping(
          value = "/content/after",
          method = RequestMethod.GET,
          produces = {MediaType.APPLICATION_JSON_VALUE, APPLICATION_JSON_PROBLEM_VALUE}
  )
  ResponseEntity<?> getContentAvailableAfter(@RequestParam(value = "date") long date);

  @RequestMapping(
          value = "/content/stream/{streamId}",
          method = RequestMethod.GET,
          produces = {MediaType.APPLICATION_JSON_VALUE, APPLICATION_JSON_PROBLEM_VALUE}
  )
  ResponseEntity<?> getContentByStreamId(@PathVariable("streamId") long streamId);

  @RequestMapping(
          value = "/content/status/{status}",
          method = RequestMethod.GET,
          produces = {MediaType.APPLICATION_JSON_VALUE, APPLICATION_JSON_PROBLEM_VALUE}
  )
  ResponseEntity<?> getContentByStatus(@PathVariable("status") String status);
}
