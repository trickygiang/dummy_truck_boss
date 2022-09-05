package com.hontoka.truckboss.service.dto;

import java.io.Serializable;
import java.time.Instant;
import java.util.Objects;

/**
 * A DTO for the {@link com.hontoka.truckboss.domain.Journey} entity.
 */
public class JourneyDTO implements Serializable {

    private Long id;

    private Instant startTime;

    private Instant endTime;

    private Integer status;

    private ScheduleDTO schedule;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Instant getStartTime() {
        return startTime;
    }

    public void setStartTime(Instant startTime) {
        this.startTime = startTime;
    }

    public Instant getEndTime() {
        return endTime;
    }

    public void setEndTime(Instant endTime) {
        this.endTime = endTime;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public ScheduleDTO getSchedule() {
        return schedule;
    }

    public void setSchedule(ScheduleDTO schedule) {
        this.schedule = schedule;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof JourneyDTO)) {
            return false;
        }

        JourneyDTO journeyDTO = (JourneyDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, journeyDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "JourneyDTO{" +
            "id=" + getId() +
            ", startTime='" + getStartTime() + "'" +
            ", endTime='" + getEndTime() + "'" +
            ", status=" + getStatus() +
            ", schedule=" + getSchedule() +
            "}";
    }
}
