//package com.beefficient;
//
//import android.content.Context;
//import android.content.res.TypedArray;
//import android.graphics.Canvas;
//import android.graphics.drawable.Drawable;
//import android.support.v4.content.ContextCompat;
//import android.support.v7.widget.RecyclerView;
//import android.view.View;
//
//import com.example.beefficient.AppConfig;
//
//public class DividerItemDecoration extends RecyclerView.ItemDecoration {
//    private static final int[] ATTRS = new int[]{
//            android.R.attr.listDivider
//    };
//
//    private Drawable divider;
//
//    public DividerItemDecoration(Context context) {
//        final TypedArray styledAttributes = context.obtainStyledAttributes(ATTRS);
//        divider = styledAttributes.getDrawable(0);
//        styledAttributes.recycle();
//    }
//
//    public DividerItemDecoration(Context context, int resId) {
//        divider = ContextCompat.getDrawable(context, resId);
//    }
//
//    @Override
//    public void onDraw(Canvas c, RecyclerView parent, RecyclerView.State state) {
//        int left = parent.getPaddingLeft();
//        int right = parent.getWidth() - parent.getPaddingRight();
//
//        int childCount = parent.getChildCount();
//        for (int i = 1; i < childCount; i++) {
//            View child = parent.getChildAt(i);
//
//            RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) child.getLayoutParams();
//            int adapterPosition = params.getViewAdapterPosition();
//            RecyclerView.ViewHolder viewHolder = parent.findViewHolderForAdapterPosition(adapterPosition);
//            if (viewHolder.getItemViewType() == viewHolder.SECTION_VIEW_TYPE) {
//
//                int top = child.getTop() + params.topMargin;
//                int bottom = top + divider.getIntrinsicHeight();
//
//                divider.setBounds(left, top, right, bottom);
//                divider.draw(c);
//            }
//        }
//    }
//}
