package com.personal.salary.kotlin.model

/**
 *    author : wangwx
 *    github : https://github.com/GitHubWebb
 *    time   : 2023/04/12
 *    desc   : 导入 模块 实体
 */
class ImportRule {

    /** 导入花名册 */
    enum class ImportType(val itemName: String) {
        IMPORT_ROSTER("导入花名册"),
        IMPORT_SOP("导入SOP规则")
    }
}