package ca.fangyux.Utils;

//基于ThreadLocal的封装工具类，用来在同一请求线程中的不同类里共享数据
public class BaseContext {
    private static ThreadLocal<Long> threadLocal=new ThreadLocal<>();

    public static void setCurrentLoginUserId(Long id){
        threadLocal.set(id);
    }

    public static Long getCurrentLoginUserId(){
        return threadLocal.get();
    }
}