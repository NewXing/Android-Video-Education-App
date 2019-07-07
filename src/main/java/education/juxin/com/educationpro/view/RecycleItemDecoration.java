package education.juxin.com.educationpro.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import education.juxin.com.educationpro.R;

/**
 * 自定义的recyclerView的分割线
 * The type Base item decoration.
 * create time 2018 -3-23
 */
public class RecycleItemDecoration extends RecyclerView.ItemDecoration {

    public static final int VERTICAL = LinearLayoutManager.VERTICAL;
    public static final int HORIZONTAL = LinearLayoutManager.HORIZONTAL;

    private int mOrientation;
    private int mDecorationSize;
    private Paint mPaint;

    /**
     * 默认构造器
     */
    public RecycleItemDecoration(Context context) {
        this(context, RecycleItemDecoration.VERTICAL, 1, R.color.gray_line);
    }

    /**
     * 完整构造器
     *
     * @param orientation     列表方向
     * @param decorationSize  间隔尺寸
     * @param decorationColor 间隔颜色
     */
    public RecycleItemDecoration(Context context, int orientation, int decorationSize, int decorationColor) {
        mOrientation = orientation;
        mDecorationSize = decorationSize;
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setColor(context.getResources().getColor(decorationColor));
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        String tag = (String) view.getTag();
        if ("Header".equals(tag) || "Footer".equals(tag)) {
            return;
        }

        if (mOrientation == LinearLayoutManager.VERTICAL) {
            outRect.set(0, 0, 0, mDecorationSize);
        } else {
            outRect.set(0, 0, mDecorationSize, 0);
        }
    }

    @Override
    public void onDraw(Canvas c, RecyclerView parent, RecyclerView.State state) {
        if (mOrientation == LinearLayoutManager.VERTICAL) {
            drawVertical(c, parent);
        } else {
            drawHorizontal(c, parent);
        }
    }

    /**
     * 绘制纵向 item 分割线
     */
    private void drawVertical(Canvas canvas, RecyclerView parent) {
        int left = parent.getPaddingLeft();
        int right = parent.getMeasuredWidth() - parent.getPaddingRight();
        int childSize = parent.getChildCount();
        for (int i = 0; i < childSize; i++) {
            View child = parent.getChildAt(i);
            RecyclerView.LayoutParams layoutParams = (RecyclerView.LayoutParams) child.getLayoutParams();
            int top = child.getBottom() + layoutParams.bottomMargin;
            int bottom = top + mDecorationSize;
            canvas.drawRect(left, top, right, bottom, mPaint);
        }
    }

    /**
     * 绘制横向 item 分割线
     */
    private void drawHorizontal(Canvas canvas, RecyclerView parent) {
        int top = parent.getPaddingTop();
        int bottom = parent.getMeasuredHeight() - parent.getPaddingBottom();
        int childSize = parent.getChildCount();
        for (int i = 1; i < childSize; i++) {
            View child = parent.getChildAt(i);
            RecyclerView.LayoutParams layoutParams = (RecyclerView.LayoutParams) child.getLayoutParams();
            int left = child.getRight() + layoutParams.rightMargin;
            int right = left + mDecorationSize;
            canvas.drawRect(left, top, right, bottom, mPaint);
        }
    }
}
