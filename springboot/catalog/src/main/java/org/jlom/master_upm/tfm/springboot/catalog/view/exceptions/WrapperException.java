package org.jlom.master_upm.tfm.springboot.catalog.view.exceptions;

public class WrapperException extends RuntimeException{

  private static final long serialVersionUID = 1L;

  public WrapperException(String message) {
    super(message,null);
  }
  public WrapperException(Throwable wrappedException) {
    this("",wrappedException);
  }

  public WrapperException(String message, Throwable wrappedException) {
    super(message);
    initCause(wrappedException);
  }

  public String getMessage() {
    String superMessage = super.getMessage();
    String wrappedMessage = (getCause()!= null)? getCause().getMessage() : null;
    String ret = "";

    if (null != superMessage) {
      ret = ret.concat(superMessage + " : ");
    }

    if (null != wrappedMessage) {
      ret = ret.concat(wrappedMessage);
    }
    return ret;
  }

  public String toString() {
    String ret = super.toString();
    if (getCause() != null) {
      ret = ret + "; \r\n\t---> Caused by " + getCause().toString();
    }
    return ret;
  }
}
