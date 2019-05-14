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

import java.util.Arrays;

public class RequestHandler2<T, R> extends RequestHandler {
  final ParameterResolver<T> resolverParam1;
  final ParameterResolver<R> resolverParam2;
  private Handler2<T, R> handler;
  private ObjectHandler2<T, R> objectHandler;
  private ErrorHandler errorHandler;
  private MediaTypeMapper mediaTypeMapper;

  RequestHandler2(final Method method,
                  final String path,
                  final ParameterResolver<T> resolverParam1,
                  final ParameterResolver<R> resolverParam2,
                  final ErrorHandler errorHandler,
                  final MediaTypeMapper mediaTypeMapper) {
    super(method, path, Arrays.asList(resolverParam1, resolverParam2));
    this.resolverParam1 = resolverParam1;
    this.resolverParam2 = resolverParam2;
    this.errorHandler = errorHandler;
    this.mediaTypeMapper = mediaTypeMapper;
  }

  Completes<Response> execute(final Request request, final T param1, final R param2, final Logger logger) {
    checkHandlerOrThrowException(handler, objectHandler);
    if (handler != null) {
      return executeRequest(() -> handler.execute(param1, param2), errorHandler, logger);
    } else {
      return executeObjectRequest(request,
                                  mediaTypeMapper,
                                  () -> objectHandler.execute(param1, param2),
                                  errorHandler,
                                  logger);
    }
  }

  public RequestHandler2<T, R> handle(final Handler2<T, R> handler) {
    if (this.objectHandler != null) {
      throw new IllegalArgumentException("Handler already specified via .handle(...)");
    }
    this.handler = handler;
    return this;
  }

  public RequestHandler2<T, R> handle(final ObjectHandler2<T, R> handler) {
    if (this.handler != null) {
      throw new IllegalArgumentException("Handler already specified via .handle(...)");
    }
    this.objectHandler = handler;
    return this;
  }

  public RequestHandler2<T, R> onError(final ErrorHandler errorHandler) {
    this.errorHandler = errorHandler;
    return this;
  }

  @Override
  public Completes<Response> execute(final Request request,
                                     final Action.MappedParameters mappedParameters,
                                     final Logger logger) {
    final T param1 = resolverParam1.apply(request, mappedParameters);
    final R param2 = resolverParam2.apply(request, mappedParameters);
    return execute(request, param1, param2, logger);
  }

  @FunctionalInterface
  public interface Handler2<T, R> {
    Completes<Response> execute(T param1, R param2);
  }

  @FunctionalInterface
  public interface ObjectHandler2<T, R> {
    Completes<ObjectResponse<?>> execute(T param1, R param2);
  }


  // region FluentAPI
  public <U> RequestHandler3<T, R, U> param(final Class<U> paramClass) {
    return new RequestHandler3<>(method, path, resolverParam1, resolverParam2, ParameterResolver.path(2, paramClass), errorHandler, mediaTypeMapper);
  }

  public <U> RequestHandler3<T, R, U> body(final Class<U> bodyClass) {
    return new RequestHandler3<>(method, path, resolverParam1, resolverParam2, ParameterResolver.body(bodyClass, mediaTypeMapper), errorHandler, mediaTypeMapper);
  }

  /**
   * Specify the class that represents the body of the request for all requests using the specified mapper for all
   * MIME types regardless of the Content-Type header.
   *
   * @deprecated Deprecated in favor of using the ContentMediaType method, which handles media types appropriately.
   * {@link RequestHandler2#body(java.lang.Class, io.vlingo.http.resource.MediaTypeMapper)} instead, or via
   * {@link RequestHandler2#body(java.lang.Class)}
   */
  public <U> RequestHandler3<T, R, U> body(final Class<U> bodyClass, final Class<? extends Mapper> mapperClass) {
    return body(bodyClass, mapperFrom(mapperClass));
  }

  /**
   * Specify the class that represents the body of the request for all requests using the specified mapper for all
   * MIME types regardless of the Content-Type header.
   *
   * @deprecated Deprecated in favor of using the ContentMediaType method, which handles media types appropriately.
   * {@link RequestHandler2#body(java.lang.Class, io.vlingo.http.resource.MediaTypeMapper)} instead, or via
   * {@link RequestHandler2#body(java.lang.Class)}
   */
  public <U> RequestHandler3<T, R, U> body(final Class<U> bodyClass, final Mapper mapper) {
    return new RequestHandler3<>(method, path, resolverParam1, resolverParam2,
      ParameterResolver.body(bodyClass, mapper),
      errorHandler,
      mediaTypeMapper);
  }

  public <U> RequestHandler3<T, R, U> body(final Class<U> bodyClass, final MediaTypeMapper mediaTypeMapper) {
    this.mediaTypeMapper = mediaTypeMapper;
    return new RequestHandler3<>(method, path, resolverParam1, resolverParam2,
      ParameterResolver.body(bodyClass, mediaTypeMapper), errorHandler, mediaTypeMapper);
  }

  public RequestHandler3<T, R, String> query(final String name) {
    return query(name, String.class);
  }

  public <U> RequestHandler3<T, R, U> query(final String name, final Class<U> queryClass) {
    return new RequestHandler3<>(method, path, resolverParam1, resolverParam2, ParameterResolver.query(name, queryClass), errorHandler, mediaTypeMapper);
  }

  public RequestHandler3<T, R, Header> header(final String name) {
    return new RequestHandler3<>(method, path, resolverParam1, resolverParam2, ParameterResolver.header(name), errorHandler, mediaTypeMapper);
  }
  // endregion
}
