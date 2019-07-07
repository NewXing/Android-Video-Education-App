package education.juxin.com.educationpro.adapter.listview;

import android.app.Activity;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import education.juxin.com.educationpro.R;
import education.juxin.com.educationpro.bean.TeacherHomePageBean;
import education.juxin.com.educationpro.interfaces.ICheckCourseValid;
import education.juxin.com.educationpro.ui.activity.dynamic.CourseDetailActivity;
import education.juxin.com.educationpro.util.ImageUtils;

public class ListViewHolderAdapter extends BaseAdapter {

    private ArrayList<TeacherHomePageBean.TeacherHomePageData.TeacherHomePageItemData> mData;
    private LayoutInflater mInflater;
    private Activity mActivity;

    public ListViewHolderAdapter(Activity activity, ArrayList<TeacherHomePageBean.TeacherHomePageData.TeacherHomePageItemData> data) {
        mData = data;
        mInflater = LayoutInflater.from(activity);
        this.mActivity = activity;
    }

    @Override
    public int getCount() {
        return mData.size();
    }

    @Override
    public Object getItem(int position) {
        return mData.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.item_dynamic_lv, null);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CourseDetailActivity.checkCourseValid(mActivity, mData.get(position).getId(), "", new ICheckCourseValid() {
                    @Override
                    public void isValid() {
                        Intent intent = new Intent(mActivity, CourseDetailActivity.class);
                        intent.putExtra("id_course_detail", mData.get(position).getId());
                        mActivity.startActivity(intent);
                    }

                    @Override
                    public void isInvalid() {
                    }
                });
            }
        });

        TeacherHomePageBean.TeacherHomePageData.TeacherHomePageItemData data = mData.get(position);
        ImageUtils.GlideUtil(mActivity, data.getCoverImgUrl(), holder.courseImg);
        holder.titleTv.setText(data.getCourseName());
        holder.dyTeacherTv.setText(String.format(mActivity.getString(R.string.main_teacher_with_name), data.getMainTeacherName()));
        holder.courseTv.setText(data.getClassNum());

        return convertView;
    }

    public final class ViewHolder {
        ImageView courseImg;
        TextView titleTv;
        TextView dyTeacherTv;
        TextView courseTv;

        ViewHolder(View convertView) {
            courseImg = convertView.findViewById(R.id.collect_book_img);
            titleTv = convertView.findViewById(R.id.tv_dy_name);
            dyTeacherTv = convertView.findViewById(R.id.collect_teacher);
            courseTv = convertView.findViewById(R.id.tv_time_long);
        }
    }

}

