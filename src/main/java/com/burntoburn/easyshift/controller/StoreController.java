package com.burntoburn.easyshift.controller;

import com.burntoburn.easyshift.dto.store.req.StoreCreateRequest;
import com.burntoburn.easyshift.dto.store.res.StoreResponse;
import com.burntoburn.easyshift.entity.store.Store;
import com.burntoburn.easyshift.service.StoreService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/stores")
@RequiredArgsConstructor
public class StoreController {

    private final StoreService storeService;

    @GetMapping
    public ResponseEntity<StoreResponse> getStore(@RequestParam("storeId") Long storeId) {
        Store store = storeService.getStoreById(storeId);
        StoreResponse response = new StoreResponse(store.getId(), store.getStoreName(), store.getStoreCode());
        return ResponseEntity.ok(response);
    }

    @PostMapping
    public ResponseEntity<Store> createStore(@RequestBody StoreCreateRequest request) {
        Store createdStore = storeService.createStore(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdStore);
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
