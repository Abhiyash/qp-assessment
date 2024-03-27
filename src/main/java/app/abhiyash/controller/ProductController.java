package app.abhiyash.controller;

import app.abhiyash.model.Product;
import app.abhiyash.model.ProductInventory;
import app.abhiyash.service.ProductService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class ProductController {
    Logger logger = LoggerFactory.getLogger(ProductController.class);

    @Autowired
    ProductService productService;

    @GetMapping("/admin/getallproducts")
    public List<Product> getProducts(){
        logger.info("Fetching all products");
        return productService.getAllProduct();
    }

    @PostMapping("/admin/addnewproduct")
    public String addNewProduct(@RequestBody Product newProduct){
       logger.info("Adding new Product: " + newProduct);
        return productService.addNewProduct(newProduct);
    }

    @PostMapping("/admin/deleteproduct/{productId}")
    public String removeProduct(@PathVariable int productId){
        logger.info("Deleting product with id: " + productId);
        return productService.deleteProduct(productId);
    }

    @PostMapping("/admin/updateproduct")
    public String updateProduct(@RequestBody Product updateProduct){
        logger.info("Updating Product: " + updateProduct);
        return productService.updateProduct(updateProduct);
    }

    @PostMapping("/admin/manageinventory")
    public String manageInventory(@RequestBody ProductInventory productInventory){
        logger.info("Managing Inventory: " + productInventory);
        return productService.manageInventory(productInventory);
    }

    @GetMapping("/getavailableproducts")
    public List<Product> getAvailableProducts(){
        logger.info("Fetching available Products");
        return productService.getAvailableProducts();
    }
}
