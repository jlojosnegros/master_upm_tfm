package org.jlom.master_upm.tfm.springboot.catalog.controller.api;

import org.jlom.master_upm.tfm.springboot.catalog.controller.api.dtos.ContentServiceResponse;
import org.jlom.master_upm.tfm.springboot.catalog.model.CatalogContent;
import org.jlom.master_upm.tfm.springboot.catalog.model.ContentStatus;

import java.util.Set;

public interface CatalogServiceCommands {
  ContentServiceResponse createContent(long contentId, long streamId, String title, Set<String> tags);
  ContentServiceResponse deleteContent(long contentId);
  CatalogContent changeStatus(long contentId, ContentStatus status);
  CatalogContent addTags(long contentId, Set<String> tags);
}
