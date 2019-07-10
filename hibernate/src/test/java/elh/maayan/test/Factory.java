package elh.maayan.test;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;
import org.hibernate.annotations.AttributeAccessor;

@Entity
@SequenceGenerator(sequenceName = "seq", name = "seq")
public class Factory {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq")
    private long id;

    public long getId() {
        return this.id;
    }

    public void setId(final long id) {
        this.id = id;
    }

    private String address;

    public String getAddress() {
        return this.address;
    }

    public void setAddress(final String name) {
        this.address = name;
    }

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @AttributeAccessor("field")
    @JoinColumn(name = "supp_ref")
    private Set<Supplier> suppliers = new HashSet<>();

    public Collection<Supplier> getSuppliers() {
        return this.suppliers;
    }

    public void addSupplier(final Supplier supplier) {
        getSuppliers().add(supplier);
    }

    public void setSuppliers(final Collection<Supplier> suppliers) {
        this.suppliers = (Set<Supplier>) suppliers;
    }

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinColumn(name = "con_ref")
    private Set<Contract> contracts = new HashSet<>();

    public void addContract(final Contract con) {
        this.contracts.add(con);
    }

    public Collection<Contract> getContracts() {
        return this.contracts;
    }

    public void setContracts(final Collection<Contract> contract) {
        this.contracts = (Set<Contract>) contract;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.MULTI_LINE_STYLE);
    }
}
