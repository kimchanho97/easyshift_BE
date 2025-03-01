package com.burntoburn.easyshift.controller;

import com.burntoburn.easyshift.common.response.ApiResponse;
import com.burntoburn.easyshift.dto.store.*;
import com.burntoburn.easyshift.service.store.StoreServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/stores")
@RequiredArgsConstructor
public class StoreController {

    private final StoreServiceImpl storeService;

    // ========================================

    /**
     * 매장 조회 API
     */
    @GetMapping("/{storeId}")
    public ResponseEntity<ApiResponse<StoreInfoResponse>> getStore(@PathVariable Long storeId) {
        // UserId는 spring security의 @AuthenticationPrincipal로 받아올 수 있음
        // Long userId = userDetails.getUserId();

        Long userId = 1L; // 여기서는 임의로 1로 설정
        StoreInfoResponse response = storeService.getStoreInfo(storeId, userId);
        return ResponseEntity.ok(ApiResponse.success(response));
    }
    // ========================================

    /**
     * 매장 생성 API
     */
    @PostMapping
    public ResponseEntity<ApiResponse<StoreCreateResponse>> createStore(@RequestBody StoreCreateRequest request) {
        StoreCreateResponse response = storeService.createStore(request);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    // ========================================

    /**
     * 매장 목록 조회 API
     */
    @GetMapping
    public ResponseEntity<ApiResponse<UserStoresResponse>> getUserStores(@RequestParam Long userId){
        UserStoresResponse response = storeService.getUserStores(userId);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    // ========================================


//    // PUT /stores/{storeId} 요청으로 스토어 이름 업데이트
//    @PutMapping("/{storeId}")
//    public ResponseEntity<StoreResponse> updateStore(
//            @PathVariable Long storeId,
//            @RequestBody Map<String, String> requestBody) {
//        // 예시: requestBody에 "storeName" 키로 새 이름이 전달된다고 가정
//        String newStoreName = requestBody.get("storeName");
//        Store updatedStore = storeService.updateStore(storeId, newStoreName);
//        StoreResponse dto = new StoreResponse(updatedStore.getId(), updatedStore.getStoreName(), updatedStore.getDescription());
//        return ResponseEntity.ok(dto);
//    }
//
//    @DeleteMapping("/{storeId}")
//    public ResponseEntity<String> deleteStore(@PathVariable("storeId") Long storeId) {
//        storeService.deleteStore(storeId);
//        return ResponseEntity.ok("매장이 성공적으로 삭제되었습니다.");
//    }
//
//
//    @PostMapping("/{storeId}/link")
//    public ResponseEntity<List<String>> linkStoreToUser(
//            @PathVariable("storeId") Long storeId,
//            @RequestHeader("Authorization") String token) {
//        if (token.startsWith("Bearer ")) {
//            token = token.substring(7);
//        }
//        List<String> storeNames = storeService.linkStoreToUser(token, storeId);
//        return ResponseEntity.ok(storeNames);
//    }
//
//    @GetMapping("/{storeId}/users")
//    public ResponseEntity<StoreUserDTO> getStoreUsers(@PathVariable Long storeId) {
//        StoreUserDTO storeDetail = storeService.getStoreUser(storeId);
//        return ResponseEntity.ok(storeDetail);
//    }
}
