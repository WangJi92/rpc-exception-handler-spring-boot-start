# rpc-exception-handler-spring-boot-start
### 我想干啥？
spring mvc 有 @ControllerAdvice and @ExceptionHandler 
最近在处理外部暴露的服务需要进行异常的处理,通过Aop 去实现的，不够优雅，代码 判断各种异常的处理，非常的恶心~ 是否可以做个 
类似 spring 的这种全局异常处理器 @RpcServiceAdvice and @RpcServiceExceptionHandler。

* 需要解析全局的 @RpcServiceAdvice。

* 参考spring mvc 还需要知道当前接口实现类中的是否含有 @RpcServiceExceptionHandler 优先级提升，找不到在去处理全局的@RpcServiceAdvice。

不过这里要注意一下 对于外部暴露的dubbo spi 服务都有统一的返回值XXXResult 不然全局处理器和方法返回的值类型不一样会导致消费者端接收失败。
一般这种异常都是记录日志，返回失败标识、提示信息等等即可。

