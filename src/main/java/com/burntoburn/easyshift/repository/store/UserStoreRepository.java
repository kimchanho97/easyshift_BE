package com.burntoburn.easyshift.repository.store;

import com.burntoburn.easyshift.entity.store.UserStore;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UserStoreRepository extends JpaRepository<UserStore, Long> {

    List<UserStore> findAllByUserId(Long userId);
    boolean existsByUserIdAndStoreId(Long userId, Long storeId);
}
