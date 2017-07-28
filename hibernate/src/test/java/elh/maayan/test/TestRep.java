package elh.maayan.test;

import java.lang.reflect.Method;
import java.util.Collection;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.boot.Metadata;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;

import junit.framework.TestCase;
import net.sf.beanlib.hibernate4.Hibernate4BeanReplicator;
import net.sf.beanlib.spi.PropertyFilter;

public class TestRep extends TestCase {

    private SessionFactory sessionFactory;
    private Session session;
    private long id;

    @Override
    protected void setUp() throws Exception {
        super.setUp();

        StandardServiceRegistry standardRegistry = new StandardServiceRegistryBuilder().configure("hibernate.cfg.xml").build();
        Metadata metadata = new MetadataSources(standardRegistry).getMetadataBuilder().build();
        this.sessionFactory = metadata.getSessionFactoryBuilder().build();
        this.session = this.sessionFactory.openSession();

        final Supplier ms = new Supplier("Microsoft");
        ms.setSsn(323);
        final Supplier oracle = new Supplier("oracle");
        oracle.setSsn(222);
        final Supplier mac = new Supplier("mac");
        mac.setSsn(2323);

        final Contract mc = new Contract(323, ms);

        final Factory f = new Factory();
        f.addContract(mc);
        f.addSupplier(ms);

        // f.addSupplier(oracle);
        // f.addSupplier(mac);

        f.setAddress("main street");

        this.session.save(f);
        this.id = f.getId();
        this.session.flush();
        // this.session.connection().commit();
        this.session.close();

    }

    public void testLoad() throws Exception {
        this.session = this.sessionFactory.openSession();
        final Factory load = this.session.load(Factory.class, this.id); // (long) 350);

        System.out.println("factory " + load);
        final Collection<Supplier> suppliers = load.getSuppliers();
        final Collection<Contract> contracts = load.getContracts();
        assertEquals(1, load.getSuppliers().size());
        for (final Contract contract : contracts) {
            final Supplier supplier = contract.getSupplier();
            assertTrue(suppliers.contains(supplier));
            supplier.setName("hello");
            assertEquals(supplier.getName(), suppliers.iterator().next().getName());
        }

        final Hibernate4BeanReplicator r = new Hibernate4BeanReplicator();
        r.initPropertyFilter(new PropertyFilter() {

            @Override
            public boolean propagate(final String propertyName, final Method readerMethod) {
                return !"id".equals(propertyName);
            }
        });
        r.initDebug(true);
        final Factory copy = r.copy(load);
        final Collection<Supplier> clonedSuppliers = copy.getSuppliers();
        final Collection<Contract> clonedContracts = copy.getContracts();
        assertEquals(1, copy.getSuppliers().size());
        for (final Contract contract : clonedContracts) {
            final Supplier supplier = contract.getSupplier();
            assertTrue(clonedSuppliers.contains(supplier));
            supplier.setName("changed value");
            assertEquals(supplier.getName(), clonedSuppliers.iterator().next().getName());
        }
        System.out.println("clone ed" + copy);
        // this.session.save(copy);
    }

    @Override
    protected void tearDown() throws Exception {
        this.session.close();
        this.sessionFactory.close();
    }

}
