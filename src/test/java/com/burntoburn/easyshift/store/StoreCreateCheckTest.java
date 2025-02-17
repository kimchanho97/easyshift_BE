package com.burntoburn.easyshift.store;

import com.burntoburn.easyshift.dto.StoreCreateRequest;
import com.burntoburn.easyshift.entity.store.Store;
import com.burntoburn.easyshift.repository.StoreRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class StoreCreateCheckTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private StoreRepository storeRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    public void setup() {
        // 각 테스트 시작 전에 데이터 초기화
        storeRepository.deleteAll();
    }

    /**
     * 매장 생성 테스트 (DTO 사용)
     * POST /api/stores 에서 storeName을 담은 DTO로 매장을 생성하는지 확인
     */
    @Test
    public void testStoreCreationWithDto() throws Exception {
        // 테스트용 매장 이름
        String storeName = "TestStore";
        // DTO 생성
        StoreCreateRequest createRequest = new StoreCreateRequest(storeName);

        // DTO를 JSON 문자열로 변환 ({"storeName":"TestStore"} 형태)
        String requestBody = objectMapper.writeValueAsString(createRequest);

        // POST 요청 실행 및 응답 검증
        mockMvc.perform(post("/api/stores")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.storeName").value(storeName))
                .andExpect(jsonPath("$.storeCode").exists());
    }

    /**
     * 매장 조회 테스트
     * GET /api/stores?storeId={id} 로 요청하여 저장된 매장을 조회하는지 확인
     */
    @Test
    public void testStoreRetrieval() throws Exception {
        // 사전 데이터 준비: Repository에 매장 데이터 저장
        Store store = Store.builder()
                .storeName("Separate Retrieval Store")
                .storeCode(UUID.randomUUID())
                .build();
        store = storeRepository.save(store);

        mockMvc.perform(get("/api/stores")
                        .param("storeId", store.getId().toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(store.getId()))
                .andExpect(jsonPath("$.storeName").value(store.getStoreName()))
                .andExpect(jsonPath("$.storeCode").value(store.getStoreCode().toString()));
    }
}