package com.myhousestair.myhousestair.mock


import org.springframework.security.test.context.support.WithSecurityContext
import java.lang.annotation.Inherited

@Target(AnnotationTarget.FUNCTION, AnnotationTarget.TYPE)
@Retention(AnnotationRetention.RUNTIME)
@Inherited
@MustBeDocumented
@WithSecurityContext(factory = WithMockUserSecurityContextFactory::class)
annotation class WithMockTestUser (
    val email: String,
    val password: String
)