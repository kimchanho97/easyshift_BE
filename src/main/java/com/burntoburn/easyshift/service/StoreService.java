//package com.burntoburn.easyshift.service;
//
//import com.burntoburn.easyshift.config.jwt.TokenProvider;
//import com.burntoburn.easyshift.dto.schedule.res.ScheduleDetailDTO;
//import com.burntoburn.easyshift.dto.schedule.res.ScheduleSummaryDTO;
//import com.burntoburn.easyshift.dto.shift.res.AssignedShiftDTO;
//import com.burntoburn.easyshift.dto.shift.res.ShiftDateDTO;
//import com.burntoburn.easyshift.dto.shift.res.ShiftGroupDTO;
//import com.burntoburn.easyshift.dto.shift.res.ShiftKey;
//import com.burntoburn.easyshift.dto.store.StoreResponse;
//import com.burntoburn.easyshift.dto.store.StoreScheduleResponseDTO;
//import com.burntoburn.easyshift.dto.store.StoreUserDTO;
//import com.burntoburn.easyshift.dto.user.UserDTO;
//import com.burntoburn.easyshift.entity.schedule.Schedule;
//import com.burntoburn.easyshift.entity.schedule.Shift;
//import com.burntoburn.easyshift.entity.store.Store;
//import com.burntoburn.easyshift.entity.store.UserStore;
//import com.burntoburn.easyshift.entity.user.User;
//import com.burntoburn.easyshift.repository.schedule.ScheduleRepository;
//import com.burntoburn.easyshift.repository.store.StoreRepository;
//import com.burntoburn.easyshift.repository.store.UserStoreRepository;
//import com.burntoburn.easyshift.repository.user.UserRepository;
//import lombok.RequiredArgsConstructor;
//import org.springframework.stereotype.Service;
//import org.springframework.transaction.annotation.Transactional;
//
//import java.time.LocalDate;
//import java.util.*;
//import java.util.stream.Collectors;
//
//@Service
//@RequiredArgsConstructor
//public class StoreService {
//
//    private final StoreRepository storeRepository;
//    private final UserRepository userRepository;
//    private final UserStoreRepository userStoreRepository;
//    private final TokenProvider tokenProvider;
//    // 추가: 스케줄 관련 조회를 위해 필요함
//    private final ScheduleRepository scheduleRepository;
//
//    public Store getStoreById(Long storeId) {
//        return storeRepository.findById(storeId)
//                .orElseThrow(() -> new RuntimeException("해당 매장을 찾을 수 없습니다. id: " + storeId));
//    }
//
//    public void deleteStore(Long storeId) {
//        Store store = storeRepository.findById(storeId)
//                .orElseThrow(() -> new RuntimeException("해당 매장을 찾을 수 없습니다. id: " + storeId));
//
//        storeRepository.delete(store);
//    }
//
//    public Store updateStore(Long storeId, String newStoreName) {
//        Store store = storeRepository.findById(storeId)
//                .orElseThrow(() -> new RuntimeException("해당 매장을 찾을 수 없습니다. id: " + storeId));
//
//        store.setStoreName(newStoreName);
//        return storeRepository.save(store);
//    }
//
//
//    @Transactional
//    public StoreUserDTO getStoreUser(Long storeId) {
//        Store store = storeRepository.findById(storeId)
//                .orElseThrow(() -> new RuntimeException("해당 매장을 찾을 수 없습니다. id: " + storeId));
//
//        // store.getUserStores()를 통해 연관된 사용자 엔티티를 가져오고, UserDTO로 매핑합니다.
//        // 주의: lazy 로딩 문제를 피하기 위해 fetch join 등을 고려할 수 있습니다.
//        var users = store.getUserStores().stream()
//                .map(userStore -> {
//                    var user = userStore.getUser();
//                    return UserDTO.builder()
//                            .id(user.getId())
//                            .name(user.getName())
//                            .email(user.getEmail())
//                            .phoneNumber(user.getPhoneNumber())
//                            .avatarUrl(user.getAvatarUrl())
//                            .role(user.getRole().toString().toLowerCase()) // 예: "worker", "administrator"
//                            .build();
//                })
//                .collect(Collectors.toList());
//
//        return StoreUserDTO.builder()
//                .storeId(store.getId())
//                .storeName(store.getStoreName())
//                .users(users)
//                .build();
//    }
//
//
//    public List<String> linkStoreToUser(String token, Long storeId) {
//        Long userId = tokenProvider.getUserIdFromToken(token);
//
//        Store store = getStoreById(storeId);
//
//        boolean exists = userStoreRepository.existsByUserIdAndStoreId(userId, storeId);
//        if (!exists) {
//            // 실제 DB 조회 없이 프록시 객체를 이용하여 연관관계 설정
//            User user = userRepository.getReferenceById(userId);
//
//            UserStore userStore = UserStore.builder()
//                    .user(user)
//                    .store(store)
//                    .build();
//
//            userStoreRepository.save(userStore);
//        }
//
//        return userStoreRepository.findAllByUserId(userId)
//                .stream()
//                .map(us -> us.getStore().getStoreName())
//                .collect(Collectors.toList());
//    }
//
//}
