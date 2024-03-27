package app.abhiyash.service;

import app.abhiyash.dao.ProductsDao;
import app.abhiyash.model.Product;
import app.abhiyash.model.ProductInventory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProductService {

    @Autowired
    ProductsDao productsDao;

    public List<Product> getAllProduct(){
        return productsDao.getAllProducts();
    }

    public String addNewProduct(Product newProduct){
        Boolean result = productsDao.addProduct(newProduct);
        if (result){
            return "Inserted new Product Successfully";
        }
        else{
            return "Insertion failed. Check logs";
        }
    }

    public String deleteProduct(int productId){
        Boolean result = productsDao.deleteProduct(productId);
        if (result){
            return "Deleted product Successfully";
        }
        else{
            return "Deletion failed. Check Logs";
        }
    }

    public String updateProduct(Product updateProduct){
        boolean result = productsDao.updateProduct(updateProduct);
        if (result){
            return "Update Successful";
        }
        else{
            return "Update unsuccessful";
        }
    }

    public String manageInventory(ProductInventory productInventory){
        boolean result = productsDao.manageInventory(productInventory);
        if (result){
            return "Update Successful";
        }
        else{
            return "Update unsuccessful";
        }
    }

    public List<Product> getAvailableProducts(){
        return productsDao.getAvailableProducts();
    }
}
