package com.burntoburn.easyshift.entity;

import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

@Entity
public class ScheduleTemplate {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String scheduleTemplateName;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "store_id", nullable = false)
    private Store store;

    @OneToMany(mappedBy = "scheduleTemplate", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ShiftTemplate> shiftTemplates = new ArrayList<>();

    protected ScheduleTemplate() {
    }

    private ScheduleTemplate(Builder builder) {
        this.scheduleTemplateName = builder.scheduleTemplateName;
        this.store = builder.store;
        this.shiftTemplates = builder.shiftTemplates != null ? builder.shiftTemplates : new ArrayList<>();
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private String scheduleTemplateName;
        private Store store;
        private List<ShiftTemplate> shiftTemplates;

        public Builder scheduleTemplateName(String scheduleTemplateName) {
            this.scheduleTemplateName = scheduleTemplateName;
            return this;
        }

        public Builder store(Store store) {
            this.store = store;
            return this;
        }

        public Builder shiftTemplates(List<ShiftTemplate> shiftTemplates) {
            this.shiftTemplates = shiftTemplates;
            return this;
        }

        public ScheduleTemplate build() {
            return new ScheduleTemplate(this);
        }
    }
}
