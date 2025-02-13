package com.burntoburn.easyshift.entity;

import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

@Entity
public class Schedule {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String scheduleName;

    // 예: "2024-11" 형식으로 월 정보를 저장
    private String scheduleMonth;

    private String description;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ScheduleStatus scheduleStatus; // PENDING, COMPLETED

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "store_id", nullable = false)
    private Store store;

    @OneToMany(mappedBy = "schedule", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Shift> shifts = new ArrayList<>();

    protected Schedule() {
    }

    private Schedule(Builder builder) {
        this.scheduleName = builder.scheduleName;
        this.scheduleMonth = builder.scheduleMonth;
        this.description = builder.description;
        this.scheduleStatus = builder.scheduleStatus;
        this.store = builder.store;
        this.shifts = builder.shifts != null ? builder.shifts : new ArrayList<>();
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private String scheduleName;
        private String scheduleMonth;
        private String description;
        private ScheduleStatus scheduleStatus;
        private Store store;
        private List<Shift> shifts;

        public Builder scheduleName(String scheduleName) {
            this.scheduleName = scheduleName;
            return this;
        }

        public Builder scheduleMonth(String scheduleMonth) {
            this.scheduleMonth = scheduleMonth;
            return this;
        }

        public Builder description(String description) {
            this.description = description;
            return this;
        }

        public Builder scheduleStatus(ScheduleStatus scheduleStatus) {
            this.scheduleStatus = scheduleStatus;
            return this;
        }

        public Builder store(Store store) {
            this.store = store;
            return this;
        }

        public Builder shifts(List<Shift> shifts) {
            this.shifts = shifts;
            return this;
        }

        public Schedule build() {
            return new Schedule(this);
        }
    }
}

