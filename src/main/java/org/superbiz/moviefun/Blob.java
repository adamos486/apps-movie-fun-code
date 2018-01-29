package org.superbiz.moviefun;

import java.io.InputStream;

public class Blob {
  public final String name;
  public final InputStream is;
  public final String contentType;

  public Blob(String name, InputStream is, String contentType) {
    this.name = name;
    this.is = is;
    this.contentType = contentType;
  }

  public String getName() {
    return name;
  }

  public InputStream getIs() {
    return is;
  }

  public String getContentType() {
    return contentType;
  }


}
