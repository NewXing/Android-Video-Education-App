package education.juxin.com.educationpro.base;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

public abstract class BaseRecyclerAdapter<T> extends RecyclerView.Adapter<BaseRecyclerHolder> {

    private static final int TYPE_HEADER = 0;
    private static final int TYPE_FOOTER = 1;
    private static final int TYPE_NORMAL = 2;

    private View mHeaderView;
    private View mFooterView;

    private List<T> mDataList;
    private int mLayoutResId;

    private OnItemClickListener mOnItemClickListener;
    private OnItemLongClickListener mOnItemLongClickListener;

    public BaseRecyclerAdapter(int layoutResId, List<T> dataList) {
        this.mLayoutResId = layoutResId;
        this.mDataList = dataList;
    }

    /**
     * 必须要实现的方法！用于绑定ItemData和ViewHolder
     */
    public abstract void itemViewBindData(BaseRecyclerHolder viewHolder, int position, T itemData);

    /**
     * Item触发事件相关
     */
    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.mOnItemClickListener = onItemClickListener;
    }

    public void setOnItemLongClickListener(OnItemLongClickListener mOnItemLongClickListener) {
        this.mOnItemLongClickListener = mOnItemLongClickListener;
    }

    /**
     * 设置头脚布局相关
     */
    public void setHeaderView(View headerView) {
        mHeaderView = headerView;
        mHeaderView.setTag("Header");
        notifyItemInserted(0);
    }

    public View getHeaderView() {
        return mHeaderView;
    }

    public void setFooterView(View footerView) {
        mFooterView = footerView;
        mFooterView.setTag("Footer");
        notifyItemInserted(getItemCount() - 1);
    }

    public View getFooterView() {
        return mFooterView;
    }

    /**
     * 以下为内部实现
     */
    @Override
    public int getItemCount() {
        int size = mDataList != null ? mDataList.size() : 0;

        if (mHeaderView == null && mFooterView == null) {
            return size;
        } else if (mHeaderView == null || mFooterView == null) {
            return size + 1;
        } else {
            return size + 2;
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (mHeaderView == null && mFooterView == null) {
            return TYPE_NORMAL;
        }
        if (mHeaderView != null && position == 0) {
            return TYPE_HEADER; // 第一个item应加载Header
        }
        if (mFooterView != null && position == getItemCount() - 1) {
            return TYPE_FOOTER; // 最后一个item应加载Footer
        }

        return TYPE_NORMAL;
    }

    @Override
    public BaseRecyclerHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (mHeaderView != null && viewType == TYPE_HEADER) {
            return new BaseRecyclerHolder(mHeaderView);
        }
        if (mFooterView != null && viewType == TYPE_FOOTER) {
            return new BaseRecyclerHolder(mFooterView);
        }

        View layout = LayoutInflater.from(parent.getContext()).inflate(mLayoutResId, parent, false);
        return new BaseRecyclerHolder(layout);
    }

    @Override
    public void onBindViewHolder(BaseRecyclerHolder holder, final int position) {
        if (getItemViewType(position) == TYPE_NORMAL && holder != null) {
            if (mOnItemClickListener != null) {
                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        mOnItemClickListener.onClick(position);
                    }
                });
            }
            if (mOnItemLongClickListener != null) {
                holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View view) {
                        mOnItemLongClickListener.onLongClick(position);
                        return false;
                    }
                });
            }

            if (mHeaderView == null && mFooterView == null) {
                itemViewBindData(holder, position, mDataList.get(position));
            } else {
                itemViewBindData(holder, position, mDataList.get(position - 1));
            }
        }
    }

    public interface OnItemClickListener {
        void onClick(int position);
    }

    public interface OnItemLongClickListener {
        void onLongClick(int position);
    }

}
