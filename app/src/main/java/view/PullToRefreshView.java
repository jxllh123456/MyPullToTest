package view;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.v4.view.ViewCompat;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Interpolator;
import android.view.animation.Transformation;
import android.widget.AbsListView;
import android.widget.ImageView;

import com.example.summer.mypulltest.R;

/**
 * Created by summer on 16/3/11.
 */
public class PullToRefreshView extends ViewGroup {

      /**
       * listview or scrollview
       */
      private View mTarget;
      private ImageView mRefreshView;
      private float mCurrentDragPercent;
      private int mCurrentOffsetTop;


      private static final float DRAG_RATE = 0.65f;
      private static final float DRAG_RATE_INSIDE = 0.3f;
      public static final int STYLE_SUN = 0;

      private static final float DECELERATE_INTERPOLATION_FACTOR = 2f;
      private static final int DRAG_MAX_DISTANCE = 180;

      private static final int ANIMATE_TO_START_DURATION = 1000;
      private int mTouchSlop;
      private int mTotalDragDistance;

      private Interpolator mDecelerateInterpolator;
      private BaseDrawable mBaseRefreshView;

      private int mTargetPaddingTop;


      private int mTargetPaddingBottom;


      private int mTargetPaddingRight;
      private int mTargetPaddingLeft;


      /**
       * being used in onTouchEvent \ onInteceptTouchEvent
       */
      private boolean mIsBeingDragged;

      public PullToRefreshView(Context context) {
            super(context);
      }

      public PullToRefreshView(Context context, AttributeSet attrs) {
            super(context, attrs);
            TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.RefreshView);
            final int type = a.getInteger(R.styleable.RefreshView_type, STYLE_SUN);
            a.recycle();
            mRefreshView = new ImageView(context);
            setRefreshViewType(type);

            addView(mRefreshView);

            // 减速篡改者
            mDecelerateInterpolator = new DecelerateInterpolator(DECELERATE_INTERPOLATION_FACTOR);
            // slop 溢出,溅出
            //Distance in pixels a touch can wander before we think the user is scrolling
            mTouchSlop = ViewConfiguration.get(context).getScaledTouchSlop();
            mTotalDragDistance = Utils.convertDpToPixel(context, DRAG_MAX_DISTANCE);

            setWillNotDraw(false);
            ViewCompat.setChildrenDrawingOrderEnabled(this, true);
      }

      private void setRefreshViewType(int type) {
            switch (type) {
                  case STYLE_SUN:
                        mBaseRefreshView = new SunRefreshView(this);
                        break;
                  default:
            }
            mRefreshView.setImageDrawable(mBaseRefreshView);
      }

      public PullToRefreshView(Context context, AttributeSet attrs, int defStyleAttr) {
            super(context, attrs, defStyleAttr);
      }

      private void init() {
      }

      @Override
      protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
            ensureTarget();
            if (mTarget == null)
                  return;
            // 在这里写EXACTLY 和 ALMOST是一回事
            widthMeasureSpec = MeasureSpec.makeMeasureSpec(getMeasuredWidth() - getPaddingRight() - getPaddingLeft(), MeasureSpec.EXACTLY);
            heightMeasureSpec = MeasureSpec.makeMeasureSpec(getMeasuredHeight() - getPaddingTop() - getPaddingBottom(), MeasureSpec.EXACTLY);
            //调用View的measure就是调用View的onMeasure ，View 的onMeasure 就是调用 setMeasuredDimesion（getDefaultSize）,这个DefaultSize方法就是从Spec中拿到size
            //所以调用这个measure就是把 Spec中的值传递给了 setMeasuredDimension
            mTarget.measure(widthMeasureSpec, heightMeasureSpec);
            mRefreshView.measure(widthMeasureSpec, heightMeasureSpec);
      }

      @Override
      protected void onLayout(boolean changed, int l, int t, int r, int b) {
            ensureTarget();
            if (mTarget == null)
                  return;
            //一个好的习惯是在onLayout的时候获得 MeasuredWidth 和height,在这里得到的width 和 height 是已经考虑过子View的padding后的。
            //但这里还没有考虑父view的padding
            int height = getMeasuredHeight();
            int width = getMeasuredWidth();
            int left = getPaddingLeft();
            int top = getPaddingTop();
            int right = getPaddingRight();
            int bottom = getPaddingBottom();
            int heightOff = (int) (width * .55f);

            //mTarget 就是要下拉刷新的ListView
            mTarget.layout(left, top, left + width - right, top + height - bottom);//这里的上\下加上 heightOff可以让mTarget在下面
            // mRefreshView是ListView上面的那个自定义Drawable
            mRefreshView.layout(left, top, left + width - right, top + height - bottom);
      }

      /**
       * this method is used to init variate of mTarget
       */
      private void ensureTarget() {
            if (mTarget != null)
                  return;
            if (getChildCount() > 0) {
                  for (int i = 0; i < getChildCount(); i++) {
                        View child = getChildAt(i);
                        if (child != mRefreshView) {


                              mTarget = child;
                              mTargetPaddingBottom = mTarget.getPaddingBottom();
                              mTargetPaddingLeft = mTarget.getPaddingLeft();
                              mTargetPaddingRight = mTarget.getPaddingRight();
                              mTargetPaddingTop = mTarget.getPaddingTop();
                        }
                  }
            }
      }

      int downX = 0;
      int downY = 0;

      @Override
      public boolean onInterceptTouchEvent(MotionEvent ev) {

            if (canChildScrollUp()) {
                  return false;
            }


            int x = (int) ev.getX();
            int y = (int) ev.getY();
            switch (ev.getAction()) {

                  case MotionEvent.ACTION_DOWN:
                        setTargetOffsetTop(0, true, false);
                        mIsBeingDragged = false;
                        downX = (int) ev.getX();
                        downY = (int) ev.getY();
                        break;
                  case MotionEvent.ACTION_MOVE:
                        //  if ((y - lasty) > mTouchSlop && !mIsBeingDragged) mIsBeingDragged = true;
                        if ((y - downY > mTouchSlop) && !mIsBeingDragged) mIsBeingDragged = true;
                        break;
                  case MotionEvent.ACTION_UP:
                        mIsBeingDragged = false;
                        break;
            }

            return mIsBeingDragged;
      }


      private int mSumY;
      private int mLastY;
      private int drawableHeight;

      @Override
      public boolean onTouchEvent(MotionEvent event) {

            int y = (int) event.getY();

            switch (event.getAction()) {

                  case MotionEvent.ACTION_DOWN:
                        break;

                  case MotionEvent.ACTION_MOVE:
                        int diffY = 0;
                        if (mLastY != 0) diffY = y - mLastY;
                        //实际上没有用到
                        mBaseRefreshView.setPercent(diffY * DRAG_RATE_INSIDE, true);
                        //改变mTop,三目运算符的意思是:如果大于一定的距离就不再能拖动
                        drawableHeight = (int) (getMeasuredWidth() * 0.35f);
                        //  Log.e("drawableHeight", drawableHeight + "::::" + diffY);
                        if (mSumY > drawableHeight) {
                              diffY = 0;
                        }
                        setTargetOffsetTop(diffY, true, false);
                        mLastY = y;
                        mSumY += diffY;
                        break;

                  case MotionEvent.ACTION_UP:
                        Log.e("mSumY",mSumY+"");
                        mLastY = 0;
                        animateToStartPosition();
                        break;

            }
            return true;
      }

      private void animateToStartPosition() {
            mAnimationToStartPosition.reset();
            mAnimationToStartPosition.setDuration(ANIMATE_TO_START_DURATION);
            mAnimationToStartPosition.setInterpolator(mDecelerateInterpolator);
            mAnimationToStartPosition.setAnimationListener(animationListener);
            mRefreshView.clearAnimation();
            mRefreshView.startAnimation(mAnimationToStartPosition);
      }

      private boolean canChildScrollUp() {
            if (android.os.Build.VERSION.SDK_INT < 14) {
                  if (mTarget instanceof AbsListView) {
                        final AbsListView absListView = (AbsListView) mTarget;
                        return absListView.getChildCount() > 0
                                              && (absListView.getFirstVisiblePosition() > 0 || absListView.getChildAt(0)
                                              .getTop() < absListView.getPaddingTop());
                  } else {
                        return mTarget.getScrollY() > 0;
                  }
            } else {
                  return ViewCompat.canScrollVertically(mTarget, -1);
            }
      }


      private void setTargetOffsetTop(int offset, boolean requiresUpdate, boolean isAnimation) {
            // offsetTopAndBottom in view view 里面的这个东西也是一个+=的过程
            mTarget.offsetTopAndBottom(offset);
            // offsetTopAndBottom(abstract method) in BaseRefreshView implements in SunRefreshView (imitate view)
            mBaseRefreshView.offSetTopAndBottom(offset);
            mCurrentOffsetTop = mTarget.getTop();
            if (requiresUpdate && android.os.Build.VERSION.SDK_INT < 11) {
                  invalidate();
            }

      }

      float lastInterpolated;
      float suma;
      private void setTargetOffsetTop(int offset, boolean requiresUpdate, boolean isAnimation,float interpolatedTime){

            float a = (interpolatedTime-lastInterpolated)*offset;

            // offsetTopAndBottom in view view 里面的这个东西也是一个+=的过程
            mTarget.offsetTopAndBottom((int)(-a));
            // offsetTopAndBottom(abstract method) in BaseRefreshView implements in SunRefreshView (imitate view)
            mBaseRefreshView.offSetTopAndBottom((int)(-a));
            mCurrentOffsetTop = mTarget.getTop();
            if (requiresUpdate && android.os.Build.VERSION.SDK_INT < 11) {
                  invalidate();
            }
            lastInterpolated = interpolatedTime;
            suma+=a;
            Log.e("a",suma+"");
      }

      public int getmTotalDragDistance() {
            return mTotalDragDistance;
      }


      /**
       * 这种形式的写法其实就是 Animation的子类
       */
      private Animation mAnimationToStartPosition = new Animation() {
            @Override
            protected void applyTransformation(float interpolatedTime, Transformation t) {
                  //  super.applyTransformation(interpolatedTime, t);
                  moveToStart(interpolatedTime);
            }
      };

      private void moveToStart(float interpolatedTime) {
          //  Log.e("taaaaaaaaag", interpolatedTime + "");
            setTargetOffsetTop((int) (mSumY * interpolatedTime), true, true,interpolatedTime);
      }


      private Animation.AnimationListener animationListener = new Animation.AnimationListener() {

            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                  mSumY = 0;
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
      };
}
