package com.burntoburn.easyshift.controller;

import com.burntoburn.easyshift.dto.store.req.StoreCreateRequest;
import com.burntoburn.easyshift.dto.store.res.StoreScheduleResponseDTO;
import com.burntoburn.easyshift.entity.store.Store;
import com.burntoburn.easyshift.service.StoreService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/stores")
@RequiredArgsConstructor
public class StoreController {

    private final StoreService storeService;

    @GetMapping
    public ResponseEntity<StoreScheduleResponseDTO> getStore(@RequestParam("storeId") Long storeId) {
        Store store = storeService.getStoreById(storeId);
        StoreScheduleResponseDTO response = StoreScheduleResponseDTO.builder()
                .storeId(store.getId())
                .storeName(store.getStoreName())
                .schedules(new ArrayList<>())         // 실제 ScheduleSummaryDTO 리스트로 대체
                .selectedSchedule(null)               // 실제 ScheduleDetailDTO 객체로 대체
                .build();
        return ResponseEntity.ok(response);
    }

    @PostMapping
    public ResponseEntity<Store> createStore(@RequestBody StoreCreateRequest request) {
        Store createdStore = storeService.createStore(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdStore);
    }

    @DeleteMapping("/{storeId}")
    public ResponseEntity<String> deleteStore(@PathVariable("storeId") Long storeId){
        storeService.deleteStore(storeId);
        return ResponseEntity.ok("매장이 성공적으로 삭제되었습니다.");
    }

    @GetMapping("/by-user")
    public ResponseEntity<List<String>> getStoreNamesByUserId(@RequestParam("userId") Long userId) {
        List<String> storeNames = storeService.getStoreNamesByUserId(userId);
        return ResponseEntity.ok(storeNames);
    }

    @PostMapping("/{storeId}/link")
    public ResponseEntity<List<String>> linkStoreToUser(
            @PathVariable("storeId") Long storeId,
            @RequestHeader("Authorization") String token){
        if(token.startsWith("Bearer ")){
            token = token.substring(7);
        }
        List<String> storeNames = storeService.linkStoreToUser(token, storeId);
        return ResponseEntity.ok(storeNames);
    }
}
