package com.bcasekuritas.mybest.app.base.mapper

abstract class BaseMapper<in T, out R> {
    abstract fun map(value: T): R
}
