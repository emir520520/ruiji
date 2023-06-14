package ca.fangyux.service.Impl;

import ca.fangyux.entity.Employee;
import ca.fangyux.mapper.EmployeeMapper;
import ca.fangyux.service.EmployeeService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

@Service
public class EmployeeServiceImpl extends ServiceImpl<EmployeeMapper, Employee> implements EmployeeService{

}