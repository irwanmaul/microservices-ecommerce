package com.irwan.bvk.product.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.irwan.bvk.product.dto.ApiResponse;
import com.irwan.bvk.product.dto.RegisterProductRequest;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Slf4j
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class ProductControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private String generatedString;

    @BeforeAll
    void setUp() {
        int leftLimit = 97; // letter 'a'
        int rightLimit = 122; // letter 'z'
        int targetStringLength = 10;
        Random random = new Random();
        generatedString = random.ints(leftLimit, rightLimit + 1)
                .limit(targetStringLength)
                .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
                .toString();
    }

    @Test
    @Order(1)
    void successRegisterProduct() throws Exception{
        RegisterProductRequest request = RegisterProductRequest.builder()
                .name(this.generatedString)
                .stock(100)
                .price(1000000).build();
        mockMvc.perform(
                post("/api/product")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
        ).andExpectAll(
                status().isCreated()
        ).andDo(result -> {
            ApiResponse<String> apiResponse = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
            });
            log.info(apiResponse.toString());
            assertEquals("OK", apiResponse.getData());
        });
    }

    @Test
    @Order(2)
    void failedRegisterProduct_nameAlreadyExist() throws Exception{
        RegisterProductRequest request = RegisterProductRequest.builder()
                .name(this.generatedString)
                .stock(100)
                .price(1000000).build();
        mockMvc.perform(
                post("/api/product")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
        ).andExpectAll(
                status().isBadRequest()
        ).andDo(result -> {
            ApiResponse<String> apiResponse = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
            });
            log.info(apiResponse.toString());
            assertEquals("Product Name Already Registered", apiResponse.getErrors());
        });
    }
}