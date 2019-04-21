package org.jlom.master_upm.tfm.springboot.catalog.controller;


import org.jlom.master_upm.tfm.springboot.catalog.controller.api.CatalogServiceCommands;
import org.jlom.master_upm.tfm.springboot.catalog.controller.api.CatalogServiceQueries;
import org.jlom.master_upm.tfm.springboot.catalog.controller.api.dtos.ContentServiceResponse;
import org.jlom.master_upm.tfm.springboot.catalog.controller.api.dtos.ContentServiceResponseFailure;
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
  public ContentServiceResponse createContent(long contentId, long streamId, String title, Set<String> tags) {

    try {
      if(null != repository.findById(contentId)) {
        return new ContentServiceResponseFailure("error: Content with id[" + contentId +"] already exist");
      } else if (null != repository.findByStreamId(streamId)) {
        return new ContentServiceResponseFailure("error: Content with streamId[" + streamId +"] already exist");
      }

      CatalogContent toInsert = CatalogContent.builder()
              .contentId(contentId)
              .streamId(streamId)
              .title(title)
              .tags(tags)
              .build();

      repository.save(toInsert);

      return new ContentServiceResponseOk();
    } catch (Exception ex) {
      return  new ContentServiceResponseFailure(ex.getMessage());
    }
  }

  @Override
  public ContentServiceResponse deleteContent(long contentId) {
    try {
      Long deleted = repository.delete(contentId);
      if (deleted == null) {
        return new ContentServiceResponseFailure("error: Null ");
      } else if (deleted == 0) {
        return new ContentServiceResponseFailure("error: Element with id["+contentId+"] does not exist");
      } else {
        return new ContentServiceResponseOk();
      }
    } catch (Exception ex) {
      return new ContentServiceResponseFailure(ex.getMessage());
    }


  }

  @Override
  public CatalogContent changeStatus(long contentId, ContentStatus status) {

    try {
      CatalogContent content = repository.findById(contentId);
      if (null == content) {
        return content;
      }

      content.setStatus(status);
      repository.save(content);
      return content;

    } catch (Exception ex) {
      return null;
    }
  }

  @Override
  public CatalogContent addTags(long contentId, Set<String> tags) {

    try {
      CatalogContent content = repository.findById(contentId);
      if (null == content) {
        return null;
      }

      if (!content.getTags().addAll(tags) ) {
        return null;
      }

      repository.save(content);
      return content;

    } catch (Exception ex) {
      return null;
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
