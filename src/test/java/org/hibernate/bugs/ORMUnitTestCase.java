/*
 * Copyright 2014 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.hibernate.bugs;

import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.bugs.models.ImmutableEntity;
import org.hibernate.bugs.models.MutableEntity;
import org.hibernate.cfg.AvailableSettings;
import org.hibernate.cfg.Configuration;
import org.hibernate.query.ImmutableEntityUpdateQueryHandlingMode;
import org.hibernate.query.Query;
import org.hibernate.testing.junit4.BaseCoreFunctionalTestCase;
import org.junit.Test;

import java.util.Date;

import static org.junit.Assert.assertEquals;

/**
 * This template demonstrates how to develop a test case for Hibernate ORM, using its built-in unit test framework.
 * Although ORMStandaloneTestCase is perfectly acceptable as a reproducer, usage of this class is much preferred.
 * Since we nearly always include a regression test with bug fixes, providing your reproducer using this method
 * simplifies the process.
 * <p>
 * What's even better?  Fork hibernate-orm itself, add your test case directly to a module's unit tests, then
 * submit it as a PR!
 */
public class ORMUnitTestCase extends BaseCoreFunctionalTestCase {

    // Add your entities here.
    @Override
    protected Class[] getAnnotatedClasses() {
        return new Class[]{
                ImmutableEntity.class,
                MutableEntity.class
        };
    }

    // If those mappings reside somewhere other than resources/org/hibernate/test, change this.
    @Override
    protected String getBaseForMappings() {
        return "org/hibernate/test/";
    }

    // Add in any settings that are specific to your test.  See resources/hibernate.properties for the defaults.
    @Override
    protected void configure(Configuration configuration) {
        super.configure(configuration);

        configuration.setProperty(AvailableSettings.SHOW_SQL, Boolean.TRUE.toString());
        configuration.setProperty(AvailableSettings.FORMAT_SQL, Boolean.TRUE.toString());

        // Set the immutable query handling mode to exception so that the test case will fail if
        // an operation that modifies an immutable entity is detected.
        configuration.setProperty(AvailableSettings.IMMUTABLE_ENTITY_UPDATE_QUERY_HANDLING_MODE,
                ImmutableEntityUpdateQueryHandlingMode.EXCEPTION.toString());
    }

    // Add your tests, using standard JUnit.
    @Test
    public void hhh12927Test() throws Exception {
        // BaseCoreFunctionalTestCase automatically creates the SessionFactory and provides the Session.
        Session s = openSession();
        Transaction tx = s.beginTransaction();
        // Do stuff...

        String selector = "foo";
        ImmutableEntity trouble = new ImmutableEntity(selector);
        s.persist(trouble);

        MutableEntity entity = new MutableEntity(trouble, "start");
        s.persist(entity);


        // Change a muteable value via selection based on an immutable property
        String statement = "Update MutableEntity e set e.changeable = :changeable where e.trouble.id in " +
                "(select i.id from ImmutableEntity i where i.selector = :selector)";

        Query query = s.createQuery(statement);
        query.setParameter("changeable", "end");
        query.setParameter("selector", "foo");
        query.executeUpdate();

        s.refresh(entity);

        assertEquals("end", entity.getChangeable());

        tx.commit();
        s.close();
    }
}
