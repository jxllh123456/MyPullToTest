package view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.drawable.Animatable;
import android.util.Log;

import com.example.summer.mypulltest.R;

/**
 * Created by summer on 16/3/11.
 */
public class SunRefreshView extends BaseDrawable implements Animatable {


      private Matrix matrix;
      private PullToRefreshView pullToRefreshView;
      private int mSunSize = 100;

      //sky的height
      private int mSkyHeight;
      //town的height
      private int mTownHeight;

      private Bitmap mSky;
      private Bitmap mTown;
      private Bitmap mSun;

      private int mTop;
      private float mPercent;


      public SunRefreshView(final PullToRefreshView pullToRefreshView) {
            this.pullToRefreshView = pullToRefreshView;
            pullToRefreshView.post(new Runnable() {
                  @Override
                  public void run() {
                        initView(pullToRefreshView.getWidth());
                  }
            });
      }

      int parentWidth;

      private void initView(int parentWidth) {
            matrix = new Matrix();
            this.parentWidth = parentWidth;
            mSkyHeight = (int) (parentWidth * 0.35f);
            mTownHeight = (int) (parentWidth * 0.35f);
           // mSkyOffset = Utils.convertDpToPixel(pullToRefreshView.getContext(), 15);
           // mTownOffset = Utils.convertDpToPixel(pullToRefreshView.getContext(), 10);

            final BitmapFactory.Options options = new BitmapFactory.Options();
            options.inPreferredConfig = Bitmap.Config.RGB_565;

            mSky = BitmapFactory.decodeResource(pullToRefreshView.getContext().getResources(), R.drawable.sky, options);
            mSky = Bitmap.createScaledBitmap(mSky, pullToRefreshView.getWidth(), mSkyHeight, true);

            mTown = BitmapFactory.decodeResource(pullToRefreshView.getContext().getResources(), R.drawable.buildings, options);
            mTown = Bitmap.createScaledBitmap(mTown, pullToRefreshView.getWidth(), mTownHeight, true);

            mSun = BitmapFactory.decodeResource(pullToRefreshView.getContext().getResources(), R.drawable.sun, options);
            mSun = Bitmap.createScaledBitmap(mSun, mSunSize, mSunSize, true);
      }

      /**
       * from super class
       *
       * @param offSet
       */
      @Override
      public void offSetTopAndBottom(int offSet) {
            mTop += offSet;
            invalidateSelf();
      }


      /**
       * from Animatable
       */
      @Override
      public void start() {

      }

      @Override
      public void stop() {

      }

      @Override
      public boolean isRunning() {
            return false;
      }


      /**
       * from drawable
       *
       * @param canvas
       */
      @Override
      public void draw(Canvas canvas) {

            final int saveCount = canvas.save();
            //mTop控制整个Drawable的移动
          //  canvas.translate(0, mTop);
            canvas.clipRect(0, 0, pullToRefreshView.getWidth(),mTop);//clipRect 决定画哪部分区域,类似于surfaceview的脏矩形
            drawSky(canvas);
            drawTown(canvas);
            drawSun(canvas);
            canvas.restoreToCount(saveCount);
      }

      float rotateDegree = 20;
      private void drawSun(Canvas canvas) {
            matrix.reset();
            //  matrix.postScale(mPercent * 0.2f, mPercent * 0.2f);
            //-(parentWidth*0.3f) 不加这个的话,drawable就跑下面去了
            matrix.postTranslate(mTop*0.3f,mTop*0.3f);//如果没有- (parentWidth * 0.35f),这个drawable就会画到下面
            rotateDegree+=10;
            matrix.postRotate(rotateDegree, 50 + mTop*0.3f,50+mTop*0.3f);//postRotate() 第一个参数是一个持续变化的值(控制旋转的速度),第二,第三个参数意思是围绕某一个点.一般是围绕中心点,因为我sun的大小是100.
        //    Log.e("rotate",);
            canvas.drawBitmap(mSun, matrix, null);
      }

      private void drawSky(Canvas canvas) {
            matrix.reset();
            // matrix.postScale(mPercent * 0.3f, mPercent * 0.3f);
           // matrix.postTranslate(0, );
            canvas.drawBitmap(mSky, matrix, null);
      }

      private void drawTown(Canvas canvas) {
            matrix.reset();
            //  matrix.postScale(mPercent * 0.5f, mPercent * 0.5f);
           // matrix.postTranslate(0,  );
            canvas.drawBitmap(mTown, matrix, null);
      }
}
