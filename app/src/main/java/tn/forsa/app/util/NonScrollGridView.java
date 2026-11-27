package tn.forsa.app.util;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.GridView;

/**
 * Role: GridView subclass that expands to fit all its rows when placed inside a ScrollView.
 * Without overriding onMeasure, a GridView inside a ScrollView only displays one row.
 */
public class NonScrollGridView extends GridView {

    public NonScrollGridView(Context context) {
        super(context);
    }

    public NonScrollGridView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public NonScrollGridView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int expandSpec = MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2, MeasureSpec.AT_MOST);
        super.onMeasure(widthMeasureSpec, expandSpec);
    }
}
