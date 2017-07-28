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
package net.sf.beanlib.hibernate4;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;

import java.lang.reflect.Method;
import java.util.Collections;
import java.util.Iterator;
import java.util.Map;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import junit.framework.JUnit4TestAdapter;
import net.sf.beanlib.spi.PropertyFilter;

/**
 * @author Joe D. Velopar
 */
public class HibernateBeanReplicatorTestMap {

    private static final Logger LOGGER = LoggerFactory.getLogger(HibernateBeanReplicatorTestMap.class);

    @Test
    public void testDeepCopyMap() {
        FooWithMap fooMap = new FooWithMap(null);
        fooMap.addToMap("1", "a");
        fooMap.addToMap("2", "b");
        // Test recursive references
        fooMap.addToMap("3", fooMap);

        FooWithList fooList = new FooWithList();
        fooList.addToList("1");
        fooList.addToList("2");
        fooList.setFooWithList(fooList);
        // Test recursive references
        fooList.addToList(fooList);
        fooList.addToList(fooList.getList());
        fooMap.addToMap("4", fooList);
        FooWithMap toMap = new Hibernate4BeanReplicator().deepCopy(fooMap);

        assertFalse(fooMap.getMap() == toMap.getMap());

        Iterator<Map.Entry<Object, Object>> itr1 = fooMap.getMap().entrySet().iterator();
        Iterator<Map.Entry<Object, Object>> itr2 = toMap.getMap().entrySet().iterator();

        while (itr1.hasNext()) {
            Map.Entry<Object, Object> n1 = itr1.next();
            Map.Entry<Object, Object> n2 = itr2.next();
            LOGGER.debug("n1=" + n1 + ", n2=" + n2);

            if (n1.getKey() instanceof String && n1.getValue() instanceof String) {
                assertEquals(n1, n2);
            }
        }
        assertFalse(itr2.hasNext());
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testCopyMap() {
        FooWithMap fooMap = new FooWithMap(null);
        fooMap.addToMap("1", "a");
        fooMap.addToMap("2", "b");

        {
            FooWithMap toFooWithMap = new Hibernate4BeanReplicator().copy(fooMap);
            Map<Object, Object> toMap = toFooWithMap.getMap();
            toMap.size();
            // log.info("toMap.size()=" + toMap.size());
            assertEquals(toMap.size(), 2);
        }
        {
            Hibernate4BeanReplicator r = new Hibernate4BeanReplicator();
            r.getHibernatePropertyFilter().withCollectionPropertyNameSet(null);
            FooWithMap toFooWithMap = r.copy(fooMap);
            Map<Object, Object> toMap = toFooWithMap.getMap();
            toMap.size();
            // log.info("toMap.size()=" + toMap.size());
            assertEquals(toMap.size(), 2);
        }
        {
            Hibernate4BeanReplicator r = new Hibernate4BeanReplicator();
            r.getHibernatePropertyFilter().withCollectionPropertyNameSet(Collections.EMPTY_SET);
            FooWithMap toFooWithMap = r.copy(fooMap);
            Map<Object, Object> toMap = toFooWithMap.getMap();
            assertNull(toMap);
        }
        {
            Hibernate4BeanReplicator r = new Hibernate4BeanReplicator();
            r.getHibernatePropertyFilter().withVetoer(new PropertyFilter() {
                @Override
                public boolean propagate(String propertyName, Method readerMethod) {
                    return !"map".equals(propertyName);
                }
            });
            FooWithMap toFooWithMap = r.copy(fooMap);
            Map<Object, Object> toMap = toFooWithMap.getMap();
            assertNull(toMap);
        }
    }

    public static junit.framework.Test suite() {
        return new JUnit4TestAdapter(HibernateBeanReplicatorTestMap.class);
    }
}
