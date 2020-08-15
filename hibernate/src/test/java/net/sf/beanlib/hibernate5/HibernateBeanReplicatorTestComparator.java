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
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNotSame;

import java.util.Comparator;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import net.sf.beanlib.hibernate5.Hibernate5BeanReplicator;

/**
 * @author Joe D. Velopar
 */
@RunWith(JUnit4.class)
public class HibernateBeanReplicatorTestComparator {

    private static Comparator<String> reverseComparator = new Comparator<String>() {
        @Override
        public int compare(String s1, String s2) {
            return s2.compareTo(s1);
        }

    };

    @Test
    public void deepCopySet() {
        Set<String> fromSet = new TreeSet<>();
        fromSet.add("1");
        fromSet.add("2");
        @SuppressWarnings("unchecked")
        Set<String> toSet = new Hibernate5BeanReplicator().deepCopy(fromSet, Set.class);
        assertNotSame(fromSet, toSet);
        assertEquals(fromSet.size(), toSet.size());
    }

    @Test
    public void deepCopySortedSetWithComparator() {
        SortedSet<String> fromSet = new TreeSet<>(reverseComparator);
        fromSet.add("1");
        fromSet.add("2");
        @SuppressWarnings("unchecked")
        SortedSet<String> toSet = new Hibernate5BeanReplicator().deepCopy(fromSet, SortedSet.class);
        assertNotSame(fromSet, toSet);
        assertEquals(fromSet.size(), toSet.size());

        assertNotNull(fromSet.comparator());
        assertNotNull(toSet.comparator());
        assertNotSame(fromSet.comparator(), toSet.comparator());
    }

    @Test
    public void deepCopyMap() {
        Map<String, String> fromMap = new TreeMap<>();
        fromMap.put("1", "1val");
        fromMap.put("2", "2val");
        @SuppressWarnings("unchecked")
        Map<String, String> toMap = new Hibernate5BeanReplicator().deepCopy(fromMap, Map.class);
        assertNotSame(fromMap, toMap);
        assertEquals(fromMap.size(), toMap.size());
    }

    @Test
    public void deepCopySortedMapWithComparator() {
        SortedMap<String, String> fromMap = new TreeMap<>(reverseComparator);
        fromMap.put("1", "1val");
        fromMap.put("2", "2val");
        @SuppressWarnings("unchecked")
        SortedMap<String, String> toMap = new Hibernate5BeanReplicator().deepCopy(fromMap, SortedMap.class);
        assertNotSame(fromMap, toMap);
        assertEquals(fromMap.size(), toMap.size());

        assertNotNull(fromMap.comparator());
        assertNotNull(toMap.comparator());
        assertNotSame(fromMap.comparator(), toMap.comparator());
    }
}
