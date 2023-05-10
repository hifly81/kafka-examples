package org.hifly.kafka.smt.aspectj;


import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;

@Aspect
public class SMTAspect {

    @Pointcut("execution(* org.apache.kafka.connect.transforms.Filter.apply(..))")
    public void standardMethod() {}

    @Before("standardMethod()")
    public void log(JoinPoint jp) throws Throwable {
        Object[] array = jp.getArgs();
        if(array != null) {
            System.out.println("Size:" + array.length);
            for(Object tmp: array)
                System.out.println(tmp);
        }
    }

}