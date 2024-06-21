package com.myhousestair.myhousestair.exception

class UnAuthorizationException : RuntimeException {
    constructor(message: String) : super(message)
    constructor(message: String, cause: Throwable) : super(message, cause)
}