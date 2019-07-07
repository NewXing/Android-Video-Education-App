package education.juxin.com.educationpro.party3.ijkplayer.utils;

import android.app.Activity;
import android.content.Context;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

public class LayoutQuery {
    private Context context;
    private Activity activity;
    private View rootView;
    private View view;

    /**
     * 拓展播放器view的方法使用
     */
    public LayoutQuery(Context context, View view) {
        this.context = context;
        this.rootView = view;
    }

    /**
     * 原来的方式使用
     */
    public LayoutQuery(Activity activity) {
        this.context = activity;
        this.activity = activity;
    }

    public LayoutQuery id(int id) {
        if (rootView == null) {
            view = activity.findViewById(id);
        } else {
            view = rootView.findViewById(id);
        }
        return this;
    }

    public LayoutQuery image(int resId) {
        if (view instanceof ImageView) {
            ((ImageView) view).setImageResource(resId);
        }
        return this;
    }

    public LayoutQuery visible() {
        if (view != null) {
            view.setVisibility(View.VISIBLE);
        }
        return this;
    }

    public LayoutQuery gone() {
        if (view != null) {
            view.setVisibility(View.GONE);
        }
        return this;
    }

    public LayoutQuery invisible() {
        if (view != null) {
            view.setVisibility(View.INVISIBLE);
        }
        return this;
    }

    public LayoutQuery clicked(View.OnClickListener handler) {
        if (view != null) {
            view.setOnClickListener(handler);
        }
        return this;
    }

    public LayoutQuery text(CharSequence text) {
        if (view != null && view instanceof TextView) {
            ((TextView) view).setText(text);
        }
        return this;
    }

    public LayoutQuery visibility(int visible) {
        if (view != null) {
            view.setVisibility(visible);
        }
        return this;
    }

    private void size(boolean width, int n, boolean dip) {
        if (view != null) {
            ViewGroup.LayoutParams lp = view.getLayoutParams();

            if (n > 0 && dip) {
                n = dip2pixel(context, n);
            }

            if (width) {
                lp.width = n;
            } else {
                lp.height = n;
            }

            view.setLayoutParams(lp);
        }
    }

    public void height(int height, boolean dip) {
        size(false, height, dip);
    }

    private int dip2pixel(Context context, float n) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, n, context.getResources().getDisplayMetrics());
    }

}