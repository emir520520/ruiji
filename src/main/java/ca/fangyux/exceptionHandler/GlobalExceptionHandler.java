package ca.fangyux.exceptionHandler;

import ca.fangyux.Utils.R;
import ca.fangyux.exception.CustomException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.sql.SQLIntegrityConstraintViolationException;

@ControllerAdvice(annotations={RestController.class, Controller.class})
@ResponseBody
@Slf4j
public class GlobalExceptionHandler {
    @ExceptionHandler(SQLIntegrityConstraintViolationException.class)
    public R<String> handleSQLIntegrityConstraintViolationException(SQLIntegrityConstraintViolationException e){
        log.error(e.getMessage());

        String msg="";

        if(e.getMessage().contains("Duplicate entry")){
            String[] splits=e.getMessage().split(" ");
            msg=splits[2]+"已存在";
        }

        return R.error(msg);
    }

    @ExceptionHandler(CustomException.class)
    public R<String> handleCustomException(CustomException e){
        log.error(e.getMessage());

        return R.error(e.getMessage());
    }
}