package education.juxin.com.educationpro.base;

import android.support.v7.widget.RecyclerView;
import android.util.SparseArray;
import android.view.View;

public class BaseRecyclerHolder extends RecyclerView.ViewHolder {

    private SparseArray<View> mViews;

    public BaseRecyclerHolder(View itemView) {
        super(itemView);
        if ("Header".equals(itemView.getTag()) || "Footer".equals(itemView.getTag())) {
            return;
        }
        mViews = new SparseArray<>();
    }

    public View findViewById(int viewId) {
        View view = mViews.get(viewId);
        if (view == null) {
            view = itemView.findViewById(viewId);
            mViews.put(viewId, view);
        }
        return view;
    }
}
