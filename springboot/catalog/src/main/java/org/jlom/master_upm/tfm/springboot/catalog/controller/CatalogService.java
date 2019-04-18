package org.jlom.master_upm.tfm.springboot.catalog.controller;


import org.jlom.master_upm.tfm.springboot.catalog.controller.api.CatalogServiceQueries;
import org.jlom.master_upm.tfm.springboot.catalog.controller.api.CatalogServiceCommands;
import org.jlom.master_upm.tfm.springboot.catalog.controller.api.dtos.ContentServiceCreateResponse;
import org.jlom.master_upm.tfm.springboot.catalog.controller.api.dtos.ContentServiceCreateResponseFailure;
import org.jlom.master_upm.tfm.springboot.catalog.controller.api.dtos.ContentServiceCreateResponseOk;
import org.jlom.master_upm.tfm.springboot.catalog.controller.api.dtos.ContentServiceDeleteResponse;
import org.jlom.master_upm.tfm.springboot.catalog.controller.api.dtos.ContentServiceDeleteResponseFailure;
import org.jlom.master_upm.tfm.springboot.catalog.controller.api.dtos.ContentServiceDeleteResponseOk;
import org.jlom.master_upm.tfm.springboot.catalog.model.CatalogContent;
import org.jlom.master_upm.tfm.springboot.catalog.model.CatalogContentRepository;
import org.jlom.master_upm.tfm.springboot.catalog.model.ContentStatus;
import org.springframework.stereotype.Service;

import java.time.ZonedDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Set;

@Service
public class CatalogService implements CatalogServiceCommands, CatalogServiceQueries {

  private final CatalogContentRepository repository;

  public CatalogService(CatalogContentRepository repository) {
    this.repository = repository;
  }

  @Override
  public ContentServiceCreateResponse createContent(long contentId, long streamId, String title, Set<String> tags) {

    try {
      if(null != repository.findById(contentId)) {
        return new ContentServiceCreateResponseFailure("error: Content with id[" + contentId +"] already exist");
      }

      CatalogContent toInsert = CatalogContent.builder()
              .contentId(contentId)
              .streamId(streamId)
              .title(title)
              .tags(tags)
              .build();

      repository.save(toInsert);

      return new ContentServiceCreateResponseOk();
    } catch (Exception ex) {
      return  new ContentServiceCreateResponseFailure(ex.getMessage());
    }
  }

  @Override
  public ContentServiceDeleteResponse deleteContent(long contentId) {
    try {
      Long deleted = repository.delete(contentId);
      if (deleted == null) {
        return new ContentServiceDeleteResponseFailure("error: Null ");
      } else if (deleted == 0) {
        return new ContentServiceDeleteResponseFailure("error: Element with id["+contentId+"] does not exist");
      } else {
        return new ContentServiceDeleteResponseOk();
      }
    } catch (Exception ex) {
      return new ContentServiceDeleteResponseFailure(ex.getMessage());
    }


  }

  @Override
  public int changeStatus(long contentId, ContentStatus status) {

    try {
      CatalogContent content = repository.findById(contentId);
      if (null == content) {
        return -1;
      }

      content.setStatus(status);
      repository.save(content);
      return 0;

    } catch (Exception ex) {
      return -1;
    }
  }

  @Override
  public int addTags(long contentId, Set<String> tags) {

    try {
      CatalogContent content = repository.findById(contentId);
      if (null == content) {
        return -1;
      }

      if (!content.getTags().addAll(tags) ) {
        return -2;
      }

      repository.save(content);
      return 0;

    } catch (Exception ex) {
      return -1;
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
  public List<CatalogContent> getAvailableAfter(ZonedDateTime datetime) {
    return repository.findAvailableAfter(datetime);
  }

  @Override
  public Collection<CatalogContent> listAll() {
    return repository.findAll();
  }
}
