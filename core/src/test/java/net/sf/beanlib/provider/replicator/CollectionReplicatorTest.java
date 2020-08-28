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
package net.sf.beanlib.provider.replicator;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotSame;

import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Set;

import org.junit.Test;

import junit.framework.JUnit4TestAdapter;
import net.sf.beanlib.provider.BeanPopulator;

/**
 * @author Joe D. Velopar
 */
public class CollectionReplicatorTest {

    private static class A {

        Set<String> set;

        public Set<String> getSet() {
            return set;
        }

        public void setSet(Set<String> set) {
            this.set = set;
        }
    }

    @Test
    public void testLinkedHashSet() {
        A from = new A();
        Set<String> set = new LinkedHashSet<String>();
        set.add("a");
        set.add("c");
        set.add("b");
        from.setSet(set);

        {
            Iterator<String> itr = from.getSet().iterator();
            assertEquals("a", itr.next());
            assertEquals("c", itr.next());
            assertEquals("b", itr.next());
        }

        A to = new A();
        new BeanPopulator(from, to).populate();

        {
            Iterator<String> itr = to.getSet().iterator();
            assertEquals("a", itr.next());
            assertEquals("c", itr.next());
            assertEquals("b", itr.next());
        }

        assertNotSame(from.getSet(), to.getSet());
    }
}
