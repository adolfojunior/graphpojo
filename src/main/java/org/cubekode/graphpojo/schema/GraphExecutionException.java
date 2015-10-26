package org.cubekode.graphpojo.schema;

import graphql.GraphQLError;

import java.util.List;

public class GraphExecutionException extends Exception {
  private static final long serialVersionUID = 1L;

  public GraphExecutionException() {
    super();
    // TODO Auto-generated constructor stub
  }

  public GraphExecutionException(String message, Throwable cause, boolean enableSuppression,
      boolean writableStackTrace) {
    super(message, cause, enableSuppression, writableStackTrace);
  }

  public GraphExecutionException(String message, Throwable cause) {
    super(message, cause);
  }

  public GraphExecutionException(String message) {
    super(message);
  }

  public GraphExecutionException(Throwable cause) {
    super(cause);
  }

  public GraphExecutionException(List<GraphQLError> errors) {
    super(errors.toString());
  }
}
