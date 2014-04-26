package elh.maayan.test;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

@Entity
@SequenceGenerator(sequenceName = "seq", name = "seq")
public class Contract {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq")
    private long id;

    public long getId() {
        return this.id;
    }

    public Contract() {

    }

    public Contract(final double price, final Supplier supplier) {
        super();
        this.price = price;
        setSupplier(supplier);
    }

    public void setId(final long id) {
        this.id = id;
    }

    private double price;

    public double getPrice() {
        return this.price;
    }

    public void setPrice(final double price) {
        this.price = price;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    private Supplier supplier;

    public Supplier getSupplier() {
        return this.supplier;
    }

    public void setSupplier(final Supplier supplier) {
        this.supplier = supplier;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.MULTI_LINE_STYLE);
    }
}
