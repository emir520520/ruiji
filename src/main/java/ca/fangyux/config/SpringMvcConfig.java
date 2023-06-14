package ca.fangyux.config;

import ca.fangyux.Utils.JacksonObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport;

import java.util.List;

@Slf4j
@Configuration
public class SpringMvcConfig extends WebMvcConfigurationSupport {
    @Override
    protected void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/backend/**").addResourceLocations("classpath:/backend/");
        registry.addResourceHandler("/front/**").addResourceLocations("classpath:/front/");
        log.info("静态资源映射开启");
    }

    //扩展Spring MVC的消息转换器，用来将控制器方法返回的值转换成JSON数据
    @Override
    protected void extendMessageConverters(List<HttpMessageConverter<?>> converters) {
        //创建Jackson提供的消息转换器
        MappingJackson2HttpMessageConverter msgConverter=new MappingJackson2HttpMessageConverter();

        //设置对象转换器
        msgConverter.setObjectMapper(new JacksonObjectMapper());

        //将Jackson消息转换器放到Spring MVC框架的消息转换器的首位
        converters.add(0,msgConverter);

        super.extendMessageConverters(converters);
    }
}