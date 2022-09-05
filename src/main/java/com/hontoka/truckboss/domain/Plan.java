package com.hontoka.truckboss.domain;

import java.io.Serializable;
import javax.persistence.*;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * A Plan.
 */
@Entity
@Table(name = "plan")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class Plan implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @Column(name = "start_lat")
    private Double startLat;

    @Column(name = "start_long")
    private Double startLong;

    @Column(name = "end_lat")
    private Double endLat;

    @Column(name = "end_long")
    private Double endLong;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Plan id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Double getStartLat() {
        return this.startLat;
    }

    public Plan startLat(Double startLat) {
        this.setStartLat(startLat);
        return this;
    }

    public void setStartLat(Double startLat) {
        this.startLat = startLat;
    }

    public Double getStartLong() {
        return this.startLong;
    }

    public Plan startLong(Double startLong) {
        this.setStartLong(startLong);
        return this;
    }

    public void setStartLong(Double startLong) {
        this.startLong = startLong;
    }

    public Double getEndLat() {
        return this.endLat;
    }

    public Plan endLat(Double endLat) {
        this.setEndLat(endLat);
        return this;
    }

    public void setEndLat(Double endLat) {
        this.endLat = endLat;
    }

    public Double getEndLong() {
        return this.endLong;
    }

    public Plan endLong(Double endLong) {
        this.setEndLong(endLong);
        return this;
    }

    public void setEndLong(Double endLong) {
        this.endLong = endLong;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Plan)) {
            return false;
        }
        return id != null && id.equals(((Plan) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Plan{" +
            "id=" + getId() +
            ", startLat=" + getStartLat() +
            ", startLong=" + getStartLong() +
            ", endLat=" + getEndLat() +
            ", endLong=" + getEndLong() +
            "}";
    }
}
