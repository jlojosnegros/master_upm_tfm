package org.jlom.master_upm.tfm.micronaut.catalog.view;

import io.micronaut.http.HttpResponse;
import io.micronaut.http.annotation.Controller;
import io.micronaut.validation.Validated;
import org.jlom.master_upm.tfm.micronaut.catalog.controller.CatalogContentService;
import org.jlom.master_upm.tfm.micronaut.catalog.utils.DtosTransformations;
import org.jlom.master_upm.tfm.micronaut.catalog.view.api.CatalogQueryInterface;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.stream.Collectors;

@Controller("/catalog")
@Validated
public class CatalogRestServer implements CatalogQueryInterface {

  private static final Logger LOG = LoggerFactory.getLogger(CatalogRestServer.class);

  private CatalogContentService service;

  public CatalogRestServer(CatalogContentService service) {
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
    return null;
  }

  @Override
  public HttpResponse<?> getContentExactlyWithTags(String[] tags) {
    return null;
  }

  @Override
  public HttpResponse<?> getContentAvailableAfter(long date) {
    return null;
  }

  @Override
  public HttpResponse<?> getContentByStreamId(long streamId) {
    return null;
  }

  @Override
  public HttpResponse<?> getContentByStatus(String status) {
    return null;
  }
}
