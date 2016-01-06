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
package de.braintags.netrelay.controller.impl.persistence;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import de.braintags.io.vertx.pojomapper.mapping.IMapper;
import de.braintags.netrelay.controller.impl.AbstractCaptureController.CaptureMap;
import de.braintags.netrelay.exception.FileNameException;
import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.MultiMap;
import io.vertx.core.file.FileSystem;
import io.vertx.ext.web.FileUpload;
import io.vertx.ext.web.RoutingContext;

/**
 * 
 * 
 * @author Michael Remme
 * 
 */
public class InsertAction extends AbstractAction {
  private static final io.vertx.core.logging.Logger LOGGER = io.vertx.core.logging.LoggerFactory
      .getLogger(InsertAction.class);

  public static final String MOVE_MESSAGE = "moved uploaded file from %s to %s";

  /**
   * 
   */
  public InsertAction(PersistenceController persitenceController) {
    super(persitenceController);
  }

  @Override
  final void handle(String entityName, RoutingContext context, CaptureMap captureMap,
      Handler<AsyncResult<Void>> handler) {
    IMapper mapper = getMapper(entityName);
    Map<String, String> params = extractProperties(entityName, captureMap, context, mapper);
    handleFileUploads(entityName, context, params);

    getPersistenceController().getMapperFactory().getStoreObjectFactory().createStoreObject(params, mapper, result -> {
      if (result.failed()) {
        handler.handle(Future.failedFuture(result.cause()));
      } else {
        Object ob = result.result().getEntity();
        saveObjectInDatastore(ob, entityName, context, mapper, handler);
      }
    });
  }

  private void handleFileUploads(String entityName, RoutingContext context, Map<String, String> params) {
    String startKey = entityName.toLowerCase() + ".";
    Set<FileUpload> fileUploads = context.fileUploads();
    for (FileUpload upload : fileUploads) {
      String fieldName = upload.name().toLowerCase();
      if (fieldName.startsWith(startKey)) {
        LOGGER.info("uploaded file detected for field name " + fieldName + ", fileName: " + upload.fileName());
        try {
          String relativePath = handleOneFile(upload);
          String pureKey = fieldName.substring(startKey.length());
          params.put(pureKey, relativePath);
        } catch (Exception e) {
          context.fail(e);
        }
      }
    }
  }

  private String handleOneFile(FileUpload upload) {
    String uploadedFile = upload.uploadedFileName();
    FileSystem fs = getPersistenceController().getVertx().fileSystem();
    String[] newDestination = examineNewDestination(fs, upload);
    fs.moveBlocking(uploadedFile, newDestination[0]);

    LOGGER.info(String.format(MOVE_MESSAGE, uploadedFile, newDestination[0]));
    return newDestination[1];
  }

  private String[] examineNewDestination(FileSystem fs, FileUpload upload) {
    if (upload.fileName() == null || upload.fileName().hashCode() == 0) {
      throw new FileNameException("The upload contains no filename");
    }
    String[] destinations = new String[2];
    String upDir = getPersistenceController().readProperty(PersistenceController.UPLOAD_DIRECTORY_PROP, null, true);
    if (!fs.existsBlocking(upDir)) {
      fs.mkdirsBlocking(upDir);
    }
    String relDir = getPersistenceController().readProperty(PersistenceController.UPLOAD_RELATIVE_PATH_PROP, null,
        true);
    String fileName = createUniquName(fs, upDir, upload.fileName());
    destinations[0] = upDir + (upDir.endsWith("/") ? "" : "/") + fileName;
    destinations[1] = relDir + (relDir.endsWith("/") ? "" : "/") + fileName;
    return destinations;
  }

  private String createUniquName(FileSystem fs, String upDir, String fileName) {
    String newFileName = fileName;
    int counter = 0;
    while (fs.existsBlocking(upDir + (upDir.endsWith("/") ? "" : "/") + newFileName)) {
      if (fileName.indexOf('.') > 0) {
        newFileName.replaceFirst(".", counter + ".");
      } else {
        newFileName = fileName + counter;
      }
    }
    newFileName = newFileName.replaceAll(" ", "_");
    return newFileName;
  }

  /**
   * Extract the properties from the request, where the name starts with the entity name, which shall be handled by the
   * current request
   * 
   * @param entityName
   *          the name, like it was specified by the parameter {@link PersistenceController#MAPPER_KEY}
   * @param captureMap
   *          the resolved capture parameters for the current request
   * @param context
   *          the {@link RoutingContext} of the request
   * @param mapper
   *          the IMapper for the current request
   * @return the key / values of the request, where the key starts with "entityName.". The key is reduced to the pure
   *         name
   */
  protected Map<String, String> extractProperties(String entityName, CaptureMap captureMap, RoutingContext context,
      IMapper mapper) {
    String startKey = entityName.toLowerCase() + ".";
    Map<String, String> map = new HashMap<>();
    extractPropertiesFromMap(startKey, map, context.request().formAttributes());
    extractPropertiesFromMap(startKey, map, context.request().params());
    return map;
  }

  /**
   * @param startKey
   * @param map
   * @param attrs
   */
  private void extractPropertiesFromMap(String startKey, Map<String, String> map, MultiMap attrs) {
    Iterator<Entry<String, String>> it = attrs.iterator();
    while (it.hasNext()) {
      Entry<String, String> entry = it.next();
      String key = entry.getKey().toLowerCase();
      if (key.startsWith(startKey)) {
        String pureKey = key.substring(startKey.length());
        String value = entry.getValue();
        map.put(pureKey, value);
      }
    }
  }

}
