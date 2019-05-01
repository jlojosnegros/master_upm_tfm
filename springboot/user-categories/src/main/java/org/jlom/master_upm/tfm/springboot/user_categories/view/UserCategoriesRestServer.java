package org.jlom.master_upm.tfm.springboot.user_categories.view;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.jlom.master_upm.tfm.springboot.user_categories.controller.UserCategoriesService;
import org.jlom.master_upm.tfm.springboot.user_categories.controller.api.dtos.CatalogContent;
import org.jlom.master_upm.tfm.springboot.user_categories.controller.api.dtos.UserCategoriesServiceResponse;
import org.jlom.master_upm.tfm.springboot.user_categories.model.daos.ContentPackage;
import org.jlom.master_upm.tfm.springboot.user_categories.model.daos.UserCategory;
import org.jlom.master_upm.tfm.springboot.user_categories.utils.DtosTransformations;
import org.jlom.master_upm.tfm.springboot.user_categories.view.api.UserCategoriesQueryInterface;
import org.jlom.master_upm.tfm.springboot.user_categories.view.api.UserCategoriesCommandInterface;
import org.jlom.master_upm.tfm.springboot.user_categories.view.api.dtos.InputCatalogContent;
import org.jlom.master_upm.tfm.springboot.user_categories.view.api.dtos.InputContentPackage;
import org.jlom.master_upm.tfm.springboot.user_categories.view.api.dtos.InputUserCategory;
import org.jlom.master_upm.tfm.springboot.user_categories.view.api.dtos.InputUserCategoryData;
import org.jlom.master_upm.tfm.springboot.user_categories.view.api.dtos.InputUserContentFiltered;
import org.jlom.master_upm.tfm.springboot.user_categories.view.api.dtos.InputUserPackages;
import org.jlom.master_upm.tfm.springboot.user_categories.view.api.dtos.InputUserWithCategory;
import org.jlom.master_upm.tfm.springboot.user_categories.view.exceptions.InvalidParamException;
import org.jlom.master_upm.tfm.springboot.user_categories.view.exceptions.WrapperException;
import org.jlom.master_upm.tfm.springboot.user_categories.view.response_handlers.FilterUserCategoriesResponseHandler;
import org.jlom.master_upm.tfm.springboot.user_categories.view.response_handlers.ModifyUserUserCategoriesResponseHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static org.jlom.master_upm.tfm.springboot.user_categories.utils.DtosTransformations.serviceToView;
import static org.jlom.master_upm.tfm.springboot.user_categories.utils.JsonUtils.ObjectToJson;
import static org.jlom.master_upm.tfm.springboot.user_categories.utils.JsonUtils.listToJson;


@RestController
@RequestMapping("/categories")
@Validated
public class UserCategoriesRestServer implements UserCategoriesQueryInterface, UserCategoriesCommandInterface {

  private static final Logger LOG = LoggerFactory.getLogger(UserCategoriesRestServer.class);


  private final UserCategoriesService service;

  public UserCategoriesRestServer(UserCategoriesService service) {
    this.service = service;
  }

  @Override
  public ResponseEntity<?> filter(HttpServletRequest request,
                                  @Valid InputUserContentFiltered inputUserContentFiltered) {

    long userId = Long.parseLong(inputUserContentFiltered.getUserId());
    List<InputCatalogContent> inputContents = inputUserContentFiltered.getContents();

    List<CatalogContent> contents = inputContents.stream()
            .map(DtosTransformations::viewToService).collect(Collectors.toList());

    UserCategoriesServiceResponse response = service.filter(userId, contents);

    return response.accept(new FilterUserCategoriesResponseHandler(request));
  }

  @Override
  public ResponseEntity<?> addUser(HttpServletRequest request, @Valid InputUserCategoryData inputUserCategoryData) {

    long userId = Long.parseLong(inputUserCategoryData.getUserId());
    String categoryId = inputUserCategoryData.getCategoryId();
    Set<String> packageIds = inputUserCategoryData.getPackageIds();

    UserCategoriesServiceResponse response = service.addUser(userId, categoryId, packageIds);

    return response.accept(new ModifyUserUserCategoriesResponseHandler(request));
  }

  @Override
  public ResponseEntity<?> removeUser(HttpServletRequest request,
                                      @Valid long userId) {


    UserCategoriesServiceResponse response = service.removeUser(userId);
    return response.accept(new ModifyUserUserCategoriesResponseHandler(request));
  }

  @Override
  public ResponseEntity<?> changeCategory(HttpServletRequest request,
                                          @Valid InputUserCategoryData inputUserCategoryData) {

    long userId = Long.parseLong(inputUserCategoryData.getUserId());
    String categoryId = inputUserCategoryData.getCategoryId();

    UserCategoriesServiceResponse response = service.changeCategoryForUser(userId, categoryId);
    return response.accept(new ModifyUserUserCategoriesResponseHandler(request));
  }

  @Override
  public ResponseEntity<?> addPackages(HttpServletRequest request,
                                       @Valid InputUserCategoryData inputUserCategoryData) {

    long userId = Long.parseLong(inputUserCategoryData.getUserId());
    Set<String> packageIds = inputUserCategoryData.getPackageIds();

    UserCategoriesServiceResponse response = service.addPackageToUser(userId, packageIds);

    return response.accept(new ModifyUserUserCategoriesResponseHandler(request));
  }

  @Override
  public ResponseEntity<?> removePackages(HttpServletRequest request,
                                          @Valid InputUserCategoryData inputUserCategoryData) {

    long userId = Long.parseLong(inputUserCategoryData.getUserId());
    Set<String> packageIds = inputUserCategoryData.getPackageIds();

    UserCategoriesServiceResponse response = service.removePackageFromUser(userId, packageIds);
    return response.accept(new ModifyUserUserCategoriesResponseHandler(request));
  }

  @Override
  public ResponseEntity<?> listPackagesForUser(HttpServletRequest request, long userId) {

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
      return new ResponseEntity<>(json,new HttpHeaders(), HttpStatus.OK);
    } catch (JsonProcessingException e) {
      throw new WrapperException("error: Unable to convertToJson obj: " + userPackages, e);
    }
  }

  @Override
  public ResponseEntity<?> getCategoryForUser(HttpServletRequest request, long userId) {

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
      return new ResponseEntity<>(json,new HttpHeaders(), HttpStatus.OK);
    } catch (JsonProcessingException e) {
      throw new WrapperException("error: Unable to convertToJson obj: " + toSend, e);
    }

  }

  @Override
  public ResponseEntity<?> listAllPackages(HttpServletRequest request) {

    List<ContentPackage> contentPackages = service.listAllPackages();

    List<InputContentPackage> toSend = contentPackages.stream()
            .map(DtosTransformations::serviceToView)
            .collect(Collectors.toList());
    try {
      String json = listToJson(toSend);
      return new ResponseEntity<>(json,new HttpHeaders(), HttpStatus.OK);
    } catch (JsonProcessingException e) {
      throw new WrapperException("error: Unable to convertToJson obj: " + toSend, e);
    }

  }

  @Override
  public ResponseEntity<?> listAllCategories(HttpServletRequest request) {

    List<UserCategory> userCategories = service.listAllCategories();

    List<InputUserCategory> toSend = userCategories.stream()
            .map(DtosTransformations::serviceToView)
            .collect(Collectors.toList());
    try {
      String json = listToJson(toSend);
      return new ResponseEntity<>(json,new HttpHeaders(), HttpStatus.OK);
    } catch (JsonProcessingException e) {
      throw new WrapperException("error: Unable to convertToJson obj: " + toSend, e);
    }

  }

}
