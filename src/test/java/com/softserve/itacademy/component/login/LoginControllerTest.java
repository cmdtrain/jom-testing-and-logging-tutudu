package com.softserve.itacademy.component.login;

import com.softserve.itacademy.controller.LoginController;
import com.softserve.itacademy.model.User;
import com.softserve.itacademy.model.UserRole;
import com.softserve.itacademy.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = {LoginController.class})
class LoginControllerTest {

    @Autowired
    private MockMvc mvc;

    @MockBean
    private UserService userService;

    @Test
    void loginShouldReturnLoginViewForAnonymousUser() throws Exception {
        mvc.perform(get("/login")
                        .contentType(MediaType.TEXT_HTML))
                .andExpect(status().isOk())
                .andExpect(view().name("login"));
    }

    @Test
    void loginShouldRedirectToHomeWhenUserInSession() throws Exception {
        mvc.perform(get("/login")
                        .contentType(MediaType.TEXT_HTML)
                        .sessionAttr("user_id", 1L))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/"));
    }

    @Test
    void loginPostShouldRedirectToHomeOnSuccess() throws Exception {
        User user = new User();
        user.setId(1L);
        user.setFirstName("John");
        user.setLastName("Doe");
        user.setEmail("john.doe@example.com");
        user.setPassword("{noop}password");
        user.setRole(UserRole.USER);

        when(userService.findByUsername("john.doe@example.com"))
                .thenReturn(Optional.of(user));

        mvc.perform(post("/login")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("username", "john.doe@example.com")
                        .param("password", "password"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/"));
    }

    @Test
    void loginPostShouldRedirectToLoginWithErrorOnInvalidCredentials() throws Exception {
        when(userService.findByUsername(anyString()))
                .thenReturn(Optional.empty());

        mvc.perform(post("/login")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("username", "wrong@example.com")
                        .param("password", "wrong"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login?error=true"));
    }

    @Test
    void logoutShouldRedirectToLoginWithLogoutParam() throws Exception {
        mvc.perform(post("/logout")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .sessionAttr("user_id", 1L))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login?logout=true"));
    }
}