package com.personal.salary.kotlin.db.dao

import androidx.room.*
import com.personal.salary.kotlin.manager.CookieStore

@Dao
interface CookieDao {

    @Transaction
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun save(cookieStoreSet: List<CookieStore>)

    @Transaction
    @Query("SELECT * FROM tb_cookies WHERE host = :host")
    fun getCookiesByHost(host: String): CookieStore?

    @Transaction
    @Query("SELECT * FROM tb_cookies WHERE domain = :domain")
    fun getCookiesByDomain(domain: String): CookieStore?

    @Transaction
    @Query("SELECT * FROM tb_cookies")
    fun getCookies(): List<CookieStore>

    @Transaction
    @Query("DELETE FROM tb_cookies")
    fun clearCookies()
}