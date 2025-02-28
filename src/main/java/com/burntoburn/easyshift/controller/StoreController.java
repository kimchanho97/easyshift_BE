package com.burntoburn.easyshift.controller;

import com.burntoburn.easyshift.dto.store.req.StoreCreateRequest;
import com.burntoburn.easyshift.dto.store.res.StoreDto;
import com.burntoburn.easyshift.dto.store.res.StoreScheduleResponseDTO;
import com.burntoburn.easyshift.dto.store.res.StoreUserDTO;
import com.burntoburn.easyshift.entity.store.Store;
import com.burntoburn.easyshift.service.StoreService;
import com.burntoburn.easyshift.service.store.StoreQueryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/stores")
@RequiredArgsConstructor
public class StoreController {

    private final StoreService storeService;
    private final StoreQueryService storeQueryService;

    // ========================================

    /**
     * 매장 조회 API
     */
    @GetMapping("/{storeId}")
    public ResponseEntity<Void> getStore(@PathVariable Long storeId) {
        // UserId는 spring security의 @AuthenticationPrincipal로 받아올 수 있음
        // Long userId = userDetails.getUserId();

        Long userId = 1L; // 여기서는 임의로 1로 설정
        storeQueryService.getStoreInfo(storeId, userId);


        return ResponseEntity.ok().build();
    }
    // ========================================

    @PostMapping
    public ResponseEntity<Store> createStore(@RequestBody StoreCreateRequest request) {
        Store createdStore = storeService.createStore(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdStore);
    }

    // PUT /stores/{storeId} 요청으로 스토어 이름 업데이트
    @PutMapping("/{storeId}")
    public ResponseEntity<StoreDto> updateStore(
            @PathVariable Long storeId,
            @RequestBody Map<String, String> requestBody) {
        // 예시: requestBody에 "storeName" 키로 새 이름이 전달된다고 가정
        String newStoreName = requestBody.get("storeName");
        Store updatedStore = storeService.updateStore(storeId, newStoreName);
        StoreDto dto = new StoreDto(updatedStore.getId(), updatedStore.getStoreName());
        return ResponseEntity.ok(dto);
    }

    @DeleteMapping("/{storeId}")
    public ResponseEntity<String> deleteStore(@PathVariable("storeId") Long storeId) {
        storeService.deleteStore(storeId);
        return ResponseEntity.ok("매장이 성공적으로 삭제되었습니다.");
    }

    @GetMapping("/by-user")
    public ResponseEntity<List<StoreDto>> getStoreNamesByUserId(@RequestParam("userId") Long userId) {
        List<StoreDto> storeNames = storeService.getStoreNamesByUserId(userId);
        return ResponseEntity.ok(storeNames);
    }

    @PostMapping("/{storeId}/link")
    public ResponseEntity<List<String>> linkStoreToUser(
            @PathVariable("storeId") Long storeId,
            @RequestHeader("Authorization") String token) {
        if (token.startsWith("Bearer ")) {
            token = token.substring(7);
        }
        List<String> storeNames = storeService.linkStoreToUser(token, storeId);
        return ResponseEntity.ok(storeNames);
    }

    /**
     * 매장 조회: storeId와 선택된 scheduleId(Optional)를 받아,
     * scheduleId가 없으면 첫 번째 스케줄을 반환합니다.
     */
    @GetMapping("/detail/{storeId}")
    public ResponseEntity<StoreScheduleResponseDTO> getStoreSchedule(
            @RequestParam("storeId") Long storeId,
            @RequestParam(value = "scheduleId", required = false) Long scheduleId) {
        StoreScheduleResponseDTO response =
                storeService.getStoreSchedule(storeId, Optional.ofNullable(scheduleId));
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{storeId}/users")
    public ResponseEntity<StoreUserDTO> getStoreUsers(@PathVariable Long storeId) {
        StoreUserDTO storeDetail = storeService.getStoreUser(storeId);
        return ResponseEntity.ok(storeDetail);
    }
}
