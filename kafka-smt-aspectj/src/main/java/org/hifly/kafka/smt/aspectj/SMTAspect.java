package org.hifly.kafka.smt.aspectj;


import org.apache.kafka.connect.connector.ConnectRecord;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;

@Aspect
public class SMTAspect {

    @Around("execution(* org.apache.kafka.connect.transforms.*(..))")
    public Object aroundAdvice(ProceedingJoinPoint pjp) {
        try {
            Object obj = pjp.proceed(pjp.getArgs());
            if(obj instanceof ConnectRecord) {
                ConnectRecord record = (ConnectRecord)obj;
                System.out.println("GOT ConnectRecord; -- > ASPECT J:" + record.topic() + "-" + record.key() + "-" + record.value());
            }
        } catch (Throwable e) {
            return null;
        }
        return null;
    }

}