package org.cubekode.graphpojo.annotation;

import graphql.schema.DataFetcher;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Relationship {

  String name();

  Class<? extends DataFetcher> fetcher() default DataFetcher.class;
}
