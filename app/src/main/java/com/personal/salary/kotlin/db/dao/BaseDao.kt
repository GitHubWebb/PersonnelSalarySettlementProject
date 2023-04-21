package com.personal.salary.kotlin.db.dao

import androidx.room.*
import androidx.sqlite.db.SimpleSQLiteQuery
import androidx.sqlite.db.SupportSQLiteQuery
import timber.log.Timber
import java.lang.reflect.ParameterizedType

/**
 * author : https://juejin.cn/post/7067897847642865677
 * desc   : Room 通用封装
 */
abstract class BaseDao<T> {

    /**
     * 添加单个对象
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract fun insert(obj: T): Long

    /**
     * 添加数组对象数据
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract fun insert(vararg objs: T): LongArray?

    /**
     * 添加对象集合
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract fun insert(personList: List<T>): List<Long>

    /**
     * 根据对象中的主键删除（主键是自动增长的，无需手动赋值）
     */
    @Delete
    abstract fun delete(obj: T)

    /**
     * 根据对象中的主键更新（主键是自动增长的，无需手动赋值）
     */
    @Update
    abstract fun update(vararg obj: T): Int

    fun deleteAll(): Int {
        val query = SimpleSQLiteQuery(
            "delete from $tableName"
        )
        return doDeleteAll(query)
    }

    fun findAll(): List<T>? {
        val query = SimpleSQLiteQuery(
            "select * from $tableName"
        )
        return doFindAll(query)
    }

    fun find(id: Long): T? {
        val query = SimpleSQLiteQuery(
            "select * from $tableName where id = ?", arrayOf<Any>(id)
        )
        return doFind(query)
    }

    /**
     * [params] 列名
     * [value] 列的值
     */
    fun deleteByParams(params: String, value: String): Int {
        val query = SimpleSQLiteQuery("delete from $tableName where $params='${value}'")
        Timber.d("deleteByParams: ${"delete from $tableName where $params='${value}'"}")
        return doDeleteByParams(query)
    }

    /**
     * 分页查询，支持传入多个字段，但必须要按照顺序传入
     * key = value，key = value 的形式，一一对应（可以使用 stringbuilder 去构造一下，这里就不演示了）
     */
    fun doQueryByLimit(vararg string: String, limit: Int = 10, offset: Int = 0): List<T>? {
        val query =
            SimpleSQLiteQuery("SELECT * FROM $tableName WHERE ${string[0]} = '${string[1]}' limit $limit offset $offset")
        return doQueryByLimit(query)
    }

    /**
     * 降序分页查询
     */
    fun doQueryByOrder(vararg string: String, limit: Int = 10, offset: Int = 10): List<T>? {
        val query =
            SimpleSQLiteQuery("SELECT * FROM $tableName ORDER BY ${string[0]} desc limit '${limit}' offset '${offset}'")
        return doQueryByLimit(query)
    }

    /**
     * 获取表名
     */
    val tableName: String
        get() {
            val clazz =
                (javaClass.superclass.genericSuperclass as ParameterizedType).actualTypeArguments[0] as Class<*>
            var tableName = clazz.simpleName
            Timber.d("getTableName: -->$tableName")
            return tableName
        }

    @RawQuery
    protected abstract fun doFindAll(query: SupportSQLiteQuery): List<T>?

    @RawQuery
    protected abstract fun doFind(query: SupportSQLiteQuery): T

    @RawQuery
    protected abstract fun doDeleteAll(query: SupportSQLiteQuery): Int

    @RawQuery
    protected abstract fun doDeleteByParams(query: SupportSQLiteQuery): Int

    @RawQuery
    protected abstract fun doQueryByLimit(query: SupportSQLiteQuery): List<T>?

    @RawQuery
    protected abstract fun doQueryByOrder(query: SupportSQLiteQuery): List<T>?

}