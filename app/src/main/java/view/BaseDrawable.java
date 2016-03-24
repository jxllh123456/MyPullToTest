package view;

import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.PixelFormat;
import android.graphics.drawable.Animatable;
import android.graphics.drawable.Drawable;
import android.telecom.Call;

/**
 * 实现了Animatable,但是并没有override 里面的方法
 * Created by summer on 16/3/11.
 */
public abstract class BaseDrawable extends Drawable implements Drawable.Callback, Animatable {


      public BaseDrawable() {
      }

      /**
       * frome Drawable.Callback
       *
       * @param who
       */
      @Override
      public void invalidateDrawable(Drawable who) {
            final Callback callback = getCallback();
            if (callback != null) {
                  callback.invalidateDrawable(this);
            }
      }

      @Override
      public void scheduleDrawable(Drawable who, Runnable what, long when) {
            final Callback callback = getCallback();
            if (callback != null) {
                  callback.scheduleDrawable(this, what, when);
            }
      }

      @Override
      public void unscheduleDrawable(Drawable who, Runnable what) {
            final Callback callback = getCallback();
            if (callback != null) {
                  callback.unscheduleDrawable(this, what);
            }
      }



      public abstract void setPercent(float percent,boolean invalidate);
      public abstract void offSetTopAndBottom(int offSet);




      @Override
      public void setAlpha(int alpha) {

      }

      @Override
      public void setColorFilter(ColorFilter colorFilter) {

      }

      @Override
      public int getOpacity() {
            return PixelFormat.TRANSLUCENT;
      }


}
