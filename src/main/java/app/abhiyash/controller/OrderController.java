package app.abhiyash.controller;

import app.abhiyash.model.Order;
import app.abhiyash.model.OrderResponse;
import app.abhiyash.service.OrderService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

@Controller
public class OrderController {
    Logger logger = LoggerFactory.getLogger(OrderController.class);

    @Autowired
    OrderService orderService;

    @PostMapping("/placeorder")
    public @ResponseBody OrderResponse placeOrder(@RequestBody List<Order> newOrder){
        logger.info("Placing a new Order");
        return orderService.placeOrder(newOrder);
    }
}
