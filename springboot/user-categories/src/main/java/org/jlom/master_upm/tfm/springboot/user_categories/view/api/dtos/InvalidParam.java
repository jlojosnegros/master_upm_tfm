package org.jlom.master_upm.tfm.springboot.user_categories.view.api.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Objects;

/**
 * InvalidParam
 */

public class InvalidParam {
  @JsonProperty("param")
  private String param;

  @JsonProperty("reason")
  private String reason;

  public InvalidParam param(String param) {
    this.param = param;
    return this;
  }

  /**
   * Get param
   * @return param
   **/
  //@ApiModelProperty(required = true, value = "")
  public String getParam() {
    return param;
  }

  public void setParam(String param) {
    this.param = param;
  }

  public InvalidParam reason(String reason) {
    this.reason = reason;
    return this;
  }

  /**
   * Get reason
   * @return reason
   **/
  //@ApiModelProperty(value = "")
  public String getReason() {
    return reason;
  }

  public void setReason(String reason) {
    this.reason = reason;
  }


  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    InvalidParam invalidParam = (InvalidParam) o;
    return Objects.equals(this.param, invalidParam.param) &&
            Objects.equals(this.reason, invalidParam.reason);
  }

  @Override
  public int hashCode() {
    return Objects.hash(param, reason);
  }


  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class InvalidParam {\n");
    sb.append("    param: ").append(toIndentedString(param)).append("\n");
    sb.append("    reason: ").append(toIndentedString(reason)).append("\n");
    sb.append("}");
    return sb.toString();
  }

  /**
   * Convert the given object to string with each line indented by 4 spaces
   * (except the first line).
   */
  private String toIndentedString(java.lang.Object o) {
    if (o == null) {
      return "null";
    }
    return o.toString().replace("\n", "\n    ");
  }

}
