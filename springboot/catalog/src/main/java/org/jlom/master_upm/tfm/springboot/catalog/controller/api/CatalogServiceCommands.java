package org.jlom.master_upm.tfm.springboot.catalog.controller.api;

import org.jlom.master_upm.tfm.springboot.catalog.controller.api.dtos.ContentServiceCreateResponse;
import org.jlom.master_upm.tfm.springboot.catalog.controller.api.dtos.ContentServiceDeleteResponse;
import org.jlom.master_upm.tfm.springboot.catalog.model.CatalogContent;
import org.jlom.master_upm.tfm.springboot.catalog.model.ContentStatus;

import java.util.Set;

public interface CatalogServiceCommands {
  ContentServiceCreateResponse createContent(long contentId, long streamId, String title, Set<String> tags);
  ContentServiceDeleteResponse deleteContent(long contentId);
  CatalogContent changeStatus(long contentId, ContentStatus status);
  CatalogContent addTags(long contentId, Set<String> tags);
}
