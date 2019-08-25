package pl.codementors.finalproject.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import pl.codementors.finalproject.model.Product;
import pl.codementors.finalproject.repository.ProductRepository;

import java.sql.Timestamp;
import java.util.List;

@RestController
public class StatusChecker {
    @Autowired private ProductRepository productRepository;

    @RequestMapping(value = "/check/list{t}", method = RequestMethod.GET, produces = "application/json")

    public List<Product> filterByNameContains(@PathVariable(name = "t") String t) {
        if (t.chars().allMatch(Character::isDigit)) {
            return productRepository.findAllByTimeStampGreaterThan(new Timestamp(Long.valueOf(t)));
        } else {
            return productRepository.findAllByTimeStampGreaterThan(new Timestamp(0));
        }
    }
}
