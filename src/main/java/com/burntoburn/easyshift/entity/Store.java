package com.burntoburn.easyshift.entity;

import jakarta.persistence.*;

import java.util.UUID;

@Entity
public class Store {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String storeName;

    @Column(unique = true, nullable = false)
    private UUID storeCode;

    protected Store() {
    }

    private Store(Builder builder) {
        this.storeName = builder.storeName;
        this.storeCode = builder.storeCode;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private String storeName;
        private UUID storeCode;

        public Builder storeName(String storeName) {
            this.storeName = storeName;
            return this;
        }

        public Builder storeCode(UUID storeCode) {
            this.storeCode = storeCode;
            return this;
        }

        public Store build() {
            return new Store(this);
        }
    }
}
