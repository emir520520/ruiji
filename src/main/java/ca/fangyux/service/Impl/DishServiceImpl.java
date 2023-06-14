package ca.fangyux.service.Impl;

import ca.fangyux.entity.Dish;
import ca.fangyux.mapper.DishMapper;
import ca.fangyux.service.DishService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class DishServiceImpl extends ServiceImpl<DishMapper, Dish> implements DishService {

}