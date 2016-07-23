package com.itheima09.hmpuzzle;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;

/**
 * Created by sanpi on 2016/6/13.
 */

public class PuzzleUtils {
    private PuzzleUtils() {

    }

    private static PuzzleUtils sInstance;

    public static PuzzleUtils getsInstance() {
        if (sInstance == null) {
            synchronized (PuzzleUtils.class) {
                if (sInstance == null) {
                    sInstance = new PuzzleUtils();
                }
            }
        }
        return sInstance;
    }

    //第一个模板
    int[][] templet_0 = new int[][]{
            {2, 2, 2, 2, 250, 150, 311, -1, -1, -1, -1, -1},//img0
            {2, 2, 2, 2, 125, 150, 312, -1, 311, -1, -1, 311},//img1
            {2, 2, 2, 2, 125, 150, 313, -1, -1, 312, 312, -1}//img2
    };
    int[][] templet_1 = {

            {2, 2, 2, 2, 250, 100, 321, -1, -1, -1, -1, -1},
            {2, 2, 2, 2, 250, 100, 322, -1, 321, -1, -1, 321},
            {2, 2, 2, 2, 250, 100, 323, -1, 322, -1, -1, 322}

    };
    int[][] templet_2 = {{2, 2, 2, 2, 125, 150, 331, -1, -1, -1, -1, -1},
            {2, 2, 2, 2, 125, 150, 332, -1, -1, 331, 331, -1},
            {2, 2, 2, 2, 250, 150, 333, -1, 331, -1, -1, 331}

    };
    int[][] templet_3 = {{2, 2, 2, 2, 125, 300, 341, -1, -1, -1, -1, -1},
            {2, 2, 2, 2, 125, 150, 342, -1, -1, 341, 341, -1},
            {2, 2, 2, 2, 125, 150, 343, -1, 342, -1, -1, 342}

    };
    int[][] templet_4 = {

            {2, 2, 2, 2, 83, 300, 351, -1, -1, -1, -1, -1},
            {2, 2, 2, 2, 84, 300, 352, -1, -1, 351, 351, -1},
            {2, 2, 2, 2, 83, 300, 353, -1, -1, 352, 352, -1}

    };
    int[][] templet_5 = {

            {2, 2, 2, 2, 125, 150, 361, -1, -1, -1, -1, -1},
            {2, 2, 2, 2, 125, 150, 362, -1, 361, -1, -1, 361},
            {2, 2, 2, 2, 125, 300, 363, -1, -1, 361, 361, -1}};
    //所有模板的集合，三维数组
    int[][][] templets = new int[][][]{
            templet_0,
            templet_1,
            templet_2,
            templet_3,
            templet_4,
            templet_5
    };

    //将模板中的大小信息转化成对应屏幕下的px
    public int[][] dp2px(int position, float density) {
        //获取对应的模板
        int[][] templet = templets[position];
        System.out.println("转化前;" + getTempletStr(templet));
        //遍历模板数组
        for (int i = 0; i < templet.length; i++) {
            //一个imageview的属性 {2, 2, 2, 2, 125, 150, 361, -1, -1, -1, -1, -1}
            int[] imgAttrs = templet[i];
            for (int j = 0; j < 6; j++) {
                //每一个属性
                int imgAttr = imgAttrs[j];
                imgAttrs[j] = (int) (imgAttr * density);
            }
        }
        System.out.println("转化后;" + getTempletStr(templet));
        return templet;
    }

    private String getTempletStr(int[][] templet) {
        StringBuilder sb = new StringBuilder();
       /* {

            {2, 2, 2, 2, 125, 150, 361, -1, -1, -1, -1, -1},
            {2, 2, 2, 2, 125, 150, 362, -1, 361, -1, -1, 361},
            {2, 2, 2, 2, 125, 300, 363, -1, -1, 361, 361, -1}};*/
        sb.append("{\r\n");
        for (int i = 0; i < templet.length; i++) {
            sb.append("{");
            int[] imgAttrs = templet[i];//{2, 2, 2, 2, 125, 150, 361, -1, -1, -1, -1, -1}
            for (int j = 0; j < imgAttrs.length; j++) {
                int imgAttr = imgAttrs[j];//2
                sb.append(imgAttr);
                if (j < imgAttrs.length - 1) {
                    sb.append(",");
                }
            }
            sb.append("}\r\n");

        }
        sb.append("}");

        return sb.toString();
    }

    //加载原图片
    public Bitmap[] loadOriginalBitmap(Resources res, int[] imgIds) {
        Bitmap[] bitmps = new Bitmap[imgIds.length];
        for (int i = 0; i < imgIds.length; i++) {
            int id = imgIds[i];//R.mipmap.img0
            //加载策略
            BitmapFactory.Options opts = new BitmapFactory.Options();
            opts.inJustDecodeBounds = true;//只加载边界信息，不真正加载到内存
            BitmapFactory.decodeResource(res, id, opts);
            //获取图片的原始宽高
            int outWidth = opts.outWidth;//700
            int outHeight = opts.outHeight;
            //480*800作为边界
            if (outWidth > 480 && outHeight > 800) {
                int w_ratio = outWidth / 480;
                int h_ratio = outHeight / 800;
                opts.inSampleSize = Math.max(w_ratio, h_ratio);
            }
            //加载图片
            opts.inJustDecodeBounds = false;
            Bitmap bitmap = BitmapFactory.decodeResource(res, id, opts);
            bitmps[i] = bitmap;
        }
        return bitmps;
    }

    //根据模板的大小缩放bitmap
    public void scaleBitmap(int[][] templet, Bitmap[] bitmaps) {
        for (int i = 0; i < templet.length; i++) {
            int[] imgAttrs = templet[i];//img0的属性 {2, 2, 2, 2, 125, 150, 361, -1, -1, -1, -1, -1}
            //模板中的宽高
            int w = imgAttrs[4];//200
            int h = imgAttrs[5];
            //获取对应图片的宽高
            Bitmap bitmap = bitmaps[i];
            //图片的宽高
            int width = bitmap.getWidth();//400
            int height = bitmap.getHeight();
            //source 需要缩放的图片
            //x,y 起点
            //width 宽度方向上切割的长度
            //height 高度方向上切割的长度
            Matrix m = new Matrix();//图形变换，缩放，平移...
            float sx = w * 1.0f / width;
            float sy = h * 1.0f / height;
            m.setScale(sx, sy);
            bitmaps[i] = Bitmap.createBitmap(bitmap, 0, 0, width, height, m, true);
        }
    }
}
