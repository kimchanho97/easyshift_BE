package com.burntoburn.easyshift.scheduler;

import com.burntoburn.easyshift.entity.user.User;

public class UserPair implements Comparable<UserPair> {
    private final int index;
    private final User user;

    public UserPair(int index, User user) {
        this.index = index;
        this.user = user;
    }

    public int getIndex() {
        return index;
    }

    public User getUser() {
        return user;
    }

    @Override
    public int compareTo(UserPair other) {
        return Integer.compare(this.index, other.index); // index 오름차순 정렬
    }
}
