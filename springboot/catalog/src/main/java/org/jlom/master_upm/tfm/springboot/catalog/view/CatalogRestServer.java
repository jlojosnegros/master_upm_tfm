package org.jlom.master_upm.tfm.springboot.catalog.view;


import com.fasterxml.jackson.core.JsonProcessingException;
import org.jlom.master_upm.tfm.springboot.catalog.controller.CatalogService;
import org.jlom.master_upm.tfm.springboot.catalog.controller.api.dtos.ContentServiceResponse;
import org.jlom.master_upm.tfm.springboot.catalog.model.CatalogContent;
import org.jlom.master_upm.tfm.springboot.catalog.model.ContentStatus;
import org.jlom.master_upm.tfm.springboot.catalog.utils.DtosTransformations;
import org.jlom.master_upm.tfm.springboot.catalog.view.api.CatalogCommandInterface;
import org.jlom.master_upm.tfm.springboot.catalog.view.api.CatalogQueryInterface;
import org.jlom.master_upm.tfm.springboot.catalog.view.api.dtos.InputCatalogContent;
import org.jlom.master_upm.tfm.springboot.catalog.view.exceptions.WrapperException;
import org.jlom.master_upm.tfm.springboot.catalog.view.serivceresponsehandlers.CreateServiceResponseHandler;
import org.jlom.master_upm.tfm.springboot.catalog.view.serivceresponsehandlers.DeleteServiceResponseHandler;
import org.jlom.master_upm.tfm.springboot.catalog.view.serivceresponsehandlers.UpdateServiceResponseHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static org.jlom.master_upm.tfm.springboot.catalog.utils.DtosTransformations.*;
import static org.jlom.master_upm.tfm.springboot.catalog.utils.JsonUtils.ListToJson;
import static org.jlom.master_upm.tfm.springboot.catalog.utils.JsonUtils.ObjectToJson;

@RestController
@RequestMapping("/catalog")
@Validated
public class CatalogRestServer implements CatalogQueryInterface, CatalogCommandInterface {

  private static final Logger LOG = LoggerFactory.getLogger(CatalogRestServer.class);


  private final CatalogService service;

  public CatalogRestServer(CatalogService service) {
    this.service = service;
  }


  @Override
  public ResponseEntity<?> getAllContent() {

    var catalogContents = service.listAll()
            .stream()
            .map(DtosTransformations::serviceToViewContent)
            .collect(Collectors.toList());


    try {
      return new ResponseEntity<>(ListToJson(catalogContents), new HttpHeaders(), HttpStatus.OK);
    } catch (JsonProcessingException e) {
      throw new WrapperException("error: Unable to convertToJson", e);
    }
  }

  @Override
  public ResponseEntity<?> getContentById(long contentId) {
    LOG.error("jlom: getContentById: id="+contentId);
    var content = serviceToViewContent(service.getContent(contentId));
    LOG.error("jlom: getContentById: content="+content);
    try {
      return new ResponseEntity<>(ObjectToJson(content), new HttpHeaders(), HttpStatus.OK);
    } catch (JsonProcessingException e) {
      throw new WrapperException("error: Unable to convertToJson obj: " + content, e);
    }
  }

  @Override
  public ResponseEntity<?> getContentExactlyWithTags(String[] tags) {
    LOG.error("jlom: getContentExactlyWithTags tags="+ Arrays.toString(tags));
    List<CatalogContent> contentsWithTags = service.getContentsWithTags(Set.of(tags));
    LOG.error("jlom: getContentExactlyWithTags: found="+contentsWithTags);

    var toSend = serviceToViewContent(contentsWithTags);

    LOG.error("jlom: getContentExactlyWithTags: tosend="+toSend);
    try {
      return new ResponseEntity<>(ListToJson(toSend), new HttpHeaders(), HttpStatus.OK);
    } catch (JsonProcessingException e) {
      throw new WrapperException("error: Unable to convertToJson obj: " + toSend, e);
    }
  }

  @Override
  public ResponseEntity<?> getContentAvailableAfter(long date) {
    LOG.error("jlom: getContentAvailableAfter date(long)="+ date);
    Date realDate = new Date(date);
    LOG.error("jlom: getContentAvailableAfter date="+ realDate);
    List<InputCatalogContent> availableAfter = serviceToViewContent(service.getAvailableAfter(realDate));
    LOG.error("jlom: getContentAvailableAfter found="+ availableAfter);

    try {
      return new ResponseEntity<>(ListToJson(availableAfter), new HttpHeaders(), HttpStatus.OK);
    } catch (JsonProcessingException e) {
      throw new WrapperException("error: Unable to convertToJson obj: " + availableAfter, e);
    }

  }

  @Override
  public ResponseEntity<?> getContentByStreamId(long streamId) {

    LOG.error("jlom: getContentByStreamId streamId="+ streamId);
    var content = serviceToViewContent(service.getContentWithStream(streamId));
    LOG.error("jlom: getContentByStreamId  found="+ content);
    try {
      return new ResponseEntity<>(ObjectToJson(content), new HttpHeaders(), HttpStatus.OK);
    } catch (JsonProcessingException e) {
      throw new WrapperException("error: Unable to convertToJson obj: " + content, e);
    }
  }


  @Override
  public ResponseEntity<?> createNewContent(HttpServletRequest request,
                                            InputCatalogContent content) {
    LOG.error("jlom: createNewContent content: " + content);

    //long contentId = Long.parseLong(content.getContentId());
    long streamId = Long.parseLong(content.getStreamId());
    ContentStatus status = ContentStatus.valueOf(content.getContentStatus());

    ContentServiceResponse response = service.createContent(streamId,
            content.getTitle(),
            status,
            content.getTags()
    );

    CreateServiceResponseHandler handler = new CreateServiceResponseHandler(request);
    return response.accept(handler);
  }



  @Override
  public ResponseEntity<?> deleteContent(HttpServletRequest request, @Valid long contentId) {

    LOG.error("jlom: deleteContent contentId: " + contentId);
    ContentServiceResponse response = service.deleteContent(contentId);
    DeleteServiceResponseHandler deleteServiceResponseHandler = new DeleteServiceResponseHandler(request);
    return response.accept(deleteServiceResponseHandler);
  }

  @Override
  public ResponseEntity<?> changeStatus(HttpServletRequest request, @Valid long contentId, @Valid ContentStatus newStatus) {

    LOG.error("jlom: changeStatus contentId: " + contentId + ";newStatus:" + newStatus);
    ContentStatus status = newStatus;
    var response = service.changeStatus(contentId, status);
    var handler = new UpdateServiceResponseHandler(request);
    return response.accept(handler);
  }

  @Override
  public ResponseEntity<?> addTags(HttpServletRequest request, @Valid long contentId, @Valid String[] newTags) {

    LOG.error("jlom: addTags contentId: " + contentId + ";newTags:" + Arrays.toString(newTags));
    Set<String> tags = Set.of(newTags);
    var response = service.addTags(contentId, tags);
    var handler = new UpdateServiceResponseHandler(request);
    return response.accept(handler);

  }

}
