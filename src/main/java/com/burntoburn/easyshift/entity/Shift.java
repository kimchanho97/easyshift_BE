package com.burntoburn.easyshift.entity;

import jakarta.persistence.*;

import java.time.LocalDate;
import java.time.LocalTime;

@Entity
public class Shift {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String shiftName;

    @Column(nullable = false)
    private LocalDate shiftDate;

    @Column(nullable = false)
    private LocalTime startTime;

    @Column(nullable = false)
    private LocalTime endTime;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "schedule_id", nullable = false)
    private Schedule schedule;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    protected Shift() {
    }

    private Shift(Builder builder) {
        this.shiftName = builder.shiftName;
        this.shiftDate = builder.shiftDate;
        this.startTime = builder.startTime;
        this.endTime = builder.endTime;
        this.schedule = builder.schedule;
        this.user = builder.user;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private String shiftName;
        private LocalDate shiftDate;
        private LocalTime startTime;
        private LocalTime endTime;
        private Schedule schedule;
        private User user;

        public Builder shiftName(String shiftName) {
            this.shiftName = shiftName;
            return this;
        }

        public Builder shiftDate(LocalDate shiftDate) {
            this.shiftDate = shiftDate;
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

        public Builder schedule(Schedule schedule) {
            this.schedule = schedule;
            return this;
        }

        public Builder user(User user) {
            this.user = user;
            return this;
        }

        public Shift build() {
            return new Shift(this);
        }
    }
}
