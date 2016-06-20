package org.pageseeder.bridge.http;

import java.util.List;

import org.xml.sax.helpers.DefaultHandler;


public abstract class Handler<T> extends DefaultHandler {

  public abstract List<T> list();

  public abstract T get();

}
