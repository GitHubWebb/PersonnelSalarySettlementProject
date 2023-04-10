package com.personal.salary.kotlin.ui.activity

import android.view.View
import by.kirich1409.viewbindingdelegate.viewBinding
import com.hjq.toast.ToastUtils
import com.hjq.widget.view.PlayButton
import com.personal.salary.kotlin.R
import com.personal.salary.kotlin.app.AppActivity
import com.personal.salary.kotlin.databinding.ActivityMainBinding
import com.personal.salary.kotlin.model.test.StudentInfo
import kotlinx.coroutines.*
import timber.log.Timber
import kotlin.concurrent.thread

class MainActivity : AppActivity() {

    //<editor-fold desc="静态变量">
    companion object {
        const val TEXT: String = "123";
    }
    //</editor-fold>

    private val mBinding by viewBinding<ActivityMainBinding>()
    /*private val tvHello: TextView? by lazy {
        Timber.d("延迟加载tvHello")
        findViewById(R.id.tv_hello)
    }*/

    /** 使用官方库的 MainScope() 获取一个协程作用域用于创建协程 */
    val mScope = MainScope()

    override fun getLayoutId(): Int = R.layout.activity_main

    override fun initView() {
        super.initView()

        with(mBinding) {
            setOnClickListener(tvHello, pbBtn, sbBtn)

        }

        ToastUtils.show(TEXT)
    }

    /** 使用launch创建协程 */
    fun testLaunch() {
        // 创建一个默认参数的协程, 默认调度模式是Dispatchers.Main
        val job1 = mScope.launch {
            Timber.tag("coroutine").d(
                "testLaunch job1: ${Thread.currentThread().name} " + Thread.currentThread()
                    .toString()
            )
            // 使用delay挂起函数 延迟1000毫秒
            delay(1000)

            withContext(Dispatchers.IO) {
                Timber.tag("coroutine").d(
                    "testLaunch job1: ${Thread.currentThread().name} " + Thread.currentThread()
                        .toString()
                )

            }

            thread {
                Timber.tag("coroutine").d(
                    "testLaunch job1: thread: ${Thread.currentThread().name} " + Thread.currentThread()
                        .toString()
                )

            }

            withContext(Dispatchers.Default) {
                Timber.tag("coroutine").d(
                    "testLaunch job1: ${Thread.currentThread().name} " + Thread.currentThread()
                        .toString()
                )

            }

            val job2 = mScope.launch(Dispatchers.IO) {
                Timber.tag("coroutine").d(
                    "testLaunch job2: ${Thread.currentThread().name} " + Thread.currentThread()
                        .toString()
                )

                withContext(Dispatchers.Main) {
                    Timber.tag("coroutine").d(
                        "testLaunch job2: ${Thread.currentThread().name} " + Thread.currentThread()
                            .toString()
                    )
                }
            }

            // 切换到IO线程
            mScope.launch(context = Dispatchers.IO) {
                val userName = getUserInfo()
                // 获取完整数据后 切回Main 更新UI
                withContext(Dispatchers.Main) {
                    mBinding.tvHello?.setText(userName)
                }

            }
        }

    }

    /** 同步获取用户信息 */
    private suspend fun getUserInfo(): String {
        var userName = "ceshi"
        var studentInfo = StudentInfo()
        studentInfo.name1 = "123"
        userName += studentInfo.name1 + studentInfo.sex
        var deferred = mScope.async {
            delay(1000)
            userName = userName + Thread.currentThread().contextClassLoader.toString()
            thread {
                userName += "/"
            }
            userName as String
        }

        var name = deferred.await()
        Timber.d("deferred.await() ${name.toString()}")
        return name.toString()
    }

    /** 测试 */
    fun testAsync() {
        mScope.launch {
            val job1 = async {
                // 请求1
                delay(1000)
                "job1"
            }
            val job2 = async {
                // 请求2
                delay(2000)
                "job2"
            }
            val job3 = async {
                // 请求3
                delay(3000)
                "job3"
            }
            val job4 = async {
                // 请求4
                delay(4000)
                "job4"
            }

            // 将结果合并
            Timber.d("testAsync: ${job1.await()},${job2.await()},${job3.await()},${job4.await()}")
        }
    }

    /** 测试函数方法 */
    suspend fun testUnit(action: () -> Unit) {
        delay(5000)
        action()
    }

    override fun onClick(view: View) {
        with(mBinding) {
            when (view) {
                tvHello -> testLaunch()
                pbBtn -> {
                    when (pbBtn.getCurrentState()) {
                        PlayButton.STATE_PAUSE -> {
                            pbBtn.play()
                            testAsync()
                        }
                        else -> pbBtn.pause()
                    }
                }
                sbBtn -> {
                    sbBtn.showProgress()
                    mScope.launch {
                        delay(2000)
                        sbBtn.showError(3000)
                        testUnit {
                            sbBtn.showProgress()
                            sbBtn.showSucceed()

                        }
                    }
                }

                else -> {}
            }
        }

    }

    override fun onDestroy() {
        super.onDestroy()
        // 取消协程 防止协程泄露 如果使用lifecycleScope则不需要手动取消
        mScope.cancel()
    }
}