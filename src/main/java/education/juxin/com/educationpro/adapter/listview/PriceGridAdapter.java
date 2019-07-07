package education.juxin.com.educationpro.adapter.listview;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Vector;

import education.juxin.com.educationpro.R;
import education.juxin.com.educationpro.bean.ExtractionPriceBean;
import education.juxin.com.educationpro.util.FormatNumberUtil;
import education.juxin.com.educationpro.util.StringUtils;

public class PriceGridAdapter extends BaseAdapter {

    private Context mContext;
    private ArrayList<ExtractionPriceBean.ExtractionPriceData> mList;
    private Vector<Boolean> vecItemState;

    public PriceGridAdapter(Context context, ArrayList<ExtractionPriceBean.ExtractionPriceData> mList) {
        this.mContext = context;
        this.mList = mList;

        this.vecItemState = new Vector<>();
    }

    @Override
    public int getCount() {
        return mList.size();
    }

    @Override
    public Object getItem(int position) {
        return mList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.item_alipay_or_wechat, parent, false);
            viewHolder = new ViewHolder(convertView);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        String number = String.format(mContext.getString(R.string.number_yuan), FormatNumberUtil.doubleFormat(mList.get(position).getPrice(), "0"));
        viewHolder.priceChb.setText(StringUtils.setFontSizeSp(mContext, number, 0, number.length() - 1, 18));

        if (vecItemState.size() != 0 && vecItemState.size() >= position) {
            if (vecItemState.get(position)) {
                viewHolder.priceChb.setBackgroundColor(mContext.getResources().getColor(R.color.light_pink));
                viewHolder.priceChb.setTextColor(mContext.getResources().getColor(R.color.accent_bottom_navigation));
            } else {
                viewHolder.priceChb.setBackgroundColor(mContext.getResources().getColor(R.color.gray_line));
                viewHolder.priceChb.setTextColor(mContext.getResources().getColor(R.color.black));
            }
        }

        return convertView;
    }

    class ViewHolder {
        TextView priceChb;

        ViewHolder(View view) {
            priceChb = view.findViewById(R.id.item_price_btn);
        }
    }

    // 控制只能单选
    public void itemChangeState(int position) {
        for (int i = 0; i < mList.size(); ++i) {
            vecItemState.set(i, false);
        }
        vecItemState.set(position, true);

        notifyDataSetChanged();
    }

    public void initVecItemState() {
        vecItemState.clear();
        vecItemState.add(true);
        for (int i = 1; i < mList.size(); ++i) {
            vecItemState.add(false);
        }
    }

    public ExtractionPriceBean.ExtractionPriceData getCurSelectedItem() {
        ExtractionPriceBean.ExtractionPriceData data = null;

        if (vecItemState.size() != 0 && vecItemState.size() == mList.size()) {
            for (int i = 0; i < vecItemState.size(); ++i) {
                if (vecItemState.get(i)) {
                    data = mList.get(i);
                }
            }
        }

        return data;
    }

}
