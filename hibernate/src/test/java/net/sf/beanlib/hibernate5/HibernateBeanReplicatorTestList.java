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
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.sf.beanlib.CollectionPropertyName;
import net.sf.beanlib.hibernate5.Hibernate5BeanReplicator;

/**
 * @author Joe D. Velopar
 */
@RunWith(JUnit4.class)
public class HibernateBeanReplicatorTestList {

    private static final Logger LOGGER = LoggerFactory.getLogger(HibernateBeanReplicatorTestList.class);

    @Test
    public void deepCopyList() {
        FooWithList fooList = new FooWithList();
        fooList.addToList("1");
        fooList.addToList("2");
        fooList.setFooWithList(fooList);
        // Test recursive references
        fooList.addToList(fooList);
        fooList.addToList(fooList.getList());
        FooWithList toList = new Hibernate5BeanReplicator().deepCopy(fooList);

        assertFalse(fooList.getList() == toList.getList());

        Iterator<Object> itr1 = fooList.getList().iterator();
        Iterator<Object> itr2 = toList.getList().iterator();

        while (itr1.hasNext()) {
            Object n1 = itr1.next();
            Object n2 = itr2.next();
            LOGGER.debug("n1=" + n1 + ", n2=" + n2);

            if (n1 instanceof String) {
                assertEquals(n1, n2);
            }
        }
        assertFalse(itr2.hasNext());
    }

    /** Test copying Collection property with zero size. */
    @Test
    public void copyEmptyCollection() {
        FooWithList fooList = new FooWithList();
        List<Object> emptyList = Collections.emptyList();
        fooList.setList(emptyList);
        assertEquals(0, fooList.getList().size());
        {
            FooWithList toList = new Hibernate5BeanReplicator().copy(fooList);
            assertEquals(0, toList.getList().size());
        }
        {
            FooWithList toList = new Hibernate5BeanReplicator().deepCopy(fooList);
            assertEquals(0, toList.getList().size());
        }
        // Explicitly specify not to copy any collection properties.
        {
            Set<CollectionPropertyName<?>> collectionPropertyNameSet = Collections.emptySet();
            Hibernate5BeanReplicator r = new Hibernate5BeanReplicator();
            r.getHibernatePropertyFilter().withCollectionPropertyNameSet(collectionPropertyNameSet);
            FooWithList toList = r.copy(fooList);
            assertNull(toList.getList());
        }
        // Deep copy, however, alwlays copy all collection properties regardless.
        {
            Set<CollectionPropertyName<?>> collectionPropertyNameSet = Collections.emptySet();
            Hibernate5BeanReplicator r = new Hibernate5BeanReplicator();
            r.getHibernatePropertyFilter().withCollectionPropertyNameSet(collectionPropertyNameSet);
            FooWithList toList = r.deepCopy(fooList);
            assertEquals(0, toList.getList().size());
        }
    }

    /** Test copying null Collection property. */
    @Test
    public void copyNullCollection() {
        FooWithList fooList = new FooWithList();
        fooList.setList(null);
        assertNull(fooList.getList());
        {
            FooWithList toList = new Hibernate5BeanReplicator().copy(fooList);
            assertNull(toList.getList());
        }
        {
            FooWithList toList = new Hibernate5BeanReplicator().deepCopy(fooList);
            assertNull(toList.getList());
        }
    }
}
