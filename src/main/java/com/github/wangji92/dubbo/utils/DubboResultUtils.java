package com.github.wangji92.dubbo.utils;

import com.github.wangji92.dubbo.filter.DubboProviderExceptionFilter;
import org.apache.dubbo.rpc.AppResponse;
import org.apache.dubbo.rpc.Result;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;

/**
 * @author 汪小哥
 * @date 14-05-2021
 */
public class DubboResultUtils {

    private static final boolean appResponsePresent;

    private static final boolean rpcResultPresent;

    static {
        ClassLoader classLoader = DubboProviderExceptionFilter.class.getClassLoader();
        appResponsePresent = ClassUtils.isPresent("org.apache.dubbo.rpc.AppResponse", classLoader);
        rpcResultPresent = ClassUtils.isPresent("org.apache.dubbo.rpc.RpcResult", classLoader);

    }

    /**
     * 创建一个实例
     *
     * @return
     */
    public static Result newDubboResultInstance() {
        Result responseResult = null;
        if (appResponsePresent) {
            responseResult = new AppResponse();
        } else if (rpcResultPresent) {
            try {
                responseResult = (Result) ClassUtils.forName("org.apache.dubbo.rpc.RpcResult", DubboProviderExceptionFilter.class.getClassLoader()).newInstance();
            } catch (Exception ex) {
                //ignore
            }
        }
        Assert.state(responseResult != null, "can't create dubbo result");
        return responseResult;

    }
}
