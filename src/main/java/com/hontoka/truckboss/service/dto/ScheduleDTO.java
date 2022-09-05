package com.hontoka.truckboss.service.dto;

import java.io.Serializable;
import java.util.Objects;

/**
 * A DTO for the {@link com.hontoka.truckboss.domain.Schedule} entity.
 */
public class ScheduleDTO implements Serializable {

    private Long id;

    private Integer status;

    private TruckDTO truck;

    private PlanDTO plan;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public TruckDTO getTruck() {
        return truck;
    }

    public void setTruck(TruckDTO truck) {
        this.truck = truck;
    }

    public PlanDTO getPlan() {
        return plan;
    }

    public void setPlan(PlanDTO plan) {
        this.plan = plan;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ScheduleDTO)) {
            return false;
        }

        ScheduleDTO scheduleDTO = (ScheduleDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, scheduleDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "ScheduleDTO{" +
            "id=" + getId() +
            ", status=" + getStatus() +
            ", truck=" + getTruck() +
            ", plan=" + getPlan() +
            "}";
    }
}
