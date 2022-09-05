package com.hontoka.truckboss.domain;

import java.io.Serializable;
import java.time.LocalDate;
import javax.persistence.*;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * A Truck.
 */
@Entity
@Table(name = "truck")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class Truck implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @Column(name = "code")
    private String code;

    @Column(name = "model")
    private String model;

    @Column(name = "registration_date")
    private LocalDate registrationDate;

    @Column(name = "month_due")
    private Integer monthDue;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Truck id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCode() {
        return this.code;
    }

    public Truck code(String code) {
        this.setCode(code);
        return this;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getModel() {
        return this.model;
    }

    public Truck model(String model) {
        this.setModel(model);
        return this;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public LocalDate getRegistrationDate() {
        return this.registrationDate;
    }

    public Truck registrationDate(LocalDate registrationDate) {
        this.setRegistrationDate(registrationDate);
        return this;
    }

    public void setRegistrationDate(LocalDate registrationDate) {
        this.registrationDate = registrationDate;
    }

    public Integer getMonthDue() {
        return this.monthDue;
    }

    public Truck monthDue(Integer monthDue) {
        this.setMonthDue(monthDue);
        return this;
    }

    public void setMonthDue(Integer monthDue) {
        this.monthDue = monthDue;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Truck)) {
            return false;
        }
        return id != null && id.equals(((Truck) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Truck{" +
            "id=" + getId() +
            ", code='" + getCode() + "'" +
            ", model='" + getModel() + "'" +
            ", registrationDate='" + getRegistrationDate() + "'" +
            ", monthDue=" + getMonthDue() +
            "}";
    }
}
