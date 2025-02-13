package com.burntoburn.easyshift.entity;

import jakarta.persistence.*;

@Entity
public class UserStore {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "store_id", nullable = false)
    private Store store;

    protected UserStore() {
    }

    private UserStore(Builder builder) {
        this.user = builder.user;
        this.store = builder.store;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private User user;
        private Store store;

        public Builder user(User user) {
            this.user = user;
            return this;
        }

        public Builder store(Store store) {
            this.store = store;
            return this;
        }

        public UserStore build() {
            return new UserStore(this);
        }
    }
}
