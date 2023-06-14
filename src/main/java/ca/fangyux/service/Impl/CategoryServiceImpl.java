package ca.fangyux.service.Impl;

import ca.fangyux.entity.Category;
import ca.fangyux.entity.Dish;
import ca.fangyux.entity.Setmeal;
import ca.fangyux.exception.CustomException;
import ca.fangyux.mapper.CategoryMapper;
import ca.fangyux.service.CategoryService;
import ca.fangyux.service.DishService;
import ca.fangyux.service.SetmealService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CategoryServiceImpl extends ServiceImpl<CategoryMapper, Category> implements CategoryService {

    @Autowired
    private DishService dishService;

    @Autowired
    private SetmealService setmealService;

    //在删除分类之前，检查该分类有没有关联任何菜品/套餐
    @Override
    public void remove(Long id) {
        LambdaQueryWrapper<Dish> dishLambdaQueryWrapper=new LambdaQueryWrapper<>();
        dishLambdaQueryWrapper.eq(Dish::getCategoryId,id);
        int dishAssociatedCount=dishService.count(dishLambdaQueryWrapper);

        if(dishAssociatedCount>0){
            throw new CustomException("该分类已绑定菜品，请解除绑定后再重试");
        }

        LambdaQueryWrapper<Setmeal> setmealLambdaQueryWrapper=new LambdaQueryWrapper<>();
        setmealLambdaQueryWrapper.eq(Setmeal::getCategoryId,id);
        int setmealAssociatedCount=setmealService.count(setmealLambdaQueryWrapper);

        if(setmealAssociatedCount>0){
            throw new CustomException("该分类已绑定套餐，请解除绑定后再重试");
        }

        super.removeById(id);
    }
}