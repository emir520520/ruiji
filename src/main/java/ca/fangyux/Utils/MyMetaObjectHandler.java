package ca.fangyux.Utils;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@Slf4j
public class MyMetaObjectHandler implements MetaObjectHandler {

    //插入操作时对某些字段进行填充
    @Override
    public void insertFill(MetaObject metaObject) {
        log.info("新增数据-->公共字段填充中...");
        metaObject.setValue("createTime", LocalDateTime.now());
        metaObject.setValue("updateTime", LocalDateTime.now());
        metaObject.setValue("createUser", BaseContext.getCurrentLoginUserId());
        metaObject.setValue("updateUser", BaseContext.getCurrentLoginUserId());
    }

    //更新操作时对某些字段进行填充
    @Override
    public void updateFill(MetaObject metaObject) {
        log.info("更新数据-->公共字段填充中...");
        metaObject.setValue("updateTime", LocalDateTime.now());
        metaObject.setValue("updateUser", BaseContext.getCurrentLoginUserId());
    }
}