package ca.fangyux.service.Impl;

import ca.fangyux.dto.SetmealDto;
import ca.fangyux.entity.Setmeal;
import ca.fangyux.entity.SetmealDish;
import ca.fangyux.exception.CustomException;
import ca.fangyux.mapper.SetmealMapper;
import ca.fangyux.service.SetmealDishService;
import ca.fangyux.service.SetmealService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class SetmealServiceImpl extends ServiceImpl<SetmealMapper, Setmeal> implements SetmealService {

    @Autowired
    private SetmealDishService setmealDishService;

    @Transactional
    public void saveWithDish(SetmealDto setmealDto) {
        //操作setmeal表，保存套餐基本信息
        this.save(setmealDto);

        //
        List<SetmealDish> setmealDishes=setmealDto.getSetmealDishes();
        setmealDishes.stream().map((setmeal)->{
            setmeal.setSetmealId(setmealDto.getId());
            return setmeal;
        }).collect(Collectors.toList());

        //操作setmeal_dish表，保存套餐和菜品的关联信息
        setmealDishService.saveBatch(setmealDishes);
    }

    @Override
    @Transactional
    public void removeWithDish(List<Long> ids) {
        //查询套餐状态，确定是否可以删除
        LambdaQueryWrapper<Setmeal> queryWrapper=new LambdaQueryWrapper<>();
        queryWrapper.in(Setmeal::getId,ids);
        queryWrapper.eq(Setmeal::getStatus,1);

        int count=this.count(queryWrapper);
        //如果不能删除，跑出一个业务异常
        if(count>0){
            throw new CustomException("套餐正在售卖中，无法删除");
        }else{
            //如果可以删除，先删除套餐表中的套餐_菜品关联关系
            LambdaQueryWrapper<SetmealDish> qw=new LambdaQueryWrapper<>();
            qw.in(SetmealDish::getSetmealId,ids);

            setmealDishService.remove(qw);

            //删除套餐
            this.removeByIds(ids);
        }
    }
}