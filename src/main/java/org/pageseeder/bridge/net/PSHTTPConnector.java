/*
 * Copyright 2015 Allette Systems (Australia)
 * http://www.allette.com.au
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.pageseeder.bridge.net;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Map;

import javax.xml.transform.Templates;

import org.pageseeder.bridge.APIException;
import org.pageseeder.bridge.PSCredentials;
import org.pageseeder.bridge.PSSession;
import org.pageseeder.bridge.net.PSHTTPConnection.Method;
import org.pageseeder.xmlwriter.XMLWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.helpers.DefaultHandler;

/**
 * Represents a request made to the PageSeeder Server.
 *
 * <p>By default the request is made anonymously. In order to make a request on behalf of a
 * PageSeeder user, use the {@link #using(PSSession)} method - this is required for any page
 * that needs login access.
 *
 * <p>For simple PageSeeder connections via GET or POST, this class provides convenience methods
 * which will open and close the connections and capture any error in XML.
 *
 * <p>For example:</p>
 * <pre>
 * PSHTTPResponseInfo connector = new PSConnector(PSResourceType.SERVICE, "/groups/123/members").using(session).get(xml);
 * </pre>
 *
 * <p>For more complex connections, involving multipart queries or if any of the default properties
 * of the connection need to be changed, this class can be used to create the connection to
 * PageSeeder, for example:</p>
 * <pre>
 * PSHTTPConnector connector = new PSHTTPConnector(PSResourceType.SERVICE, "/groups/123/upload").using(session);
 *
 * PSHTTPConnection connection = connector.connect(Type.MULTIPART);
 * connection.addXMLPart(xml1);
 * connection.addXMLPart(xml2);
 * connection.addXMLPart(xml3);
 * connection.disconnect();
 * </pre>
 *
 * <p>Note: This class was forked from Bastille 0.8.29
 *
 * @author Christophe Lauret
 * @version 0.3.6
 * @since 0.2.0
 */
public final class PSHTTPConnector {

  /** Logger for this class */
  private static final Logger LOGGER = LoggerFactory.getLogger(PSHTTPConnector.class);

  /**
   * The type of resource accessed.
   */
  private final PSHTTPResource.Builder _resource;

  /**
   * Credentials that can be used to connect to the connector.
   */
  private PSCredentials credentials = null;

  /**
   * If specified, the request will be made on behalf of that user.
   */
  private PSSession session = null;

  /**
   * Creates a new connection to the specified resource.
   *
   * @param type     The type of resource.
   * @param resource The
   */
  public PSHTTPConnector(PSHTTPResourceType type, String resource) {
    this._resource = new PSHTTPResource.Builder(type, resource);
  }

  /**
   * Sets the session for this request as a chainable method.
   *
   * @param session the user for this request.
   * @return this connector
   */
  public PSHTTPConnector using(PSSession session) {
    this.credentials = session;
    this.session = session;
    return this;
  }

  /**
   * Sets the username and password for this request as a chainable method.
   *
   * @param username the user for this request.
   * @param password the password
   *
   * @return this connector
   */
  public PSHTTPConnector using(String username, String password) {
    if (this.session != null) throw new IllegalStateException("Session already specified.");
    this.credentials = new UsernamePassword(username, password);
    return this;
  }

  /**
   * Sets the user for this request.
   * @param user the user for this request.
   */
  public void setUser(PSSession user) {
    this.session = user;
  }

  /**
   * @return the session which may have been updated after the connection
   */
  public PSSession getSession() {
    return this.session;
  }

  /**
   * Add a parameter to this request.
   *
   * @param name  The name of the parameter
   * @param value The value of the parameter
   */
  public void addParameter(String name, String value) {
    this._resource.addParameter(name, value);
  }

  /**
   * Sets whether this resource should include the error content.
   *
   * @param include <code>true</code> to include the content of response even when the response code
   *                is greater than 400 (included);
   *                <code>false</code> to only include the response when the response code is
   *                between 200 and 299.
   */
  public void includeErrorContent(boolean include) {
    this._resource.includeErrorContent(include);
  }

  // Connection
  // ----------------------------------------------------------------------------------------------

  /**
   * Connect to PageSeeder using the specified method.
   *
   * @param type  The connection type using the specified method
   *
   * @return The PS connection created as a result.
   * @throws IOException If thrown while trying to open the connection or if the URL for the
   *                     underlying resource is malformed.
   */
  public PSHTTPConnection connect(Method type) throws IOException {
    PSHTTPResource r = this._resource.build();
    return PSHTTPConnection.connect(r, type, this.session);
  }

  // Shorthand requests
  // ----------------------------------------------------------------------------------------------

  /**
   * Connect to PageSeeder via GET and discard the output.
   *
   * <p>Error messages will be captured.
   *
   * @throws APIException If an error occurs when trying to write the XML.
   *
   * @return The PageSeeder HTTP response metadata
   */
  public PSHTTPResponseInfo get() throws APIException {
    return handle(Method.GET, (DefaultHandler)null);
  }

  /**
   * Connect to PageSeeder and fetch the content using the GET method to be parsed by a handler.
   *
   * @param out where the output should be copied.
   *
   * @throws APIException Wrap any error while communicating with PageSeeder or parsing the output
   *
   * @return The PageSeeder HTTP response metadata
   */
  public PSHTTPResponseInfo get(OutputStream out) throws APIException {
    return handle(Method.GET, out);
  }

  /**
   * Connect to PageSeeder and fetch the XML using the GET method to be parsed by a handler.
   *
   * @param handler the handler for the XML returned by PageSeeder
   *
   * @throws APIException Wrap any error while communicating with PageSeeder or parsing the output
   *
   * @return The PageSeeder HTTP response metadata
   */
  public PSHTTPResponseInfo get(DefaultHandler handler) throws APIException {
    return handle(Method.GET, handler);
  }

  /**
   * Connect to PageSeeder via GET and copy the output onto the specified XML writer.
   *
   * @param xml the XML to copy the output from PageSeeder
   *
   * @throws APIException Wrap any error while communicating with PageSeeder or writing the XML.
   *
   * @return The PageSeeder HTTP response metadata
   */
  public PSHTTPResponseInfo get(XMLWriter xml) throws APIException {
    return copy(Method.GET, xml);
  }

  /**
   * Connect to PageSeeder via GET and transform the XML output using the specified templates.
   *
   * <p>Templates can be specified to transform the XML returned from PageSeeder. In that case,
   * the XML written out is the result of the transformation.
   *
   * <p>If the templates are omitted, the XML writer received a copy of the output from PageSeeder.
   *
   * @param xml       The result of the transformation or source XML if no templates
   * @param templates A set of templates to process the XML
   *
   * @throws APIException Wrap any error while communicating with PageSeeder, writing or transforming the XML.
   *
   * @return The PageSeeder HTTP response metadata
   */
  public PSHTTPResponseInfo get(XMLWriter xml, Templates templates) throws APIException {
    return transform(Method.GET, xml, templates, null);
  }

  /**
   * Connect to PageSeeder via GET and transform the XML output using the specified templates.
   *
   * <p>Templates can be specified to transform the XML returned from PageSeeder. In that case,
   * the XML written out is the result of the transformation.
   *
   * <p>If the templates are omitted, the XML writer received a copy of the output from PageSeeder.
   *
   * @param xml        The result of the transformation or source XML if no templates
   * @param templates  A set of templates to process the XML
   * @param parameters Parameters to send to the XSLT transformer (optional)
   *
   * @throws APIException Wrap any error while communicating with PageSeeder, writing or transforming the XML.
   *
   * @return The PageSeeder HTTP response metadata
   */
  public PSHTTPResponseInfo get(XMLWriter xml, Templates templates, Map<String, String> parameters)
      throws APIException {
    return transform(Method.GET, xml, templates, parameters);
  }

  /**
   * Connect to PageSeeder via PATCH and discard the output.
   *
   * <p>Error messages will be captured.
   *
   * @throws APIException If an error occurs when trying to write the XML.
   *
   * @return The PageSeeder HTTP response metadata
   */
  public PSHTTPResponseInfo patch() throws APIException {
    return handle(Method.PATCH, (DefaultHandler)null);
  }

  /**
   * Connect to PageSeeder and fetch the XML using the PATCH method to be parsed by a handler.
   *
   * @param handler the handler for the XML returned by PageSeeder
   *
   * @throws APIException Wrap any error while communicating with PageSeeder or parsing the output
   *
   * @return The PageSeeder HTTP response metadata
   */
  public PSHTTPResponseInfo patch(DefaultHandler handler) throws APIException {
    return handle(Method.PATCH, handler);
  }

  /**
   * Connect to PageSeeder via PATCH and copy the output onto the specified XML writer.
   *
   * @param xml the XML to copy the output from PageSeeder
   *
   * @throws APIException Wrap any error while communicating with PageSeeder or writing the XML.
   *
   * @return The PageSeeder HTTP response metadata
   */
  public PSHTTPResponseInfo patch(XMLWriter xml) throws APIException {
    return copy(Method.PATCH, xml);
  }

  /**
   * Connect to PageSeeder via PATCH and transform the XML output using the specified templates.
   *
   * <p>Templates can be specified to transform the XML returned from PageSeeder. In that case,
   * the XML written out is the result of the transformation.
   *
   * <p>If the templates are omitted, the XML writer received a copy of the output from PageSeeder.
   *
   * @param xml       The result of the transformation or source XML if no templates
   * @param templates A set of templates to process the XML
   *
   * @throws APIException Wrap any error while communicating with PageSeeder, writing or transforming the XML.
   *
   * @return The PageSeeder HTTP response metadata
   */
  public PSHTTPResponseInfo patch(XMLWriter xml, Templates templates) throws APIException {
    return transform(Method.PATCH, xml, templates, null);
  }

  /**
   * Connect to PageSeeder via PATCH and transform the XML output using the specified templates.
   *
   * <p>Templates can be specified to transform the XML returned from PageSeeder. In that case,
   * the XML written out is the result of the transformation.
   *
   * <p>If the templates are omitted, the XML writer received a copy of the output from PageSeeder.
   *
   * @param xml        The result of the transformation or source XML if no templates
   * @param templates  A set of templates to process the XML
   * @param parameters Parameters to send to the XSLT transformer (optional)
   *
   * @throws APIException Wrap any error while communicating with PageSeeder, writing or transforming the XML.
   *
   * @return The PageSeeder HTTP response metadata
   */
  public PSHTTPResponseInfo patch(XMLWriter xml, Templates templates, Map<String, String> parameters)
      throws APIException {
    return transform(Method.PATCH, xml, templates, parameters);
  }

  /**
   * Connect to PageSeeder via POST and discard the output.
   *
   * <p>Error messages will be captured.
   *
   * @throws APIException If an error occurs when trying to write the XML.
   *
   * @return The PageSeeder HTTP response metadata
   */
  public PSHTTPResponseInfo post() throws APIException {
    return handle(Method.POST, (DefaultHandler)null);
  }

  /**
   * Connect to PageSeeder and fetch the XML using the POST method to be parsed by a handler.
   *
   * @param handler the handler for the XML returned by PageSeeder
   *
   * @throws APIException Wrap any error while communicating with PageSeeder or parsing the output
   *
   * @return The PageSeeder HTTP response metadata
   */
  public PSHTTPResponseInfo post(DefaultHandler handler) throws APIException {
    return handle(Method.POST, handler);
  }

  /**
   * Connect to PageSeeder via POST and copy the output onto the specified XML writer.
   *
   * @param xml the XML to copy the output from PageSeeder
   *
   * @throws APIException Wrap any error while communicating with PageSeeder or writing the XML.
   *
   * @return The PageSeeder HTTP response metadata
   */
  public PSHTTPResponseInfo post(XMLWriter xml) throws APIException {
    return copy(Method.POST, xml);
  }

  /**
   * Connect to PageSeeder via POST and transform the XML output using the specified templates.
   *
   * <p>Templates can be specified to transform the XML returned from PageSeeder. In that case,
   * the XML written out is the result of the transformation.
   *
   * <p>If the templates are omitted, the XML writer received a copy of the output from PageSeeder.
   *
   * @param xml       The result of the transformation or source XML if no templates
   * @param templates A set of templates to process the XML
   *
   * @throws APIException Wrap any error while communicating with PageSeeder, writing or transforming the XML.
   *
   * @return The PageSeeder HTTP response metadata
   */
  public PSHTTPResponseInfo post(XMLWriter xml, Templates templates) throws APIException {
    return transform(Method.POST, xml, templates, null);
  }

  /**
   * Connect to PageSeeder via POST and transform the XML output using the specified templates.
   *
   * <p>Templates can be specified to transform the XML returned from PageSeeder. In that case,
   * the XML written out is the result of the transformation.
   *
   * <p>If the templates are omitted, the XML writer received a copy of the output from PageSeeder.
   *
   * @param xml        The result of the transformation or source XML if no templates
   * @param templates  A set of templates to process the XML
   * @param parameters Parameters to send to the XSLT transformer (optional)
   *
   * @throws APIException Wrap any error while communicating with PageSeeder, writing or transforming the XML.
   *
   * @return The PageSeeder HTTP response metadata
   */
  public PSHTTPResponseInfo post(XMLWriter xml, Templates templates, Map<String, String> parameters)
      throws APIException {
    return transform(Method.POST, xml, templates, parameters);
  }

  /**
   * Connect to PageSeeder and fetch the XML using the PUT method to be parsed by a handler.
   *
   * @param handler the handler for the XML returned by PageSeeder
   *
   * @throws APIException Wrap any error while communicating with PageSeeder or parsing the output
   *
   * @return The PageSeeder HTTP response metadata
   */
  public PSHTTPResponseInfo put(DefaultHandler handler) throws APIException {
    return handle(Method.PUT, handler);
  }

  /**
   * Connect to PageSeeder via PUT and copy the output onto the specified XML writer.
   *
   * @param xml the XML to copy the output from PageSeeder
   *
   * @throws APIException Wrap any error while communicating with PageSeeder or writing the XML.
   *
   * @return The PageSeeder HTTP response metadata
   */
  public PSHTTPResponseInfo put(XMLWriter xml) throws APIException {
    return copy(Method.PUT, xml);
  }

  /**
   * Connect to PageSeeder and fetch the XML using the DELETE method to be parsed by a handler.
   *
   * @param handler the handler for the XML returned by PageSeeder
   *
   * @throws APIException Wrap any error while communicating with PageSeeder or parsing the output
   *
   * @return The PageSeeder HTTP response metadata
   */
  public PSHTTPResponseInfo delete(DefaultHandler handler) throws APIException {
    return handle(Method.DELETE, handler);
  }

  /**
   * Connect to PageSeeder via DELETE and copy the output onto the specified XML writer.
   *
   * @param xml the XML to copy the output from PageSeeder
   *
   * @throws APIException Wrap any error while communicating with PageSeeder or writing the XML.
   *
   * @return The PageSeeder HTTP response metadata
   */
  public PSHTTPResponseInfo delete(XMLWriter xml) throws APIException {
    return copy(Method.DELETE, xml);
  }

  // Private helpers
  // ----------------------------------------------------------------------------------------------

  /**
   * Connect to PageSeeder and handle the XML response using the specified handler.
   *
   * @param method The HTTP Method to use
   * @param out    Where the output should be copied to
   *
   * @throws APIException Will wrap any I/O error thrown by the underlying connection.
   *
   * @return The PageSeeder HTTP response metadata
   */
  private PSHTTPResponseInfo handle(Method method, OutputStream out) throws APIException {
    PSHTTPResource resource = this._resource.build();
    PSHTTPResponseInfo response = new PSHTTPResponseInfo();
    try {
      PSHTTPConnection connection = PSHTTPConnection.connect(resource, method, this.credentials);
      connection.process(response, out);
      // get the session
      this.session = connection.getSession();
    } catch (IOException ex) {
      throw new APIException(ex);
    } finally {
      LOGGER.info("{} [{}] -> {}", resource, method, response);
    }
    return response;
  }

  /**
   * Connect to PageSeeder and handle the XML response using the specified handler.
   *
   * @param method    The HTTP Method to use
   * @param handler   The content handler to parse the XML
   *
   * @throws APIException Will wrap any I/O error thrown by the underlying connection.
   *
   * @return The PageSeeder HTTP response metadata
   */
  private PSHTTPResponseInfo handle(Method method, DefaultHandler handler) throws APIException {
    PSHTTPResource resource = this._resource.build();
    PSHTTPResponseInfo response = new PSHTTPResponseInfo();
    try {
      PSHTTPConnection connection = PSHTTPConnection.connect(resource, method, this.credentials);
      connection.process(response, handler);
      this.session = connection.getSession();
    } catch (IOException ex) {
      throw new APIException(ex);
    } finally {
      LOGGER.info("{} [{}] -> {}", resource, method, response);
    }
    return response;
  }

  /**
   * Connect to PageSeeder and fetch the XML using the GET method.
   *
   * <p>If the handler is not specified, the xml writer receives a copy of the PageSeeder XML.
   *
   * <p>If templates are specified they take precedence over the handler.
   *
   * @param method  The method
   * @param xml     The XML to copy from PageSeeder
   *
   * @throws APIException If an error occurs when trying to write the XML.
   *
   * @return The PageSeeder HTTP response metadata
   */
  private PSHTTPResponseInfo copy(Method method, XMLWriter xml) throws APIException {
    PSHTTPResource resource = this._resource.build();
    PSHTTPResponseInfo response = new PSHTTPResponseInfo();
    try {
      PSHTTPConnection connection = PSHTTPConnection.connect(resource, method, this.credentials);
      connection.process(response, xml);
      this.session = connection.getSession();
    } catch (IOException ex) {
      throw new APIException(ex);
    } finally {
      LOGGER.info("{} [{}] -> {}", resource, method, response);
    }
    return response;
  }

  /**
   * Connect to PageSeeder and fetch the XML using the GET method.
   *
   * <p>If the handler is not specified, the XML writer receives a copy of the PageSeeder XML.
   *
   * <p>If templates are specified they take precedence over the handler.
   *
   * @param xml        The XML to copy from PageSeeder
   * @param method     The type of connection
   * @param templates  A set of templates to process the XML (optional)
   * @param parameters Parameters to send to the XSLT transformer (optional)
   *
   * @throws APIException If an error occurs when trying to write the XML.
   *
   * @return The PageSeeder HTTP response metadata
   */
  private PSHTTPResponseInfo transform(Method method, XMLWriter xml, Templates templates, Map<String, String> parameters)
      throws APIException {
    PSHTTPResource resource = this._resource.build();
    PSHTTPResponseInfo response = new PSHTTPResponseInfo();
    try {
      PSHTTPConnection connection = PSHTTPConnection.connect(resource, method, this.credentials);
      connection.process(response, xml, templates, parameters);
      this.session = connection.getSession();
    } catch (IOException ex) {
      throw new APIException(ex);
    } finally {
      LOGGER.info("{} [{}] -> {}", resource, method, response);
    }
    return response;
  }

}
