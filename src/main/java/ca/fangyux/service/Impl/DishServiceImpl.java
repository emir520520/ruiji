package ca.fangyux.service.Impl;

import ca.fangyux.dto.DishDto;
import ca.fangyux.entity.Dish;
import ca.fangyux.entity.DishFlavor;
import ca.fangyux.mapper.DishMapper;
import ca.fangyux.service.DishFlavorService;
import ca.fangyux.service.DishService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class DishServiceImpl extends ServiceImpl<DishMapper, Dish> implements DishService {

    @Autowired
    private DishFlavorService dishFlavorService;

    @Override
    @Transactional
    public void saveWithFlavor(DishDto dishDto) {
        //保存菜品基本信息到dish表中
        this.save(dishDto);

        //向dish表中插入数据后，会自动将dishId封装给dishDto
        Long dishId=dishDto.getId();

        //遍历DishFlavor集合，向每一DishFlavor添加dishId
        List<DishFlavor> dishFlavors=dishDto.getFlavors();

        dishFlavors=dishFlavors.stream().map((item)->{
            item.setDishId(dishId);
            return item;
        }).collect(Collectors.toList());

        //保存菜品口味到dish_flavor表中
        dishFlavorService.saveBatch(dishFlavors);
    }

    //根据菜品id查询菜品信息和口味信息
    @Override
    public DishDto getByIdWithFlavor(Long id) {
        Dish dish=this.getById(id);

        LambdaQueryWrapper<DishFlavor> queryWrapper=new LambdaQueryWrapper<>();
        queryWrapper.eq(DishFlavor::getDishId,id);

        List<DishFlavor> flavors=dishFlavorService.list(queryWrapper);

        DishDto dishDto=new DishDto();
        BeanUtils.copyProperties(dish, dishDto);

        dishDto.setFlavors(flavors);

        return dishDto;
    }

    @Override
    @Transactional
    public void updateWithFlavor(DishDto dishDto) {
        //更新dish表基本信息
        this.updateById(dishDto);

        //清空dish_flavor表中的该菜品的口味信息
        LambdaQueryWrapper<DishFlavor> queryWrapper=new LambdaQueryWrapper<>();
        queryWrapper.eq(DishFlavor::getDishId,dishDto.getId());

        dishFlavorService.remove(queryWrapper);

        //向dish_flavor中重新添加该菜品的口味信息
        List<DishFlavor> flavors=dishDto.getFlavors();

        flavors=flavors.stream().map((item)->{
            item.setDishId(dishDto.getId());
            return item;
        }).collect(Collectors.toList());

        dishFlavorService.saveBatch(flavors);
    }
}