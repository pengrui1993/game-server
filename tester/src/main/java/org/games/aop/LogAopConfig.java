package org.games.aop;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
/**
 * https://blog.csdn.net/Tiglle/article/details/78241316
 * https://www.cnblogs.com/liaojie970/p/7883687.html
 * https://www.tutorialandexample.com/spring-aop-pointcut-expressions
 * https://blog.csdn.net/qq_42734859/article/details/87373840
 */
@Aspect
@Component
public class LogAopConfig {
    static final Logger log = LoggerFactory.getLogger(LogAopConfig.class);
    @Value("${config.debug:false}")
    protected boolean debug;
    //这个方法定义了切入点
    @Pointcut("@annotation(org.games.aop.ApiLog)")
    public void pc() {}
    private void print(String msg){
        System.out.println(msg);
    }
    /**
     * 描述aop 调用的顺序
     */
    @interface Sequence{
        int value()default 0;
    }
    private long now(){
        return System.currentTimeMillis();
    }
    @Sequence(1)
    @Around("pc()")  //这个方法定义了具体的通知
    public Object around(ProceedingJoinPoint pjp) throws Throwable {
        if(!debug){
            return pjp.proceed();
        }
//        print("around");
        final long t1 = now();
        final Object obj = pjp.proceed();
        final long t2 = now();
//        logService.submit(()->log.info("\nmethod:{} ,call time:{} ms",pjp.getSignature(),t2-t1));
        log.info("\nmethod:{} ,call time:{} ms",pjp.getSignature(),t2-t1);
        return obj;
    }
    //这个方法定义了具体的通知
    @Sequence(2)
//    @Before("pc()")
    public void before(JoinPoint joinPoint) {
        print("before");
    }

    @Sequence(4)
//    @After("pc()") //这个方法定义了具体的通知
    public void after(JoinPoint joinPoint) {
        print("after");
    }

//    @AfterReturning(value = "execution(public int com.test.Controller.*(int,int))",returning = "res")
    public void afterReturning(JoinPoint joinPoint,Object res){

    }

    /**
     * @param joinPoint
     * @param error 参数名需要和 注解的throwing 一致
     */
    @Sequence(3)
    //@AfterThrowing(pointcut = "pc()",throwing = "error")
    public void thr(JoinPoint joinPoint,Throwable error){
        print("throwing");
        //signature:String com.habf.controller.StatisticsController.hello(),error:null
        //System.out.println(String.format("signature:%s,error:%s",joinPoint.getSignature(),error.getMessage()));
    }
}
