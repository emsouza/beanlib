/*
 * Copyright 2005 The Apache Software Foundation.
 *
 * Licensed under the Apache License, Version 2.0 (the "License") you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */
package net.sf.beanlib.provider;

import net.sf.beanlib.BeanlibException;
import net.sf.beanlib.PropertyInfo;
import net.sf.beanlib.spi.BeanMethodCollector;
import net.sf.beanlib.spi.BeanMethodFinder;
import net.sf.beanlib.spi.BeanPopulationExceptionHandler;
import net.sf.beanlib.spi.BeanPopulatorBaseConfig;
import net.sf.beanlib.spi.BeanPopulatorSpi;
import net.sf.beanlib.spi.BeanSourceHandler;
import net.sf.beanlib.spi.BeanTransformerSpi;
import net.sf.beanlib.spi.DetailedPropertyFilter;
import net.sf.beanlib.spi.PropertyFilter;
import net.sf.beanlib.spi.Transformable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.beans.Introspector;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.security.AccessController;
import java.security.PrivilegedAction;

/**
 * Default implementation of {@link BeanPopulatorSpi}.
 * <p>
 * A Bean Populator can be used to populate the properties from a JavaBean instance to another
 * JavaBean instance. For example, <blockquote>
 *
 * <pre>
 * Bean from = ...
 * Bean to = ...
 * new BeanPopulator(from, to).populate();
 * </pre>
 *
 * </blockquote> By default, every public setter method of the target JavaBean is invoked with the
 * value retrieved from the corresponding public getter method (of the same property name and type)
 * of the source JavaBean.
 * <p>
 * How the set of setter methods and getter methods are determined can be overridden via the methods
 * {@link BeanPopulator#initSetterMethodCollector(BeanMethodCollector)} and
 * {@link #initReaderMethodFinder(BeanMethodFinder)} before the {@link #populate()} method is
 * invoked.
 * <p>
 * During the property propagation process, various options exist to override the default behavior:
 * <ol>
 * <li>A {@link DetailedPropertyFilter} can be used to control whether a specific JavaBean property
 * should be propagated across.<br>
 * See {@link #initDetailedPropertyFilter(DetailedPropertyFilter)}.<br>
 * By default, there is no detailed bean property selector configured.</li>
 * <li>Similar to {@link DetailedPropertyFilter} but with a simpler API, a {@link PropertyFilter}
 * can be used to control whether a specific JavaBean property should be propagated across.<br>
 * See {@link #initPropertyFilter(PropertyFilter)}.<br>
 * By default, there is no bean property selector configured.</li>
 * <li>A {@link BeanSourceHandler} can be used to act as a call-back (to produce whatever
 * side-effects deemed necessary) after the property value has been retrieved from the source bean,
 * but before being propagated across to the target bean. <br>
 * See {@link #initBeanSourceHandler(BeanSourceHandler)}.<br>
 * By default, there is no bean source handler configured.</li>
 * <li>A {@link Transformable} can be used to transform and replace the property value to be
 * propagated across to the target bean.<br>
 * See {@link #initTransformer(Transformable)}.<br>
 * By default, there is no transformer configured.</li>
 * <li>A {@link BeanPopulationExceptionHandler} can be used to handle any exception thrown.<br>
 * See {@link #initBeanPopulationExceptionHandler(BeanPopulationExceptionHandler)} and
 * {@link BeanPopulationExceptionHandler}.<br>
 * By default, an abort policy is used in that any such exception will cause the propagation to
 * terminate immediately, and the exception itself will get bubbled up as an unchecked
 * exception.</li>
 * </ol>
 * Finally, all these options are grouped as a base configuration, which can be alternatively
 * changed via {@link #initBeanPopulatorBaseConfig(BeanPopulatorBaseConfig)}.
 *
 * @author Joe D. Velopar
 */
public class BeanPopulator implements BeanPopulatorSpi {

  private static final Logger LOGGER = LoggerFactory.getLogger(BeanPopulator.class);

  public static final Factory factory = new Factory();

  private BeanPopulatorBaseConfig baseConfig = new BeanPopulatorBaseConfig();

  private Transformable transformer = new BeanTransformer(factory);

  private final Object fromBean;

  private final Object toBean;

  /**
   * Bean Populator Factory, which implements the general factory interface of a BeanPopulatorSpi
   * instance.
   *
   * @author Joe D. Velopar
   */
  public static class Factory implements BeanPopulatorSpi.Factory {

    private Factory() {
    }

    /**
     * Notes the co-variant return type of a specific {@link BeanPopulatorSpi}.
     *
     * @see net.sf.beanlib.spi.BeanPopulatorSpi.Factory#newBeanPopulator(java.lang.Object,
     *      java.lang.Object)
     */
    @Override
    public BeanPopulator newBeanPopulator(Object from, Object to) {
      return new BeanPopulator(from, to);
    }
  }

  /**
   * @param fromBean from bean
   * @param toBean to bean
   */
  public BeanPopulator(Object fromBean, Object toBean) {
    this.fromBean = fromBean;
    this.toBean = toBean;
  }

  private BeanTransformerSpi getBeanTransformerSpi() {
    return (BeanTransformerSpi) (transformer instanceof BeanTransformerSpi ? transformer : null);
  }

  /**
   * Processes a specific setter method for the toBean.
   *
   * @param setterMethod a specific method of the toBean
   */
  private void processSetterMethod(Method setterMethod) {
    String methodName = setterMethod.getName();
    final String propertyString = methodName.substring(baseConfig.getSetterMethodCollector()
        .getMethodPrefix().length());

    LOGGER.debug("Process property [{}] fromClass [{}] toClass [{}].", propertyString, fromBean
        .getClass().getName(), toBean.getClass().getName());

    Method readerMethod = baseConfig.getReaderMethodFinder().find(propertyString, fromBean);

    if (readerMethod == null) {
      return;
    }
    // Reader method of fromBean found
    Class<?> paramType = setterMethod.getParameterTypes()[0];
    String propertyName = Introspector.decapitalize(propertyString);
    try {
      doit(setterMethod, readerMethod, paramType, propertyName);
    } catch (Exception ex) {
      baseConfig.getBeanPopulationExceptionHandler().initFromBean(fromBean).initToBean(toBean)
          .initPropertyName(propertyName).initReaderMethod(readerMethod).initSetterMethod(
              setterMethod).handleException(ex, LOGGER);
    }
  }

  private <T> void doit(Method setterMethod, Method readerMethod, Class<T> paramType,
      final String propertyName) {
    if (baseConfig.getDetailedPropertyFilter() != null) {
      if (!baseConfig.getDetailedPropertyFilter().propagate(propertyName, fromBean, readerMethod,
          toBean, setterMethod)) {
        return;
      }
    }
    if (baseConfig.getPropertyFilter() != null) {
      if (!baseConfig.getPropertyFilter().propagate(propertyName, readerMethod)) {
        return;
      }
    }
    Object propertyValue = this.invokeMethodAsPrivileged(fromBean, readerMethod, null);

    if (baseConfig.getBeanSourceHandler() != null) {
      baseConfig.getBeanSourceHandler().handleBeanSource(fromBean, readerMethod, propertyValue);
    }

    if (transformer != null) {
      PropertyInfo propertyInfo = new PropertyInfo(propertyName, fromBean, toBean);
      propertyValue = transformer.transform(propertyValue, paramType, propertyInfo);
    }

    // Invoke setter method
    Object[] args = {propertyValue};
    this.invokeMethodAsPrivileged(toBean, setterMethod, args);
  }

  /**
   * Invoke the given method as a privileged action, if necessary.
   */
  private Object invokeMethodAsPrivileged(final Object target, final Method method,
      final Object[] args) {
    if (Modifier.isPublic(method.getModifiers())) {
      try {
        return method.invoke(target, args);
      } catch (IllegalAccessException ex) {
        // drop thru to try again
      } catch (InvocationTargetException e) {
        throw new BeanlibException(e.getTargetException());
      } catch (RuntimeException ex) {
        // the try-catch is unnecessary i know,
        // but just so we can set a break point here
        throw ex;
      }
    }
    return AccessController.doPrivileged(new PrivilegedAction<Object>() {
      @Override
      public Object run() {
        method.setAccessible(true);
        try {
          return method.invoke(target, args);
        } catch (IllegalArgumentException e) {
          throw new BeanlibException(e);
        } catch (IllegalAccessException e) {
          throw new BeanlibException(e);
        } catch (InvocationTargetException e) {
          throw new BeanlibException(e.getTargetException());
        }
      }
    });
  }

  // --------------------------- BeanPopulatorSpi ---------------------------

  @Override
  public Transformable getTransformer() {
    return transformer;
  }

  @Override
  public BeanPopulator initTransformer(Transformable transformer) {
    this.transformer = transformer;

    if (this.getBeanTransformerSpi() != null) {
      this.getBeanTransformerSpi().initBeanPopulatorBaseConfig(baseConfig);
    }
    return this;
  }

  @Override
  @SuppressWarnings("unchecked")
  public <T> T populate() {
    if (getBeanTransformerSpi() != null) {
      getBeanTransformerSpi().getClonedMap().put(fromBean, toBean);
    }
    // invoking all declaring setter methods of toBean from all matching getter methods of fromBean
    for (Method m : baseConfig.getSetterMethodCollector().collect(toBean)) {
      processSetterMethod(m);
    }
    return (T) toBean;
  }

  // -------------------------- BeanPopulatorBaseSpi --------------------------

  @Override
  public BeanPopulator initPropertyFilter(PropertyFilter propertyFilter) {
    baseConfig.setPropertyFilter(propertyFilter);

    if (this.getBeanTransformerSpi() != null) {
      this.getBeanTransformerSpi().initPropertyFilter(propertyFilter);
    }
    return this;
  }

  @Override
  public BeanPopulator initBeanSourceHandler(BeanSourceHandler beanSourceHandler) {
    baseConfig.setBeanSourceHandler(beanSourceHandler);

    if (this.getBeanTransformerSpi() != null) {
      this.getBeanTransformerSpi().initBeanSourceHandler(beanSourceHandler);
    }
    return this;
  }

  @Override
  public BeanPopulator initDetailedPropertyFilter(DetailedPropertyFilter detailedPropertyFilter) {
    baseConfig.setDetailedPropertyFilter(detailedPropertyFilter);

    if (this.getBeanTransformerSpi() != null) {
      this.getBeanTransformerSpi().initDetailedPropertyFilter(detailedPropertyFilter);
    }
    return this;
  }

  @Override
  public BeanPopulator initReaderMethodFinder(BeanMethodFinder readerMethodFinder) {
    if (readerMethodFinder != null) {
      baseConfig.setReaderMethodFinder(readerMethodFinder);

      if (this.getBeanTransformerSpi() != null) {
        this.getBeanTransformerSpi().initReaderMethodFinder(readerMethodFinder);
      }
    }
    return this;
  }

  @Override
  public BeanPopulator initSetterMethodCollector(BeanMethodCollector setterMethodCollector) {
    if (setterMethodCollector != null) {
      baseConfig.setSetterMethodCollector(setterMethodCollector);

      if (this.getBeanTransformerSpi() != null) {
        this.getBeanTransformerSpi().initSetterMethodCollector(setterMethodCollector);
      }
    }
    return this;
  }

  @Override
  public BeanPopulator initBeanPopulationExceptionHandler(
      BeanPopulationExceptionHandler beanPopulationExceptionHandler) {
    baseConfig.setBeanPopulationExceptionHandler(beanPopulationExceptionHandler);

    if (this.getBeanTransformerSpi() != null) {
      this.getBeanTransformerSpi().initBeanPopulationExceptionHandler(
          beanPopulationExceptionHandler);
    }
    return this;
  }

  @Override
  public BeanPopulator initBeanPopulatorBaseConfig(BeanPopulatorBaseConfig baseConfig) {
    this.baseConfig = baseConfig;

    if (getBeanTransformerSpi() != null) {
      getBeanTransformerSpi().initBeanPopulatorBaseConfig(baseConfig);
    }
    return this;
  }

  @Override
  public PropertyFilter getPropertyFilter() {
    return baseConfig.getPropertyFilter();
  }

  @Override
  public BeanPopulationExceptionHandler getBeanPopulationExceptionHandler() {
    return baseConfig.getBeanPopulationExceptionHandler();
  }

  /**
   * Notes if the returned base config is modified, a subsequent
   * {@link BeanPopulator#initBeanPopulatorBaseConfig(BeanPopulatorBaseConfig)} needs to be invoked
   * to keep the configuration in sync.
   */
  @Override
  public BeanPopulatorBaseConfig getBeanPopulatorBaseConfig() {
    return baseConfig;
  }

  @Override
  public BeanSourceHandler getBeanSourceHandler() {
    return baseConfig.getBeanSourceHandler();
  }

  @Override
  public DetailedPropertyFilter getDetailedPropertyFilter() {
    return baseConfig.getDetailedPropertyFilter();
  }

  @Override
  public BeanMethodFinder getReaderMethodFinder() {
    return baseConfig.getReaderMethodFinder();
  }

  @Override
  public BeanMethodCollector getSetterMethodCollector() {
    return baseConfig.getSetterMethodCollector();
  }
}
