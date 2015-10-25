package org.cubekode.graphpojo.schema;

import graphql.GraphQLException;
import graphql.Scalars;
import graphql.language.FloatValue;
import graphql.language.IntValue;
import graphql.schema.Coercing;
import graphql.schema.GraphQLScalarType;

import java.util.HashMap;
import java.util.Map;

public class ScalarsExtension extends Scalars {

  private static final Map<Class<?>, GraphQLScalarType> TYPES = new HashMap<>();

  static {
    TYPES.put(String.class, ScalarsExtension.GraphQLString);
    TYPES.put(Float.class, ScalarsExtension.GraphQLFloat);
    TYPES.put(Double.class, ScalarsExtension.GraphQLDouble);
    TYPES.put(Integer.class, ScalarsExtension.GraphQLInt);
    TYPES.put(Long.class, ScalarsExtension.GraphQLLong);
    TYPES.put(Boolean.class, ScalarsExtension.GraphQLBoolean);
    TYPES.put(float.class, ScalarsExtension.GraphQLFloat);
    TYPES.put(double.class, ScalarsExtension.GraphQLDouble);
    TYPES.put(int.class, ScalarsExtension.GraphQLInt);
    TYPES.put(long.class, ScalarsExtension.GraphQLLong);
    TYPES.put(boolean.class, ScalarsExtension.GraphQLBoolean);
  }

  public static boolean isScalarType(Class<?> type) {
    return TYPES.containsKey(type);
  }

  public static GraphQLScalarType getScalarType(Class<?> type) {
    return TYPES.get(type);
  }

  public static GraphQLScalarType GraphQLLong = new GraphQLScalarType("Long", "Extended Long",
      new Coercing() {
        @Override
        public Object coerce(Object input) {
          if (input instanceof String) {
            return Long.parseLong((String) input);
          } else if (input instanceof Long) {
            return input;
          } else {
            throw new GraphQLException("");
          }
        }

        @Override
        public Object coerceLiteral(Object input) {
          if (!(input instanceof IntValue))
            return null;
          return ((IntValue) input).getValue();
        }
      });

  public static GraphQLScalarType GraphQLDouble = new GraphQLScalarType("Double",
      "Extended Double", new Coercing() {
        @Override
        public Object coerce(Object input) {
          if (input instanceof String) {
            return Double.parseDouble((String) input);
          } else if (input instanceof Double) {
            return input;
          } else {
            throw new GraphQLException("");
          }
        }

        @Override
        public Object coerceLiteral(Object input) {
          return ((FloatValue) input).getValue().floatValue();
        }
      });

}
