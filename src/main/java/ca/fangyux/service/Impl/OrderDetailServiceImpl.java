package ca.fangyux.service.Impl;

import ca.fangyux.entity.OrderDetail;
import ca.fangyux.mapper.OrderDetailMapper;
import ca.fangyux.service.OrderDetailService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

@Service
public class OrderDetailServiceImpl extends ServiceImpl<OrderDetailMapper, OrderDetail> implements OrderDetailService {

}