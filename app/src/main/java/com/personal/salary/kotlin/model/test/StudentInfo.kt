package com.personal.salary.kotlin.model.test

data class StudentInfo @JvmOverloads constructor(
    val userName: String = "userNameStr"
) {
    var name1: String? by PeopleInfo()
    var sex: String? by PeopleInfo()
}