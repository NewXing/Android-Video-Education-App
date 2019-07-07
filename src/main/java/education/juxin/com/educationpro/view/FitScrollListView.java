package education.juxin.com.educationpro.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ListView;

/**
 * 解决ListView在滚动层中的嵌套问题
 */
public class FitScrollListView extends ListView {

    public FitScrollListView(Context context) {
        this(context, null);
    }

    public FitScrollListView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public FitScrollListView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        //测量大小由一个32位的数字表示，前两位表示测量模式，后30位表示大小
        int expandSpec = MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2, MeasureSpec.AT_MOST);
        super.onMeasure(widthMeasureSpec, expandSpec);
    }
}
