package com.burntoburn.easyshift.controller;

import com.burntoburn.easyshift.dto.StoreCreateRequest;
import com.burntoburn.easyshift.entity.store.Store;
import com.burntoburn.easyshift.service.StoreService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/stores")
@RequiredArgsConstructor
public class StroeController {

    private final StoreService storeService;

    @GetMapping
    public ResponseEntity<Store> getStore(@RequestParam("storeId") Long storeId) {
        Store store = storeService.getStoreById(storeId);
        return ResponseEntity.ok(store);
    }

    @PostMapping
    public ResponseEntity<Store> createStore(@RequestBody StoreCreateRequest request) {
        Store createdStore = storeService.createStore(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdStore);
    }
}
