package education.juxin.com.educationpro.adapter.listview;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import education.juxin.com.educationpro.R;
import education.juxin.com.educationpro.bean.CourseCategoryBean;
import education.juxin.com.educationpro.util.ImageUtils;

public class CourseTypeGridAdapter extends BaseAdapter {

    private Context context;
    private ArrayList<CourseCategoryBean.CourseCategoryData> typeCategoryDataList;

    public CourseTypeGridAdapter(Context context, ArrayList<CourseCategoryBean.CourseCategoryData> typeCategoryDataList) {
        this.context = context;
        this.typeCategoryDataList = typeCategoryDataList;
    }

    @Override
    public int getCount() {
        return typeCategoryDataList.size();
    }

    @Override
    public Object getItem(int position) {
        return typeCategoryDataList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_home_pager_course_type, parent, false);
            viewHolder = new ViewHolder(convertView);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        ImageUtils.GlideUtil(context, typeCategoryDataList.get(position).getIcon(), viewHolder.imageView);
        viewHolder.textView.setText(typeCategoryDataList.get(position).getName());

        return convertView;
    }

    class ViewHolder {
        ImageView imageView;
        TextView textView;

        ViewHolder(View rootView) {
            imageView = rootView.findViewById(R.id.course_type_one_img);
            textView = rootView.findViewById(R.id.course_type_one_tv);
        }
    }
}
