package pl.codementors.finalproject.controller;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.MessageSource;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.codec.Base64;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import pl.codementors.finalproject.repository.LocalUserRepository;
import java.nio.charset.StandardCharsets;
import java.util.Locale;

import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.hamcrest.CoreMatchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class LoginControllerTest {

    @Autowired
    private WebApplicationContext context;
    private MockMvc mockMvc;
    @Autowired
    private MessageSource messageSource;

    @Autowired LocalUserRepository userRepository;

    final String loginUrl="/login";
    final String loginErrorUrl = "/login?error";
    final String logoutUrl = "/login?logout";

    @Before
    public void setup() {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(context)
                .apply(springSecurity())
                .build();
    }
    @Test
    public void loginAvailableForAll() throws Exception {
        mockMvc
                .perform(get(loginUrl))
                .andExpect(status().isOk())
                .andExpect(view().name("login"));
    }

    @Test
    public void invalidLoginDeniedMessage() throws Exception {
        mockMvc
                .perform(get(loginErrorUrl))
                .andExpect(content().string(containsString(messageSource.getMessage("login.invalid", null, Locale.ENGLISH))));
    }
    @Test
    public void logoutSuccess() throws Exception {
        mockMvc
                .perform(get(logoutUrl))
                .andExpect(content().string(containsString(messageSource.getMessage("login.logout", null, Locale.ENGLISH))));
    }

    @Test
    public void loginBadPassword() throws Exception {
        mockMvc.perform(loginBuild("andrzeje","andrzej1"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    public void loginBadUser() throws Exception {
        mockMvc.perform(loginBuild("andrzeje1","andrzej"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    public void loginNoPassword() throws Exception {
        mockMvc.perform(loginBuild("andrzeje",""))
                .andExpect(status().isUnauthorized());
    }

    @Test
    public void loginNoUserName() throws Exception {
        mockMvc.perform(loginBuild("","andrzej"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    public void loginSuccessWIthUserName() throws Exception {
        mockMvc.perform(loginBuild("andrzejek","andrzej"))
                .andExpect(status().isOk());
    }

    @Test
    public void loginSuccessWIthEmail() throws Exception {
        mockMvc.perform(loginBuild("a@poczta.pl","andrzej"))
                .andExpect(status().isOk());
    }

    public MockHttpServletRequestBuilder loginBuild(String user, String password){
        return MockMvcRequestBuilders.get(loginUrl)
                .contentType(MediaType.APPLICATION_JSON)
                .accept("*/*")
                .header("Authorization", base64Authorization(user,password));
    }
    public String base64Authorization(String user, String password) {
        String toEncode=user+":"+password;
        return "Basic "+ new String(Base64.encode(toEncode.getBytes(StandardCharsets.UTF_8)));
    }
}