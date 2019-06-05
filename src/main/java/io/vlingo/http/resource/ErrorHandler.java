package io.vlingo.http.resource;

import io.vlingo.common.Completes;
import io.vlingo.http.Response;

public interface ErrorHandler {

  Response handle(final Throwable error);

  static ErrorHandler handleAllWith(final Response.Status status) {
    return (error) -> Response.of(status);
  }
}
