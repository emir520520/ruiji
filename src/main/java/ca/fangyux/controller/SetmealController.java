package ca.fangyux.controller;

import ca.fangyux.Utils.R;
import ca.fangyux.dto.SetmealDto;
import ca.fangyux.entity.Category;
import ca.fangyux.entity.Setmeal;
import ca.fangyux.service.CategoryService;
import ca.fangyux.service.SetmealService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/setmeal")
@Slf4j
public class SetmealController {
    @Autowired
    private SetmealService setmealService;

    @Autowired
    private CategoryService categoryService;

    @PostMapping
    public R<String> save(@RequestBody SetmealDto setmealDto){

        setmealService.saveWithDish(setmealDto);

        return R.success("新增套餐成功");
    }

    @GetMapping("/page")
    public R<Page> page(int page, int pageSize, String name){
        Page<Setmeal> pageInfo=new Page<Setmeal>();
        Page<SetmealDto> pageInfoDto=new Page<>();

        LambdaQueryWrapper<Setmeal> queryWrapper=new LambdaQueryWrapper<>();
        queryWrapper.like(name!=null,Setmeal::getName,name);
        queryWrapper.orderByDesc(Setmeal::getUpdateTime);
        setmealService.page(pageInfo, queryWrapper);

        BeanUtils.copyProperties(pageInfo,pageInfoDto,"records");
        List<Setmeal> records=pageInfo.getRecords();
        List<SetmealDto> dtoRecords=null;

        dtoRecords=records.stream().map((record)->{
            SetmealDto newRecord=new SetmealDto();
            BeanUtils.copyProperties(record,newRecord);

            Category category=categoryService.getById(record.getCategoryId());

            if(category!=null){
                newRecord.setCategoryName(category.getName());
            }

            return newRecord;
        }).collect(Collectors.toList());

        pageInfoDto.setRecords(dtoRecords);

        return R.success(pageInfoDto);
    }

    @DeleteMapping
    public R<String> delete(@RequestParam List<Long> ids){
        setmealService.removeWithDish(ids);

        return R.success("套餐删除成功");
    }

    @GetMapping("/list")
    public R<List<Setmeal>> list(Setmeal setmeal){
        LambdaQueryWrapper<Setmeal> queryWrapper=new LambdaQueryWrapper<>();
        queryWrapper.eq(setmeal.getCategoryId()!=null,Setmeal::getCategoryId,setmeal.getCategoryId());
        queryWrapper.eq(setmeal.getStatus()!=null,Setmeal::getStatus,setmeal.getStatus());

        queryWrapper.orderByDesc(Setmeal::getUpdateTime);

        List<Setmeal> list=setmealService.list(queryWrapper);

        return R.success(list);
    }
}