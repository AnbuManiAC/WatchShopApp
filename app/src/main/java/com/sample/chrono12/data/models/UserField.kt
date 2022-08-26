package com.sample.chrono12.data.models

enum class UserField(var response: Response) {
    NAME(Response.SUCCESS),
    EMAIL(Response.SUCCESS),
    MOBILE(Response.SUCCESS),
    PASSWORD(Response.SUCCESS),
    CONFIRM_PASSWORD(Response.SUCCESS),
    ALL(Response.SUCCESS)
}