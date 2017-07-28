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
package net.sf.beanlib.hibernate4;

import java.util.Map;

import org.hibernate.Hibernate;

import net.sf.beanlib.hibernate.UnEnhancer;
import net.sf.beanlib.provider.replicator.MapReplicator;
import net.sf.beanlib.spi.BeanTransformerSpi;
import net.sf.beanlib.spi.replicator.MapReplicatorSpi;

/**
 * Hibernate 4 specific Map Replicator.
 *
 * @author Joe D. Velopar
 */
public class Hibernate4MapReplicator extends MapReplicator {

    private static final Factory factory = new Factory();

    public static Factory getFactory() {
        return factory;
    }

    /**
     * Factory for {@link MapReplicator}
     *
     * @author Joe D. Velopar
     */
    public static class Factory implements MapReplicatorSpi.Factory {
        private Factory() {}

        @Override
        public Hibernate4MapReplicator newMapReplicatable(BeanTransformerSpi beanTransformer) {
            return new Hibernate4MapReplicator(beanTransformer);
        }
    }

    public static Hibernate4MapReplicator newMapReplicatable(BeanTransformerSpi beanTransformer) {
        return factory.newMapReplicatable(beanTransformer);
    }

    protected Hibernate4MapReplicator(BeanTransformerSpi beanTransformer) {
        super(beanTransformer);
    }

    @Override
    public <K, V, T> T replicateMap(Map<K, V> from, Class<T> toClass) {
        if (!Hibernate.isInitialized(from)) {
            Hibernate.initialize(from);
        }
        return super.replicateMap(from, toClass);
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
    protected final <T> T unenhanceObject(T from) {
        return UnEnhancer.unenhanceObject(from);
    }
}
