package com.itheima09.hmpuzzle;

import android.app.Activity;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import java.util.Random;

public class MainActivity extends Activity {

        private GridView gridview;
        int[] templetIds = new int[]{
              R.mipmap.templet_0,
              R.mipmap.templet_1,
              R.mipmap.templet_2,
              R.mipmap.templet_3,
              R.mipmap.templet_4,
              R.mipmap.templet_5
        };
        //图片资源
        int[] imgIds = new int[]{
              R.mipmap.img_0,
              R.mipmap.img_1,
              R.mipmap.img_2
        };
        private float density;
        private Bitmap[] bitmaps;
        private RelativeLayout puzzleArea;
        private int[][] templet;
        private ImageView viewShot;

        @Override
        protected void onCreate(Bundle savedInstanceState) {
                super.onCreate(savedInstanceState);
                requestWindowFeature(Window.FEATURE_NO_TITLE);
                setContentView(R.layout.activity_main);
                puzzleArea = (RelativeLayout) findViewById(R.id.puzzle_area);
                gridview = (GridView) findViewById(R.id.gv);
                viewShot = (ImageView) findViewById(R.id.view_shot);
                //设置列数
                int length = templetIds.length;
                gridview.setNumColumns(length);
                //设置宽度
                density = getResources().getDisplayMetrics().density;
                LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams((int) (78 * length * density),
                      ViewGroup.LayoutParams.WRAP_CONTENT);
                gridview.setLayoutParams(layoutParams);
                //设置数据源
                gridview.setAdapter(new MyAdapter());
                gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                //获取模板数据
                                templet = PuzzleUtils.getsInstance().dp2px(position, density);
                                //获取原图片
                                bitmaps = PuzzleUtils.getsInstance().loadOriginalBitmap(getResources(), imgIds);
                                //根据模块的大小缩放bitmap
                                PuzzleUtils.getsInstance().scaleBitmap(templet, bitmaps);
                                //创建拼图
                                createPuzzleViews();
                        }

                });
        }

        private void createPuzzleViews() {
                //移除所有的子view
                puzzleArea.removeAllViews();
                //遍历模板生成imageview
                for (int i = 0; i < templet.length; i++) {
                        //创建imageview
                        ImageView imageView = new ImageView(this);
                        //设置图片
                        Bitmap bitmap = bitmaps[i];
                        imageView.setImageBitmap(bitmap);
                        //宽高属性
                        int[] imgAttrs = templet[i];//img0属性
                        //宽
                        int w = imgAttrs[4];
                        int h = imgAttrs[5];

                        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(w, h);
                        //创建一个线性布局
                        LinearLayout linearLayout = new LinearLayout(this);
                        //TODO 处理边距
                        int padding = imgAttrs[0] / 2;
                        linearLayout.setPadding(padding, padding, padding, padding);
                        //设置拼图区域的内边距
                        puzzleArea.setPadding(padding, padding, padding, padding);

                        //将imageview添加到线性布局
                        linearLayout.addView(imageView, params);
                        //设置布局id
                        int id = imgAttrs[6];
                        linearLayout.setId(id);
                        //添加linearLayout到拼图区域中
                        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                        //设置位置信息

                        if (imgAttrs[8] != -1) {
                                //below的属性
                                layoutParams.addRule(RelativeLayout.BELOW, imgAttrs[8]);
                        }
                        if (imgAttrs[9] != -1) {
                                layoutParams.addRule(RelativeLayout.RIGHT_OF, imgAttrs[9]);
                        }
                        if (imgAttrs[10] != -1) {
                                layoutParams.addRule(RelativeLayout.ALIGN_TOP, imgAttrs[10]);
                        }
                        if (imgAttrs[11] != -1) {
                                layoutParams.addRule(RelativeLayout.ALIGN_LEFT, imgAttrs[11]);
                        }

                        puzzleArea.addView(linearLayout, layoutParams);
                }
                //设置imageview的动画
                int childCount = puzzleArea.getChildCount();
                Random random = new Random();
                for (int i = 0; i < childCount; i++) {
                        View child = puzzleArea.getChildAt(i);
                        float fromX = random.nextInt(500);
                        float fromY = random.nextInt(400);
                        //位移动画
                        TranslateAnimation ta = new TranslateAnimation(fromX, 0, fromY, 0);
                        ta.setDuration(400);
                        child.setAnimation(ta);
                        //设置动画结束的监听
                        if (i == childCount - 1) {
                                ta.setAnimationListener(new Animation.AnimationListener() {
                                        @Override
                                        public void onAnimationStart(Animation animation) {

                                        }

                                        @Override
                                        public void onAnimationEnd(Animation animation) {
                                                //保存拼图
                                                puzzleArea.setDrawingCacheEnabled(true);//开启绘制缓存的功能
                                                puzzleArea.buildDrawingCache();//创建缓存
                                                Bitmap bitmap = puzzleArea.getDrawingCache();
                                                Bitmap newBitmap = Bitmap.createBitmap(bitmap);
                                                viewShot.setImageBitmap(newBitmap);
                                        }

                                        @Override
                                        public void onAnimationRepeat(Animation animation) {

                                        }
                                });
                        }
                        ta.start();

                }

        }


        private class MyAdapter extends BaseAdapter {
                @Override
                public int getCount() {
                        return templetIds.length;
                }

                @Override
                public Object getItem(int position) {
                        return templetIds[position];
                }

                @Override
                public long getItemId(int position) {
                        return position;
                }

                @Override
                public View getView(int position, View convertView, ViewGroup parent) {
                        ImageView imageView = new ImageView(parent.getContext());
                        imageView.setImageResource(templetIds[position]);
                        return imageView;
                }
        }
}
