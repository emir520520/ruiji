package ca.fangyux.service;

import ca.fangyux.dto.DishDto;
import ca.fangyux.entity.Dish;
import com.baomidou.mybatisplus.extension.service.IService;

public interface DishService extends IService<Dish> {

    //新增菜品，同时插入菜品对应的口味数据。两张表（dish,dish_flavor)
    public void saveWithFlavor(DishDto dishDto);

    //根据菜品id查询菜品信息和口味信息
    public DishDto getByIdWithFlavor(Long id);

    //更新菜品信息，同时更新菜品对应的口味数据。两张表（dish,dish_flavor)
    void updateWithFlavor(DishDto dishDto);
}