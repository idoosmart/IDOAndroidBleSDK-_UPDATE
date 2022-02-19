package test.com.ido.utils;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import java.util.List;

/**
 * @author: shensonglong
 * @Package: com.sslong.app.myapp.sort
 * @Description: ${TODO}(通用的适配器)
 * @date: 2016/3/7 17:20
 */
public abstract class ViewAdapter<T> extends BaseAdapter {
    protected Context context;
    protected List<T> datas;
    private final int mItemLayoutId;

    public ViewAdapter(Context context, List<T> datas, int mItemLayoutId) {
        this.context = context;
        this.datas = datas;
        this.mItemLayoutId = mItemLayoutId;
    }
    public void clearData() {
        if (this.datas!=null){
            this.datas.clear();
        }

        if (viewHolder!=null){
            viewHolder.clear();
        }
        notifyDataSetChanged();
    }
    public void setDatas(List<T> datas) {
        this.datas = datas;
    }

    @Override
    public int getCount() {
        return (datas == null) ? 0 : datas.size();
    }

    @Override
    public T getItem(int position) {
        // TODO Auto-generated method stub
        return (datas == null) ? null : datas.get(position);
    }

    @Override
    public long getItemId(int position) {
        // TODO Auto-generated method stub
        return 0;
    }
    private  ViewHolder viewHolder;
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        viewHolder = getViewHolder(convertView, parent, position);
        convert(viewHolder, getItem(position));
        convert(viewHolder, position);
        return viewHolder.getConverView();
    }

    public void convert(ViewHolder helper, T item) {
    }

    public void convert(ViewHolder helper, int position) {
    }

    protected ViewHolder getViewHolder(View convertView, ViewGroup parent, int position) {
        return ViewHolder.get(context, convertView, parent, mItemLayoutId, position);
    }
}
