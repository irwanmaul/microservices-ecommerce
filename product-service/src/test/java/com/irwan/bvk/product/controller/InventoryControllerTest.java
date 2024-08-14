package com.irwan.bvk.product.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.irwan.bvk.product.dto.ApiResponse;
import com.irwan.bvk.product.dto.GetInventoryResponse;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Slf4j
class InventoryControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void successCheckAvailability() throws Exception {
        MultiValueMap<String, String> param = new LinkedMultiValueMap<>();
        param.addAll("pid", List.of("1", "2"));

        log.info(param.toString());

        mockMvc.perform(
                get("/api/inventory").queryParams(param)
        ).andExpectAll(
                status().isOk()
        ).andDo(result -> {
            ApiResponse<List<GetInventoryResponse>> apiResponse = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
            });
            log.info(apiResponse.toString());
            assertNotNull(apiResponse.getData());
        });
    }
}