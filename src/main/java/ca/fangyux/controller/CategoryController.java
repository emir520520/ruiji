package ca.fangyux.controller;

import ca.fangyux.Utils.R;
import ca.fangyux.entity.Category;
import ca.fangyux.service.CategoryService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/category")
public class CategoryController {
    @Autowired
    private CategoryService categoryService;

    @GetMapping("/page")
    public R<Page> getPage(int page, int pageSize){
        Page<Category> p=new Page<>(page,pageSize);

        LambdaQueryWrapper<Category> queryWrapper=new LambdaQueryWrapper<>();

        queryWrapper.orderByAsc(Category::getSort);

        categoryService.page(p, queryWrapper);

        return R.success(p);
    }

    @PostMapping
    public R<String> save(@RequestBody Category category){
        categoryService.save(category);

        if(category.getType()==1){
            return R.success("新增菜品分类成功");
        }else{
            return R.success("新增套餐分类成功");
        }
    }

    @DeleteMapping
    public R<String> deleteById(Long id){
        categoryService.remove(id);

        return R.success("分类已删除");
    }

    @PutMapping
    public R<String> update(@RequestBody Category category){
        categoryService.updateById(category);

        return R.success("修改分类信息成功");
    }

    @GetMapping("/list")
    public R<List<Category>> getCategories(Category category){
        LambdaQueryWrapper<Category> queryWrapper=new LambdaQueryWrapper<>();

        queryWrapper.eq(category.getType()!=null,Category::getType,category.getType());

        queryWrapper.orderByAsc(Category::getSort).orderByDesc(Category::getUpdateTime);

        List<Category> categories=categoryService.list(queryWrapper);

        return R.success(categories);
    }
}