package app.abhiyash.dao;

import app.abhiyash.configurations.DatabaseConfigs;
import app.abhiyash.model.Product;
import app.abhiyash.model.ProductInventory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;


import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class ProductsDao {
    Logger logger = LoggerFactory.getLogger(ProductsDao.class);
    @Autowired
    DatabaseConfigs dbconfigs;

    public List<Product> getAllProducts(){
        String allProducts = "Select * from ProductDetails";
        List<Product> allProductsList = getProducts(allProducts);
       return allProductsList;
    }

    public boolean addProduct(Product newProduct){
        String addProductSql = "INSERT INTO ProductDetails(Name, Category, Description, Price) VALUES (?,?,?,?)";
        String addInventorySql = "INSERT INTO Inventory(product_id, Quantity) VALUES (?,?)";
        try(Connection connection = dbconfigs.getMSSQLDataSource().getConnection();
            PreparedStatement addProductPs = connection.prepareStatement(addProductSql, Statement.RETURN_GENERATED_KEYS);
            PreparedStatement addInventoryPs = connection.prepareStatement(addInventorySql)){
            addProductPs.setString(1,newProduct.getName());
            addProductPs.setString(2, newProduct.getCategory());
            addProductPs.setString(3, newProduct.getDescription());
            addProductPs.setInt(4,newProduct.getPrice());
            addProductPs.setQueryTimeout(10);
            addProductPs.executeUpdate();
            ResultSet rs = addProductPs.getGeneratedKeys();

            while (rs != null && rs.next()){
                int productId = rs.getInt(1);
                System.out.println("Inserted New Product: " + newProduct.getName() + " with product_ID: " + productId);
                addInventoryPs.setInt(1,productId);
                addInventoryPs.setInt(2,0);
                int affectedRow = addInventoryPs.executeUpdate();
                if (affectedRow > 0){
                    logger.info("Inserted Quantity 0 for Product_ID " + productId);
                }
            }
        }
        catch (SQLException sqlException){
            logger.error("Exception Occurred: " + sqlException);
            return false;
        }
        return true;
    }
    public boolean deleteProduct(int productId){
        boolean result = true;
        String selectSql = "SELECT * FROM ProductDetails where ID = ?";
        String deleteProductSql = "DELETE FROM ProductDetails where ID = ?";
        String deleteInventorySql = "DELETE FROM Inventory where product_id = ?";
        try(Connection connection = dbconfigs.getMSSQLDataSource().getConnection();
            PreparedStatement selectPs = connection.prepareStatement(selectSql);
            PreparedStatement deleteProductPs = connection.prepareStatement(deleteProductSql);
            PreparedStatement deleteInventoryPs = connection.prepareStatement(deleteInventorySql)){
            selectPs.setInt(1,productId);
            ResultSet rs = selectPs.executeQuery();
            if ( rs != null){
                deleteProductPs.setInt(1,productId);
                int affectedRows = deleteProductPs.executeUpdate();
                if (affectedRows == 1){
                    System.out.println("Successfully deleted product from ProductDetails");
                    deleteInventoryPs.setInt(1,productId);
                    affectedRows = deleteInventoryPs.executeUpdate();
                    if (affectedRows == 1){
                        System.out.println("Successfully deleted product from Inventory");
                    }
                    else{
                        System.out.println("Failed to deleted product quantity from Inventory");
                        result = false;
                    }
                }
                else {
                    System.out.println("Failed deleted product from ProductDetails");
                    result = false;
                }

            }
        }
        catch (SQLException sqlException){
            logger.error("Exception Occurred: " + sqlException);
            result = false;
        }
        return result;
    }
    public boolean updateProduct(Product updateProduct){
        boolean result = true;
        String updateProductSql = "UPDATE ProductDetails SET Name = ?, Category = ?, Description = ?, Price = ? where ID = ? ";
        try(Connection connection = dbconfigs.getMSSQLDataSource().getConnection();
            PreparedStatement updateProductPs = connection.prepareStatement(updateProductSql)){
            updateProductPs.setString(1,updateProduct.getName());
            updateProductPs.setString(2,updateProduct.getCategory());
            updateProductPs.setString(3,updateProduct.getDescription());
            updateProductPs.setInt(4,updateProduct.getPrice());
            updateProductPs.setInt(5,updateProduct.getProductId());
            int affectedRows = updateProductPs.executeUpdate();
            if (affectedRows == 1){
                logger.info("Update Successful");
            }
            else {
                logger.info("Update not successful");
                result = false;
            }
        }
        catch (SQLException sqlException){
            logger.error("Exception Occurred: " + sqlException);
            result = false;
        }
        return result;
    }

    public boolean manageInventory(ProductInventory productInventory){
        boolean result = true;
        String updateInventorySql = "UPDATE Inventory SET Quantity = ? where product_id = ?";
        try(Connection connection = dbconfigs.getMSSQLDataSource().getConnection();
        PreparedStatement updateInventoryPs = connection.prepareStatement(updateInventorySql)){
            updateInventoryPs.setInt(1,productInventory.getQuantity());
            updateInventoryPs.setInt(2,productInventory.getProductId());
            int affectedRows = updateInventoryPs.executeUpdate();
            if (affectedRows == 1){
                logger.info("Update Successful");
            }
            else {
               logger.info("Update not successful");
                result = false;
            }
        }
        catch (SQLException sqlException){
            logger.error("Exception Occurred: " + sqlException);
            result = false;
        }
        return result;
    }

    public List<Product> getAvailableProducts(){
        String availableProductSql = "SELECT pd.ID,pd.Name,pd.Category,pd.Description,pd.Price FROM ProductDetails as pd INNER JOIN Inventory as i on pd.ID = i.product_id where i.Quantity > 0";
        List<Product> availableProductsList = getProducts(availableProductSql);
        return availableProductsList;
    }

    public List<Product> getProducts(String sql){
        JdbcTemplate jdbcTemplate = new JdbcTemplate(dbconfigs.getMSSQLDataSource());
        List<Product> products = new ArrayList<>();
        List<Map<String, Object>> productMapList = jdbcTemplate.queryForList(sql);
        for (Map productRow : productMapList){
            products.add(new Product(Integer.parseInt(productRow.get("ID").toString()),productRow.get("Name").toString(),productRow.get("Category").toString(),productRow.get("Description").toString(),Integer.parseInt(productRow.get("Price").toString())));
        }
        return products;
    }
}
