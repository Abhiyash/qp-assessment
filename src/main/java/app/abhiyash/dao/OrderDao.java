package app.abhiyash.dao;

import app.abhiyash.configurations.DatabaseConfigs;
import app.abhiyash.model.Order;
import app.abhiyash.utils.GroceryAppUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class OrderDao {
    Logger logger = LoggerFactory.getLogger(OrderDao.class);

    @Autowired
    DatabaseConfigs dbconfigs;

    @Autowired
    GroceryAppUtils groceryAppUtils;
    public int placeOrder(List<Order> orders){
        int orderId = Integer.MIN_VALUE;
        String userName = groceryAppUtils.getUserName();
        int userId = getUserId(userName);
        String placeOrderSql = "INSERT INTO Orders (user_id,CreatedDateTime) VALUES (?,?)";
        String addOrderDetailsSql = "INSERT INTO OrderDetailsMain(orderId,productId,quantity) VALUES (?,?,?)";
        try(Connection connection = dbconfigs.getMSSQLDataSource().getConnection();
            PreparedStatement placeOrderPs = connection.prepareStatement(placeOrderSql, Statement.RETURN_GENERATED_KEYS);
            PreparedStatement addOrderDetailsPs = connection.prepareStatement(addOrderDetailsSql)) {
            placeOrderPs.setInt(1,userId);
            placeOrderPs.setObject(2, LocalDateTime.now());
            placeOrderPs.executeUpdate();
            ResultSet rs = placeOrderPs.getGeneratedKeys();

            if (rs != null && rs.next()){
                orderId = rs.getInt(1);
                logger.info("Placed new Order - " + orderId);
                try{
                    for (Order order: orders){
                        addOrderDetailsPs.setInt(1,orderId);
                        addOrderDetailsPs.setInt(2,order.getProductId());
                        addOrderDetailsPs.setInt(3,order.getQuantity());
                        addOrderDetailsPs.execute();
                    }
                }
                catch (SQLException sqlException){
                    logger.error("Exception Occurred while inserting in OrderDetailsMain" + sqlException);
                }
                finally {
                    //If an error occurs then delete the inserted order
                    String deleteOrderSql = "DELETE FROM Orders where ID = ?";
                    try(PreparedStatement deleteOrderPs = connection.prepareStatement(deleteOrderSql)){
                        deleteOrderPs.setInt(1,orderId);
                        boolean result = deleteOrderPs.execute();
                        if(result){
                            logger.info("Rollback Successful. Order {} successfully deleted",orderId);
                        }
                        else {
                            logger.error("Could not delete the order");
                        }
                    }
                    catch (SQLException sqlException){
                        logger.error("Exception occurred during rollback: " + sqlException);
                    }
                }
            }
        }
        catch(SQLException sqlException){
            logger.error("Exception Occurred: " +sqlException);
            orderId = Integer.MIN_VALUE;
        }
        return orderId;
    }

    public int computeOrderAmount(int orderId) {
        String computeOrderAmountSql = "SELECT SUM(pd.Price*od.quantity) as total from OrderDetailsMain as od INNER JOIN ProductDetails pd on od.productId = pd.ID where od.orderId = ?";
        int totalAmount = 0;
        try(Connection connection = dbconfigs.getMSSQLDataSource().getConnection();
            PreparedStatement computeOrderAmountPs = connection.prepareStatement(computeOrderAmountSql)){
            computeOrderAmountPs.setInt(1,orderId);
            ResultSet rs = computeOrderAmountPs.executeQuery();

            if (rs != null && rs.next()){
                totalAmount = rs.getInt(1);
            }
        }
        catch (SQLException sqlException){
            logger.error("Exception Occurred: " +sqlException);
        }
        return totalAmount;
    }

    public int getUserId(String userName){
        String getUserIdSql = "SELECT id FROM UsersActual where username = ?";
        int userId = Integer.MIN_VALUE;
        try(Connection connection = dbconfigs.getMSSQLDataSource().getConnection();
            PreparedStatement getUserIdPs = connection.prepareStatement(getUserIdSql)){
            getUserIdPs.setString(1,userName);
            ResultSet rs = getUserIdPs.executeQuery();

            if (rs != null && rs.next()){
                userId = rs.getInt(1);
            }
        }
        catch (SQLException sqlException) {
            logger.error("Exception Occurred: " + sqlException);
        }
        return userId;
    }
}
