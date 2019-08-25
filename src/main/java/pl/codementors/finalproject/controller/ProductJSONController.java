package pl.codementors.finalproject.controller;

import com.fasterxml.jackson.annotation.JsonView;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import pl.codementors.finalproject.model.Product;
import pl.codementors.finalproject.model.ProductView;
import pl.codementors.finalproject.repository.ProductRepository;

import java.util.List;

@RestController
public class ProductJSONController {
    @Autowired private ProductRepository productRepository;

    @RequestMapping(value = "/product/list", method = RequestMethod.GET, produces = "application/json")
    @JsonView(ProductView.class)
    public List<Product> filterByNameContains( ) {
        return productRepository.findAll();
    }
}
