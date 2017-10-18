package com.example.senzec.datingapp.adapters;

/**
 * Created by power hashing on 5/3/2017.
 */

import android.content.Context;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.view.PagerAdapter;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.senzec.datingapp.R;


public class CustomSwipeAdapter extends PagerAdapter {
    private int[] image_resource = {R.drawable.profile_pic2, R.drawable.profile_pic, R.drawable.profile_pic2, R.drawable.profile_pic};
    private Context ctx;
    private LayoutInflater layoutInflater;
    private TextView[] dots;
    LinearLayout layoutDots;

    public CustomSwipeAdapter(Context ctx) {
        this.ctx = ctx;
    }

    @Override
    public int getCount() {
        return image_resource.length;
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {

        return (view == (FrameLayout) object);
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        layoutInflater = (LayoutInflater) ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View item_view = layoutInflater.inflate(R.layout.swipelayout, container, false);
        ImageView imageview = (ImageView) item_view.findViewById(R.id.image_view);
        layoutDots=(LinearLayout)item_view.findViewById(R.id.layoutDots);
        addBottomDots(position);
        imageview.setImageResource(image_resource[position]);
        addBottomDots(position);
        container.addView(item_view);

        return item_view;
    }

    private void addBottomDots(int currentPage) {
        dots = new TextView[image_resource.length];

        int[] colorsActive = ctx.getResources().getIntArray(R.array.array_dot_active);
        int[] colorsInactive = ctx.getResources().getIntArray(R.array.array_dot_inactive);

        layoutDots.removeAllViews();
        for (int i = 0; i < dots.length; i++) {
            dots[i] = new TextView(ctx);
            dots[i].setText(Html.fromHtml("&#8226;"));
            dots[i].setTextSize(35);
            dots[i].setTextColor(colorsInactive[currentPage]);
            layoutDots.addView(dots[i]);
        }

        if (dots.length > 0)
            dots[currentPage].setTextColor(colorsActive[currentPage]);
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((FrameLayout) object);
    }

}