package ca.fangyux.controller;

import ca.fangyux.Utils.BaseContext;
import ca.fangyux.Utils.R;
import ca.fangyux.entity.ShoppingCart;
import ca.fangyux.service.ShoppingCartService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/shoppingCart")
public class ShoppingCartController {
    @Autowired
    private ShoppingCartService shoppingCartService;

    @PostMapping("/add")
    public R<ShoppingCart> add(@RequestBody ShoppingCart shoppingCart){
        //设置userId
        Long currentUserId= BaseContext.getCurrentLoginUserId();
        shoppingCart.setUserId(currentUserId);

        //查询当前菜品或套餐是否在购物车中，如果在，将数量+1
        Long dishId=shoppingCart.getDishId();

        LambdaQueryWrapper<ShoppingCart> queryWrapper=new LambdaQueryWrapper<>();

        queryWrapper.eq(ShoppingCart::getUserId,currentUserId);

        if(dishId!=null){
            //菜品
            queryWrapper.eq(ShoppingCart::getDishId,dishId);
        }else{
            //套餐
            queryWrapper.eq(ShoppingCart::getSetmealId,shoppingCart.getSetmealId());
        }
        ShoppingCart shoppingCart1=shoppingCartService.getOne(queryWrapper);

        if(shoppingCart1!=null){
            shoppingCart1.setNumber(shoppingCart1.getNumber()+1);
            shoppingCartService.updateById(shoppingCart1);

            return R.success(shoppingCart1);
        }else{
            shoppingCart.setNumber(1);
            shoppingCart.setCreateTime(LocalDateTime.now());
            shoppingCartService.save(shoppingCart);

            return R.success(shoppingCart);
        }
    }

    @GetMapping("/list")
    public R<List<ShoppingCart>> getList(){
        LambdaQueryWrapper<ShoppingCart> queryWrapper=new LambdaQueryWrapper<>();
        queryWrapper.eq(ShoppingCart::getUserId,BaseContext.getCurrentLoginUserId());
        queryWrapper.orderByAsc(ShoppingCart::getCreateTime);

        List<ShoppingCart> list=shoppingCartService.list(queryWrapper);

        return R.success(list);
    }

    @DeleteMapping("/clean")
    public R<String> clean(){
        LambdaQueryWrapper<ShoppingCart> queryWrapper=new LambdaQueryWrapper<>();
        queryWrapper.eq(ShoppingCart::getUserId,BaseContext.getCurrentLoginUserId());

        shoppingCartService.remove(queryWrapper);

        return R.success("购物车已清空");
    }
}