package pl.codementors.finalproject.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import pl.codementors.finalproject.FinalprojectApplication;
import pl.codementors.finalproject.model.*;
import pl.codementors.finalproject.repository.LocalUserRepository;
import pl.codementors.finalproject.repository.ProductRepository;

import javax.transaction.Transactional;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

@Controller
@RequestMapping("/product")
public class ProductController {

    private static final Logger LOGGER = Logger.getLogger(FinalprojectApplication.class.getName());

    @Autowired private ProductRepository productRepository;
    @Autowired private LocalUserRepository userRepository;

    @GetMapping("")
    public String productList(Model model) {
        if (LoginUserInfo.isUserHasRole(UserRole.ADMIN)) {
            model.addAttribute("editDelete", true);
        }else{
            model.addAttribute("editDelete", false);
        }
        model.addAttribute("products", productRepository.findAllByAvailable(true));
        model.addAttribute("currentUser", LoginUserInfo.getUser());
        return "product";
    }

    @GetMapping("/owner")
    public String productListOwner(Model model) {
        model.addAttribute("editDelete",true);
        model.addAttribute("products", productRepository.findByUserId(LoginUserInfo.getUserID()));
        model.addAttribute("currentUser", LoginUserInfo.getUser());
        return "product";
    }

    @GetMapping("/add")
    public String addProduct(Model model) {
        model.addAttribute("product", new Product());
        return "addproduct";
    }

    @PostMapping(value = "/add")
    public String addProduct(@ModelAttribute("product") Product product) {
        if (StringUtils.isEmpty(product.getId())) {
            product.setId(Generate.Id());
            product.setUser(userRepository.findOne(LoginUserInfo.getUserID()));
            LOGGER.log(Level.INFO,
                       String.format("User id: %s add new product id: %s",
                                     LoginUserInfo.getUserID(), product.getId()));
            productRepository.save(product);
        } else {
            Optional<Product> ids = productRepository.findByUserIdAndId(LoginUserInfo.getUserID(), product.getId());
            if (ids.isPresent()) {
                product.setUser(userRepository.findOne(LoginUserInfo.getUserID()));
                LOGGER.log(Level.INFO,
                           String.format("User id: %s add new product id: %s",
                                         LoginUserInfo.getUserID(), product.getId()));
                productRepository.save(product);
            }
        }

        return "redirect:/product";
    }

    @GetMapping("/remove/{id}")
    @Transactional
    public String deleteProduct(@PathVariable String id) {
        if (productCanByChange(productRepository.findOne(id))) {
            LOGGER.log(Level.INFO,
                       String.format("User id: %s delete product id: %s",
                                     LoginUserInfo.getUserID(), id));
            productRepository.delete(id);
        }
        return "redirect:/product";
    }

    @GetMapping("/update/{id}")
    public String updateProduct(@PathVariable String id, Model model) {
        Product product = productRepository.findOne(id);
        if (productCanByChange(product)) {
                model.addAttribute("product", product);
                return "addproduct";
            }
        return "redirect:/product";
    }


    @PostMapping("/update")
    public String updateProduct(@ModelAttribute("product") Product updateProduct) {
        Product product = productRepository.findOne(updateProduct.getId());
        if (productCanByChange(product)) {
            product.setAvailable(updateProduct.isAvailable());
            product.setDescription(updateProduct.getDescription());
            product.setName(updateProduct.getName());
            product.setPrice(updateProduct.getPrice());
            LOGGER.log(Level.INFO,
                       String.format("User id: %s update product id: %s",
                                     LoginUserInfo.getUserID(), updateProduct.getId()));
            productRepository.save(product);
            return "redirect:/product";
        }
        return "error";
    }

    @GetMapping("/search")
    public String filterByNameContains(@RequestParam(value = "name") String name, Model model) {
        model.addAttribute("products", productRepository.findByNameContains(name));
        return "product";
    }

    private boolean productCanByChange(Product product) {
        if (product==null){
            return false;
        }
        if (LoginUserInfo.isUserHasRole(UserRole.USER)) {
            return product.getUser().getId().equals(LoginUserInfo.getUserID());
        } else if (LoginUserInfo.isUserHasRole(UserRole.ADMIN)) {
            if (product.getUser().getRole().equals(UserRole.ADMIN)) {
                return product.getUser().getId().equals(LoginUserInfo.getUserID());
            } else {
                return true;
            }
        }
        return true;
    }

//    @GetMapping("/{sortBy}")
//    public List<Product> sortBy(@PathVariable(value = "sortBy") String sortBy) {
//        if (sortBy.equals("price")) {
//            return productRepository.findAllByOrderByPrice();
//        } else {
//            return productRepository.findAllByOrderByName();
//        }
//    }
//
//    @GetMapping("/listByName/{name}")
//    public List<Product> filterByName(@PathVariable String name) {
//        return productRepository.findByName(name);
//    }
//    @GetMapping("/listByOwner")
//    public String filerByOwnerId(Model model) {
//        model.addAttribute("products", productRepository.findByUserId(LoginUserInfo.getUserID()));
//        return "product";
//    }
}
