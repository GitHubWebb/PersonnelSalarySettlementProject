package com.personal.salary.kotlin.model.test

import timber.log.Timber
import kotlin.reflect.KProperty

class PeopleInfo {
    var name: String? = "我是假数据"

    operator fun getValue(studentInfo: StudentInfo, property: KProperty<*>): String? {
        Timber.d("getValue property.name: ${property.name}, ${studentInfo}")
        return name + studentInfo
    }

    operator fun setValue(studentInfo: StudentInfo, property: KProperty<*>, value: String?) {
        Timber.d("setValue property.name: ${property.name}")
        Timber.d("value: $value")
        name = value
    }

}