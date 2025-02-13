package com.burntoburn.easyshift.entity;

import jakarta.persistence.*;

import java.time.LocalTime;

@Entity
public class ShiftTemplate {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String shiftTemplateName;

    @Column(nullable = false)
    private LocalTime startTime;

    @Column(nullable = false)
    private LocalTime endTime;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "schedule_template_id", nullable = false)
    private ScheduleTemplate scheduleTemplate;

    protected ShiftTemplate() {
    }

    private ShiftTemplate(Builder builder) {
        this.shiftTemplateName = builder.shiftTemplateName;
        this.startTime = builder.startTime;
        this.endTime = builder.endTime;
        this.scheduleTemplate = builder.scheduleTemplate;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private String shiftTemplateName;
        private LocalTime startTime;
        private LocalTime endTime;
        private ScheduleTemplate scheduleTemplate;

        public Builder shiftTemplateName(String shiftTemplateName) {
            this.shiftTemplateName = shiftTemplateName;
            return this;
        }

        public Builder startTime(LocalTime startTime) {
            this.startTime = startTime;
            return this;
        }

        public Builder endTime(LocalTime endTime) {
            this.endTime = endTime;
            return this;
        }

        public Builder scheduleTemplate(ScheduleTemplate scheduleTemplate) {
            this.scheduleTemplate = scheduleTemplate;
            return this;
        }

        public ShiftTemplate build() {
            return new ShiftTemplate(this);
        }
    }
}

