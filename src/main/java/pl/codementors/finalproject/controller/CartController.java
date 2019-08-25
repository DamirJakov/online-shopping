package pl.codementors.finalproject.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import pl.codementors.finalproject.model.Generate;
import pl.codementors.finalproject.model.LoginUserInfo;
import pl.codementors.finalproject.model.Product;
import pl.codementors.finalproject.model.PurchaseForm;
import pl.codementors.finalproject.repository.ProductRepository;
import pl.codementors.finalproject.repository.PurchaseFormRepository;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.transaction.Transactional;
import java.util.*;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/cart")
public class CartController {
    Logger LOG = LoggerFactory.getLogger(CartController.class);
    @Autowired private ProductRepository productRepository;
    @Autowired private PurchaseFormRepository purchaseFormRepository;

    @GetMapping
    public String viewCart(HttpServletRequest request, Model model) {

        Cookie productsCookie = getProductsCookie(request);
        String[] cartList = productsCookie.getValue().split("\\|");//Use regEx to split
        model.addAttribute("products", productRepository.findAll(Arrays.asList(cartList)));
        return "cart";
    }

    @PostMapping("/")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public String addToCart(@RequestParam("id") String productId,
                            HttpServletRequest request,
                            HttpServletResponse response) {
        Cookie productsCookie = getProductsCookie(request);

        Product product=productRepository.findOne(productId);
        if (product!=null) {
            if (!LoginUserInfo.getUserID().equals(product.getUser().getId())) {
                Set<String> names = Arrays.stream(productsCookie.getValue().split("\\|")).collect(Collectors.toSet());
                if (!names.contains(productId)) {
                    names.add(productId);
                    productsCookie.setPath("/");
                    productsCookie.setValue(names.stream().map(Object::toString).collect(Collectors.joining("|")));
                    response.addCookie(productsCookie);
                    return "202";
                } else {
                    productsCookie.setPath("/");
                    productsCookie.setValue(names.stream().map(Object::toString).collect(Collectors.joining("|")));
                    response.addCookie(productsCookie);
                    return "208";
                }
            }else {
                response.addCookie(productsCookie);
                return "403";
            }
        }else{
            response.addCookie(productsCookie);
            return "404";
        }
    }

    @GetMapping("/remove/{id}")
    @Transactional
    public String removeFromCart(@PathVariable String id, HttpServletRequest request, HttpServletResponse response) {
        Cookie productsCookie = getProductsCookie(request);
        Set<String> names = Arrays.stream(productsCookie.getValue().split("\\|")).collect(Collectors.toSet());
        names.remove(id);
        productsCookie.setValue(names.stream().map(Object::toString).collect(Collectors.joining("|")));
        productsCookie.setPath("/");
        response.addCookie(productsCookie);
        return "redirect:/cart";

    }

    @GetMapping("/buy")
    public String purchaseProduct(Model model, HttpServletRequest request) {
        model.addAttribute("localUser", LoginUserInfo.getUsername());
        Cookie productsCookie = getProductsCookie(request);
        String[] cartList = productsCookie.getValue().split("\\|");//Use regEx to split
        model.addAttribute("products", productRepository.findAll(Arrays.asList(cartList)));
        model.addAttribute("PurchaseForm", new PurchaseForm());
        return "purchaseform";
    }

    @PostMapping("/buy")
    public String buyProduct(@ModelAttribute("PurchaseForm") PurchaseForm purchaseForm) {

        purchaseForm.setId(Generate.Id());
        purchaseForm.setUserId(LoginUserInfo.getUserID());
        purchaseFormRepository.save(purchaseForm);

        return "complete";
    }

    private Cookie getProductsCookie(HttpServletRequest request) {
        Cookie[] cookies = Optional.ofNullable(request.getCookies()).orElse(new Cookie[]{});
        return Arrays.stream(cookies)
                .filter(cookie -> cookie.getName().equals("products"))
                .findFirst()
                .orElseGet(() -> new Cookie("products", ""));

    }

}