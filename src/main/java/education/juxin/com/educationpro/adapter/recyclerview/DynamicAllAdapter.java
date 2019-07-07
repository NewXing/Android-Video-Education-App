package education.juxin.com.educationpro.adapter.recyclerview;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

import education.juxin.com.educationpro.R;
import education.juxin.com.educationpro.adapter.listview.ListViewHolderAdapter;
import education.juxin.com.educationpro.bean.TeacherHomePageBean;
import education.juxin.com.educationpro.ui.activity.dynamic.SpaceAllCourseActivity;
import education.juxin.com.educationpro.view.FitScrollListView;

/**
 * 动态列表的适配器 为recyclerView的列表展示
 * Created by Administrator on 2018/3/21.
 */
public class DynamicAllAdapter extends RecyclerView.Adapter<DynamicAllAdapter.ViewHolder> {

    private static final int TYPE_HEADER = 0;  //说明是带有Header的
    private static final int TYPE_FOOTER = 1;  //说明是带有Footer的
    private static final int TYPE_NORMAL = 2;  //说明是不带有header和footer的

    private View mHeaderView;
    private View mFooterView;

    private ArrayList<ArrayList<TeacherHomePageBean.TeacherHomePageData.TeacherHomePageItemData>> itemDataList;
    private Activity mContext;
    private String mTeacherId;

    public DynamicAllAdapter(Activity context, ArrayList<ArrayList<TeacherHomePageBean.TeacherHomePageData.TeacherHomePageItemData>> itemDataList, String mTeacherId) {
        this.mContext = context;
        this.itemDataList = itemDataList;
        this.mTeacherId = mTeacherId;
    }

    @Override
    public int getItemViewType(int position) {
        if (mHeaderView == null && mFooterView == null) {
            return TYPE_NORMAL;
        }
        if (mHeaderView != null && position == 0) {
            return TYPE_HEADER;
        }
        if (mFooterView != null && position == getItemCount() - 1) {
            return TYPE_FOOTER;
        }
        return TYPE_NORMAL;
    }

    @Override
    public DynamicAllAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (mHeaderView != null && viewType == TYPE_HEADER) {
            return new DynamicAllAdapter.ViewHolder(mHeaderView);
        }
        if (mFooterView != null && viewType == TYPE_FOOTER) {
            return new DynamicAllAdapter.ViewHolder(mFooterView);
        }
        View layout = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_dynamic_all, parent, false);
        return new DynamicAllAdapter.ViewHolder(layout);
    }

    @Override
    public void onBindViewHolder(DynamicAllAdapter.ViewHolder holder, final int position) {
        if (getItemViewType(position) == TYPE_NORMAL) {
            if (holder != null) {
                final ArrayList<TeacherHomePageBean.TeacherHomePageData.TeacherHomePageItemData> data = itemDataList.get(position - 1);
                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        mOnItemClickListener.onClick(position);
                    }
                });
                holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View view) {
                        mOnItemClickListener.onLongClick(position);
                        return false;
                    }
                });
                holder.llCollect.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(mContext, SpaceAllCourseActivity.class);
                        intent.putExtra("teacher_id", mTeacherId);
                        intent.putExtra("classification_id", data.get(0).getClassificationId());
                        mContext.startActivity(intent);
                    }
                });
                holder.tvDynamicName.setText(data.get(0).getName());

                ListViewHolderAdapter mDynamicAdapter = new ListViewHolderAdapter(mContext, data);
                holder.fitScrollListView.setAdapter(mDynamicAdapter);
                holder.fitScrollListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        mOnItemClickListener.onClick(position);
                    }
                });
            }
            return;
        } else if (getItemViewType(position) == TYPE_HEADER) {
            return;
        } else {
            return;
        }
    }

    @Override
    public int getItemCount() {
        if (mHeaderView == null && mFooterView == null) {
            return itemDataList.size();
        } else if (mHeaderView == null || mFooterView == null) {
            return itemDataList.size() + 1;
        } else {
            return itemDataList.size() + 2;
        }
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvDynamicName;
        FitScrollListView fitScrollListView;
        LinearLayout llCollect;

        ViewHolder(View view) {
            super(view);
            if (itemView == mHeaderView) {
                return;
            }
            if (itemView == mFooterView) {
                return;
            }

            tvDynamicName = view.findViewById(R.id.tv_dynamic_name);
            fitScrollListView = view.findViewById(R.id.lv_view);
            llCollect = view.findViewById(R.id.ll_collect);
        }
    }

    // Item触发事件
    private OnItemClickListener mOnItemClickListener;

    public interface OnItemClickListener {
        void onClick(int position);

        void onLongClick(int position);
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.mOnItemClickListener = onItemClickListener;
    }

    public void setListViewItemClickListener(OnItemClickListener onItemClickListener) {
        this.mOnItemClickListener = onItemClickListener;
    }

    public void setHeaderView(View headerView) {
        mHeaderView = headerView;
        mHeaderView.setTag("Header");
        notifyItemInserted(0);
    }

    public void setFooterView(View footerView) {
        mFooterView = footerView;
        mFooterView.setTag("Footer");
        notifyItemInserted(getItemCount() - 1);
    }

}
