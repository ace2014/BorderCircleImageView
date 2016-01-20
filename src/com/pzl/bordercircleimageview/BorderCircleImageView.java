package com.pzl.bordercircleimageview;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

/**
 * 
 * @author zl.peng
 *         
 */
public class BorderCircleImageView extends View
{
    private float mBorderWidth;// 边框宽度
    
    private int mBorderColor;// 边框颜色
    
    private int mBorderAlpha;// 边框透明度
    
    private int mImageRes;// 初始化静态图片resId
    
    private Bitmap mOriginalBitmap;// 原始没缩放的bitmap
    
    private Bitmap mCircleBitmap;// 最终显示的圆形bitmap
    
    private Bitmap scaledBitmap;// 按比例缩放后的bitmap
    
    private Paint mPaintBorder, mPaintImage;
    
    private float mCxBorder;// 边框圆心x坐标
    
    private float mCyBorder;// 边框圆心y坐标
    
    private float mRadiusBorder;// 边框圆心半径
    
    private float mMinValueOfWidthHeight;// 本View取宽度高度最小值
    
    private static final String TAG = "BorderCircleImageView";
    
    public BorderCircleImageView(Context context)
    {
        this(context, null);
    }
    
    public BorderCircleImageView(Context context, AttributeSet attrs)
    {
        this(context, attrs, 0);
        Log.i(TAG, "2参构造");
    }
    
    public BorderCircleImageView(Context context, AttributeSet attrs, int defStyle)
    {
        super(context, attrs, defStyle);
        Log.i(TAG, "3参构造");
        
        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.BorderCircleImageView);
        
        mBorderWidth = ta.getDimension(R.styleable.BorderCircleImageView_border_width, 8);
        mBorderColor = ta.getColor(R.styleable.BorderCircleImageView_border_color, Color.GRAY);
        mBorderAlpha = ta.getInt(R.styleable.BorderCircleImageView_border_alpha, 100);
        mImageRes = ta.getResourceId(R.styleable.BorderCircleImageView_image_res, android.R.drawable.btn_star_big_on);
        
        mOriginalBitmap = BitmapFactory.decodeResource(getResources(), mImageRes);
        
        mPaintBorder = new Paint();
        mPaintBorder.setStyle(Paint.Style.FILL);
        mPaintBorder.setColor(mBorderColor);
        mPaintBorder.setAlpha(mBorderAlpha);
        mPaintBorder.setAntiAlias(true);
        
    }
    
    @Override
    protected void onDraw(Canvas canvas)
    {
        super.onDraw(canvas);
        Log.i(TAG, "onDraw");
        scaledBitmap = calcScaleBitmap(mOriginalBitmap, mMinValueOfWidthHeight - mBorderWidth);
        mCircleBitmap = tailorCircleBitmap(scaledBitmap);
        canvas.drawCircle(mCxBorder, mCyBorder, mRadiusBorder, mPaintBorder);
        canvas.drawBitmap(mCircleBitmap, mBorderWidth / 2, mBorderWidth / 2, null);
        recycleBitmap();
    }
    
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec)
    {
        Log.i(TAG, "onMeasure");
        setMeasuredDimension(calcWidth(widthMeasureSpec), calcHeight(heightMeasureSpec));
        mMinValueOfWidthHeight = Math.min(getWidth(), getHeight());
        if (mMinValueOfWidthHeight == getWidth())
        {
            mCxBorder = mMinValueOfWidthHeight / 2;
            mCyBorder = getHeight() / 2;
        }
        else
        {
            mCxBorder = getWidth() / 2;
            mCyBorder = mMinValueOfWidthHeight / 2;
        }
        mRadiusBorder = mMinValueOfWidthHeight / 2;
    }
    
    private int calcWidth(int widthMeasureSpec)
    {
        int specSize = MeasureSpec.getSize(widthMeasureSpec);
        return specSize;
    }
    
    private int calcHeight(int heightMeasureSpec)
    {
        int specSize = MeasureSpec.getSize(heightMeasureSpec);
        return specSize;
    }
    
    /**
     * 计算缩放后的bitmap
     */
    
    private Bitmap calcScaleBitmap(Bitmap originalBitmap, float expectedDimension)
    {
        int width = originalBitmap.getWidth();
        int height = originalBitmap.getHeight();
        float scaleWidth = expectedDimension / width;
        float scaleHeight = expectedDimension / height;
        Matrix matrix = new Matrix();
        matrix.postScale(scaleWidth, scaleHeight);
        return Bitmap.createBitmap(originalBitmap, 0, 0, width, height, matrix, true);
    }
    
    /**
     * 裁剪圆形bitmap
     * 
     * @param scaledBitmap (缩放后的bitmap)
     * @return
     */
    private Bitmap tailorCircleBitmap(Bitmap scaledBitmap)
    {
        int width = scaledBitmap.getWidth();
        int height = scaledBitmap.getHeight();
        float roundPx;
        float left, top, right, bottom, dst_left, dst_top, dst_right, dst_bottom;
        if (width <= height)
        {
            roundPx = width / 2;
            left = 0;
            top = 0;
            right = width;
            bottom = width;
            height = width;
            dst_left = 0;
            dst_top = 0;
            dst_right = width;
            dst_bottom = width;
        }
        else
        {
            roundPx = height / 2;
            float clip = (width - height) / 2;
            left = clip;
            right = width - clip;
            top = 0;
            bottom = height;
            width = height;
            dst_left = 0;
            dst_top = 0;
            dst_right = height;
            dst_bottom = height;
        }
        
        Bitmap output = Bitmap.createBitmap(width, height, Config.ARGB_8888);
        Canvas canvas = new Canvas(output);
        
        final int color = 0xff424242;
        final Paint paint = new Paint();
        final Rect src = new Rect((int)left, (int)top, (int)right, (int)bottom);
        final Rect dst = new Rect((int)dst_left, (int)dst_top, (int)dst_right, (int)dst_bottom);
        final RectF rectF = new RectF(dst);
        
        paint.setAntiAlias(true);// 设置画笔无锯齿
        
        canvas.drawARGB(0, 0, 0, 0); // 填充整个Canvas
        paint.setColor(color);
        
        // 以下有两种方法画圆,drawRounRect和drawCircle
        // canvas.drawRoundRect(rectF, roundPx, roundPx, paint);//
        // 画圆角矩形，第一个参数为图形显示区域，第二个参数和第三个参数分别是水平圆角半径和垂直圆角半径。
        canvas.drawCircle(roundPx, roundPx, roundPx, paint);
        
        paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));// 设置两张图片相交时的模式,参考http://trylovecatch.iteye.com/blog/1189452
        canvas.drawBitmap(scaledBitmap, src, dst, paint); // 以Mode.SRC_IN模式合并bitmap和已经draw了的Circle
        
        return output;
    }
    
    /**
     * 设置图片
     * 
     * @param bitmap
     */
    public void setBitmap(Bitmap bitmap)
    {
        if (mOriginalBitmap != null && !mOriginalBitmap.isRecycled())
        {
            mOriginalBitmap.recycle();
            Log.i(TAG, "recycle:mOriginalBitmap");
        }
        mOriginalBitmap = bitmap;
        invalidate();
    }
    
    /**
     * 回收
     */
    private void recycleBitmap()
    {
        if (mCircleBitmap != null && !mCircleBitmap.isRecycled())
        {
            mCircleBitmap.recycle();
            Log.i(TAG, "recycle:mCircleBitmap");
        }
        if (scaledBitmap != null && !scaledBitmap.isRecycled())
        {
            scaledBitmap.recycle();
            Log.i(TAG, "recycle:scaledBitmap");
        }
    }
    
}
