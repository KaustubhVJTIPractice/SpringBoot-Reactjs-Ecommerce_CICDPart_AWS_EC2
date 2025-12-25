package com.cart.ecom_proj.controller;

import com.cart.ecom_proj.model.Product;
import com.cart.ecom_proj.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin
@RequestMapping("/api")
public class ProductController {

    @Autowired
    private ProductService service;

    // ✅ Get all products
    @GetMapping("/products")
    public ResponseEntity<List<Product>> getAllProducts() {
        return new ResponseEntity<>(service.getAllProducts(), HttpStatus.OK);
    }

    // ✅ Get product by ID
    @GetMapping("/product/{id}")
    public ResponseEntity<Product> getProduct(@PathVariable int id) {
        Product product = service.getProductById(id);
        if (product != null) {
            return new ResponseEntity<>(product, HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    // ✅ ADD PRODUCT (JSON ONLY – FIXED)
    @PostMapping("/add_product")
    public ResponseEntity<Product> addProduct(@RequestBody Product product) {
        Product savedProduct = service.addProduct(product);
        return new ResponseEntity<>(savedProduct, HttpStatus.CREATED);
    }

    // ✅ Update product
    @PutMapping("/product/{id}")
    public ResponseEntity<String> updateProduct(
            @PathVariable int id,
            @RequestBody Product product
    ) {
        Product updated = service.updateProduct(id, product);
        if (updated != null) {
            return new ResponseEntity<>("Updated successfully", HttpStatus.OK);
        }
        return new ResponseEntity<>("Product not found", HttpStatus.NOT_FOUND);
    }

    // ✅ Delete product
    @DeleteMapping("/product/{id}")
    public ResponseEntity<String> deleteProduct(@PathVariable int id) {
        Product product = service.getProductById(id);
        if (product != null) {
            service.deleteProduct(id);
            return new ResponseEntity<>("Deleted successfully", HttpStatus.OK);
        }
        return new ResponseEntity<>("Product not found", HttpStatus.NOT_FOUND);
    }

    // ✅ Search products
    @GetMapping("/products/search")
    public ResponseEntity<List<Product>> searchProducts(@RequestParam String keyword) {
        return new ResponseEntity<>(service.searchProducts(keyword), HttpStatus.OK);
    }
}
