/*
 * Copyright 2007 The Apache Software Foundation.
 *
 * Licensed under the Apache License, Version 2.0 (the "License")
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package net.sf.beanlib.hibernate5;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import java.sql.Timestamp;
import java.util.Date;
import java.util.Map;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import net.sf.beanlib.PropertyInfo;
import net.sf.beanlib.hibernate.HibernateBeanReplicator;
import net.sf.beanlib.hibernate5.Hibernate5BeanReplicator;
import net.sf.beanlib.spi.BeanTransformerSpi;
import net.sf.beanlib.spi.CustomBeanTransformerSpi;

/**
 * @author Joe D. Velopar
 */
@RunWith(JUnit4.class)
public class DateTest {

    @Test
    public void convertTimestampToDate() {
        final Pojo source = new Pojo();
        // Replicate Timestamp into Date
        HibernateBeanReplicator replicator = new Hibernate5BeanReplicator().initCustomTransformerFactory(new CustomBeanTransformerSpi.Factory() {
            @Override
            public CustomBeanTransformerSpi newCustomBeanTransformer(final BeanTransformerSpi beanTransformer) {
                return new CustomBeanTransformerSpi() {

                    @Override
                    public boolean isTransformable(Object from, Class<?> toClass, PropertyInfo propertyInfo) {
                        return from instanceof Date && toClass == Date.class;
                    }

                    @Override
                    @SuppressWarnings("unchecked")
                    public <T> T transform(Object in, Class<T> toClass, PropertyInfo propertyInfo) {
                        assertTrue("date".equals(propertyInfo.getPropertyName()) || "dateRef".equals(propertyInfo.getPropertyName()));
                        assertSame(source, propertyInfo.getFromBean());
                        assertNotSame(source, propertyInfo.getToBean());
                        Map<Object, Object> cloneMap = beanTransformer.getClonedMap();
                        Object clone = cloneMap.get(in);

                        if (clone != null) {
                            return (T) clone;
                        }
                        Date d = (Date) in;
                        clone = new Date(d.getTime());
                        cloneMap.put(in, clone);
                        return (T) clone;
                    }
                };
            }
        });
        Pojo clone = replicator.deepCopy(source);

        assertNotSame(clone, source);
        assertEquals(clone.getText(), source.getText());

        assertSame(source.getDate().getClass(), Timestamp.class);
        assertSame(clone.getDate().getClass(), Date.class);

        assertTrue(clone.getDate().getTime() == source.getDate().getTime());

        assertSame(source.getDate(), source.getDateRef());
        assertSame(clone.getDate(), clone.getDateRef());
        assertNotSame(source.getDate(), clone.getDate());
    }
}
