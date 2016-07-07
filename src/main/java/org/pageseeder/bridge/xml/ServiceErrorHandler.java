package org.pageseeder.bridge.xml;

import org.pageseeder.bridge.http.ServiceError;
import org.xml.sax.Attributes;

/**
 * Handler for services error responses.
 *
 * @author Christophe Lauret
 *
 * @version 0.9.2
 * @since 0.9.2
 */
public final class ServiceErrorHandler extends BasicHandler<ServiceError> {

  /** The error id */
  private int id = -1;

  /** The message with the service error */
  private String message;

  @Override
  public void startElement(String element, Attributes atts) {
    if (isElement("error")) {
      try {
        this.id = Integer.parseInt(atts.getValue("id"), 16);
      } catch (NumberFormatException ex) {
        // Probably not a service error
      }
    } else if (isElement("message") && isParent("error")) {
      newBuffer();
    }
  }

  @Override
  public void endElement(String element) {
    if (isElement("message") && isParent("error")) {
      this.message = buffer(true);
    } else if (isElement("error")) {
      if (this.id >= 0) {
        ServiceError error = new ServiceError(this.id, this.message);
        add(error);
      }
      // There usually is only one error, so no need to reset
    }
  }

}