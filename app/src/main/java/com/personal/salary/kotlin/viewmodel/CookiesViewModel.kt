package com.personal.salary.kotlin.viewmodel

import android.app.Application
import android.webkit.CookieManager
import androidx.lifecycle.AndroidViewModel
import com.personal.salary.kotlin.app.AppApplication
import com.personal.salary.kotlin.db.dao.CookieDao
import com.personal.salary.kotlin.manager.CookieStore

/**
 * author : A Lonely Cat
 * github : https://github.com/anjiemo/SunnyBeach
 * time   : 2021/06/18
 * desc   : Cookie的 ViewModel
 */
class CookiesViewModel(application: Application) : AndroidViewModel(application) {

    private val cookieDao: CookieDao = AppApplication.getDatabase().cookieDao()

    /**
     * 保存 Cookies
     */
    fun save(cookieStoreSet: List<CookieStore>) {
        // 把 Cookies 保存到存储 Cookie 的 Room 数据库中
        cookieDao.save(cookieStoreSet)
        // 将 Cookie 同步到 WebView 中
        val cm = CookieManager.getInstance()
        for (cookieStore in cookieStoreSet) {
            for (cookie in cookieStore.cookies) {
                cm.setCookie(cookieStore.host, cookie.value)
            }
        }
        cm.flush()
    }

    fun getCookiesByHost(host: String) = cookieDao.getCookiesByHost(host)

    fun getCookies() = cookieDao.getCookies()
}