package education.juxin.com.educationpro.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Path;
import android.util.AttributeSet;

/**
 * 圆角矩形
 * Created by Administrator on 2018/3/27 0027.
 */
public class RoundImageView extends android.support.v7.widget.AppCompatImageView {

    private float width;
    private float height;
    private Path path;

    public RoundImageView(Context context) {
        this(context, null);
    }

    public RoundImageView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public RoundImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        path = new Path();
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        width = getWidth();
        height = getHeight();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (width > 12 && height > 12) {
            path.moveTo(12, 0);
            path.lineTo(width - 12, 0);
            path.quadTo(width, 0, width, 12);
            path.lineTo(width, height - 12);
            path.quadTo(width, height, width - 12, height);
            path.lineTo(12, height);
            path.quadTo(0, height, 0, height - 12);
            path.lineTo(0, 12);
            path.quadTo(0, 0, 12, 0);
            canvas.clipPath(path);
        }
        super.onDraw(canvas);
    }

}
