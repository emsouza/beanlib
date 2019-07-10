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
import static org.junit.Assert.assertNotSame;

import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Set;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

/**
 * @author Joe D. Velopar
 */
@RunWith(JUnit4.class)
public class HibernateBeanReplicatorTestCollection {

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
    public void linkedHashSet() {
        A from = new A();
        Set<String> set = new LinkedHashSet<>();
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

        A to = new Hibernate4BeanReplicator().deepCopy(from);
        {
            Iterator<String> itr = to.getSet().iterator();
            assertEquals("a", itr.next());
            assertEquals("c", itr.next());
            assertEquals("b", itr.next());
        }

        assertNotSame(from.getSet(), to.getSet());
    }
}
