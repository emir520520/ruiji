package ca.fangyux.service.Impl;

import ca.fangyux.entity.ShoppingCart;
import ca.fangyux.mapper.ShoppingCartMapper;
import ca.fangyux.service.ShoppingCartService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

@Service
public class ShoppingCartServiceImpl extends ServiceImpl<ShoppingCartMapper, ShoppingCart> implements ShoppingCartService {

}