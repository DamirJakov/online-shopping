package pl.codementors.finalproject.controller;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.codec.Base64;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import pl.codementors.finalproject.model.LocalUser;
import pl.codementors.finalproject.model.UserRole;
import pl.codementors.finalproject.repository.LocalUserRepository;

import java.nio.charset.StandardCharsets;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class LocalUserControllerTest {
    private final String adminName = "andrzejek";
    private final String adminPass = "andrzej";
    private final String secondAdminName = "ulka";
    private final String secondAdminPass = "ula";
    private final String userName = "damir";
    private final String userPass = "damir";
    private LocalUser[] localUsers = new LocalUser[3];

    @Autowired
    private WebApplicationContext context;
    private MockMvc mockMvc;

    @Autowired    LocalUserController userController;

    @Autowired private LocalUserRepository localUserRepository;

    @Before
    public void setup() {
        mockMvc = MockMvcBuilders.webAppContextSetup(context).apply(springSecurity()).build();
        localUserRepository.findOneByUsername(adminName).ifPresent(localUser -> localUsers[0] = localUser);
        localUserRepository.findOneByUsername(secondAdminName).ifPresent(localUser -> localUsers[1] = localUser);
        localUserRepository.findOneByUsername(userName).ifPresent(localUser -> localUsers[2] = localUser);
    }

    @Test
    public void contexLoads() {
        assertThat(userController).isNotNull();
    }

    public String base64Authorization(String user, String password) {
        String toEncode=user+":"+password;
        return "Basic "+ new String(Base64.encode(toEncode.getBytes(StandardCharsets.UTF_8)));

    }
    @Test
    public void listOfUsersLoggedAsAdmin() throws Exception {
        mockMvc.perform(get("/user")
                .header("Authorization", base64Authorization(adminName,adminPass)))
                .andExpect(status().isOk())
                .andExpect(model().attribute("users", hasSize(3)));
    }

    @Test
    public void listOfUsersLoggedAsUser() throws Exception {
        mockMvc.perform(get("/user")
                .header("Authorization", base64Authorization(userName,userPass)))
                .andExpect(status().isOk())
                .andExpect(model().attribute("users", hasSize(1)));
    }

    @Test
    public void getUserTemplate() throws Exception {
        mockMvc.perform(get("/user/add")).andExpect(status().isOk());
    }

    @Test
    public void addNewUser() throws Exception{
        LocalUser user = new LocalUser();
        user.setName("Kojot");
        user.setSurname("Volk");
        user.setUsername("KojotVolk");
        user.setPassword("kojot");
        user.setEmail("k@poczta.pl");
        user.setRole(UserRole.USER);
        user.setActive(true);
        mockMvc.perform(post("/user/add").flashAttr("user", user))
        .andExpect(status().is3xxRedirection());
    }

    @Test
    public void updateOwnAdminAsAdmin() throws Exception{
        mockMvc.perform(get("/user/update/"+localUsers[1].getId())
                .header("Authorization", base64Authorization(adminName,adminPass)))
                .andExpect(view().name("user/addUser"));
    }
    @Test
    public void updateUserAsAdmin() throws Exception{
        mockMvc.perform(get("/user/update/"+localUsers[2].getId())
                .header("Authorization", base64Authorization(adminName,adminPass)))
                .andExpect(view().name("user/addUser"));
    }

    @Test
    public void updateAdminAsUser() throws Exception{
        mockMvc.perform(get("/user/update/"+localUsers[1].getId())
                .header("Authorization", base64Authorization(userName,userPass)))
                .andExpect(view().name("error"));
    }

    @Test
    public void updateOwnUserInfo() throws Exception{
        mockMvc.perform(get("/user/update/"+localUsers[2].getId())
                .header("Authorization", base64Authorization(userName,userPass)))
                .andExpect(view().name("user/addUser"));
    }


    @Test
    public void deleteAdminAsUser() throws Exception{
        mockMvc.perform(get("/user/delete/"+localUsers[1].getId())
                .header("Authorization", base64Authorization(userName,userPass)))
                .andExpect(view().name("error"));
    }
    @Test
    public void deleteOwnUser() throws Exception{
        mockMvc.perform(get("/user/delete/"+localUsers[2].getId())
                .header("Authorization", base64Authorization(userName,userPass)))
                .andExpect(view().name("redirect:/user"));
    }
    @Test
    public void deleteOwnAdminAsAdmin() throws Exception{
        mockMvc.perform(get("/user/delete/"+localUsers[1].getId())
                .header("Authorization", base64Authorization(adminName,adminPass)))
                .andExpect(view().name("redirect:/user"));
    }

    @Test
    public void deleteUserAsAdmin() throws Exception{
        mockMvc.perform(get("/user/delete/"+localUsers[2].getId())
                .header("Authorization", base64Authorization(adminName,adminPass)))
                .andExpect(view().name("redirect:/user"));
    }
}