package ca.fangyux.service;

import ca.fangyux.entity.Orders;
import com.baomidou.mybatisplus.extension.service.IService;

public interface OrderService extends IService<Orders> {

    void submitOrder(Orders order);
}