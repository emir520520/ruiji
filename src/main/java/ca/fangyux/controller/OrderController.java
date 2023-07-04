package ca.fangyux.controller;

import ca.fangyux.Utils.R;
import ca.fangyux.entity.Orders;
import ca.fangyux.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/order")
public class OrderController {
    @Autowired
    private OrderService orderService;

    @PostMapping("/submit")
    public R<String> submitOrder(@RequestBody Orders order){
        orderService.submitOrder(order);

        return R.success("下单成功");
    }
}