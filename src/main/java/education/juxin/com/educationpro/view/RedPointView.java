package education.juxin.com.educationpro.view;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.RoundRectShape;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.FrameLayout.LayoutParams;
import android.widget.TabWidget;

import education.juxin.com.educationpro.util.LogManager;

public class RedPointView extends android.support.v7.widget.AppCompatTextView {

    private boolean mHideOnNull = true;

    public RedPointView(Context context) {
        this(context, null);
    }

    public RedPointView(Context context, AttributeSet attrs) {
        this(context, attrs, android.R.attr.textViewStyle);
    }

    public RedPointView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    private void init() {
        if (!(getLayoutParams() instanceof LayoutParams)) {
            LayoutParams layoutParams = new LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, Gravity.END | Gravity.TOP);
            setLayoutParams(layoutParams);
        }

        // set default font
        setTextColor(Color.WHITE);
        setTypeface(Typeface.DEFAULT_BOLD);
        setTextSize(TypedValue.COMPLEX_UNIT_SP, 11);
        setPadding(dip2Px(5), dip2Px(1), dip2Px(5), dip2Px(1));

        // set default background
        setBackground(9, Color.parseColor("#d3321b"));

        setGravity(Gravity.CENTER);

        // default values
        setHideOnNull(true);
        setRedPointCount(0);
    }

    public void setBackground(int dipRadius, int redPointColor) {
        int radius = dip2Px(dipRadius);
        float[] radiusArray = new float[]{radius, radius, radius, radius, radius, radius, radius, radius};

        RoundRectShape roundRect = new RoundRectShape(radiusArray, null, null);
        ShapeDrawable bgDrawable = new ShapeDrawable(roundRect);
        bgDrawable.getPaint().setColor(redPointColor);
        setBackground(bgDrawable);
    }

    /**
     * @return Returns true if view is hidden on RedPoint value 0 or null;
     */
    public boolean isHideOnNull() {
        return mHideOnNull;
    }

    /**
     * @param hideOnNull the hideOnNull to set
     */
    public void setHideOnNull(boolean hideOnNull) {
        mHideOnNull = hideOnNull;
        setText(getText());
    }

    /**
     * @see android.widget.TextView#setText(java.lang.CharSequence, android.widget.TextView.BufferType)
     */
    @Override
    public void setText(CharSequence text, BufferType type) {
        if (isHideOnNull() && (text == null || text.toString().equalsIgnoreCase("0"))) {
            setVisibility(View.GONE);
        } else {
            setVisibility(View.VISIBLE);
        }
        super.setText(text, type);
    }

    public void setRedPointCount(int count) {
        if (count >= 100) {
            setText("···");
        } else {
            setText(String.valueOf(count));
        }
    }

    public Integer getRedPointCount() {
        if (getText() == null) {
            return null;
        }

        String text = getText().toString();
        try {
            return Integer.parseInt(text);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    public void setRedPointGravity(int gravity) {
        LayoutParams params = (LayoutParams) getLayoutParams();
        params.gravity = gravity;
        setLayoutParams(params);
    }

    public int getRedPointGravity() {
        LayoutParams params = (LayoutParams) getLayoutParams();
        return params.gravity;
    }

    public void setRedPointMargin(int dipMargin) {
        setRedPointMargin(dipMargin, dipMargin, dipMargin, dipMargin);
    }

    public void setRedPointMargin(int leftDipMargin, int topDipMargin, int rightDipMargin, int bottomDipMargin) {
        LayoutParams params = (LayoutParams) getLayoutParams();
        params.leftMargin = dip2Px(leftDipMargin);
        params.topMargin = dip2Px(topDipMargin);
        params.rightMargin = dip2Px(rightDipMargin);
        params.bottomMargin = dip2Px(bottomDipMargin);
        setLayoutParams(params);
    }

    public int[] getRedPointMargin() {
        LayoutParams params = (LayoutParams) getLayoutParams();
        return new int[]{params.leftMargin, params.topMargin, params.rightMargin, params.bottomMargin};
    }

    public void incrementRedPointCount(int increment) {
        Integer count = getRedPointCount();
        if (count == null) {
            setRedPointCount(increment);
        } else {
            setRedPointCount(increment + count);
        }
    }

    public void decrementRedPointCount(int decrement) {
        incrementRedPointCount(-decrement);
    }

    /**
     * Attach the RedPointView to the TabWidget
     *
     * @param target   the TabWidget to attach the RedPointView
     * @param tabIndex index of the tab
     */
    public void setTargetView(TabWidget target, int tabIndex) {
        View tabView = target.getChildTabViewAt(tabIndex);
        setTargetView(tabView);
    }

    /**
     * Attach the RedPointView to the target view
     *
     * @param target the view to attach the RedPointView
     */
    public void setTargetView(View target) {
        if (getParent() != null) {
            ((ViewGroup) getParent()).removeView(this);
        }

        if (target == null) {
            return;
        }

        if (target.getParent() instanceof FrameLayout) {
            ((FrameLayout) target.getParent()).addView(this);

        } else if (target.getParent() instanceof ViewGroup) {
            // use a new FrameLayout container for adding RedPoint
            ViewGroup parentContainer = (ViewGroup) target.getParent();
            int groupIndex = parentContainer.indexOfChild(target);
            parentContainer.removeView(target);

            FrameLayout redPointContainer = new FrameLayout(getContext());
            ViewGroup.LayoutParams parentLayoutParams = target.getLayoutParams();

            redPointContainer.setLayoutParams(parentLayoutParams);
            target.setLayoutParams(new ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));

            parentContainer.addView(redPointContainer, groupIndex, parentLayoutParams);
            redPointContainer.addView(target);

            redPointContainer.addView(this);
        } else if (target.getParent() == null) {
            LogManager.e(getClass().getSimpleName(), "ParentView is needed");
        }

    }

    /**
     * converts dip to px
     */
    private int dip2Px(float dip) {
        return (int) (dip * getContext().getResources().getDisplayMetrics().density + 0.5f);
    }
}
