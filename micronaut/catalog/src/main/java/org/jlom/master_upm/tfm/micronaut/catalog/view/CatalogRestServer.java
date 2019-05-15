package org.jlom.master_upm.tfm.micronaut.catalog.view;

import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.annotation.Controller;
import io.micronaut.validation.Validated;
import org.jlom.master_upm.tfm.micronaut.catalog.controller.api.dtos.ContentServiceResponse;
import org.jlom.master_upm.tfm.micronaut.catalog.controller.api.dtos.ICatalogService;
import org.jlom.master_upm.tfm.micronaut.catalog.model.CatalogContent;
import org.jlom.master_upm.tfm.micronaut.catalog.model.ContentStatus;
import org.jlom.master_upm.tfm.micronaut.catalog.utils.DtosTransformations;
import org.jlom.master_upm.tfm.micronaut.catalog.view.api.CatalogCommandInterface;
import org.jlom.master_upm.tfm.micronaut.catalog.view.api.CatalogQueryInterface;
import org.jlom.master_upm.tfm.micronaut.catalog.view.api.dtos.InputCatalogContent;
import org.jlom.master_upm.tfm.micronaut.catalog.view.serivceresponsehandlers.CreateServiceResponseHandler;
import org.jlom.master_upm.tfm.micronaut.catalog.view.serivceresponsehandlers.DeleteServiceResponseHandler;
import org.jlom.master_upm.tfm.micronaut.catalog.view.serivceresponsehandlers.UpdateServiceResponseHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.validation.Valid;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static org.jlom.master_upm.tfm.micronaut.catalog.utils.DtosTransformations.serviceToViewContent;

@Controller("/catalog")
@Validated
public class CatalogRestServer implements CatalogQueryInterface, CatalogCommandInterface {

  private static final Logger LOG = LoggerFactory.getLogger(CatalogRestServer.class);

  private ICatalogService service;

  public CatalogRestServer(ICatalogService service) {
    this.service = service;
  }

  @Override
  public HttpResponse<?> getAllContent() {

    var catalogContents = service.listAll()
            .stream()
            .map(DtosTransformations::serviceToViewContent)
            .collect(Collectors.toList());

    return HttpResponse.ok(catalogContents);
  }

  @Override
  public HttpResponse<?> getContentById(long contentId) {
    CatalogContent content = service.getContent(contentId);
    InputCatalogContent catalogContent = serviceToViewContent(content);
    return HttpResponse.ok(catalogContent);
  }

  @Override
  public HttpResponse<?> getContentExactlyWithTags(String tags) {
    String[] split = tags.split(",");
    Set<String> tags1 = Set.of(split);
    LOG.info("Rest: getContentExactlyWithTags: " + tags1);
    List<CatalogContent> contentsWithTags = service.getContentsWithTags(tags1);
    List<InputCatalogContent> collected = contentsWithTags.stream()
            .map(DtosTransformations::serviceToViewContent)
            .collect(Collectors.toList());

    return HttpResponse.ok(collected);
  }

  @Override
  public HttpResponse<?> getContentAvailableAfter(long date) {
    LOG.info("Rest: getContentAvailableAfter date(long)="+ date);
    Date realDate = new Date(date);
    LOG.info("Rest: getContentAvailableAfter date="+ realDate);
    List<InputCatalogContent> availableAfter = serviceToViewContent(service.getAvailableAfter(realDate));
    LOG.info("Rest: getContentAvailableAfter found="+ availableAfter);

    return HttpResponse.ok(availableAfter);
  }

  @Override
  public HttpResponse<?> getContentByStreamId(long streamId) {
    LOG.info("Rest: getContentByStreamId streamId: "+ streamId);
    CatalogContent contentWithStream = service.getContentWithStream(streamId);
    LOG.info("Rest: getContentByStreamId content: "+ contentWithStream);
    return HttpResponse.ok(serviceToViewContent(contentWithStream));
  }

  @Override
  public HttpResponse<?> getContentByStatus(String status) {
    return null;
  }

  @Override
  public HttpResponse<?> createNewContent(HttpRequest<?> request,
                                          @Valid InputCatalogContent content) {

    LOG.info("Rest: createNewContent content: " + content);

    long streamId = Long.parseLong(content.getStreamId());
    ContentStatus status = ContentStatus.valueOf(content.getContentStatus());

    ContentServiceResponse response = service.createContent(streamId,
            content.getTitle(),
            status,
            content.getTags(),
            content.getAvailable()
    );

    CreateServiceResponseHandler handler = new CreateServiceResponseHandler(request);
    return response.accept(handler);
  }

  @Override
  public HttpResponse<?> deleteContent(HttpRequest<?> request, @Valid long contentId) {
    LOG.info("Rest: deleteContent contentId: " + contentId);
    ContentServiceResponse response = service.deleteContent(contentId);
    DeleteServiceResponseHandler deleteServiceResponseHandler = new DeleteServiceResponseHandler(request);
    return response.accept(deleteServiceResponseHandler);
  }

  @Override
  public HttpResponse<?> changeStatus(HttpRequest<?> request, @Valid long contentId, @Valid ContentStatus newStatus) {
    LOG.info("Rest: changeStatus contentId: " + contentId + ";newStatus:" + newStatus);
    ContentStatus status = newStatus;
    var response = service.changeStatus(contentId, status);
    var handler = new UpdateServiceResponseHandler(request);
    return response.accept(handler);
  }

  @Override
  public HttpResponse<?> addTags(HttpRequest<?> request, @Valid long contentId, @Valid String newTags) {
    LOG.info("Rest: addTags contentId: " + contentId + ";newTags:" + newTags);
    Set<String> tags = Set.of(newTags.split(","));

    LOG.info("Rest: addTags contentId: " + contentId + ";newTags:" + tags);
    var response = service.addTags(contentId, tags);
    var handler = new UpdateServiceResponseHandler(request);
    return response.accept(handler);
  }
}
