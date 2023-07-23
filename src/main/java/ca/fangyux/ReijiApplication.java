package ca.fangyux;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.cache.annotation.EnableCaching;

@Slf4j
@SpringBootApplication
@ServletComponentScan
@EnableCaching
public class ReijiApplication {
    public static void main(String[] args) {
        SpringApplication.run(ReijiApplication.class,args);
        System.out.println("瑞吉外卖项目启动成功");
    }
}