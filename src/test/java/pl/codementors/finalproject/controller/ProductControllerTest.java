package pl.codementors.finalproject.controller;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.MessageSource;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.codec.Base64;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import pl.codementors.finalproject.model.Generate;
import pl.codementors.finalproject.model.LocalUser;
import pl.codementors.finalproject.model.Product;
import pl.codementors.finalproject.repository.LocalUserRepository;
import pl.codementors.finalproject.repository.ProductRepository;

import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Locale;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.CoreMatchers.containsString;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


//@RunWith(SpringRunner.class)
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class ProductControllerTest {
    private final String productURL = "/product";
    private final String productAddURL = "/product/add";
    private final String productRemoveURL = "/product/remove/";
    private final String productUpdateURL = "/product/update/";
    private final String productOwnerURL = "/product/owner";
    private final String adminName = "andrzejek";
    private final String adminPass = "andrzej";
    private final String secondAdminName = "ulka";
    private final String secondAdminPass = "ula";
    private final String userName = "damir";
    private final String userPass = "damir";
    private int databaseSize = 3;
    private Product[] product = new Product[3];
    private LocalUser[] localUsers = new LocalUser[3];
    private Product newProduct=new Product();

    @Autowired private WebApplicationContext context;
    private MockMvc mockMvc;

    @Autowired private MessageSource messageSource;
    @Autowired private ProductRepository productRepository;
    @Autowired private LocalUserRepository localUserRepository;

    @Before
    public void setup() {
        mockMvc = MockMvcBuilders.webAppContextSetup(context).apply(springSecurity()).build();
        localUserRepository.findOneByUsername(adminName).ifPresent(localUser -> localUsers[0] = localUser);
        localUserRepository.findOneByUsername(secondAdminName).ifPresent(localUser -> localUsers[1] = localUser);
        localUserRepository.findOneByUsername(userName).ifPresent(localUser -> localUsers[2] = localUser);
        List<Product> productList = productRepository.findAll();
        databaseSize=productList.size();
        productList.forEach(product1 -> {
            if (product1.getUser().getId().equals(localUsers[0].getId())) {
                product[0] = product1;//Admin product
            }
        });
        productList.forEach(product1 -> {
            if (product1.getUser().getId().equals(localUsers[1].getId())) {
                product[1] = product1;//Second admin product
            }
        });
        productList.forEach(product1 -> {
            if (product1.getUser().getId().equals(localUsers[2].getId())) {
                product[2] = product1;//User product
            }
        });
        newProduct.setDescription("Description");
        newProduct.setName(Generate.Id());//To unique name
        newProduct.setPrice(new BigDecimal(100));
        newProduct.setAvailable(true);
    }


    @Test
    public void checkProductsHasSizeWithOutLoginOnWebSiteProduct() throws Exception {
        mockMvc.perform(get(productURL))
                .andExpect(status().isOk())
                .andExpect(model().attribute("products", hasSize(databaseSize)));
    }

    @Test
    public void checkProductsHasSizeWithLoginOnWebSiteProduct() throws Exception {
        mockMvc.perform(loginBuild(userName, userPass, productURL))
                .andExpect(status().isOk())
                .andExpect(model().attribute("products", hasSize(databaseSize)));
    }

    @Test
    public void checkProductsHasSizeWithLoginAsAdminOnWebSiteProduct() throws Exception {
        mockMvc.perform(loginBuild(adminName, adminPass, productURL))
                .andExpect(status().isOk())
                .andExpect(model().attribute("products", hasSize(databaseSize)));
    }
    @Test
    public void checkProductsHasSizeWithLoginAsSecondAdminOnWebSiteProduct() throws Exception {
        mockMvc.perform(loginBuild(secondAdminName, secondAdminPass, productURL))
                .andExpect(status().isOk())
                .andExpect(model().attribute("products", hasSize(databaseSize)));
    }

    @Test
    public void checkEditProductsByAdminOnWebSiteProduct() throws Exception {
        String result = mockMvc.perform(loginBuild(adminName, adminPass, productURL))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();
        final Document parse = Jsoup.parse(result);
        assertThat(parse.select("a[href=\""+productUpdateURL + product[0].getId() + "\"]").html())
                .contains(readMessage("general.edit"));
        assertThat(parse.select("a[href=\""+productUpdateURL + product[2].getId() + "\"]").html())
                .contains(readMessage("general.edit"));
    }

    @Test
    public void checkAdminCanNotEditSecondAdminProductOnWebSiteProduct() throws Exception {
        String result = mockMvc.perform(loginBuild(adminName, adminPass, productURL))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();
        assertThat(Jsoup.parse(result)
                           .select("a[href=\"" + productUpdateURL + product[1].getId() + "\"]")
                           .html()).doesNotContain(readMessage("general.edit"));
    }
    @Test
    public void checkAdminCanNotEditSecondAdminProduct() throws Exception {
        mockMvc.perform(loginBuild(adminName, adminPass, productUpdateURL+"/"+product[1].getId()))
                .andExpect(status().is3xxRedirection());
    }



    @Test
    public void checkProductsDeleteByAdminOnWebSiteMyProduct() throws Exception {
        String result = mockMvc.perform(loginBuild(adminName, adminPass, productURL))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();
        final Document parse = Jsoup.parse(result);
        assertThat(parse.select("a#" + product[0].getId()).attr("onclick")).contains("deleteProduct(this.id)");
        assertThat(parse.select("a#" + product[2].getId()).attr("onclick")).contains("deleteProduct(this.id)");
    }
    @Test
    public void checkAdminCanNotDeleteSecondAdminProductOnWebSiteProduct() throws Exception {
        String result = mockMvc.perform(loginBuild(adminName, adminPass, productURL))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();
        assertThat(Jsoup.parse(result).select("a#" + product[1].getId()).attr("onclick")).doesNotContain("deleteProduct(this.id)");
    }


    @Test
    public void checkUserDoNotSeeEditButtonOnWebSiteProducts() throws Exception {
        String result = mockMvc.perform(loginBuild(userName, userPass, productURL))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();
        final Document parse = Jsoup.parse(result);
        assertThat(parse
                           .select("a[href=\"" + productUpdateURL + product[0].getId() + "\"]")
                           .html()).doesNotContain(readMessage("general.edit"));
        assertThat(parse
                           .select("a[href=\"" + productUpdateURL + product[1].getId() + "\"]")
                           .html()).doesNotContain(readMessage("general.edit"));
        assertThat(parse
                           .select("a[href=\"" + productUpdateURL + product[2].getId() + "\"]")
                           .html()).doesNotContain(readMessage("general.edit"));
    }
    @Test
    public void checkUserDoNotSeeDeleteButtonOnWebSiteProducts() throws Exception {
        String result = mockMvc.perform(loginBuild(userName, userPass, productURL))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();
        final Document parse = Jsoup.parse(result);
        assertThat(parse.select("a#" + product[0].getId()).attr("onclick")).doesNotContain("deleteProduct(this.id)");
        assertThat(parse.select("a#" + product[1].getId()).attr("onclick")).doesNotContain("deleteProduct(this.id)");
        assertThat(parse.select("a#" + product[2].getId()).attr("onclick")).doesNotContain("deleteProduct(this.id)");
    }


    @Test
    public void checkUserCanEditProductsByOnWebSiteMyProduct() throws Exception {
        String result = mockMvc.perform(loginBuild(userName, userPass, productOwnerURL))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();
        final Document parse = Jsoup.parse(result);
        assertThat(parse.select("a[href=\"" + productUpdateURL + product[2].getId() + "\"]").html()).contains(readMessage("general.edit"));
    }

    @Test
    public void checkUserCanDeleteProductsByOnWebSiteMyProduct() throws Exception {
        String result = mockMvc.perform(loginBuild(userName, userPass, productOwnerURL))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();
        final Document parse = Jsoup.parse(result);
        assertThat(parse.select("a#" + product[2].getId()).attr("onclick")).contains("deleteProduct(this.id)");
    }

    @Test
    public void checkUserCanNotEditProductOtherUserOnWebSiteMyProduct() throws Exception {
        String result = mockMvc.perform(loginBuild(userName, userPass, productOwnerURL))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();
        final Document parse = Jsoup.parse(result);
        assertThat(parse.select("a[href=\"/product/update/" + product[0].getId() + "\"]").html()).doesNotContain(readMessage("general.edit"));
        assertThat(parse.select("a[href=\"/product/update/" + product[1].getId() + "\"]").html()).doesNotContain(readMessage("general.edit"));
    }

    @Test
    public void checkUserCanNotDeleteProductOtherUserOnWebSiteMyProduct() throws Exception {
        String result = mockMvc.perform(loginBuild(userName, userPass, productOwnerURL))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();
        final Document parse = Jsoup.parse(result);
        assertThat(parse.select("a#" + product[0].getId()).attr("onclick")).doesNotContain("deleteProduct(this.id)");
        assertThat(parse.select("a#" + product[1].getId()).attr("onclick")).doesNotContain("deleteProduct(this.id)");
    }

    @Test
    public void checkUserCanNotDeleteOtherUserProducts() throws Exception {
        mockMvc.perform(loginBuild(userName, userPass, productRemoveURL+product[0].getId()))
                .andExpect(redirectedUrl(productURL))
                .andExpect(status().isFound());

        mockMvc.perform(loginBuild(userName, userPass, productRemoveURL+product[1].getId()))
                .andExpect(redirectedUrl(productURL))
                .andExpect(status().isFound());
    }

    @Test
    public void checkUserCanNotEditOtherUserProducts() throws Exception {
        mockMvc.perform(loginBuild(userName, userPass, productUpdateURL + product[0].getId()))
                .andExpect(redirectedUrl(productURL))
                .andExpect(status().isFound());
        mockMvc.perform(loginBuild(userName, userPass, productUpdateURL + product[1].getId()))
                .andExpect(redirectedUrl(productURL))
                .andExpect(status().isFound());
    }


    @Test
    public void checkProductsAddWithLoginAsAdmin() throws Exception {
        mockMvc.perform(loginBuild(adminName, adminPass, productAddURL))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString(readMessage("add.product.details"))));
    }

    @Test
    public void checkProductsAddWithLoginAsUser() throws Exception {
        mockMvc.perform(loginBuild(userName, userPass, productAddURL))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString(readMessage("add.product.details"))));
    }

    @Test
    public void checkUserCanAddNewProduct() throws Exception {
        mockMvc.perform(loginBuildPostForm(userName, userPass, productAddURL).flashAttr("product", newProduct))
                .andExpect(status().is3xxRedirection());
        mockMvc.perform(get(productURL))
                .andExpect(status().isOk())
                .andExpect(model().attribute("products", hasSize(databaseSize+1)));
        //Cleanup
        final List<Product> byNameContains = productRepository.findByNameContains(newProduct.getName());
        productRepository.delete(byNameContains.get(0).getId());
    }
    @Test
    public void checkProductsListIncreasedAfterAddingNewProduct() throws Exception {
        mockMvc.perform(loginBuildPostForm(userName, userPass, productAddURL).flashAttr("product", newProduct))
                .andExpect(status().is3xxRedirection());
        mockMvc.perform(get(productURL))
                .andExpect(status().isOk())
                .andExpect(model().attribute("products", hasSize(databaseSize+1)));
        //Cleanup
        final List<Product> byNameContains = productRepository.findByNameContains(newProduct.getName());
        productRepository.delete(byNameContains.get(0).getId());
    }
    @Test
    public void checkUserCanAddNewProductWithNotAvailableFlag() throws Exception {
        newProduct.setAvailable(false);
        mockMvc.perform(loginBuildPostForm(userName, userPass, productAddURL).flashAttr("product", newProduct))
                .andExpect(status().is3xxRedirection());
        mockMvc.perform(get(productURL))
                .andExpect(status().isOk())
                .andExpect(model().attribute("products", hasSize(databaseSize)));
        //Cleanup
        final List<Product> byNameContains = productRepository.findByNameContains(newProduct.getName());
        productRepository.delete(byNameContains.get(0).getId());
        newProduct.setAvailable(true);
    }
    @Test
    public void checkUserCanAddNewProductWithNotAvailableFlagSizeOfProductDoNotChange() throws Exception {
        newProduct.setAvailable(false);
        mockMvc.perform(loginBuildPostForm(userName, userPass, productAddURL).flashAttr("product", newProduct))
                .andExpect(status().is3xxRedirection());

        mockMvc.perform(get(productURL))
                .andExpect(status().isOk())
                .andExpect(model().attribute("products", hasSize(databaseSize)));
        //Cleanup
        final List<Product> byNameContains2 = productRepository.findAll();
        final List<Product> byNameContains = productRepository.findByNameContains(newProduct.getName());
        productRepository.delete(byNameContains.get(0).getId());
        newProduct.setAvailable(true);
    }





















    public MockHttpServletRequestBuilder loginBuildPostForm(String user, String password, String url) {
        return MockMvcRequestBuilders.post(url)
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .accept("*/*")
                .header("Authorization", base64Authorization(user, password));
    }


    public MockHttpServletRequestBuilder loginBuild(String user, String password, String url) {
        return MockMvcRequestBuilders.get(url)
                .contentType(MediaType.APPLICATION_JSON)
                .accept("*/*")
                .header("Authorization", base64Authorization(user, password));
    }

    public String base64Authorization(String user, String password) {
        String toEncode = user + ":" + password;
        return "Basic " + new String(Base64.encode(toEncode.getBytes(StandardCharsets.UTF_8)));
    }
    public String readMessage(String message){
        return messageSource.getMessage(message, null, Locale.ENGLISH);
    }

}