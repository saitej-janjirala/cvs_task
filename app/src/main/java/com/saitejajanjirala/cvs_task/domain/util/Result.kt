package com.saitejajanjirala.cvs_task.domain.util

sealed class Result<T> (val d :T?=null,val m :String?=null ){
    data class Success<T>(val data: T) : Result<T>(d=data)
    data class Error<T>(val message: String) : Result<T>(m=message)
    class Loading<T>() : Result<T>()
    class Idle<T>(): Result<T>()
}