package org.jlom.master_upm.tfm.graalvm.catalog.controller;

import org.jlom.master_upm.tfm.graalvm.catalog.controller.api.dtos.ContentServiceResponseFailureInvalidInputParameter;
import org.jlom.master_upm.tfm.graalvm.catalog.controller.api.dtos.ContentServiceResponseOk;
import org.jlom.master_upm.tfm.graalvm.catalog.model.CatalogContent;
import org.jlom.master_upm.tfm.graalvm.catalog.model.CatalogContentRepository;
import org.jlom.master_upm.tfm.graalvm.catalog.controller.api.dtos.ContentServiceResponse;
import org.jlom.master_upm.tfm.graalvm.catalog.controller.api.dtos.ContentServiceResponseFailureException;
import org.jlom.master_upm.tfm.graalvm.catalog.controller.api.dtos.ContentServiceResponseFailureInternalError;
import org.jlom.master_upm.tfm.graalvm.catalog.controller.api.dtos.ContentServiceResponseFailureNotFound;
import org.jlom.master_upm.tfm.graalvm.catalog.controller.api.dtos.ICatalogService;
import org.jlom.master_upm.tfm.graalvm.catalog.model.ContentStatus;
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
  public ContentServiceResponse createContent(long streamId,
                                              String title,
                                              ContentStatus status,
                                              Set<String> tags,
                                              Date available) {
    try {
      if (null != repository.findByStreamId(streamId)) {
        return new ContentServiceResponseFailureInvalidInputParameter("Content with streamId already exists",
                "streamId", streamId);
      }

      CatalogContent toInsert = CatalogContent.builder()
              .streamId(streamId)
              .contentId(streamId)
              .status(status)
              .title(title)
              .tags(tags)
              .available(available)
              .build();
      LOG.info("toInsert: " + toInsert);
      repository.save(toInsert);

      CatalogContent insertedContent = repository.findByStreamId(streamId);
      LOG.info("inserted: " + insertedContent);
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
}
