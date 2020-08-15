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

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import net.sf.beanlib.PropertyInfo;
import net.sf.beanlib.hibernate5.Hibernate5BeanReplicator;
import net.sf.beanlib.provider.BeanTransformer;
import net.sf.beanlib.provider.replicator.BeanReplicator;
import net.sf.beanlib.spi.BeanTransformerSpi;
import net.sf.beanlib.spi.CustomBeanTransformerSpi;

/**
 * @author Joe D. Velopar
 */
@RunWith(JUnit4.class)
public class CustomBeanTransformerSpiTest {

    static class A {
        private B b;

        public B getB() {
            return b;
        }

        public void setB(B b) {
            this.b = b;
        }
    }

    static abstract class B {}

    static class B1 extends B {
        private B b;

        public B getB() {
            return b;
        }

        public void setB(B b) {
            this.b = b;
        }
    }

    static class B2 extends B {}

    private static class MyCustomBeanTransformer implements CustomBeanTransformerSpi {
        public static class Factory implements CustomBeanTransformerSpi.Factory {
            @Override
            public CustomBeanTransformerSpi newCustomBeanTransformer(BeanTransformerSpi beanTransformer) {
                return new MyCustomBeanTransformer(beanTransformer);
            }
        }

        private final BeanTransformerSpi beanTransformer;

        private MyCustomBeanTransformer(BeanTransformerSpi beanTransformer) {
            this.beanTransformer = beanTransformer;
        }

        @Override
        public boolean isTransformable(Object from, Class<?> toClass, PropertyInfo propertyInfo) {
            return from != null && toClass == B.class;
        }

        @Override
        @SuppressWarnings("unchecked")
        public <T> T transform(Object in, Class<T> toClass, PropertyInfo propertyInfo) {
            return (T) beanTransformer.getBeanReplicatable().replicateBean(in, in.getClass());
        }
    }

    @Test
    public void abstractClassCopyViaBeanReplicator() {
        A fromA = new A();
        B1 fromB1 = new B1();
        B2 fromB2 = new B2();
        fromB1.setB(fromB2);
        fromA.setB(fromB1);
        A toA = new BeanReplicator(new BeanTransformer().initCustomTransformerFactory(new MyCustomBeanTransformer.Factory())).replicateBean(fromA,
                fromA.getClass());
        assertNotNull(toA);
        assertNotSame(fromA, toA);

        assertTrue(toA.getB().getClass() == B1.class);
        B1 toB1 = (B1) toA.getB();
        assertNotSame(fromA.getB(), toA.getB());

        assertTrue(toB1.getB().getClass() == B2.class);
        B2 toB2 = (B2) toB1.getB();
        assertNotSame(fromB2, toB2);
    }

    @Test
    public void abstractClassCopyViaBeanReplicator2() {
        A fromA = new A();
        B1 fromB1 = new B1();
        B2 fromB2 = new B2();
        fromB1.setB(fromB2);
        fromA.setB(fromB1);
        A toA = new BeanReplicator(new BeanTransformer()).replicateBean(fromA, fromA.getClass());
        assertNotNull(toA);
        assertNotSame(fromA, toA);

        assertTrue(toA.getB().getClass() == B1.class);
        B1 toB1 = (B1) toA.getB();
        assertNotSame(fromA.getB(), toA.getB());

        assertTrue(toB1.getB().getClass() == B2.class);
        B2 toB2 = (B2) toB1.getB();
        assertNotSame(fromB2, toB2);
    }

    @Test
    public void abstractClassCopyViaHibernate3BeanReplicator() {
        A fromA = new A();
        B1 fromB1 = new B1();
        B2 fromB2 = new B2();
        fromB1.setB(fromB2);
        fromA.setB(fromB1);
        A toA = new Hibernate5BeanReplicator().initCustomTransformerFactory(new MyCustomBeanTransformer.Factory()).copy(fromA);
        assertNotNull(toA);
        assertNotSame(fromA, toA);

        assertTrue(toA.getB().getClass() == B1.class);
        B1 toB1 = (B1) toA.getB();
        assertNotSame(fromA.getB(), toA.getB());

        assertTrue(toB1.getB().getClass() == B2.class);
        B2 toB2 = (B2) toB1.getB();
        assertNotSame(fromB2, toB2);
    }

    @Test
    public void abstractClassCopyViaHibernate3BeanReplicator2() {
        A fromA = new A();
        B1 fromB1 = new B1();
        B2 fromB2 = new B2();
        fromB1.setB(fromB2);
        fromA.setB(fromB1);
        A toA = new Hibernate5BeanReplicator().copy(fromA);
        assertNotNull(toA);
        assertNotSame(fromA, toA);

        assertTrue(toA.getB().getClass() == B1.class);
        B1 toB1 = (B1) toA.getB();
        assertNotSame(fromA.getB(), toA.getB());

        assertTrue(toB1.getB().getClass() == B2.class);
        B2 toB2 = (B2) toB1.getB();
        assertNotSame(fromB2, toB2);
    }
}
