package org.jlom.master_upm.tfm.graalvm.user_categories.utils;



import org.jlom.master_upm.tfm.graalvm.user_categories.controller.api.dtos.CatalogContent;
import org.jlom.master_upm.tfm.graalvm.user_categories.controller.api.dtos.ContentStatus;
import org.jlom.master_upm.tfm.graalvm.user_categories.model.daos.ContentPackage;
import org.jlom.master_upm.tfm.graalvm.user_categories.model.daos.UserCategory;
import org.jlom.master_upm.tfm.graalvm.user_categories.model.daos.UserData;
import org.jlom.master_upm.tfm.graalvm.user_categories.view.api.dtos.InputCatalogContent;
import org.jlom.master_upm.tfm.graalvm.user_categories.view.api.dtos.InputContentPackage;
import org.jlom.master_upm.tfm.graalvm.user_categories.view.api.dtos.InputUserCategory;
import org.jlom.master_upm.tfm.graalvm.user_categories.view.api.dtos.InputUserCategoryData;

import java.util.Date;
import java.util.Set;

public class DtosTransformations {


  public static InputContentPackage serviceToView(ContentPackage contentPackage) {

    return InputContentPackage.builder()
            .name(contentPackage.getName())
            .price(contentPackage.getPrice())
            .packageId(contentPackage.getPackageId())
            .tagsFilter(contentPackage.getTagsFilter())
            .build();

  }

  public static InputCatalogContent serviceToView(final CatalogContent catalogContent) {
    return InputCatalogContent.builder()
            .contentId(String.valueOf(catalogContent.getContentId()))
            .streamId(String.valueOf(catalogContent.getStreamId()))
            .title(catalogContent.getTitle())
            .tags(catalogContent.getTags())
            .contentStatus(catalogContent.getStatus().toString())
            .build();
  }

  public static CatalogContent viewToService(final InputCatalogContent inputCatalogContent) {
    long contentId = Long.parseLong(inputCatalogContent.getContentId());
    String title = inputCatalogContent.getTitle();
    long streamId = Long.parseLong(inputCatalogContent.getStreamId());
    Set<String> tags = inputCatalogContent.getTags();
    Date available = inputCatalogContent.getAvailable();
    ContentStatus status  = ContentStatus.valueOf(inputCatalogContent.getContentStatus());

    return new CatalogContent(contentId,title,streamId,tags,available,status);
  }

  public static InputUserCategoryData serviceToView(UserData userData) {
    return InputUserCategoryData.builder()
            .userId(String.valueOf(userData.getUserId()))
            .categoryId(userData.getCategoryId())
            .packageIds(userData.getPackageIds())
            .build();
  }

  public static InputUserCategory serviceToView(final UserCategory userCategory) {

    return InputUserCategory.builder()
            .name(userCategory.getName())
            .categoryId(userCategory.getCategoryId())
            .price(userCategory.getPrice())
            .tagId(userCategory.getTagId())
            .build();
  }
}