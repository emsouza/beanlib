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
package net.sf.beanlib.hibernate;

import org.hibernate.HibernateException;
import org.hibernate.proxy.HibernateProxy;
import org.hibernate.proxy.LazyInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Joe D. Velopar
 * @author Hanson Char
 */
public abstract class UnEnhancer {

  private static final Logger LOGGER = LoggerFactory.getLogger(UnEnhancer.class);

  /**
   * Returns true if the given class is found to be a ByteBuddy enhanced class; false otherwise.
   */
  private static boolean isHibernateEnhanced(Class<?> c) {
    return HibernateProxy.class.isAssignableFrom(c);
  }

  /**
   * Digs out the pre ByteBuddy enhanced class, if any.
   */
  @SuppressWarnings("unchecked")
  public static <T> Class<T> unenhanceClass(Class<?> c) {
    boolean enhanced = true;

    while (c != null && enhanced) {
      enhanced = isHibernateEnhanced(c);
      if (enhanced) {
        c = c.getSuperclass();
      }
    }

    Class<T> ret = (Class<T>) c;
    return ret;
  }

  @SuppressWarnings("unchecked")
  public static <T> Class<T> getActualClass(Object object) {
    Class<?> c = object.getClass();
    boolean enhanced = true;

    while (c != null && enhanced) {
      enhanced = isHibernateEnhanced(c);
      if (enhanced) {
        if (object instanceof HibernateProxy) {
          HibernateProxy hibernateProxy = (HibernateProxy) object;
          LazyInitializer lazyInitializer = hibernateProxy.getHibernateLazyInitializer();
          try {
            // suggested by Chris (harris3@sourceforge.net)
            Object impl = lazyInitializer.getImplementation();

            if (impl != null) {
              Class<T> ret = (Class<T>) impl.getClass();
              return ret;
            }
          } catch (HibernateException ex) {
            LOGGER.warn("Unable to retrieve the underlying persistent object", ex);
          }
          Class<T> ret = lazyInitializer.getPersistentClass();
          return ret;
        }
        c = c.getSuperclass();
      }
    }
    Class<T> ret = (Class<T>) c;
    return ret;
  }

  @SuppressWarnings("unchecked")
  public static <T> T unenhanceObject(T object) {
    if (object instanceof HibernateProxy) {
      HibernateProxy hibernateProxy = (HibernateProxy) object;
      LazyInitializer lazyInitializer = hibernateProxy.getHibernateLazyInitializer();

      T ret = (T) lazyInitializer.getImplementation();
      return ret;
    }
    return object;
  }
}
