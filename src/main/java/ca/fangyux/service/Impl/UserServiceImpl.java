package ca.fangyux.service.Impl;

import ca.fangyux.entity.User;
import ca.fangyux.mapper.UserMapper;
import ca.fangyux.service.UserService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

}