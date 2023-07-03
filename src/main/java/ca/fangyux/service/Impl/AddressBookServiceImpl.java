package ca.fangyux.service.Impl;

import ca.fangyux.entity.AddressBook;
import ca.fangyux.mapper.AddressBookMapper;
import ca.fangyux.service.AddressBookService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

@Service
public class AddressBookServiceImpl extends ServiceImpl<AddressBookMapper, AddressBook> implements AddressBookService {

}