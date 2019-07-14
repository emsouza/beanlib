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

import net.sf.beanlib.hibernate.UnEnhancer;
import net.sf.beanlib.provider.replicator.BeanReplicator;
import net.sf.beanlib.spi.BeanTransformerSpi;
import net.sf.beanlib.spi.replicator.BeanReplicatorSpi;

/**
 * Hibernate 4 specific JavaBean Replicator.
 *
 * @author Joe D. Velopar
 */
public class Hibernate5JavaBeanReplicator extends BeanReplicator {

    private static final Factory factory = new Factory();

    public static Factory getFactory() {
        return factory;
    }

    /**
     * Factory for {@link Hibernate5JavaBeanReplicator}
     *
     * @author Joe D. Velopar
     */
    public static class Factory implements BeanReplicatorSpi.Factory {
        @Override
        public Hibernate5JavaBeanReplicator newBeanReplicatable(BeanTransformerSpi beanTransformer) {
            return new Hibernate5JavaBeanReplicator(beanTransformer);
        }
    }

    public static Hibernate5JavaBeanReplicator newBeanReplicatable(BeanTransformerSpi beanTransformer) {
        return factory.newBeanReplicatable(beanTransformer);
    }

    protected Hibernate5JavaBeanReplicator(BeanTransformerSpi beanTransformer) {
        super(beanTransformer);
    }

    @Override
    protected <T> T createToInstance(Object from, Class<T> toClass)
            throws InstantiationException, IllegalAccessException, SecurityException, NoSuchMethodException {
        // figure out the pre-enhanced class
        Class<T> actualClass = UnEnhancer.getActualClass(from);
        Class<T> targetClass = chooseClass(actualClass, toClass);
        return newInstanceAsPrivileged(targetClass);
    }

    @Override
    public <V, T> T replicateBean(V from, Class<T> toClass) {
        return super.replicateBean(unenhanceObject(from), toClass, from);
    }

    @Override
    protected final <T> T unenhanceObject(T from) {
        return UnEnhancer.unenhanceObject(from);
    }
}
