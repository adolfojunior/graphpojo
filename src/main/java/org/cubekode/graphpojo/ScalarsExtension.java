package org.cubekode.graphpojo;

import graphql.GraphQLException;
import graphql.Scalars;
import graphql.language.FloatValue;
import graphql.language.IntValue;
import graphql.schema.Coercing;
import graphql.schema.GraphQLScalarType;

public class ScalarsExtension extends Scalars {

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
