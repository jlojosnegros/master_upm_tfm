package org.jlom.master_upm.tfm.springboot.catalog.controller;


import org.jlom.master_upm.tfm.springboot.catalog.controller.api.CatalogServiceCommands;
import org.jlom.master_upm.tfm.springboot.catalog.controller.api.CatalogServiceQueries;
import org.jlom.master_upm.tfm.springboot.catalog.controller.api.dtos.ContentServiceResponseFailureInternalError;
import org.jlom.master_upm.tfm.springboot.catalog.controller.api.dtos.ContentServiceResponseFailureNotFound;
import org.jlom.master_upm.tfm.springboot.catalog.controller.api.dtos.ContentServiceResponse;
import org.jlom.master_upm.tfm.springboot.catalog.controller.api.dtos.ContentServiceResponseFailureException;
import org.jlom.master_upm.tfm.springboot.catalog.controller.api.dtos.ContentServiceResponseFailureInvalidInputParameter;
import org.jlom.master_upm.tfm.springboot.catalog.controller.api.dtos.ContentServiceResponseOk;
import org.jlom.master_upm.tfm.springboot.catalog.model.CatalogContent;
import org.jlom.master_upm.tfm.springboot.catalog.model.CatalogContentRepository;
import org.jlom.master_upm.tfm.springboot.catalog.model.ContentStatus;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class CatalogService implements CatalogServiceCommands, CatalogServiceQueries {

  private final CatalogContentRepository repository;

  public CatalogService(CatalogContentRepository repository) {
    this.repository = repository;
  }

  @Override
  public ContentServiceResponse createContent(long streamId, String title, ContentStatus status, Set<String> tags) {

    try {
      if (null != repository.findByStreamId(streamId)) {
        return new ContentServiceResponseFailureInvalidInputParameter("Content with streamId already exists",
                "streamId", streamId);
      }

      CatalogContent toInsert = CatalogContent.builder()
              .streamId(streamId)
              .status(status)
              .title(title)
              .tags(tags)
              .build();

      repository.save(toInsert);

      CatalogContent insertedContent = repository.findByStreamId(streamId);
      if (null == insertedContent) {
        return new ContentServiceResponseFailureInternalError("Unable to insert new content: " + toInsert);
      }
      return new ContentServiceResponseOk(insertedContent);
    } catch (Exception ex) {
      return  new ContentServiceResponseFailureException(ex);
    }
  }

  @Override
  public ContentServiceResponse deleteContent(long contentId) {
    try {
      CatalogContent content = repository.findById(contentId);
      if (null == content) {
        return new ContentServiceResponseFailureInvalidInputParameter("Element with id already exists",
                "contentId", contentId);
      }

      Long deleted = repository.delete(contentId);
      if ( (deleted == null ) || ( deleted == 0) ) {
        return new ContentServiceResponseFailureInternalError("error: Unknown error. Could not delete " + content );
      }
      return new ContentServiceResponseOk(content);

    } catch (Exception ex) {
      return new ContentServiceResponseFailureException(ex);
    }


  }

  @Override
  public ContentServiceResponse changeStatus(long contentId, ContentStatus status) {

    try {
      CatalogContent content = repository.findById(contentId);
      if (null == content) {
        return new ContentServiceResponseFailureNotFound("contentId", contentId);
      }

      content.setStatus(status);
      repository.save(content);
      return new ContentServiceResponseOk(content);

    } catch (Exception ex) {
      return new ContentServiceResponseFailureException(ex);
    }
  }

  @Override
  public ContentServiceResponse addTags(long contentId, Set<String> tags) {

    try {
      CatalogContent content = repository.findById(contentId);
      if (null == content) {
        return new ContentServiceResponseFailureNotFound("contentId", contentId);
      }

      content.getTags().addAll(tags);
      repository.save(content);
      return new ContentServiceResponseOk(content);

    } catch (Exception ex) {
      return new ContentServiceResponseFailureException(ex);
    }
  }

  @Override
  public CatalogContent getContent(long contentId) {
    return repository.findById(contentId);
  }

  @Override
  public List<CatalogContent> getContentsWithTags(Set<String> tags) {
    return repository.findWithExactlyTags(tags);
  }

  @Override
  public List<CatalogContent> getContentWithStatus(ContentStatus contentStatus) {
    return repository.findWithStatus(contentStatus);
  }

  @Override
  public CatalogContent getContentWithStream(long streamId) {
    return repository.findByStreamId(streamId);
  }

  @Override
  public List<CatalogContent> getAvailableAfter(Date date) {
    return repository.findAvailableAfter(date);
  }

  @Override
  public List<CatalogContent> listAll() {
    return repository.findAll().stream().sorted().collect(Collectors.toList());
  }
}
