/*
 * Copyright 2005 The Apache Software Foundation.
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
public class EnumTest {

    public enum Status {
        BEGIN,
        WIP,
        END;
    }

    public static class C {
        private Status status;
        private String testString;

        public Status getStatus() {
            return status;
        }

        public void setStatus(Status status) {
            this.status = status;
        }

        public String getTestString() {
            return testString;
        }

        public void setTestString(String testString) {
            this.testString = testString;
        }
    }

    @Test
    public void copyWithCustomTransformer() {
        C c = new C();
        c.setStatus(Status.BEGIN);
        c.setTestString("testStr");

        // Customer transformer used to be necessary to handle enum,
        // before beanlib was entirely moved to Java 5.
        HibernateBeanReplicator replicator = new Hibernate5BeanReplicator().initCustomTransformerFactory(new CustomBeanTransformerSpi.Factory() {
            @Override
            public CustomBeanTransformerSpi newCustomBeanTransformer(BeanTransformerSpi beanTransformer) {
                return new CustomBeanTransformerSpi() {
                    @Override
                    public boolean isTransformable(Object from, Class<?> toClass, PropertyInfo propertyInfo) {
                        return toClass.isEnum();
                    }

                    @Override
                    public <T> T transform(Object in, Class<T> toClass, PropertyInfo propertyInfo) {
                        @SuppressWarnings("unchecked")
                        T ret = (T) in;
                        return ret;
                    }
                };
            }
        });
        C c2 = replicator.deepCopy(c);
        assertNotSame(c2, c);
        assertSame(c2.getStatus(), c.getStatus());
        assertEquals(c.getTestString(), c2.getTestString());
    }

    @Test
    public void copy() {
        C c = new C();
        c.setStatus(Status.BEGIN);
        c.setTestString("testStr");
        HibernateBeanReplicator replicator = new Hibernate5BeanReplicator();
        C c2 = replicator.deepCopy(c);
        assertNotSame(c2, c);
        assertSame(c2.getStatus(), c.getStatus());
        assertEquals(c.getTestString(), c2.getTestString());
    }
}
