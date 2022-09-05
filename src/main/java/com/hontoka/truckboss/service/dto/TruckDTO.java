package com.hontoka.truckboss.service.dto;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.Objects;

/**
 * A DTO for the {@link com.hontoka.truckboss.domain.Truck} entity.
 */
public class TruckDTO implements Serializable {

    private Long id;

    private String code;

    private String model;

    private LocalDate registrationDate;

    private Integer monthDue;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public LocalDate getRegistrationDate() {
        return registrationDate;
    }

    public void setRegistrationDate(LocalDate registrationDate) {
        this.registrationDate = registrationDate;
    }

    public Integer getMonthDue() {
        return monthDue;
    }

    public void setMonthDue(Integer monthDue) {
        this.monthDue = monthDue;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof TruckDTO)) {
            return false;
        }

        TruckDTO truckDTO = (TruckDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, truckDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "TruckDTO{" +
            "id=" + getId() +
            ", code='" + getCode() + "'" +
            ", model='" + getModel() + "'" +
            ", registrationDate='" + getRegistrationDate() + "'" +
            ", monthDue=" + getMonthDue() +
            "}";
    }
}
