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
package net.sf.beanlib.provider.collector;

import static org.junit.Assert.assertTrue;

import java.util.Date;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import net.sf.beanlib.hibernate.HibernateBeanReplicator;
import net.sf.beanlib.hibernate5.Hibernate5BeanReplicator;

@RunWith(JUnit4.class)
public class OrderedMethodCollectorTest {

    @Test
    public void test() {
        B b1 = new B("b1");
        b1.setDate(new Date());

        B b2 = new B();
        b1.addB(b1);
        b1.addB(b2);
        assertTrue(2 == b1.getBSet().size());

        HibernateBeanReplicator replicator = new Hibernate5BeanReplicator().initSetterMethodCollector(new OrderedMethodCollector());
        B a1clone = replicator.copy(b1);
        assertTrue(a1clone.getBSet().size() == 2);
    }
}
