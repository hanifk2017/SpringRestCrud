package com.example.demo;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class SpringRestCrudApplicationTests {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private AccountRepository accountRepository;

	@Before
	public void deleteAllBeforeTests() throws Exception {
		accountRepository.deleteAll();
	}

	@Test
	public void shouldCreateEntity() throws Exception {

		mockMvc.perform(post("/account").content("{\"firstName\": \"Joe\", \"secondName\":\"Doe\", \"accountNumber\":\"1234\"}"))
				.andExpect(status().isCreated());
	}

	@Test
	public void shouldRetrieveAllEntities() throws Exception {
		
		mockMvc.perform(post("/account")
				.content("{\"firstName\": \"Joe\", \"secondName\":\"Doe\", \"accountNumber\":\"1234\"}"))
				.andExpect(status().isCreated()).andReturn();
		mockMvc.perform(post("/account")
				.content("{\"firstName\": \"John\", \"secondName\":\"Doe\", \"accountNumber\":\"1235\"}"))
				.andExpect(status().isCreated()).andReturn();

		mockMvc.perform(get("/account")).andExpect(status().isOk()).andExpect(jsonPath("$..account[0].firstName").value("Joe"))
				.andExpect(jsonPath("$..account[0].secondName").value("Doe")).andExpect(jsonPath("$..account[0].accountNumber").value("1234"))
						.andExpect(jsonPath("$..account[1].firstName").value("John"))
						.andExpect(jsonPath("$..account[1].secondName").value("Doe")).andExpect(jsonPath("$..account[1].accountNumber").value("1235"));
	}
	
	@Test
	public void shouldRetrieveEntity() throws Exception {

		MvcResult mvcResult = mockMvc
				.perform(post("/account").content("{\"firstName\": \"Joe\", \"secondName\":\"Doe\", \"accountNumber\":\"1234\"}"))
				.andExpect(status().isCreated()).andReturn();

		String location = mvcResult.getResponse().getHeader("Location");
		mockMvc.perform(get(location)).andExpect(status().isOk()).andExpect(jsonPath("$.firstName").value("Joe"))
				.andExpect(jsonPath("$.secondName").value("Doe")).andExpect(jsonPath("$.accountNumber").value("1234"));
	}

	@Test
	public void shouldDeleteEntity() throws Exception {

		MvcResult mvcResult = mockMvc
				.perform(post("/account").content("{\"firstName\": \"Joe\", \"secondName\":\"Doe\", \"accountNumber\":\"1234\"}"))
				.andExpect(status().isCreated()).andReturn();

		String location = mvcResult.getResponse().getHeader("Location");
		mockMvc.perform(delete(location)).andExpect(status().isNoContent());

		mockMvc.perform(get(location)).andExpect(status().isNotFound());
	}

}
