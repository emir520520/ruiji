package ca.fangyux.controller;

import ca.fangyux.Utils.R;
import ca.fangyux.dto.DishDto;
import ca.fangyux.entity.Category;
import ca.fangyux.entity.Dish;
import ca.fangyux.entity.DishFlavor;
import ca.fangyux.service.CategoryService;
import ca.fangyux.service.DishFlavorService;
import ca.fangyux.service.DishService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

//菜品管理
@RestController
@RequestMapping("/dish")
@Slf4j
public class DishController {
    @Autowired
    private DishService dishService;

    @Autowired
    private DishFlavorService dishFlavorService;

    @Autowired
    private CategoryService categoryService;

    @PostMapping
    public R<String> saveDish(@RequestBody DishDto dishDto){
        dishService.saveWithFlavor(dishDto);

        return R.success("菜品添加成功");
    }

    @GetMapping("/page")
    public R<Page> page(int page, int pageSize, String name){
        //分页构造器对象
        Page<Dish> pageInfo=new Page<>(page, pageSize);

        LambdaQueryWrapper<Dish> queryWrapper=new LambdaQueryWrapper<>();

        queryWrapper.like(name!=null, Dish::getName, name);

        queryWrapper.orderByDesc(Dish::getUpdateTime);

        dishService.page(pageInfo, queryWrapper);

        //取出pageInfo里的records集合，放入到DishDto类型的PageInfo中
        Page<DishDto> pages=new Page<>();
        List<DishDto> dishDtos=new ArrayList<>();

        List<Dish> dishes=pageInfo.getRecords();

        //获取菜品分类名称（因为dish里只存了分类id)
        for(int i=0;i<dishes.size();i++){
            DishDto dishdto = new DishDto();
            BeanUtils.copyProperties(dishes.get(i),dishdto);

            Category category=categoryService.getById(dishes.get(i).getCategoryId());
            dishdto.setCategoryName(category.getName());

            dishDtos.add(dishdto);
        }

        BeanUtils.copyProperties(pageInfo,pages);
        pages.setRecords(dishDtos);

        return R.success(pages);
    }

    //根据菜品id查询菜品信息和口味信息
    @GetMapping("/{id}")
    public R<DishDto> getDishInfo(@PathVariable Long id){
        DishDto dishDto=dishService.getByIdWithFlavor(id);

        return R.success(dishDto);
    }

    @PutMapping
    public R<String> updateDish(@RequestBody DishDto dishDto){
        dishService.updateWithFlavor(dishDto);

        return R.success("菜品修改成功");
    }

    @GetMapping("/list")
    public R<List<DishDto>> getDishByCategory(Dish dish){
        LambdaQueryWrapper<Dish> queryWrapper=new LambdaQueryWrapper<>();
        queryWrapper.eq(dish.getCategoryId()!=null,Dish::getCategoryId,dish.getCategoryId());
        queryWrapper.eq(Dish::getStatus,1);
        queryWrapper.orderByAsc(Dish::getSort).orderByDesc(Dish::getUpdateTime);

        List<Dish> list=dishService.list(queryWrapper);

        List<DishDto> dtoList=new ArrayList<>();

        for(int i=0;i<list.size();i++){
            DishDto dishdto = new DishDto();
            BeanUtils.copyProperties(list.get(i),dishdto);

            Category category=categoryService.getById(list.get(i).getCategoryId());
            dishdto.setCategoryName(category.getName());

            //获取当前菜品的口味信息
            LambdaQueryWrapper<DishFlavor> queryWrapper1=new LambdaQueryWrapper<DishFlavor>();
            queryWrapper1.eq(DishFlavor::getDishId,list.get(i).getId());

            List<DishFlavor> dishFlavorList= dishFlavorService.list(queryWrapper1);

            dishdto.setFlavors(dishFlavorList);

            dtoList.add(dishdto);
        }

        return R.success(dtoList);
    }
}