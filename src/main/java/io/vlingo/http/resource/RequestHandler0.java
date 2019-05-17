/*
 * Copyright © 2012-2018 Vaughn Vernon. All rights reserved.
 *
 * This Source Code Form is subject to the terms of the
 * Mozilla Public License, v. 2.0. If a copy of the MPL
 * was not distributed with this file, You can obtain
 * one at https://mozilla.org/MPL/2.0/.
 */

package io.vlingo.http.resource;

import io.vlingo.actors.Logger;
import io.vlingo.common.Completes;
import io.vlingo.http.Header;
import io.vlingo.http.Method;
import io.vlingo.http.Request;
import io.vlingo.http.Response;
import io.vlingo.http.resource.RequestHandler0.Handler0;

import java.util.Collections;
import java.util.function.Supplier;

class RequestExecutor0 extends RequestExecutor {

  private final Handler0 handler;

  public RequestExecutor0(Handler0 handler) {
    this.handler = handler;
  }

  @Override
  Completes<Response> execute(Request request,
                              Action.MappedParameters mappedParameters,
                              Logger logger) {
    return ;
  }

  RequestExecutor0 from(Handler0 handler) {
    return new RequestExecutor0(handler);
  }
}



public class RequestHandler0 extends RequestHandler {
  private Handler0 handler;
  private ObjectHandler0 objectHandler;

  @FunctionalInterface
  public interface Handler0 {
    Completes<Response> execute();
  }

  @FunctionalInterface
  public interface ObjectHandler0 {
    Completes<ObjectResponse<?>> execute();
  }

  RequestHandler0(final Method method, final String path) {
    super(method, path, Collections.emptyList());
  }

  public RequestHandler0 handle(final Handler0 handler) {
    if (this.objectHandler != null) {
      throw new IllegalArgumentException("Handler already specified via .handle(...)");
    }
    this.handler = handler;
    return this;
  }

  public RequestHandler0 handle(final ObjectHandler0 handler) {
    if (this.handler != null) {
      throw new IllegalArgumentException("Handler already specified via .handle(...)");
    }
    this.objectHandler = handler;
    return this;
  }

  public RequestHandler0 onError(final ErrorHandler errorHandler) {
    this.errorHandler = errorHandler;
    return this;
  }

  public RequestHandler0 mapper(final MediaTypeMapper mediaTypeMapper) {
    this.mediaTypeMapper = mediaTypeMapper;
    return this;
  }

  Completes<Response> execute(final Request request, final Logger logger) {
    return executeFirstValidHandler(request,
                              handler,
                              () -> handler.execute(),
                              objectHandler,
                              () -> objectHandler.execute(),
                              logger);
  }

  @Override
  Completes<Response> execute(final Request request,
                              final Action.MappedParameters mappedParameters,
                              final Logger logger) {
    return execute(request, logger);
  }

  // region FluentAPI
  public <T> RequestHandler1<T> param(final Class<T> paramClass) {
    return new RequestHandler1<>(method, path, ParameterResolver.path(0, paramClass), errorHandler, mediaTypeMapper);
  }

  public <T> RequestHandler1<T> body(final Class<T> paramClass) {
    return new RequestHandler1<>(method, path, ParameterResolver.body(paramClass, mediaTypeMapper), errorHandler, mediaTypeMapper);
  }

  /**
   * Specify the class that represents the body of the request for all requests using the specified mapper for all
   * MIME types regardless of the Content-Type header.
   *
   * @deprecated Deprecated in favor of using the ContentMediaType method, which handles media types appropriately.
   * {@link RequestHandler0#body(java.lang.Class, io.vlingo.http.resource.MediaTypeMapper)} instead, or via
   * {@link RequestHandler0#body(java.lang.Class)}
   */
  @Deprecated
  public <T> RequestHandler1<T> body(final Class<T> paramClass, final Class<? extends Mapper> mapperClass) {
    return body(paramClass, mapperFrom(mapperClass));
  }

  /**
   * Specify the class that represents the body of the request for all requests using the specified mapper for all
   * MIME types regardless of the Content-Type header.
   *
   * @deprecated Deprecated in favor of using the ContentMediaType method, which handles media types appropriately.
   * {@link RequestHandler0#body(java.lang.Class, io.vlingo.http.resource.MediaTypeMapper)} instead, or via
   * {@link RequestHandler0#body(java.lang.Class)}
   */
  @Deprecated
  public <T> RequestHandler1<T> body(final Class<T> paramClass, final Mapper mapper) {
    return new RequestHandler1<>(method, path, ParameterResolver.body(paramClass, mapper), errorHandler, mediaTypeMapper);
  }

  public <T> RequestHandler1<T> body(final Class<T> paramClass, final MediaTypeMapper mediaTypeMapper) {
    this.mediaTypeMapper = mediaTypeMapper;
    return new RequestHandler1<>(method, path, ParameterResolver.body(paramClass, mediaTypeMapper), errorHandler, mediaTypeMapper);
  }

  public RequestHandler1<String> query(final String name) {
    return query(name, String.class);
  }

  public <T> RequestHandler1<T> query(final String name, final Class<T> type) {
    return query(name, type, null);
  }

  public <T> RequestHandler1<T> query(final String name, final Class<T> type, final T defaultValue) {
    return new RequestHandler1<>(method, path, ParameterResolver.query(name, type, defaultValue), errorHandler, mediaTypeMapper);
  }

  public RequestHandler1<Header> header(final String name) {
    return new RequestHandler1<>(method, path, ParameterResolver.header(name), errorHandler, mediaTypeMapper);
  }
  // endregion
}
