package com.personal.salary.kotlin.ui.fragment

import android.app.Activity
import android.content.Intent
import android.net.Uri
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.ConcatAdapter
import androidx.recyclerview.widget.LinearLayoutManager
import by.kirich1409.viewbindingdelegate.viewBinding
import com.personal.salary.kotlin.ui.adapter.delegate.AdapterDelegate
import com.personal.salary.kotlin.viewmodel.app.AppViewModel
import com.dylanc.longan.viewLifecycleScope
import com.hjq.bar.TitleBar
import com.hjq.permissions.Permission
import com.huawei.hms.hmsscankit.ScanUtil
import com.huawei.hms.ml.scan.HmsScan
import com.huawei.hms.ml.scan.HmsScanAnalyzerOptions
import com.personal.salary.kotlin.R
import com.personal.salary.kotlin.action.OnBack2TopListener
import com.personal.salary.kotlin.action.StatusAction
import com.personal.salary.kotlin.aop.Permissions
import com.personal.salary.kotlin.app.AppActivity
import com.personal.salary.kotlin.app.TitleBarFragment
import com.personal.salary.kotlin.databinding.FishListFragmentBinding
import com.personal.salary.kotlin.databinding.RosterListFragmentBinding
import com.personal.salary.kotlin.ktx.*
import com.personal.salary.kotlin.model.MourningCalendar
import com.personal.salary.kotlin.model.RefreshStatus
import com.personal.salary.kotlin.ui.activity.ImagePreviewActivity
import com.personal.salary.kotlin.ui.activity.ViewUserActivity
import com.personal.salary.kotlin.ui.adapter.EmptyAdapter
import com.personal.salary.kotlin.ui.adapter.FishListAdapter
import com.personal.salary.kotlin.util.MyScanUtil
import com.personal.salary.kotlin.util.SimpleLinearSpaceItemDecoration
import com.personal.salary.kotlin.viewmodel.fish.FishViewModel
import com.personal.salary.kotlin.widget.StatusLayout
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import timber.log.Timber
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject
import kotlin.text.orEmpty

/**
 * author : A Lonely Cat
 * github : https://github.com/anjiemo/SunnyBeach
 * time   : 2021/06/20
 * desc   : 鱼塘 Fragment
 */
@AndroidEntryPoint
class FishListFragment : TitleBarFragment<AppActivity>(), StatusAction, OnBack2TopListener {

    private val mBinding: FishListFragmentBinding by viewBinding()

    @Inject
    lateinit var mAppViewModel: AppViewModel

    private val mFishPondViewModel by activityViewModels<FishViewModel>()
    private val mRefreshStatus = RefreshStatus()
    private val mFishListAdapterDelegate = AdapterDelegate()
    private val mFishListAdapter = FishListAdapter(mFishListAdapterDelegate)
    private val loadStateListener = loadStateListener(mFishListAdapter) { mBinding.refreshLayout.finishRefresh() }

    override fun getLayoutId(): Int = R.layout.fish_list_fragment

    override fun initView() {
        // This emptyAdapter is like a hacker.
        // Its existence allows the PagingAdapter to scroll to the top before being refreshed,
        // avoiding the problem that the PagingAdapter cannot return to the top after being refreshed.
        // But it needs to be used in conjunction with ConcatAdapter, and must appear before PagingAdapter.
        val emptyAdapter = EmptyAdapter()
        val concatAdapter = ConcatAdapter(emptyAdapter, mFishListAdapter)
        mBinding.apply {
            rvFishPondList.apply {
                layoutManager = LinearLayoutManager(context)
                adapter = concatAdapter
                addItemDecoration(SimpleLinearSpaceItemDecoration(6.dp))
            }
        }
    }

    override fun initData() {
        loadFishList()
        mAppViewModel.getMourningCalendar().observe(viewLifecycleOwner) {
            val result = it.getOrNull() ?: return@observe
            setMourningStyleByDate(result)
        }
    }

    private fun loadFishList() {
        viewLifecycleScope.launchWhenCreated {
            /*mFishPondViewModel.getFishListByCategoryId("recommend").collectLatest {
                onBack2Top()
                mFishListAdapter.submitData(it)
            }*/
        }
    }

    override fun initEvent() {
        mBinding.apply {
            titleBar.setDoubleClickListener {
                onBack2Top()
            }
            refreshLayout.setOnRefreshListener {
                mFishListAdapter.refresh()
            }
            ivPublish.setFixOnClickListener {
                viewLifecycleScope.launch {
                    // 操作按钮点击回调，判断是否已经登录过账号
                    requireActivity().tryShowLoginDialog()
                }
            }
        }
        // 需要在 View 销毁的时候移除 listener
        mFishListAdapter.addLoadStateListener(loadStateListener)
        mFishListAdapterDelegate.setOnItemClickListener { _, position ->
            // mFishListAdapter.snapshotList[position]?.let { FishPondDetailActivity.start(requireContext(), it.id) }
        }
        mFishListAdapter.setOnMenuItemClickListener { view, item, position ->

        }
        mFishListAdapter.setOnNineGridClickListener { sources, index ->
            ImagePreviewActivity.start(requireContext(), sources.toMutableList(), index)
        }
    }

    override fun initObserver() {
        mAppViewModel.mourningCalendarListLiveData.observe(viewLifecycleOwner) { setMourningStyleByDate(it) }
        mFishPondViewModel.fishListStateLiveData.observe(viewLifecycleOwner) { mFishListAdapter.refresh() }
    }

    private fun setMourningStyleByDate(mourningCalendarList: List<MourningCalendar>) {
        val sdf = SimpleDateFormat("MM月dd日", Locale.getDefault())
        val formatDate = sdf.format(System.currentTimeMillis())
        val rootView = requireView()
        mourningCalendarList.find { it.date == formatDate }?.let { rootView.setMourningStyle() }
            ?: rootView.removeMourningStyle()
    }

    @Permissions(Permission.CAMERA)
    override fun onRightClick(titleBar: TitleBar) {
        // “QRCODE_SCAN_TYPE”和“DATAMATRIX_SCAN_TYPE”表示只扫描QR和Data Matrix的码
        val options = HmsScanAnalyzerOptions.Creator()
            .setHmsScanTypes(HmsScan.QRCODE_SCAN_TYPE)
            .create()
        MyScanUtil.startScan(this, REQUEST_CODE_SCAN_ONE, options)
    }

    override fun showLoading(id: Int) {
        takeIf { mRefreshStatus.isFirstRefresh }?.let { super.showLoading(id) }
        mRefreshStatus.isFirstRefresh = false
    }

    override fun getStatusLayout(): StatusLayout = mBinding.hlFishPondHint

    override fun isStatusBarEnabled(): Boolean {
        // 使用沉浸式状态栏
        return !super.isStatusBarEnabled()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        mFishListAdapter.removeLoadStateListener(loadStateListener)
    }

    override fun onBack2Top() {
        mBinding.rvFishPondList.scrollToPosition(0)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode != Activity.RESULT_OK || data == null) return
        if (requestCode == REQUEST_CODE_SCAN_ONE) {
            // 导入图片扫描返回结果
            val hmsScan = data.getParcelableExtra(ScanUtil.RESULT) as HmsScan?
            if (hmsScan != null) {
                // 展示解码结果
                showResult(hmsScan)
            } else {
                showNoContentTips()
            }
        }
    }

    private fun showNoContentTips() {
        toast("什么内容也没有~")
    }

    private fun showResult(hmsScan: HmsScan?) {
        val result = hmsScan?.showResult.orEmpty()
        if (result.isBlank()) {
            showNoContentTips()
            return
        }

        // result can never be null.
        val uri = Uri.parse(result)
        val scheme = uri.scheme.orEmpty()
        val authority = uri.authority.orEmpty()
        val userId = uri.lastPathSegment.orEmpty()

        Timber.d("showResult：===> scheme is $scheme authority is $authority userId is $userId")
        Timber.d("showResult：===> result is $result")
        // toast(userId)

        when {
            else -> ViewUserActivity.start(requireContext(), userId)
        }
    }

    private fun unsupportedParsedContent() = toast("不支持解析的内容")

    companion object {

        private val REQUEST_CODE_SCAN_ONE = hashCode()

        @JvmStatic
        fun newInstance() = FishListFragment()
    }
}