package com.example.demo

import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultMatchers
import org.springframework.web.servlet.mvc.Controller

@WebMvcTest(Controller::class)
internal class ControllerTest {

    @Autowired
    private lateinit var mockMvc: MockMvc

    @Test
    fun hello() {
        val result = mockMvc.perform(MockMvcRequestBuilders.get("/hello"))

        result
            .andExpect(MockMvcResultMatchers.status().isOk)
            .andExpect(MockMvcResultMatchers.content().string("Hello, Spring-boot!"))
    }
}