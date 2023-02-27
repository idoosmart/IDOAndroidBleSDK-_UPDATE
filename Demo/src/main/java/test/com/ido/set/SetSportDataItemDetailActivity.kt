package test.com.ido.set

import android.os.Bundle
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.ido.ble.BLEManager
import com.ido.ble.callback.OperateCallBack
import com.ido.ble.protocol.model.Sport100TypeSort
import com.ido.ble.protocol.model.SportSubItemParaSort
import kotlinx.android.synthetic.main.activity_sports_data_view_detail_list.*
import test.com.ido.CallBack.BaseOperateCallback
import test.com.ido.R
import test.com.ido.connect.BaseAutoConnectActivity
import test.com.ido.model.SportTypeBean
import test.com.ido.model.SportsDataViewDetailBean
import test.com.ido.utils.ListUtils
import test.com.ido.utils.OnItemClickListener
import test.com.ido.utils.ResourceUtil

/**
 * @author tianwei
 * @date 2023/2/27
 * @time 11:24
 * 用途:
 */
class SetSportDataItemDetailActivity : BaseAutoConnectActivity() {
    private var mAdapter: SportsDataViewDetailListAdapter? = null

    private var mNotAddedAdapter: SportsDataViewDetailListAdapter? = null

    private var mList: MutableList<SportsDataViewDetailBean> = mutableListOf()
    private var mNotAddedList: MutableList<SportsDataViewDetailBean> =
        mutableListOf()

    private var dataViewBean: SportTypeBean? = null

    companion object {
        const val MIN_RETENTION = 2//最小保留的数量
        const val ADDED = 1
        const val NOT_ADDED = 2
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        dataViewBean = intent.getSerializableExtra("data") as SportTypeBean?
        setContentView(R.layout.activity_sports_data_view_detail_list)
        mAdapter = SportsDataViewDetailListAdapter(mList, ADDED)
        recyclerview.adapter = mAdapter
        mNotAddedAdapter = SportsDataViewDetailListAdapter(
            mNotAddedList,
            NOT_ADDED
        )
        mAdapter?.onItemClickListener = object : OnItemClickListener {
            override fun onItemClick(position: Int) {
                val deleteData = mList.removeAt(position)
                mNotAddedList.add(deleteData)
                updateList()
            }

        }
        mNotAddedAdapter?.onItemClickListener = object : OnItemClickListener {
            override fun onItemClick(position: Int) {
                val deleteData = mNotAddedList.removeAt(position)
                mList.add(deleteData)
                updateList()
            }

        }
        not_added_recyclerview.adapter = mNotAddedAdapter
        querySportSubItemParaSort(dataViewBean!!.type)
    }

    private fun querySportSubItemParaSort(sportType: Int) {
        BLEManager.registerOperateCallBack(mOperateQueryCallback)
        BLEManager.querySportSubItemParaSort(sportType)
        showProgressDialog("")
    }

    private val mOperateQueryCallback: OperateCallBack.ICallBack =
        object : BaseOperateCallback() {
            override fun onQueryResult(
                operateType: OperateCallBack.OperateType?,
                data: Any?
            ) {
                if (operateType == OperateCallBack.OperateType.SPORT_SUB_ITEM_PARA_SORT) {
                    closeProgressDialog()
                    if (data is SportSubItemParaSort && data.items.isNotEmpty()) {
                        val addSize = data.now_user_location
                        mList.addAll(data.items.subList(0, addSize).map {
                            SportsDataViewDetailBean(
                                it,
                                getMotionTypeDetailName(it)
                            )
                        })
                        mNotAddedList.addAll(
                            data.items.subList(
                                addSize,
                                data.items.size
                            ).map {
                                SportsDataViewDetailBean(
                                    it,
                                    getMotionTypeDetailName(it)
                                )
                            })
                        updateList()
                    }
                }
            }

            override fun onSetResult(
                operateType: OperateCallBack.OperateType?,
                b: Boolean
            ) {
                if (operateType == OperateCallBack.OperateType.SPORT_SUB_ITEM_PARA_SORT) {
                    closeProgressDialog()
                    showToast(if (b) R.string.set_tip_success else R.string.set_tip_failed)
                }
            }
        }

    fun getMotionTypeDetailName(detailType: Int): String {
        val nameRes =
            ResourceUtil.getStringResId("motion_type_detail_$detailType")
        return if (nameRes > 0) resources.getString(nameRes) else ""
    }

    private fun updateAddedList() {
        //最低保留2个
        mAdapter?.enable = mList.size > MIN_RETENTION
        mAdapter?.notifyDataSetChanged()
    }

    private fun updateNotAddedList() {
        //最高添加7个
        mNotAddedAdapter?.notifyDataSetChanged()
    }

    private fun updateList() {
        updateAddedList()
        updateNotAddedList()
    }

    inner class SportsDataViewDetailListAdapter(
        val list: MutableList<SportsDataViewDetailBean>?,
        val viewType: Int
    ) :
        RecyclerView.Adapter<SportsDataViewDetailListAdapter.VH>() {
        var onItemClickListener: OnItemClickListener? = null
        var enable: Boolean = true

        inner class VH(itemView: View) : RecyclerView.ViewHolder(itemView) {
            var ivOptIcon: ImageView? = null
            var ivAddOrDelete: ImageView? = null
            var tvMotionName: TextView? = null

            init {
                ivAddOrDelete = itemView.findViewById(R.id.ivAddOrDelete)
                ivOptIcon = itemView.findViewById(R.id.ivOptIcon)
                tvMotionName = itemView.findViewById(R.id.tvMotionName)
                itemView.setOnClickListener {
                    if (enable) {
                        onItemClickListener?.onItemClick(adapterPosition)
                    }
                }
            }
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
            return VH(
                LayoutInflater.from(parent.context)
                    .inflate(
                        R.layout.item_sports_data_view_detail_list,
                        parent,
                        false
                    )
            )
        }

        override fun onBindViewHolder(holder: VH, position: Int) {
            val data = getItem(position)
            data?.let {
                holder.tvMotionName?.setText(it.name)
            }
            holder.ivAddOrDelete?.setImageResource(getDeleteOrAddIcon())
            holder.ivAddOrDelete?.isEnabled = enable
            holder.ivOptIcon?.visibility =
                if (viewType == ADDED) View.VISIBLE else View.GONE
            holder.ivAddOrDelete?.visibility = View.VISIBLE
        }

        override fun getItemCount(): Int = list?.size ?: 0

        fun getItem(position: Int) = list?.get(position)

        private fun getDeleteOrAddIcon() = if (viewType == ADDED) {
            if (enable)
                R.drawable.icon_motion_type_delete
            else
                R.drawable.icon_motion_type_delete_disable
        } else {
            if (enable)
                R.drawable.icon_motion_type_add
            else
                R.drawable.icon_motion_type_add_disable
        }
    }

    fun btSet(view: android.view.View) {
        setSportSubItemParaSort(dataViewBean!!.type, mList, mNotAddedList)
        showProgressDialog("")
    }


    fun setSportSubItemParaSort(
        motionType: Int,
        mList: MutableList<SportsDataViewDetailBean>,
        mNotAddedList: MutableList<SportsDataViewDetailBean>
    ) {
        val resultList = mutableListOf<SportsDataViewDetailBean>()
        resultList.addAll(mList)
        resultList.addAll(mNotAddedList)
        val types = resultList.map { it.type }
        BLEManager.setSportSubItemParaSort(types, mList.size, motionType)
    }
}