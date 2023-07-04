package ca.fangyux.service.Impl;

import ca.fangyux.Utils.BaseContext;
import ca.fangyux.entity.*;
import ca.fangyux.exception.CustomException;
import ca.fangyux.mapper.OrderMapper;
import ca.fangyux.service.*;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@Service
public class OrderServiceImpl extends ServiceImpl<OrderMapper, Orders> implements OrderService {

    @Autowired
    private ShoppingCartService shoppingCartService;

    @Autowired
    private UserService userService;

    @Autowired
    private AddressBookService addressBookService;

    @Autowired
    private OrderDetailService orderDetailService;

    @Override
    @Transactional
    public void submitOrder(Orders order) {
        //获得当前用户id
        Long userId= BaseContext.getCurrentLoginUserId();

        //查询当前用户的购物车数据
        LambdaQueryWrapper<ShoppingCart> queryWrapper=new LambdaQueryWrapper<ShoppingCart>();
        queryWrapper.eq(ShoppingCart::getUserId,userId);

        List<ShoppingCart> items=shoppingCartService.list(queryWrapper);

        if(items==null || items.size()==0){
            throw new CustomException("购物车为空，无法下单");
        }

        // 查询用户数据
        User user=userService.getById(userId);

        //查询地址数据
        AddressBook addressBook=addressBookService.getById(order.getAddressBookId());

        if(addressBook==null){
            throw new CustomException("地址有误，请重新确认地址");
        }

        //向order表插入数据
        long orderId = IdWorker.getId();//订单号
        AtomicInteger amount = new AtomicInteger(0);

        //遍历购物车数据，计算总金额
        List<OrderDetail> orderDetails=items.stream().map(item->{
            OrderDetail orderDetail = new OrderDetail();
            orderDetail.setOrderId(orderId);
            orderDetail.setNumber(item.getNumber());
            orderDetail.setDishFlavor(item.getDishFlavor());
            orderDetail.setDishId(item.getDishId());
            orderDetail.setSetmealId(item.getSetmealId());
            orderDetail.setName(item.getName());
            orderDetail.setImage(item.getImage());
            orderDetail.setAmount(item.getAmount());
            amount.addAndGet(item.getAmount().multiply(new BigDecimal(item.getNumber())).intValue());
            return orderDetail;
        }).collect(Collectors.toList());

        order.setId(orderId);
        order.setOrderTime(LocalDateTime.now());
        order.setCheckoutTime(LocalDateTime.now());
        order.setStatus(2);
        order.setAmount(new BigDecimal(amount.get()));//总金额
        order.setUserId(userId);
        order.setNumber(String.valueOf(orderId));
        order.setUserName(user.getName());
        order.setConsignee(addressBook.getConsignee());
        order.setPhone(addressBook.getPhone());
        order.setAddress((addressBook.getProvinceName() == null ? "" : addressBook.getProvinceName())
                + (addressBook.getCityName() == null ? "" : addressBook.getCityName())
                + (addressBook.getDistrictName() == null ? "" : addressBook.getDistrictName())
                + (addressBook.getDetail() == null ? "" : addressBook.getDetail()));

        this.save(order);

        //向order_detail表插入数据
        orderDetailService.saveBatch(orderDetails);

        //清空该用户的购物车数据
        shoppingCartService.remove(queryWrapper);
    }
}