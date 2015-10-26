package org.cubekode.graphpojo.schema;

import graphql.schema.DataFetcher;
import graphql.schema.DataFetchingEnvironment;

import java.lang.invoke.MethodHandle;
import java.lang.reflect.Method;
import java.util.Map;

import org.cubekode.graphpojo.schema.GraphPojoSchemaBuilder.PojoProperty;
import org.cubekode.graphpojo.util.ReflectionUtils;

public enum PropertyFetcherStrategies implements PropertyFetcherStrategy {

  METHOD_REFLECTION {
    @Override
    public DataFetcher createFetcher(PojoProperty property) {
      return new MethodReflectionFetcher(property);
    }
  },
  FIELD_REFLECTION {
    @Override
    public DataFetcher createFetcher(PojoProperty property) {
      return new FieldReflectionFetcher(property);
    }
  },
  METHOD_HANDLE {
    @Override
    public DataFetcher createFetcher(PojoProperty property) {
      return new MethodHandleFetcher(property);
    }
  };

  static abstract class PropertyFetcher implements DataFetcher {

    protected PojoProperty property;

    public PropertyFetcher(PojoProperty property) {
      this.property = property;
    }

    @Override
    public Object get(DataFetchingEnvironment environment) {
      Object source = environment.getSource();
      if (source == null) {
        return null;
      }
      if (source instanceof Map) {
        return ((Map<?, ?>) source).get(property.name);
      }
      return getProperty(environment, source);
    }

    protected abstract Object getProperty(DataFetchingEnvironment environment, Object source);
  }

  static class MethodReflectionFetcher extends PropertyFetcher {

    private Method method;

    public MethodReflectionFetcher(PojoProperty property) {
      super(property);
      try {
        this.method = ReflectionUtils.findGetterMethod(property.field);
      } catch (Exception e) {
        throw new IllegalStateException("Cant get method for field " + property.field, e);
      }
    }

    @Override
    protected Object getProperty(DataFetchingEnvironment environment, Object source) {
      try {
        return method.invoke(source);
      } catch (Exception e) {
        throw new IllegalStateException("Cant get value for field " + property.field, e);
      }
    }
  }

  static class FieldReflectionFetcher extends PropertyFetcher {

    public FieldReflectionFetcher(PojoProperty property) {
      super(property);
      property.field.setAccessible(true);
    }

    @Override
    protected Object getProperty(DataFetchingEnvironment environment, Object source) {
      try {
        return property.field.get(source);
      } catch (Exception e) {
        throw new IllegalStateException("Cant get value for field " + property.field, e);
      }
    }
  }

  static class MethodHandleFetcher extends PropertyFetcher {

    private MethodHandle method;

    public MethodHandleFetcher(PojoProperty property) {
      super(property);
      try {
        this.method = ReflectionUtils.findGetterHandler(property.field);
      } catch (Exception e) {
        throw new IllegalStateException("Cant get method for field " + property.field, e);
      }
    }

    @Override
    protected Object getProperty(DataFetchingEnvironment environment, Object source) {
      try {
        return method.invoke(source);
      } catch (Throwable e) {
        throw new IllegalStateException("Cant get value for field " + property.field, e);
      }
    }
  }
}
