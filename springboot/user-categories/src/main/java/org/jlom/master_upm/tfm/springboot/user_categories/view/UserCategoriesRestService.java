package org.jlom.master_upm.tfm.springboot.user_categories.view;

import org.jlom.master_upm.tfm.springboot.user_categories.controller.UserCategoriesService;
import org.jlom.master_upm.tfm.springboot.user_categories.view.api.UserCategoriesQueryInterface;
import org.jlom.master_upm.tfm.springboot.user_categories.view.api.UserCategoriesCommandInterface;
import org.jlom.master_upm.tfm.springboot.user_categories.view.api.dtos.InputUserCategoryData;
import org.jlom.master_upm.tfm.springboot.user_categories.view.api.dtos.InputUserContentFiltered;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;


@RestController
@RequestMapping("/stream-control")
@Validated
public class UserCategoriesRestService implements UserCategoriesQueryInterface, UserCategoriesCommandInterface {

  private static final Logger LOG = LoggerFactory.getLogger(UserCategoriesRestService.class);


  private final UserCategoriesService service;

  public UserCategoriesRestService(UserCategoriesService service) {
    this.service = service;
  }

  @Override
  public ResponseEntity<?> filter(HttpServletRequest request, @Valid InputUserContentFiltered inputUserContentFiltered) {
    return null;
  }

  @Override
  public ResponseEntity<?> addUser(HttpServletRequest request, @Valid InputUserCategoryData inputUserCategoryData) {
    return null;
  }

  @Override
  public ResponseEntity<?> removeUser(HttpServletRequest request, @Valid InputUserCategoryData inputUserCategoryData) {
    return null;
  }

  @Override
  public ResponseEntity<?> changeCategory(HttpServletRequest request, @Valid InputUserCategoryData inputUserCategoryData) {
    return null;
  }

  @Override
  public ResponseEntity<?> addPackages(HttpServletRequest request, @Valid InputUserCategoryData streamData) {
    return null;
  }

  @Override
  public ResponseEntity<?> removePackages(HttpServletRequest request, @Valid InputUserCategoryData streamData) {
    return null;
  }

  @Override
  public ResponseEntity<?> listPackagesForUser(HttpServletRequest request, long userId) {
    return null;
  }

  @Override
  public ResponseEntity<?> getCategoryForUser(HttpServletRequest request, long userId) {
    return null;
  }

  @Override
  public ResponseEntity<?> listAllPackages(HttpServletRequest request) {
    return null;
  }

  @Override
  public ResponseEntity<?> listAllCategories(HttpServletRequest request) {
    return null;
  }


  // @Override
  // public ResponseEntity<?> addDeviceToUser(HttpServletRequest request, @Valid InputUserCategoryData userDevice) {
  //   LOG.info("addDeviceToUser: userDevice=" + userDevice);


  //   StreamControlData ud = viewToService(userDevice);

  //   UserCategoriesServiceResponse response = service.addDevicesToUser(ud.getUserId(), ud.getDevices());

  //   return response.accept(new CreateUserCategoriesResponseHandler(request));
  // }

 

  // @Override
  // public ResponseEntity<?> listAllUsers(HttpServletRequest request) {
  //   LOG.info("listAllUsers");
  //   List<InputUserCategoryData> output = service.listAll()
  //           .stream()
  //           .map(DtosTransformations::serviceToView)
  //           .collect(Collectors.toList());

  //   try {
  //     return new ResponseEntity<>(ListToJson(output), new HttpHeaders(), HttpStatus.OK);
  //   } catch (JsonProcessingException e) {
  //     throw new WrapperException("error: Unable to convertToJson obj: " + output, e);
  //   }
  // }

  
}
