package de.braintags.netrelay.util;
/*
 * #%L
 * netrelay
 * %%
 * Copyright (C) 2015 Braintags GmbH
 * %%
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * #L%
 */


import io.vertx.core.buffer.Buffer;
import io.vertx.core.http.HttpClientRequest;

/**
 * This utility class provides an abstraction layer for sending multipart HTTP
 * POST requests to a web server.
 * 
 * @author Michael Remme
 * 
 */
public class MultipartUtil {
  private final String boundary;
  private static final String LINE_FEED = "\r\n";
  private String charset;
  private String contentType;
  private Buffer writer;

  /**
   * Constructor with defautl charset UTF-8 and content type multipart/form-data
   * 
   */
  public MultipartUtil() {
    this("UTF-8", "multipart/form-data");
  }

  /**
   * This constructor initializes a new HTTP POST request with content type
   * is set to multipart/form-data
   * 
   * @param charset
   * @param contentType
   *          the content type to be used
   */
  public MultipartUtil(String charset, String contentType) {
    this.charset = charset;
    this.contentType = contentType;
    // creates a unique boundary based on time stamp
    boundary = "===" + System.currentTimeMillis() + "===";
    writer = Buffer.buffer();
  }

  /**
   * Adds a form field to the request
   * 
   * @param name
   *          field name
   * @param value
   *          field value
   */
  public void addFormField(String name, String value) {
    writer.appendString("--" + boundary).appendString(LINE_FEED);
    writer.appendString("Content-Disposition: form-data; name=\"" + name + "\"").appendString(LINE_FEED);
    writer.appendString("Content-Type: text/plain; charset=" + charset).appendString(LINE_FEED);
    writer.appendString(LINE_FEED);
    writer.appendString(value).appendString(LINE_FEED);
  }

  /**
   * Adds a upload file section to the request
   * 
   * @param fieldName
   *          name attribute in <input type="file" name="..." />
   * @param uploadFile
   *          a File to be uploaded
   * @param contentType
   *          the content type to be set for the attachment
   * @param fileData
   *          the data of the file
   */
  public void addFilePart(String fieldName, String fileName, String contentType, Buffer fileData) {
    writer.appendString("--" + boundary).appendString(LINE_FEED);
    writer.appendString("Content-Disposition: form-data; name=\"").appendString(fieldName)
        .appendString("\"; filename=\"").appendString(fileName).appendString("\"").appendString(LINE_FEED);
    writer.appendString("Content-Type: ").appendString(contentType).appendString(LINE_FEED);
    writer.appendString("Content-Transfer-Encoding: binary").appendString(LINE_FEED);
    writer.appendString(LINE_FEED);
    writer.appendBuffer(fileData);
    writer.appendString(LINE_FEED);
  }

  /**
   * Adds a header field to the request.
   * 
   * @param name
   *          - name of the header field
   * @param value
   *          - value of the header field
   */
  public void addHeaderField(String name, String value) {
    writer.appendString(name + ": " + value).appendString(LINE_FEED);
  }

  /**
   * Completes the request and writes the buffer to the connection
   * 
   */
  public void finish(HttpClientRequest httpConn) {
    httpConn.headers().set("Content-Type", contentType + "; boundary=" + boundary);
    httpConn.headers().set("User-Agent", "CodeJava Agent");
    httpConn.headers().set("Test", "Bonjour");

    // writer.appendString(LINE_FEED);
    writer.appendString("--" + boundary + "--").appendString(LINE_FEED);
    httpConn.headers().set("content-length", String.valueOf(writer.length()));
    httpConn.write(writer);
  }
}
