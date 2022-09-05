package com.hontoka.truckboss.service.dto;

import java.io.Serializable;
import java.util.Objects;

/**
 * A DTO for the {@link com.hontoka.truckboss.domain.Plan} entity.
 */
public class PlanDTO implements Serializable {

    private Long id;

    private Double startLat;

    private Double startLong;

    private Double endLat;

    private Double endLong;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Double getStartLat() {
        return startLat;
    }

    public void setStartLat(Double startLat) {
        this.startLat = startLat;
    }

    public Double getStartLong() {
        return startLong;
    }

    public void setStartLong(Double startLong) {
        this.startLong = startLong;
    }

    public Double getEndLat() {
        return endLat;
    }

    public void setEndLat(Double endLat) {
        this.endLat = endLat;
    }

    public Double getEndLong() {
        return endLong;
    }

    public void setEndLong(Double endLong) {
        this.endLong = endLong;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof PlanDTO)) {
            return false;
        }

        PlanDTO planDTO = (PlanDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, planDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "PlanDTO{" +
            "id=" + getId() +
            ", startLat=" + getStartLat() +
            ", startLong=" + getStartLong() +
            ", endLat=" + getEndLat() +
            ", endLong=" + getEndLong() +
            "}";
    }
}
