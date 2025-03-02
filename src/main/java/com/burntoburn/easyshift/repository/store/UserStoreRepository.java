package com.burntoburn.easyshift.repository.store;

import com.burntoburn.easyshift.dto.user.UserDTO;
import com.burntoburn.easyshift.entity.store.Store;
import com.burntoburn.easyshift.entity.store.UserStore;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface UserStoreRepository extends JpaRepository<UserStore, Long> {

    @Query("SELECT us.store FROM UserStore us WHERE us.user.id = :userId")
    List<Store> findStoresByUserId(@Param("userId") Long userId);

    boolean existsByUserIdAndStoreId(Long userId, Long storeId);

    @Query("SELECT new com.burntoburn.easyshift.dto.user.UserDTO(u.id, u.name, u.email, u.phoneNumber, u.avatarUrl, u.role) " +
            "FROM UserStore us JOIN us.user u " +
            "WHERE us.store.id = :storeId")
    List<UserDTO> findUserDTOsByStoreId(@Param("storeId") Long storeId);
}
