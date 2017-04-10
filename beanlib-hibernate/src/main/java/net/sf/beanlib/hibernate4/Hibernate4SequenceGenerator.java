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

import java.io.Serializable;
import java.util.Properties;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.id.IdentifierGenerator;
import org.hibernate.id.enhanced.SequenceStyleGenerator;
import org.hibernate.internal.SessionFactoryImpl;
import org.hibernate.internal.SessionImpl;
import org.hibernate.service.ServiceRegistry;
import org.hibernate.type.LongType;

/**
 * Hibernate 4 DB Sequence Generator.
 *
 * @author Joe D. Velopar
 */
public class Hibernate4SequenceGenerator {

    private Hibernate4SequenceGenerator() {}

    /** Returns the next sequence id from the specified sequence and session. */
    public static long nextval(final String sequenceName, final Session session) {
        Object target = session;

        SessionImpl sessionImpl;

        if (target instanceof SessionImpl) {
            sessionImpl = (SessionImpl) target;
        } else {
            throw new IllegalStateException("Not yet know how to handle the given session!");
        }
        IdentifierGenerator idGenerator = createIdentifierGenerator(sequenceName, session);
        Serializable id = idGenerator.generate(sessionImpl, null);
        return (Long) id;
    }

    /** Returns the identifier generator created for the specified sequence and session. */
    @SuppressWarnings("resource")
    private static IdentifierGenerator createIdentifierGenerator(String sequenceName, Session session) {
        SessionFactory sessionFactory = session.getSessionFactory();

        if (sessionFactory instanceof SessionFactoryImpl) {

            SessionFactoryImpl sessionFactoryImpl = (SessionFactoryImpl) sessionFactory;
            ServiceRegistry registry = sessionFactoryImpl.getServiceRegistry();

            Properties params = new Properties();
            params.setProperty("sequence", sequenceName);

            SequenceStyleGenerator sequenceStyleGenerator = new SequenceStyleGenerator();
            sequenceStyleGenerator.configure(LongType.INSTANCE, params, registry);

            return sequenceStyleGenerator;

        } else {

            throw new IllegalStateException("Not yet know how to handle the session factory of the given session!");

        }
    }
}
