package com.vaccine.common.annotation;

import java.lang.annotation.*;

/**
 * 管理员权限注解
 * 便捷注解，等价于 @RequireRole("ADMIN")
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@RequireRole("ADMIN")
public @interface RequireAdmin {
}