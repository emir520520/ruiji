package ca.fangyux.filter;

import ca.fangyux.Utils.BaseContext;
import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.AntPathMatcher;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Slf4j
@WebFilter(filterName = "PageAccessAuthorizationFilter", urlPatterns = "/*")
public class PageAccessAuthorizationFilter implements Filter {

    public static final AntPathMatcher PATH_MATCHER=new AntPathMatcher();

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest httpRequest= (HttpServletRequest) request;
        HttpServletResponse httpResponse= (HttpServletResponse) response;

        //1.获取本次请求路径
        String requestURL=httpRequest.getRequestURI();

        //2.定义不需要拦截的请求路径
        String[] urls=new String[]{
                "/employee/login",
                "/employee/logout",
                "/backend/**",
                "/front/**",
                "/user/sendMsg",
                "/user/login",
                "/doc.html",
                "/webjars/**",
                "/swagger-resources",
                "/v2/api-docs"
        };

        //3.判断请求是否需要登陆
        boolean result=check(requestURL,urls);

        if(result){
            chain.doFilter(httpRequest,httpResponse);
            return;
        }

        //4.1 判断后台人员登录状态
        if(httpRequest.getSession().getAttribute("employeeId")!=null){
            //将当前登录用户的id存入BaseContext中，以便在同一请求线程中的不同类里共享数据
            Long id= (Long) ((HttpServletRequest) request).getSession().getAttribute("employeeId");
            BaseContext.setCurrentLoginUserId(id);

            chain.doFilter(httpRequest,httpResponse);
            return;
        }

        //4.2 判断移动端用户登录状态
        if(httpRequest.getSession().getAttribute("userId")!=null){
            //将当前登录用户的id存入BaseContext中，以便在同一请求线程中的不同类里共享数据
            Long id= (Long) ((HttpServletRequest) request).getSession().getAttribute("userId");
            BaseContext.setCurrentLoginUserId(id);

            chain.doFilter(httpRequest,httpResponse);
            return;
        }

        httpResponse.getWriter().write(JSON.toJSONString(ca.fangyux.Utils.R.error("NOTLOGIN")));
    }

    public boolean check(String requestURL, String[] urls){
        for(String url:urls){
            if(PATH_MATCHER.match(url,requestURL)){
                return true;
            }
        }

        return false;
    }
}