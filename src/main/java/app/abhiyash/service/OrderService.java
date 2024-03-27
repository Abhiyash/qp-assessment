package app.abhiyash.service;

import app.abhiyash.dao.OrderDao;
import app.abhiyash.model.Order;
import app.abhiyash.model.OrderResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class OrderService {

    @Autowired
    OrderDao orderDao;

    public OrderResponse placeOrder(List<Order> orders){
        int orderId = orderDao.placeOrder(orders);
        int orderAmount = orderDao.computeOrderAmount(orderId);
        return new OrderResponse(orderId,orderAmount);
    }
}
