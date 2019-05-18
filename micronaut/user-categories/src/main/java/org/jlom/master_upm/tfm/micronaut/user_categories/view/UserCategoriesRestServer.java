package org.jlom.master_upm.tfm.micronaut.user_categories.view;

import com.fasterxml.jackson.core.JsonProcessingException;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.annotation.Controller;

import io.micronaut.validation.Validated;
import org.jlom.master_upm.tfm.micronaut.user_categories.controller.UserCategoriesService;
import org.jlom.master_upm.tfm.micronaut.user_categories.controller.api.dtos.CatalogContent;
import org.jlom.master_upm.tfm.micronaut.user_categories.controller.api.dtos.UserCategoriesServiceResponse;
import org.jlom.master_upm.tfm.micronaut.user_categories.model.daos.ContentPackage;
import org.jlom.master_upm.tfm.micronaut.user_categories.model.daos.UserCategory;
import org.jlom.master_upm.tfm.micronaut.user_categories.utils.DtosTransformations;
import org.jlom.master_upm.tfm.micronaut.user_categories.view.api.UserCategoriesCommandInterface;
import org.jlom.master_upm.tfm.micronaut.user_categories.view.api.UserCategoriesQueryInterface;
import org.jlom.master_upm.tfm.micronaut.user_categories.view.api.dtos.InputCatalogContent;
import org.jlom.master_upm.tfm.micronaut.user_categories.view.api.dtos.InputContentPackage;
import org.jlom.master_upm.tfm.micronaut.user_categories.view.api.dtos.InputUserCategory;
import org.jlom.master_upm.tfm.micronaut.user_categories.view.api.dtos.InputUserCategoryData;
import org.jlom.master_upm.tfm.micronaut.user_categories.view.api.dtos.InputUserContentFiltered;
import org.jlom.master_upm.tfm.micronaut.user_categories.view.api.dtos.InputUserPackages;
import org.jlom.master_upm.tfm.micronaut.user_categories.view.api.dtos.InputUserWithCategory;
import org.jlom.master_upm.tfm.micronaut.user_categories.view.exceptions.InvalidParamException;
import org.jlom.master_upm.tfm.micronaut.user_categories.view.exceptions.WrapperException;
import org.jlom.master_upm.tfm.micronaut.user_categories.view.response_handlers.FilterUserCategoriesResponseHandler;
import org.jlom.master_upm.tfm.micronaut.user_categories.view.response_handlers.ModifyUserUserCategoriesResponseHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.validation.Valid;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static org.jlom.master_upm.tfm.micronaut.user_categories.utils.JsonUtils.ObjectToJson;
import static org.jlom.master_upm.tfm.micronaut.user_categories.utils.JsonUtils.listToJson;
import static org.jlom.master_upm.tfm.micronaut.user_categories.utils.DtosTransformations.serviceToView;


@Controller("/categories")
@Validated
public class UserCategoriesRestServer implements UserCategoriesQueryInterface, UserCategoriesCommandInterface {

  private static final Logger LOG = LoggerFactory.getLogger(UserCategoriesRestServer.class);


  private final UserCategoriesService service;

  public UserCategoriesRestServer(UserCategoriesService service) {
    this.service = service;
  }

  @Override
  public HttpResponse<?> filter(HttpRequest request,
                                @Valid InputUserContentFiltered inputUserContentFiltered) {

    long userId = Long.parseLong(inputUserContentFiltered.getUserId());
    List<InputCatalogContent> inputContents = inputUserContentFiltered.getContents();

    List<CatalogContent> contents = inputContents.stream()
            .map(DtosTransformations::viewToService).collect(Collectors.toList());

    UserCategoriesServiceResponse response = service.filter(userId, contents);

    return response.accept(new FilterUserCategoriesResponseHandler(request));
  }

  @Override
  public HttpResponse<?> addUser(HttpRequest request, @Valid InputUserCategoryData inputUserCategoryData) {

    long userId = Long.parseLong(inputUserCategoryData.getUserId());
    String categoryId = inputUserCategoryData.getCategoryId();
    Set<String> packageIds = inputUserCategoryData.getPackageIds();

    UserCategoriesServiceResponse response = service.addUser(userId, categoryId, packageIds);

    return response.accept(new ModifyUserUserCategoriesResponseHandler(request));
  }

  @Override
  public HttpResponse<?> removeUser(HttpRequest request,@Valid long userId) {
    UserCategoriesServiceResponse response = service.removeUser(userId);
    return response.accept(new ModifyUserUserCategoriesResponseHandler(request));
  }

  @Override
  public HttpResponse<?> changeCategory(HttpRequest request,
                                          @Valid InputUserCategoryData inputUserCategoryData) {

    long userId = Long.parseLong(inputUserCategoryData.getUserId());
    String categoryId = inputUserCategoryData.getCategoryId();

    UserCategoriesServiceResponse response = service.changeCategoryForUser(userId, categoryId);
    return response.accept(new ModifyUserUserCategoriesResponseHandler(request));
  }

  @Override
  public HttpResponse<?> addPackages(HttpRequest request,
                                       @Valid InputUserCategoryData inputUserCategoryData) {

    long userId = Long.parseLong(inputUserCategoryData.getUserId());
    Set<String> packageIds = inputUserCategoryData.getPackageIds();

    UserCategoriesServiceResponse response = service.addPackageToUser(userId, packageIds);

    return response.accept(new ModifyUserUserCategoriesResponseHandler(request));
  }

  @Override
  public HttpResponse<?> removePackages(HttpRequest request,
                                          @Valid InputUserCategoryData inputUserCategoryData) {

    long userId = Long.parseLong(inputUserCategoryData.getUserId());
    Set<String> packageIds = inputUserCategoryData.getPackageIds();

    UserCategoriesServiceResponse response = service.removePackageFromUser(userId, packageIds);
    return response.accept(new ModifyUserUserCategoriesResponseHandler(request));
  }

  @Override
  public HttpResponse<?> listPackagesForUser(HttpRequest request, long userId) {

    List<ContentPackage> packagesForUser = service.getPackagesForUser(userId);
    if (null == packagesForUser) {
      throw new InvalidParamException("userId: " +userId, "UserId not found");
    }

    List<InputContentPackage> packages = packagesForUser.stream()
            .map(DtosTransformations::serviceToView)
            .collect(Collectors.toList());

    InputUserPackages userPackages = InputUserPackages.builder()
            .userId(String.valueOf(userId))
            .packages(packages)
            .build();

    try {
      String json = ObjectToJson(userPackages);
      return HttpResponse.ok(json);
    } catch (JsonProcessingException e) {
      throw new WrapperException("error: Unable to convertToJson obj: " + userPackages, e);
    }
  }

  @Override
  public HttpResponse<?> getCategoryForUser(HttpRequest request, long userId) {

    UserCategory categoryForUser = service.getCategoryForUser(userId);
    if (null == categoryForUser) {
      throw new InvalidParamException("userId: " +userId, "UserId not found");
    }

    InputUserCategory inputUserCategory = serviceToView(categoryForUser);
    InputUserWithCategory toSend = InputUserWithCategory.builder()
            .userId(String.valueOf(userId))
            .userCategory(inputUserCategory)
            .build();

    try {
      String json = ObjectToJson(toSend);
      return HttpResponse.ok(json);
    } catch (JsonProcessingException e) {
      throw new WrapperException("error: Unable to convertToJson obj: " + toSend, e);
    }

  }

  @Override
  public HttpResponse<?> listAllPackages(HttpRequest request) {

    List<ContentPackage> contentPackages = service.listAllPackages();

    List<InputContentPackage> toSend = contentPackages.stream()
            .map(DtosTransformations::serviceToView)
            .collect(Collectors.toList());
    try {
      String json = listToJson(toSend);
      return HttpResponse.ok(json);
    } catch (JsonProcessingException e) {
      throw new WrapperException("error: Unable to convertToJson obj: " + toSend, e);
    }

  }

  @Override
  public HttpResponse<?> listAllCategories(HttpRequest request) {

    List<UserCategory> userCategories = service.listAllCategories();

    List<InputUserCategory> toSend = userCategories.stream()
            .map(DtosTransformations::serviceToView)
            .collect(Collectors.toList());
    try {
      String json = listToJson(toSend);
      return HttpResponse.ok(json);
    } catch (JsonProcessingException e) {
      throw new WrapperException("error: Unable to convertToJson obj: " + toSend, e);
    }

  }

}
