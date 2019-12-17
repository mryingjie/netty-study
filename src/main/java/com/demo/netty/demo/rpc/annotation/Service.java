package com.demo.netty.demo.rpc.annotation;

import java.lang.annotation.*;

/**
 * created by Yingjie Zheng at 2019-12-13 17:34
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Service {
}
