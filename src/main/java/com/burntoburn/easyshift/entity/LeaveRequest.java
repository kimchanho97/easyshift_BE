package com.burntoburn.easyshift.entity;

import jakarta.persistence.*;

import java.time.LocalDate;

@Entity
public class LeaveRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private LocalDate date;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ApprovalStatus approvalStatus; // PENDING, APPROVED, REJECTED

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "schedule_id", nullable = false)
    private Schedule schedule;

    protected LeaveRequest() {
    }

    private LeaveRequest(Builder builder) {
        this.date = builder.date;
        this.approvalStatus = builder.approvalStatus;
        this.user = builder.user;
        this.schedule = builder.schedule;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private LocalDate date;
        private ApprovalStatus approvalStatus;
        private User user;
        private Schedule schedule;

        public Builder date(LocalDate date) {
            this.date = date;
            return this;
        }

        public Builder approvalStatus(ApprovalStatus approvalStatus) {
            this.approvalStatus = approvalStatus;
            return this;
        }

        public Builder user(User user) {
            this.user = user;
            return this;
        }

        public Builder schedule(Schedule schedule) {
            this.schedule = schedule;
            return this;
        }

        public LeaveRequest build() {
            return new LeaveRequest(this);
        }
    }
}
