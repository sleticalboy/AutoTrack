package com.sleticalboy.aspectj;

import android.util.Log;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;

import java.lang.reflect.Method;

/**
 * Created on 19-5-12.
 * <p>
 * 目前只对 .java 源文件生效， .kt 无效
 *
 * @author leebin
 */
@Aspect
public final class AspectJTrack {

    private static final String TAG = "AspectJTrack";

    // public * *(..) 匹配所有 public 方法
    // public *java.lang.String *(java.lang.String) 匹配所有 public 修饰且返回值为 String 且参数为 String的方法
    // public *java.lang.String *(android.view.View)
    @Around("execution(* *(..))")
    public Object weaveAllMethod(ProceedingJoinPoint joinPoint) throws Throwable {
        final long start = System.nanoTime();
        final Object ret = joinPoint.proceed();
        final long cost = System.nanoTime() - start;
        final MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        final Method method = signature.getMethod();
        Log.d(TAG, String.format("Method: %s cost: %s ns", method.toGenericString(), "" + cost));
        return ret;
    }

}