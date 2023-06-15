package org.hifly.kafka.smt.aspectj;


import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
@Aspect
public class SMTAspect {

    private static final Logger LOGGER = LoggerFactory.getLogger(SMTAspect.class);

    @Pointcut("execution(* org.apache.kafka.connect.transforms.*.apply(..)) && !execution(* org.apache.kafka.connect.runtime.PredicatedTransformation.apply(..))")
    public void standardMethod() {}

    @Before("standardMethod()")
    public void log(JoinPoint jp) throws Throwable {

        Object[] array = jp.getArgs();
        if(array != null) {
            for(Object tmp: array)
                LOGGER.info(tmp.toString());
        }
    }

}