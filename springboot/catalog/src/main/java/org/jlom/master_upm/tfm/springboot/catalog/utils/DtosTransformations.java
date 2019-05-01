package org.jlom.master_upm.tfm.springboot.catalog.utils;

import org.jlom.master_upm.tfm.springboot.catalog.model.CatalogContent;
import org.jlom.master_upm.tfm.springboot.catalog.view.api.dtos.InputCatalogContent;

import java.util.List;
import java.util.stream.Collectors;

public class DtosTransformations {

  public static InputCatalogContent
  serviceToViewContent(CatalogContent content) {
    return InputCatalogContent.builder()
            .title(content.getTitle())
            .streamId(String.valueOf(content.getStreamId()))
            .available(content.getAvailable())
            .tags(content.getTags())
            .contentId(String.valueOf(content.getContentId()))
            .contentStatus(content.getStatus().name())
            .build();

  }

  public static List<InputCatalogContent>
  serviceToViewContent(List<CatalogContent> content) {
    return content.stream().map(DtosTransformations::serviceToViewContent).collect(Collectors.toList());

  }
}
