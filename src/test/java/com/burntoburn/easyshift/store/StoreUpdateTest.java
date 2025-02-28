package com.burntoburn.easyshift.store;

import com.jayway.jsonpath.JsonPath;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class StoreUpdateTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    public void createUpdateRetrieveStoreIntegrationTest() throws Exception {
        // 1. 매장 생성 (POST /api/stores)
        String createRequestJson = "{\"storeName\": \"OriginalStoreName\"}";
        MvcResult createResult = mockMvc.perform(post("/api/stores")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(createRequestJson))
                .andExpect(status().isCreated())
                // 생성 응답은 Store 엔티티를 반환하므로 필드명이 "storeName"입니다.
                .andExpect(jsonPath("$.storeName").value("OriginalStoreName"))
                .andReturn();

        // 생성된 매장의 id 추출
        String createResponse = createResult.getResponse().getContentAsString();
        Long storeId = JsonPath.parse(createResponse).read("$.id", Long.class);

        // 2. 매장 이름 업데이트 (PUT /api/stores/{storeId})
        String updateRequestJson = "{\"storeName\": \"UpdatedStoreName\"}";
        MvcResult updateResult = mockMvc.perform(put("/api/stores/{storeId}", storeId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(updateRequestJson))
                .andExpect(status().isOk())
                // 업데이트 응답은 StoreDto를 반환하므로 필드명이 "name"입니다.
                .andExpect(jsonPath("$.name").value("UpdatedStoreName"))
                .andReturn();
    }
}
