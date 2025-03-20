package com.burntoburn.easyshift.service.store;


import com.burntoburn.easyshift.dto.store.use.StoreCreateRequest;
import com.burntoburn.easyshift.dto.store.use.StoreCreateResponse;
import com.burntoburn.easyshift.dto.store.use.StoreResponse;
import com.burntoburn.easyshift.dto.store.use.StoreUpdateRequest;
import com.burntoburn.easyshift.entity.store.Store;
import com.burntoburn.easyshift.repository.schedule.ScheduleTemplateRepository;
import com.burntoburn.easyshift.repository.schedule.ShiftRepository;
import com.burntoburn.easyshift.repository.store.StoreRepository;
import com.burntoburn.easyshift.repository.store.UserStoreRepository;
import com.burntoburn.easyshift.repository.user.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
public class StoreQueryTest {

    @Autowired
    private StoreService storeService;

    @Autowired
    private StoreRepository storeRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserStoreRepository userStoreRepository;

    @Autowired
    private ScheduleTemplateRepository scheduleTemplateRepository;


    @Autowired
    private ShiftRepository shiftRepository;

    @Test
    @DisplayName("매장 생성 쿼리 확인")
    void createStoreQueryTest() {
        // given
        StoreCreateRequest request = new StoreCreateRequest();
        request.setStoreName("Test Store");
        request.setDescription("Test Store Description");

        // when
        StoreCreateResponse response = storeService.createStore(request);

        // then
        assertNotNull(response);
        Long storeId = response.getStoreId();
        Optional<Store> storeOpt = storeRepository.findById(storeId);
        assertTrue(storeOpt.isPresent(), "저장된 매장을 조회할 수 있어야 합니다.");

        Store store = storeOpt.get();
        assertEquals("Test Store", store.getStoreName());
        assertEquals("Test Store Description", store.getDescription());
    }

    @Test
    @DisplayName("매장 수정 쿼리 확인")
    void testUpdateStoreQueryExecution() {
        // given: 먼저 매장을 생성
        StoreCreateRequest createRequest = new StoreCreateRequest();
        createRequest.setStoreName("Original Store Name");
        createRequest.setDescription("Original Description");
        StoreCreateResponse createResponse = storeService.createStore(createRequest);
        Long storeId = createResponse.getStoreId();

        // when: 매장 정보 수정
        StoreUpdateRequest updateRequest = new StoreUpdateRequest("Updated Store Name", "Updated Description");
        storeService.updateStore(storeId, updateRequest);
        storeRepository.flush();

        // then: 수정된 매장을 조회하여 검증
        Store updatedStore = storeRepository.findById(storeId).orElseThrow();
        assertEquals("Updated Store Name", updatedStore.getStoreName());
        assertEquals("Updated Description", updatedStore.getDescription());
        // ※ 콘솔 로그에 update 쿼리가 출력됩니다.
    }

    @Test
    @DisplayName("매장 삭제 쿼리 확인")
    void testDeleteStoreQueryExecution() {
        // given: 삭제할 매장을 생성
        StoreCreateRequest createRequest = new StoreCreateRequest();
        createRequest.setStoreName("Store To Delete");
        createRequest.setDescription("Store To Delete Description");
        StoreCreateResponse createResponse = storeService.createStore(createRequest);
        Long storeId = createResponse.getStoreId();

        // when: 매장 삭제
        storeService.deleteStore(storeId);
        storeRepository.flush();

        // then: 삭제된 매장을 조회할 때 null이어야 함
        Optional<Store> storeOpt = storeRepository.findById(storeId);
        assertFalse(storeOpt.isPresent(), "매장이 삭제되어 조회되지 않아야 합니다.");
        // ※ 콘솔 로그에 delete 쿼리가 출력됩니다.
    }

    @Test
    @DisplayName("매장 정보 단순 조회 쿼리 확인")
    void testGetStoreSimpleInfo() {
        // given: 매장 생성
        StoreCreateRequest createRequest = new StoreCreateRequest();
        createRequest.setStoreName("Simple Info Store");
        createRequest.setDescription("Simple info description");
        StoreCreateResponse createResponse = storeService.createStore(createRequest);

        Store store = storeRepository.findById(createResponse.getStoreId()).orElseThrow();

        // when: storeCode로 단순 조회
        StoreResponse response = storeService.getStoreSimpleInfo(store.getStoreCode());

        // then
        assertNotNull(response, "응답이 null이 아니어야 합니다.");
        assertEquals(store.getId(), response.getStoreId());
        assertEquals(store.getStoreName(), response.getStoreName());
        assertEquals(store.getDescription(), response.getDescription());
        // ※ 콘솔 로그에 select 쿼리가 출력됨을 확인하세요.
    }
}
