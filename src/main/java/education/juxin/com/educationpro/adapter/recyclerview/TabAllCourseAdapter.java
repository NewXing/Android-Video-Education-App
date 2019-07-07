package education.juxin.com.educationpro.adapter.recyclerview;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import education.juxin.com.educationpro.R;
import education.juxin.com.educationpro.bean.SpaceCourseBean;
import education.juxin.com.educationpro.util.FormatTimeUtil;
import education.juxin.com.educationpro.util.ImageUtils;

/**
 * 适配器 为recyclerView的列表展示
 * The type Tab course adapter.
 * create time 2018 -3-20
 */
public class TabAllCourseAdapter extends RecyclerView.Adapter<TabAllCourseAdapter.ViewHolder> {

    private ArrayList<SpaceCourseBean.SpaceCourseData> itemDataList;
    private Context context;

    private boolean isShowSelectCheckBox; // 界面复用

    private SparseBooleanArray isCheckedMap = new SparseBooleanArray();

    public TabAllCourseAdapter(Context context, ArrayList<SpaceCourseBean.SpaceCourseData> itemDataList) {
        this.context = context;
        this.itemDataList = itemDataList;

        this.isShowSelectCheckBox = false;
        setAllOrAllNoSelected(false);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_course_tab, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        if (holder != null) {
            SpaceCourseBean.SpaceCourseData data = itemDataList.get(position);
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
            ImageUtils.GlideUtil(context, data.getCoverImgUrl(), holder.coursePictureImg);
            holder.courseTitleTv.setText(data.getName());
            holder.courseTeacherTv.setText(String.format(context.getString(R.string.main_teacher_with_name), data.getMainTeacherName()));
            holder.courseTimeTv.setText(data.getClassNum());
            String endTime = data.getEndTime();
            String courseTimeStamp = FormatTimeUtil.formatDateStr2TimeStamp(endTime, "yyyy-MM-dd HH:mm:ss");
            String yearMounthTime = FormatTimeUtil.formatTimeStamp2DateStr(courseTimeStamp, "yyyy-MM-dd");
            holder.validityTimeTv.setText(String.format(context.getString(R.string.end_time_with_param), yearMounthTime));

            holder.courseTypeTv.setText("");
            holder.courseTypeTv.setVisibility(View.INVISIBLE);

            if (isShowSelectCheckBox) {
                holder.selectCheckBox.setVisibility(View.VISIBLE);
                holder.selectCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        isCheckedMap.put(position, isChecked);

                        mOnSelectItemListener.onAllSelected(isAllOrAllNoSelected());
                    }
                });
                holder.selectCheckBox.setChecked(isCheckedMap.get(position));
            } else {
                setAllOrAllNoSelected(false);
                holder.selectCheckBox.setVisibility(View.GONE);
                holder.selectCheckBox.setChecked(false);
            }
        }
    }

    @Override
    public int getItemCount() {
        return itemDataList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        ImageView coursePictureImg;
        TextView courseTitleTv;
        TextView courseTeacherTv;
        TextView courseTimeTv;
        TextView validityTimeTv;
        TextView courseTypeTv;
        CheckBox selectCheckBox;

        ViewHolder(View itemView) {
            super(itemView);

            coursePictureImg = itemView.findViewById(R.id.course_picture_img);
            courseTitleTv = itemView.findViewById(R.id.course_title_tv);
            courseTeacherTv = itemView.findViewById(R.id.course_teacher_tv);
            courseTimeTv = itemView.findViewById(R.id.course_time_tv);
            validityTimeTv = itemView.findViewById(R.id.validity_time_tv);
            courseTypeTv = itemView.findViewById(R.id.course_type_tv);
            selectCheckBox = itemView.findViewById(R.id.select_checkbox);
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

    // 全选相关
    public void setShowSelectCheckBox(boolean showSelectCheckBox) {
        isShowSelectCheckBox = showSelectCheckBox;
    }

    private OnSelectItemListener mOnSelectItemListener;

    public interface OnSelectItemListener {
        void onAllSelected(boolean isAll);
    }

    public void setOnSelectItemListener(OnSelectItemListener onSelectItemListener) {
        this.mOnSelectItemListener = onSelectItemListener;
    }

    public void setAllOrAllNoSelected(boolean isAllOrAllNo) {
        for (int i = 0; i < itemDataList.size(); i++) {
            isCheckedMap.put(i, isAllOrAllNo);
        }
    }

    private boolean isAllOrAllNoSelected() {
        boolean isAll = true;
        for (int i = 0; i < isCheckedMap.size(); i++) {
            if (!isCheckedMap.get(i)) {
                isAll = false;
                break;
            }
        }
        return isAll;
    }

}
