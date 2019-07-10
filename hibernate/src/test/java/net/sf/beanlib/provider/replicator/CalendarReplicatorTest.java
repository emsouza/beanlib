/*
 * Copyright 2008 The Apache Software Foundation.
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

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.sameInstance;
import static org.junit.Assert.assertThat;

import java.util.Calendar;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import net.sf.beanlib.hibernate4.Hibernate4BeanReplicator;

/**
 * @author Joe D. Velopar
 */
@RunWith(JUnit4.class)
public class CalendarReplicatorTest {

    private static class CalendarBean {
        private Calendar calendar;
        private Calendar calendar2;

        public Calendar getCalendar2() {
            return calendar2;
        }

        public void setCalendar2(Calendar calendar2) {
            this.calendar2 = calendar2;
        }

        public Calendar getCalendar() {
            return calendar;
        }

        public void setCalendar(Calendar calendar) {
            this.calendar = calendar;
        }

        @Override
        public boolean equals(Object obj) {
            if (obj instanceof CalendarBean) {
                CalendarBean that = (CalendarBean) obj;
                return calendar == null ? that.getCalendar() == null : calendar.equals(that.getCalendar());
            }
            return false;
        }
    }

    @Test
    public void beanReplicator() {
        CalendarBean from = new CalendarBean();
        from.setCalendar(Calendar.getInstance());
        from.setCalendar2(from.getCalendar());
        CalendarBean to = new BeanReplicator().replicateBean(from);

        assertThat(from, not(sameInstance(to)));
        assertThat(from.getCalendar(), not(sameInstance(to.getCalendar())));
        assertThat(from, is(to));
        assertThat(from.getCalendar(), sameInstance(from.getCalendar2()));
        assertThat(to.getCalendar(), sameInstance(to.getCalendar2()));
    }

    @Test
    public void hibernate5BeanReplicator() {
        CalendarBean from = new CalendarBean();
        from.setCalendar(Calendar.getInstance());
        CalendarBean to = new Hibernate4BeanReplicator().copy(from);

        assertThat(from, not(sameInstance(to)));
        assertThat(from.getCalendar(), not(sameInstance(to.getCalendar())));
        assertThat(from, is(to));
    }
}
