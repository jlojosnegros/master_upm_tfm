package org.jlom.master_upm.tfm.micronaut.catalog.controller;

import org.jlom.master_upm.tfm.micronaut.catalog.controller.api.dtos.ContentServiceResponse;
import org.jlom.master_upm.tfm.micronaut.catalog.controller.api.dtos.ICatalogService;
import org.jlom.master_upm.tfm.micronaut.catalog.model.CatalogContent;
import org.jlom.master_upm.tfm.micronaut.catalog.model.CatalogContentRepository;
import org.jlom.master_upm.tfm.micronaut.catalog.model.ContentStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Singleton;
import java.util.Date;
import java.util.List;
import java.util.Set;

@Singleton
public class CatalogContentService implements ICatalogService {

  private static Logger LOG = LoggerFactory.getLogger(CatalogContentService.class);


  private CatalogContentRepository repository;

  public CatalogContentService(CatalogContentRepository repository) {
    this.repository = repository;
  }


  @Override
  public CatalogContent getContent(long contentId) {
    return repository.findById(contentId);
  }

  @Override
  public List<CatalogContent> getContentsWithTags(Set<String> tags) {
    LOG.info("Service:getContentsWithTags:  " + tags);
    return repository.findWithExactlyTags(tags);
  }

  @Override
  public List<CatalogContent> getContentWithStatus(ContentStatus contentStatus) {
    return null;
  }

  @Override
  public CatalogContent getContentWithStream(long streamId) {
    LOG.info("Service:getContentWithStream: " + streamId);
    return repository.findByStreamId(streamId);
  }

  @Override
  public List<CatalogContent> getAvailableAfter(Date date) {
    return repository.findAvailableAfter(date);
  }

  @Override
  public List<CatalogContent> listAll() {
    return repository.findAll();
  }

  @Override
  public ContentServiceResponse createContent(long streamId, String title, ContentStatus status, Set<String> tags) {
    return null;
  }

  @Override
  public ContentServiceResponse deleteContent(long contentId) {
    return null;
  }

  @Override
  public ContentServiceResponse changeStatus(long contentId, ContentStatus status) {
    return null;
  }

  @Override
  public ContentServiceResponse addTags(long contentId, Set<String> tags) {
    return null;
  }
}
