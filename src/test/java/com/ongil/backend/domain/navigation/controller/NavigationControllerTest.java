package com.ongil.backend.domain.navigation.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class NavigationControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@Test
	void getNavigationItems_ShouldReturnEmptyList_WhenNoNavigationExists() throws Exception {
		mockMvc.perform(get("/api/navigation"))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.status").value("OK"))
			.andExpect(jsonPath("$.data").isArray());
	}

	@Test
	void getAllNavigationItems_ShouldReturnEmptyList_WhenNoNavigationExists() throws Exception {
		mockMvc.perform(get("/api/navigation/all"))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.status").value("OK"))
			.andExpect(jsonPath("$.data").isArray());
	}
}
