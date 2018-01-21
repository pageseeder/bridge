/*
 * Copyright 2017 Allette Systems (Australia)
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
package org.pageseeder.bridge.xml.stax;

import org.pageseeder.bridge.core.Document;
import org.pageseeder.bridge.core.ExternalURI;
import org.pageseeder.bridge.core.Folder;
import org.pageseeder.bridge.core.URI;
import org.pageseeder.bridge.xml.InvalidElementException;
import org.pageseeder.bridge.xml.MissingAttributeException;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

public class XMLStreamURI extends BasicXMLStreamHandler<URI> implements XMLStreamItem<URI> {

  public XMLStreamURI() {
    super("uri");
  }

  @Override
  public URI toItem(XMLStreamReader xml) throws XMLStreamException {
    if (isOnElement(xml)) {
      long id = attribute(xml, "id", -1L);
      if (id == -1L) throw new MissingAttributeException("Missing URI ID");
      boolean isExternal = "true".equals(optionalAttribute(xml, "external"));
      boolean isFolder = "true".equals(optionalAttribute(xml, "folder")) || "folder".equals(optionalAttribute(xml, "mediatype"));
      boolean isArchived = "true".equals(optionalAttribute(xml, "archived"));

      String scheme = attribute(xml, "scheme");
      String host = attribute(xml, "host");
      int port = Integer.parseInt(attribute(xml, "port"));
      String path = attribute(xml, "path");

      String created = optionalAttribute(xml, "created");
      String docid = optionalAttribute(xml, "docid");
      String documenttype = attribute(xml, "documenttype", "default");
      String mediatype = attribute(xml, "mediatype", "default");
      String modified = optionalAttribute(xml, "modified");
      String title = attribute(xml, "title", "");
      long size = attribute(xml, "size", -1);

      URI uri = null;
      if (isExternal) {
        uri = new ExternalURI.Builder()
            .id(id)
            .url(scheme, host, port, path)
            .created(created)
            .modified(modified)
            .mediaType(mediatype)
            .title(title)
            .build();
      } else if (isFolder) {
        uri = new Folder.Builder()
            .id(id)
            .url(scheme, host, port, path)
            .created(created)
            .modified(modified)
            .mediaType(mediatype)
            .title(title)
            .build();
      } else {
        uri = new Document.Builder()
            .id(id)
            .documentType(documenttype)
            .url(scheme, host, port, path)
            .created(created)
            .modified(modified)
            .docid(docid)
            .mediaType(mediatype)
            .title(title)
            .build();
      }

      skipToEndElement(xml, element());
      return uri;
    } else throw new InvalidElementException("not a URI");
  }

}