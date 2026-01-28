package com.cart.ecom_proj.controller;

import com.cart.ecom_proj.model.Product;
import com.cart.ecom_proj.model.ErrorResponse;
import com.cart.ecom_proj.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;


@RestController
@CrossOrigin
@RequestMapping("/api")
public class ProductController {

    @Autowired
    private ProductService service;

    @GetMapping("/products")
    public ResponseEntity<List<Product>> getAllProducts(){
        return new ResponseEntity<>(service.getAllProducts(), HttpStatus.OK);
    }

    @GetMapping("/product/{id}")
    public ResponseEntity<Product> getProduct(@PathVariable int id){

        Product product = service.getProductById(id);

        if(product != null)
            return new ResponseEntity<>(product, HttpStatus.OK);
        else
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @PostMapping(value = "/product", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> addProduct(
            @RequestPart("product") Product product,
            @RequestPart(value = "image", required = false) MultipartFile imageFile) {

        try {
            // Validate required fields
            if (product == null || product.getName() == null || product.getName().trim().isEmpty()) {
                return new ResponseEntity<>(new ErrorResponse("Product name is required"), HttpStatus.BAD_REQUEST);
            }

            if (imageFile == null || imageFile.isEmpty()) {
                return new ResponseEntity<>(new ErrorResponse("Image file is required"), HttpStatus.BAD_REQUEST);
            }

            System.out.println("[AddProduct] Received product: " + product);
            System.out.println("[AddProduct] Image name: " + imageFile.getOriginalFilename() + ", Size: " + imageFile.getSize());

            Product savedProduct = service.addProduct(product, imageFile);
            System.out.println("[AddProduct] Successfully saved product with ID: " + savedProduct.getId());
            return new ResponseEntity<>(savedProduct, HttpStatus.CREATED);
        } catch (Exception e) {
            System.err.println("[AddProduct] Error: " + e.getMessage());
            e.printStackTrace();
            return new ResponseEntity<>(
                    new ErrorResponse("Error adding product: " + e.getMessage()),
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("product/{productId}/image")
    public ResponseEntity<byte[]> getImageByProductId(@PathVariable int productId){

        Product product = service.getProductById(productId);
        
        if (product == null || product.getImageDate() == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        
        byte[] imageFile = product.getImageDate();
        String contentType = product.getImageType() != null ? product.getImageType() : "application/octet-stream";

        return ResponseEntity.ok()
                .contentType(MediaType.valueOf(contentType))
                .body(imageFile);
    }

    @PutMapping("/product/{id}")
    public ResponseEntity<String> updateProduct(@PathVariable int id,
                                                @RequestPart Product product,
                                                @RequestPart(required = false) MultipartFile imageFile){

        try {
            // Validate product exists
            Product existingProduct = service.getProductById(id);
            if (existingProduct == null) {
                return new ResponseEntity<>("Product not found", HttpStatus.NOT_FOUND);
            }
            
            // If no image provided, use existing image
            if (imageFile == null || imageFile.isEmpty()) {
                imageFile = null; // Let service handle null image
            }
            
            Product updatedProduct = service.updateProduct(id, product, imageFile);
            return new ResponseEntity<>("Updated", HttpStatus.OK);
        } catch (Exception e) {
            System.err.println("[UpdateProduct] Error: " + e.getMessage());
            e.printStackTrace();
            return new ResponseEntity<>("Error updating product: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping("/product/{id}")
    public ResponseEntity<String> deleteProduct(@PathVariable int id){
        Product product = service.getProductById(id);
        if(product != null) {
            service.deleteProduct(id);
            return new ResponseEntity<>("Deleted", HttpStatus.OK);
        }
        else {
            return new ResponseEntity<>("Product not found", HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/products/search")
    public ResponseEntity<List<Product>> searchProducts(@RequestParam String keyword){
        System.out.println("searching with " + keyword);
        List<Product> products = service.searchProducts(keyword);
        return new ResponseEntity<>(products, HttpStatus.OK);
    }
}

