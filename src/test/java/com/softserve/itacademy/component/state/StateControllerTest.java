package com.softserve.itacademy.component.state;

import com.softserve.itacademy.controller.StateController;
import com.softserve.itacademy.dto.StateDto;
import com.softserve.itacademy.service.StateService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = {StateController.class})
class StateControllerTest {

    @Autowired
    private MockMvc mvc;

    @MockBean
    private StateService stateService;

    @Test
    void listStatesShouldReturnStateListView() throws Exception {
        List<StateDto> states = List.of(
                StateDto.builder().id(1L).name("New").build(),
                StateDto.builder().id(2L).name("In progress").build()
        );

        when(stateService.findAll()).thenReturn(states);

        mvc.perform(get("/states")
                        .contentType(MediaType.TEXT_HTML))
                .andExpect(status().isOk())
                .andExpect(view().name("state/state-list"))
                .andExpect(model().attributeExists("states"));
    }
}