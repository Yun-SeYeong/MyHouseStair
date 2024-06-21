package com.myhousestair.myhousestair.exception

class BadRequestException: RuntimeException {
    constructor(message: String): super(message)
    constructor(message: String, cause: Throwable): super(message, cause)
}