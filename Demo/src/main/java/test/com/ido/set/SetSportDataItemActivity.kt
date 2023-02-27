package test.com.ido.set

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.ido.ble.BLEManager
import com.ido.ble.callback.OperateCallBack
import com.ido.ble.protocol.model.Sport100TypeSort
import kotlinx.android.synthetic.main.activity_set_sport_data_item.*
import test.com.ido.CallBack.BaseOperateCallback
import test.com.ido.R
import test.com.ido.connect.BaseAutoConnectActivity
import test.com.ido.model.SportTypeBean
import test.com.ido.utils.Constant
import test.com.ido.utils.ListUtils
import test.com.ido.utils.ResourceUtil
import test.com.ido.utils.ResourceUtils

/**
 * @author tianwei
 * @date 2023/2/27
 * @time 10:51
 * 用途:
 */
class SetSportDataItemActivity : BaseAutoConnectActivity() {
    private var list: MutableList<SportTypeBean> = mutableListOf()
    private var adapter: SportAdapter? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_set_sport_data_item)
        adapter = SportAdapter()
        recyclerview.layoutManager = LinearLayoutManager(this)
        recyclerview.adapter = adapter

        getSportTypeList()
    }

    private fun getSportTypeList() {
        BLEManager.registerOperateCallBack(mOperateQueryCallback)
        BLEManager.querySport100TypeSort()
        showProgressDialog("")
    }

    private val mOperateQueryCallback: OperateCallBack.ICallBack =
        object : BaseOperateCallback() {
            override fun onQueryResult(
                operateType: OperateCallBack.OperateType?,
                data: Any?
            ) {
                if (operateType == OperateCallBack.OperateType.SPORT_100_TYPE_SORT) {
                    closeProgressDialog()
                    BLEManager.unregisterOperateCallBack(this)
                    if (data is Sport100TypeSort?) {
                        if (data != null) {
                            val useSize = data.now_user_location
                            if (ListUtils.isNotEmpty(data.items)) {
                                list.addAll(data.items.subList(0, useSize).map {
                                    SportTypeBean(
                                        type = it.type,
                                        iconResId = getMotionTypeIcon(it.type),
                                        name = getMotionTypeName(it.type),
                                        iconFlag = it.flag // 是否有图标
                                    )
                                })
                                adapter?.notifyDataSetChanged()
                            }
                        }
                    }
                }
            }
        }

    fun getMotionTypeName(motionType: Int): String {
        val resId =
            ResourceUtil.getStringResId("${Constant.PREFIX_MOTION_RESOURCE}${motionType}")
        return if (resId > 0) {
            resources.getString(resId)
        } else {
            ""
        }
    }

    /**
     * 获取运动类型图标
     */
    fun getMotionTypeIcon(motionType: Int): Int {
        try {
            return ResourceUtils.getMipmapResId("${Constant.PREFIX_MOTION_RESOURCE}${motionType}")
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return 0
    }

    inner class SportAdapter : RecyclerView.Adapter<SportAdapter.VH>() {
        inner class VH(itemView: View) : RecyclerView.ViewHolder(itemView) {
            var ivMotionIcon: ImageView? = null
            var tvMotionName: TextView? = null
            var lay_content: RelativeLayout? = null

            init {
                ivMotionIcon = itemView.findViewById(R.id.ivMotionIcon)
                tvMotionName = itemView.findViewById(R.id.tvMotionName)
                lay_content = itemView.findViewById(R.id.lay_content)
                lay_content?.setOnClickListener {
                    startActivity(
                        Intent(
                            this@SetSportDataItemActivity,
                            SetSportDataItemDetailActivity::class.java
                        ).putExtra("data", list[adapterPosition])
                    )
                }
            }
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
            return VH(
                LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_sports_data_view_list, parent, false)
            )
        }

        override fun onBindViewHolder(holder: VH, position: Int) {
            val data = getItem(position)
            holder.tvMotionName?.text = data?.name
            holder.ivMotionIcon?.setImageResource(if (data?.iconResId > 0) data!!.iconResId else R.mipmap.motion_4)
        }

        override fun getItemCount(): Int {
            return list.size
        }

        fun getItem(position: Int) = list?.get(position)
    }
}