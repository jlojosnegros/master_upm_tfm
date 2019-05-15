package org.jlom.master_upm.tfm.micronaut.catalog.controller.api;

import org.jlom.master_upm.tfm.micronaut.catalog.controller.api.dtos.ContentServiceResponse;
import org.jlom.master_upm.tfm.micronaut.catalog.model.ContentStatus;

import java.util.Set;

public interface CatalogServiceCommands {
  ContentServiceResponse createContent(long streamId, String title, ContentStatus status, Set<String> tags);
  ContentServiceResponse deleteContent(long contentId);
  ContentServiceResponse changeStatus(long contentId, ContentStatus status);
  ContentServiceResponse addTags(long contentId, Set<String> tags);
}

