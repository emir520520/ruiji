package ca.fangyux.controller;

import ca.fangyux.Utils.R;
import ca.fangyux.entity.Employee;
import ca.fangyux.service.EmployeeService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.DigestUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;

@Slf4j
@RestController
@RequestMapping("/employee")
public class EmployeeController {

    @Autowired
    private EmployeeService employeeService;

    @PostMapping("/login")
    public R<Employee> login(HttpServletRequest request,@RequestBody Employee e){
        //1.将页面提交过来的密码进行MD5加密
        String password=e.getPassword();
        password=DigestUtils.md5DigestAsHex(password.getBytes());

        //2.根据用户名查询数据库
        LambdaQueryWrapper<Employee> qw=new LambdaQueryWrapper<>();
        qw.eq(Employee::getUsername,e.getUsername());
        Employee employee=employeeService.getOne(qw);

        //3.检查查询结果
        if(employee==null){
            return R.error("用户名不存在");
        }

        //4.比对密码
        if(!employee.getPassword().equals(password)){
            return R.error("密码错误，登录失败");
        }

        //5.检查员工状态
        if(employee.getStatus()==0){
            return R.error("此账号已被禁用");
        }

        //6.登录成功。将员工id存入session并返回成功结果
        request.getSession().setAttribute("employeeId",employee.getId());
        return R.success(employee);
    }

    @PostMapping("/logout")
    public R<String> logout(HttpServletRequest request){
        //清理session中保存的当前登入员工信息
        request.getSession().removeAttribute("employeeId");
        return R.success("退出成功");
    }

    @PostMapping
    public R<String> save(HttpServletRequest request,@RequestBody Employee employee){
        //用MD5加密初始密码
        employee.setPassword(DigestUtils.md5DigestAsHex("123456".getBytes()));

        employeeService.save(employee);

        return R.success("新增员工成功");
    }

    @GetMapping("/page")
    public R<Page> getEmployeesByPagination(int page, int pageSize, String name){
        //分页构造器
        Page pagination=new Page(page,pageSize);

        //条件构造器
        LambdaQueryWrapper<Employee> queryWrapper=new LambdaQueryWrapper();
        queryWrapper.like(!StringUtils.isEmpty(name),Employee::getName,name);

        //排序条件
        queryWrapper.orderByDesc(Employee::getUpdateTime);

        employeeService.page(pagination,queryWrapper);

        return R.success(pagination);
    }

    @PutMapping
    public R<String> update(HttpServletRequest request,@RequestBody Employee employee){

        employeeService.updateById(employee);

        if(employee.getStatus()==0){
            return R.success("员工已禁用");
        }else{
            return R.success("员工已启用");
        }
    }

    @GetMapping("/{id}")
    public R<Employee> getEmployeeById(@PathVariable Long id){
        Employee e=employeeService.getById(id);

        return R.success(e);
    }
}