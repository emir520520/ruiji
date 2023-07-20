package ca.fangyux.controller;

import ca.fangyux.Utils.R;
import ca.fangyux.Utils.ValidateCodeUtils;
import ca.fangyux.entity.User;
import ca.fangyux.service.UserService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpSession;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("/user")
@Slf4j
public class UserController {
    @Autowired
    private UserService userService;

    @Autowired
    private RedisTemplate redisTemplate;

    @PostMapping("/sendMsg")
    public R<String> sendMsg(@RequestBody User user, HttpSession session){
        //获取手机号
        String phone=user.getPhone();

        if(!StringUtils.isEmpty(phone)){
            //生成随机的4位验证码
            String code= String.valueOf(ValidateCodeUtils.generateValidateCode(4));
            log.info("手机验证码--->"+code);

            //调用阿里云短信服务API发送短信
//            SMSUtils.sendMessage("瑞吉外卖","",phone,code);

            //将生成的验证码保存到session中
//            session.setAttribute(phone,code);

            //将生成的验证码缓存到Redis中，有效期为5分钟
            redisTemplate.opsForValue().set(phone,code, 5,TimeUnit.MINUTES);

            return R.success("短信验证码发送成功");
        }

        return  R.error("手机号不能为空");
    }

    @PostMapping("/login")
    public R<User> login(@RequestBody Map map, HttpSession session){
        String phone= map.get("phone").toString();
        String code=map.get("code").toString();

//        Object sessionCode=session.getAttribute(phone);

        //从Redis中获取缓存的验证码
        Object sessionCode=redisTemplate.opsForValue().get(phone);

        if(session!=null && sessionCode.equals(code)){
            //如果是新用户，就自动注册
            LambdaQueryWrapper<User> queryWrapper=new LambdaQueryWrapper<>();
            queryWrapper.eq(User::getPhone,phone);

            User user=userService.getOne(queryWrapper);

            if(user==null){
                log.info("注册新用户...");
                //注册新用户
                user=new User();
                user.setPhone(phone);
                user.setStatus(1);
                userService.save(user);
            }
            session.setAttribute("userId",user.getId());

            //用户登录成功，删除Redis中的验证码
            redisTemplate.delete(phone);

            return R.success(user);
        }

        return  R.error("验证码错误");
    }
}