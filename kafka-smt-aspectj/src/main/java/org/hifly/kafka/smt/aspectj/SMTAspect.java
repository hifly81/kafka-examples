package org.hifly.kafka.smt.aspectj;


import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;

@Aspect
public class SMTAspect {

    @Pointcut("execution(* org.apache.kafka.connect.transforms.*.apply(..)) && !execution(* org.apache.kafka.connect.runtime.PredicatedTransformation.apply(..))")
    public void standardMethod() {}

    @Before("standardMethod()")
    public void log(JoinPoint jp) throws Throwable {

        System.out.println(jp);

        Object[] array = jp.getArgs();
        if(array != null) {
            System.out.println("Size:" + array.length);
            for(Object tmp: array)
                System.out.println(tmp);
        }
    }

}