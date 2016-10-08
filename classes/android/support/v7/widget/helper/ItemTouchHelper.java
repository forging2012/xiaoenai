package android.support.v7.widget.helper;

import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.os.Build.VERSION;
import android.support.annotation.Nullable;
import android.support.v4.animation.AnimatorCompatHelper;
import android.support.v4.animation.AnimatorListenerCompat;
import android.support.v4.animation.ValueAnimatorCompat;
import android.support.v4.view.GestureDetectorCompat;
import android.support.v4.view.MotionEventCompat;
import android.support.v4.view.VelocityTrackerCompat;
import android.support.v4.view.ViewCompat;
import android.support.v7.b.a.a;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.ChildDrawingOrderCallback;
import android.support.v7.widget.RecyclerView.ItemAnimator;
import android.support.v7.widget.RecyclerView.ItemDecoration;
import android.support.v7.widget.RecyclerView.LayoutManager;
import android.support.v7.widget.RecyclerView.OnChildAttachStateChangeListener;
import android.support.v7.widget.RecyclerView.OnItemTouchListener;
import android.support.v7.widget.RecyclerView.State;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.util.Log;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewParent;
import android.view.animation.Interpolator;
import java.util.ArrayList;
import java.util.List;

public class ItemTouchHelper
  extends RecyclerView.ItemDecoration
  implements RecyclerView.OnChildAttachStateChangeListener
{
  private static final int ACTION_MODE_DRAG_MASK = 16711680;
  private static final int ACTION_MODE_IDLE_MASK = 255;
  private static final int ACTION_MODE_SWIPE_MASK = 65280;
  public static final int ACTION_STATE_DRAG = 2;
  public static final int ACTION_STATE_IDLE = 0;
  public static final int ACTION_STATE_SWIPE = 1;
  private static final int ACTIVE_POINTER_ID_NONE = -1;
  public static final int ANIMATION_TYPE_DRAG = 8;
  public static final int ANIMATION_TYPE_SWIPE_CANCEL = 4;
  public static final int ANIMATION_TYPE_SWIPE_SUCCESS = 2;
  private static final boolean DEBUG = false;
  private static final int DIRECTION_FLAG_COUNT = 8;
  public static final int DOWN = 2;
  public static final int END = 32;
  public static final int LEFT = 4;
  private static final int PIXELS_PER_SECOND = 1000;
  public static final int RIGHT = 8;
  public static final int START = 16;
  private static final String TAG = "ItemTouchHelper";
  public static final int UP = 1;
  int mActionState = 0;
  int mActivePointerId = -1;
  Callback mCallback;
  private RecyclerView.ChildDrawingOrderCallback mChildDrawingOrderCallback = null;
  private List<Integer> mDistances;
  private long mDragScrollStartTimeInMs;
  float mDx;
  float mDy;
  private GestureDetectorCompat mGestureDetector;
  float mInitialTouchX;
  float mInitialTouchY;
  float mMaxSwipeVelocity;
  private final RecyclerView.OnItemTouchListener mOnItemTouchListener = new b(this);
  private View mOverdrawChild = null;
  private int mOverdrawChildPosition = -1;
  final List<View> mPendingCleanup = new ArrayList();
  List<b> mRecoverAnimations = new ArrayList();
  private RecyclerView mRecyclerView;
  private final Runnable mScrollRunnable = new a(this);
  RecyclerView.ViewHolder mSelected = null;
  int mSelectedFlags;
  float mSelectedStartX;
  float mSelectedStartY;
  private int mSlop;
  private List<RecyclerView.ViewHolder> mSwapTargets;
  float mSwipeEscapeVelocity;
  private final float[] mTmpPosition = new float[2];
  private Rect mTmpRect;
  private VelocityTracker mVelocityTracker;
  
  public ItemTouchHelper(Callback paramCallback)
  {
    this.mCallback = paramCallback;
  }
  
  private void addChildDrawingOrderCallback()
  {
    if (Build.VERSION.SDK_INT >= 21) {
      return;
    }
    if (this.mChildDrawingOrderCallback == null) {
      this.mChildDrawingOrderCallback = new e(this);
    }
    this.mRecyclerView.setChildDrawingOrderCallback(this.mChildDrawingOrderCallback);
  }
  
  private int checkHorizontalSwipe(RecyclerView.ViewHolder paramViewHolder, int paramInt)
  {
    int j = 8;
    if ((paramInt & 0xC) != 0)
    {
      int i;
      if (this.mDx > 0.0F)
      {
        i = 8;
        if ((this.mVelocityTracker == null) || (this.mActivePointerId <= -1)) {
          break label155;
        }
        this.mVelocityTracker.computeCurrentVelocity(1000, this.mCallback.getSwipeVelocityThreshold(this.mMaxSwipeVelocity));
        f2 = VelocityTrackerCompat.getXVelocity(this.mVelocityTracker, this.mActivePointerId);
        f1 = VelocityTrackerCompat.getYVelocity(this.mVelocityTracker, this.mActivePointerId);
        if (f2 <= 0.0F) {
          break label149;
        }
      }
      for (;;)
      {
        f2 = Math.abs(f2);
        if (((j & paramInt) == 0) || (i != j) || (f2 < this.mCallback.getSwipeEscapeVelocity(this.mSwipeEscapeVelocity)) || (f2 <= Math.abs(f1))) {
          break label155;
        }
        return j;
        i = 4;
        break;
        label149:
        j = 4;
      }
      label155:
      float f1 = this.mRecyclerView.getWidth();
      float f2 = this.mCallback.getSwipeThreshold(paramViewHolder);
      if (((paramInt & i) != 0) && (Math.abs(this.mDx) > f1 * f2)) {
        return i;
      }
    }
    return 0;
  }
  
  private boolean checkSelectForSwipe(int paramInt1, MotionEvent paramMotionEvent, int paramInt2)
  {
    if ((this.mSelected != null) || (paramInt1 != 2) || (this.mActionState == 2) || (!this.mCallback.isItemViewSwipeEnabled())) {}
    RecyclerView.ViewHolder localViewHolder;
    float f1;
    float f2;
    do
    {
      float f3;
      float f4;
      do
      {
        do
        {
          do
          {
            do
            {
              return false;
            } while (this.mRecyclerView.getScrollState() == 1);
            localViewHolder = findSwipedView(paramMotionEvent);
          } while (localViewHolder == null);
          paramInt1 = (this.mCallback.getAbsoluteMovementFlags(this.mRecyclerView, localViewHolder) & 0xFF00) >> 8;
        } while (paramInt1 == 0);
        f1 = MotionEventCompat.getX(paramMotionEvent, paramInt2);
        f2 = MotionEventCompat.getY(paramMotionEvent, paramInt2);
        f1 -= this.mInitialTouchX;
        f2 -= this.mInitialTouchY;
        f3 = Math.abs(f1);
        f4 = Math.abs(f2);
      } while ((f3 < this.mSlop) && (f4 < this.mSlop));
      if (f3 <= f4) {
        break;
      }
    } while (((f1 < 0.0F) && ((paramInt1 & 0x4) == 0)) || ((f1 > 0.0F) && ((paramInt1 & 0x8) == 0)));
    do
    {
      this.mDy = 0.0F;
      this.mDx = 0.0F;
      this.mActivePointerId = MotionEventCompat.getPointerId(paramMotionEvent, 0);
      select(localViewHolder, 1);
      return true;
      if ((f2 < 0.0F) && ((paramInt1 & 0x1) == 0)) {
        break;
      }
    } while ((f2 <= 0.0F) || ((paramInt1 & 0x2) != 0));
    return false;
  }
  
  private int checkVerticalSwipe(RecyclerView.ViewHolder paramViewHolder, int paramInt)
  {
    int j = 2;
    if ((paramInt & 0x3) != 0)
    {
      int i;
      if (this.mDy > 0.0F)
      {
        i = 2;
        if ((this.mVelocityTracker == null) || (this.mActivePointerId <= -1)) {
          break label152;
        }
        this.mVelocityTracker.computeCurrentVelocity(1000, this.mCallback.getSwipeVelocityThreshold(this.mMaxSwipeVelocity));
        f1 = VelocityTrackerCompat.getXVelocity(this.mVelocityTracker, this.mActivePointerId);
        f2 = VelocityTrackerCompat.getYVelocity(this.mVelocityTracker, this.mActivePointerId);
        if (f2 <= 0.0F) {
          break label146;
        }
      }
      for (;;)
      {
        f2 = Math.abs(f2);
        if (((j & paramInt) == 0) || (j != i) || (f2 < this.mCallback.getSwipeEscapeVelocity(this.mSwipeEscapeVelocity)) || (f2 <= Math.abs(f1))) {
          break label152;
        }
        return j;
        i = 1;
        break;
        label146:
        j = 1;
      }
      label152:
      float f1 = this.mRecyclerView.getHeight();
      float f2 = this.mCallback.getSwipeThreshold(paramViewHolder);
      if (((paramInt & i) != 0) && (Math.abs(this.mDy) > f1 * f2)) {
        return i;
      }
    }
    return 0;
  }
  
  private void destroyCallbacks()
  {
    this.mRecyclerView.removeItemDecoration(this);
    this.mRecyclerView.removeOnItemTouchListener(this.mOnItemTouchListener);
    this.mRecyclerView.removeOnChildAttachStateChangeListener(this);
    int i = this.mRecoverAnimations.size() - 1;
    while (i >= 0)
    {
      b localb = (b)this.mRecoverAnimations.get(0);
      this.mCallback.clearView(this.mRecyclerView, localb.h);
      i -= 1;
    }
    this.mRecoverAnimations.clear();
    this.mOverdrawChild = null;
    this.mOverdrawChildPosition = -1;
    releaseVelocityTracker();
  }
  
  private int endRecoverAnimation(RecyclerView.ViewHolder paramViewHolder, boolean paramBoolean)
  {
    int i = this.mRecoverAnimations.size() - 1;
    while (i >= 0)
    {
      b localb = (b)this.mRecoverAnimations.get(i);
      if (localb.h == paramViewHolder)
      {
        localb.m |= paramBoolean;
        if (!b.a(localb)) {
          localb.b();
        }
        this.mRecoverAnimations.remove(i);
        return b.b(localb);
      }
      i -= 1;
    }
    return 0;
  }
  
  private b findAnimation(MotionEvent paramMotionEvent)
  {
    if (this.mRecoverAnimations.isEmpty())
    {
      paramMotionEvent = null;
      return paramMotionEvent;
    }
    View localView = findChildView(paramMotionEvent);
    int i = this.mRecoverAnimations.size() - 1;
    for (;;)
    {
      if (i < 0) {
        break label77;
      }
      b localb = (b)this.mRecoverAnimations.get(i);
      paramMotionEvent = localb;
      if (localb.h.itemView == localView) {
        break;
      }
      i -= 1;
    }
    label77:
    return null;
  }
  
  private View findChildView(MotionEvent paramMotionEvent)
  {
    float f1 = paramMotionEvent.getX();
    float f2 = paramMotionEvent.getY();
    if (this.mSelected != null)
    {
      paramMotionEvent = this.mSelected.itemView;
      if (hitTest(paramMotionEvent, f1, f2, this.mSelectedStartX + this.mDx, this.mSelectedStartY + this.mDy)) {
        return paramMotionEvent;
      }
    }
    int i = this.mRecoverAnimations.size() - 1;
    while (i >= 0)
    {
      paramMotionEvent = (b)this.mRecoverAnimations.get(i);
      View localView = paramMotionEvent.h.itemView;
      if (hitTest(localView, f1, f2, paramMotionEvent.k, paramMotionEvent.l)) {
        return localView;
      }
      i -= 1;
    }
    return this.mRecyclerView.findChildViewUnder(f1, f2);
  }
  
  private List<RecyclerView.ViewHolder> findSwapTargets(RecyclerView.ViewHolder paramViewHolder)
  {
    int i;
    int m;
    int n;
    int i1;
    int i2;
    int i3;
    int i4;
    label141:
    View localView;
    if (this.mSwapTargets == null)
    {
      this.mSwapTargets = new ArrayList();
      this.mDistances = new ArrayList();
      i = this.mCallback.getBoundingBoxMargin();
      m = Math.round(this.mSelectedStartX + this.mDx) - i;
      n = Math.round(this.mSelectedStartY + this.mDy) - i;
      i1 = paramViewHolder.itemView.getWidth() + m + i * 2;
      i2 = paramViewHolder.itemView.getHeight() + n + i * 2;
      i3 = (m + i1) / 2;
      i4 = (n + i2) / 2;
      RecyclerView.LayoutManager localLayoutManager = this.mRecyclerView.getLayoutManager();
      int i5 = localLayoutManager.getChildCount();
      i = 0;
      if (i >= i5) {
        break label407;
      }
      localView = localLayoutManager.getChildAt(i);
      if (localView != paramViewHolder.itemView) {
        break label193;
      }
    }
    for (;;)
    {
      i += 1;
      break label141;
      this.mSwapTargets.clear();
      this.mDistances.clear();
      break;
      label193:
      if ((localView.getBottom() >= n) && (localView.getTop() <= i2) && (localView.getRight() >= m) && (localView.getLeft() <= i1))
      {
        RecyclerView.ViewHolder localViewHolder = this.mRecyclerView.getChildViewHolder(localView);
        if (this.mCallback.canDropOver(this.mRecyclerView, this.mSelected, localViewHolder))
        {
          int j = Math.abs(i3 - (localView.getLeft() + localView.getRight()) / 2);
          int k = localView.getTop();
          k = Math.abs(i4 - (localView.getBottom() + k) / 2);
          int i6 = j * j + k * k;
          int i7 = this.mSwapTargets.size();
          k = 0;
          j = 0;
          while ((j < i7) && (i6 > ((Integer)this.mDistances.get(j)).intValue()))
          {
            k += 1;
            j += 1;
          }
          this.mSwapTargets.add(k, localViewHolder);
          this.mDistances.add(k, Integer.valueOf(i6));
        }
      }
    }
    label407:
    return this.mSwapTargets;
  }
  
  private RecyclerView.ViewHolder findSwipedView(MotionEvent paramMotionEvent)
  {
    RecyclerView.LayoutManager localLayoutManager = this.mRecyclerView.getLayoutManager();
    if (this.mActivePointerId == -1) {}
    do
    {
      float f3;
      float f1;
      do
      {
        return null;
        int i = MotionEventCompat.findPointerIndex(paramMotionEvent, this.mActivePointerId);
        f3 = MotionEventCompat.getX(paramMotionEvent, i);
        float f4 = this.mInitialTouchX;
        f1 = MotionEventCompat.getY(paramMotionEvent, i);
        float f2 = this.mInitialTouchY;
        f3 = Math.abs(f3 - f4);
        f1 = Math.abs(f1 - f2);
      } while (((f3 < this.mSlop) && (f1 < this.mSlop)) || ((f3 > f1) && (localLayoutManager.canScrollHorizontally())) || ((f1 > f3) && (localLayoutManager.canScrollVertically())));
      paramMotionEvent = findChildView(paramMotionEvent);
    } while (paramMotionEvent == null);
    return this.mRecyclerView.getChildViewHolder(paramMotionEvent);
  }
  
  private void getSelectedDxDy(float[] paramArrayOfFloat)
  {
    if ((this.mSelectedFlags & 0xC) != 0) {
      paramArrayOfFloat[0] = (this.mSelectedStartX + this.mDx - this.mSelected.itemView.getLeft());
    }
    while ((this.mSelectedFlags & 0x3) != 0)
    {
      paramArrayOfFloat[1] = (this.mSelectedStartY + this.mDy - this.mSelected.itemView.getTop());
      return;
      paramArrayOfFloat[0] = ViewCompat.getTranslationX(this.mSelected.itemView);
    }
    paramArrayOfFloat[1] = ViewCompat.getTranslationY(this.mSelected.itemView);
  }
  
  private boolean hasRunningRecoverAnim()
  {
    int j = this.mRecoverAnimations.size();
    int i = 0;
    while (i < j)
    {
      if (!b.a((b)this.mRecoverAnimations.get(i))) {
        return true;
      }
      i += 1;
    }
    return false;
  }
  
  private static boolean hitTest(View paramView, float paramFloat1, float paramFloat2, float paramFloat3, float paramFloat4)
  {
    return (paramFloat1 >= paramFloat3) && (paramFloat1 <= paramView.getWidth() + paramFloat3) && (paramFloat2 >= paramFloat4) && (paramFloat2 <= paramView.getHeight() + paramFloat4);
  }
  
  private void initGestureDetector()
  {
    if (this.mGestureDetector != null) {
      return;
    }
    this.mGestureDetector = new GestureDetectorCompat(this.mRecyclerView.getContext(), new a(null));
  }
  
  private void moveIfNecessary(RecyclerView.ViewHolder paramViewHolder)
  {
    if (this.mRecyclerView.isLayoutRequested()) {}
    label10:
    int i;
    int j;
    Object localObject;
    int k;
    int m;
    do
    {
      do
      {
        float f;
        do
        {
          break label10;
          do
          {
            return;
          } while (this.mActionState != 2);
          f = this.mCallback.getMoveThreshold(paramViewHolder);
          i = (int)(this.mSelectedStartX + this.mDx);
          j = (int)(this.mSelectedStartY + this.mDy);
        } while ((Math.abs(j - paramViewHolder.itemView.getTop()) < paramViewHolder.itemView.getHeight() * f) && (Math.abs(i - paramViewHolder.itemView.getLeft()) < f * paramViewHolder.itemView.getWidth()));
        localObject = findSwapTargets(paramViewHolder);
      } while (((List)localObject).size() == 0);
      localObject = this.mCallback.chooseDropTarget(paramViewHolder, (List)localObject, i, j);
      if (localObject == null)
      {
        this.mSwapTargets.clear();
        this.mDistances.clear();
        return;
      }
      k = ((RecyclerView.ViewHolder)localObject).getAdapterPosition();
      m = paramViewHolder.getAdapterPosition();
    } while (!this.mCallback.onMove(this.mRecyclerView, paramViewHolder, (RecyclerView.ViewHolder)localObject));
    this.mCallback.onMoved(this.mRecyclerView, paramViewHolder, m, (RecyclerView.ViewHolder)localObject, k, i, j);
  }
  
  private void obtainVelocityTracker()
  {
    if (this.mVelocityTracker != null) {
      this.mVelocityTracker.recycle();
    }
    this.mVelocityTracker = VelocityTracker.obtain();
  }
  
  private void postDispatchSwipe(b paramb, int paramInt)
  {
    this.mRecyclerView.post(new d(this, paramb, paramInt));
  }
  
  private void releaseVelocityTracker()
  {
    if (this.mVelocityTracker != null)
    {
      this.mVelocityTracker.recycle();
      this.mVelocityTracker = null;
    }
  }
  
  private void removeChildDrawingOrderCallbackIfNecessary(View paramView)
  {
    if (paramView == this.mOverdrawChild)
    {
      this.mOverdrawChild = null;
      if (this.mChildDrawingOrderCallback != null) {
        this.mRecyclerView.setChildDrawingOrderCallback(null);
      }
    }
  }
  
  private boolean scrollIfNecessary()
  {
    if (this.mSelected == null)
    {
      this.mDragScrollStartTimeInMs = Long.MIN_VALUE;
      return false;
    }
    long l2 = System.currentTimeMillis();
    long l1;
    int j;
    int i;
    label125:
    int k;
    if (this.mDragScrollStartTimeInMs == Long.MIN_VALUE)
    {
      l1 = 0L;
      RecyclerView.LayoutManager localLayoutManager = this.mRecyclerView.getLayoutManager();
      if (this.mTmpRect == null) {
        this.mTmpRect = new Rect();
      }
      localLayoutManager.calculateItemDecorationsForChild(this.mSelected.itemView, this.mTmpRect);
      if (!localLayoutManager.canScrollHorizontally()) {
        break label348;
      }
      j = (int)(this.mSelectedStartX + this.mDx);
      i = j - this.mTmpRect.left - this.mRecyclerView.getPaddingLeft();
      if ((this.mDx >= 0.0F) || (i >= 0)) {
        break label296;
      }
      if (!localLayoutManager.canScrollVertically()) {
        break label409;
      }
      k = (int)(this.mSelectedStartY + this.mDy);
      j = k - this.mTmpRect.top - this.mRecyclerView.getPaddingTop();
      if ((this.mDy >= 0.0F) || (j >= 0)) {
        break label353;
      }
      label176:
      if (i == 0) {
        break label426;
      }
      i = this.mCallback.interpolateOutOfBoundsScroll(this.mRecyclerView, this.mSelected.itemView.getWidth(), i, this.mRecyclerView.getWidth(), l1);
    }
    label296:
    label348:
    label353:
    label409:
    label426:
    for (;;)
    {
      if (j != 0) {
        j = this.mCallback.interpolateOutOfBoundsScroll(this.mRecyclerView, this.mSelected.itemView.getHeight(), j, this.mRecyclerView.getHeight(), l1);
      }
      for (;;)
      {
        if ((i != 0) || (j != 0))
        {
          if (this.mDragScrollStartTimeInMs == Long.MIN_VALUE) {
            this.mDragScrollStartTimeInMs = l2;
          }
          this.mRecyclerView.scrollBy(i, j);
          return true;
          l1 = l2 - this.mDragScrollStartTimeInMs;
          break;
          if (this.mDx > 0.0F)
          {
            j = j + this.mSelected.itemView.getWidth() + this.mTmpRect.right - (this.mRecyclerView.getWidth() - this.mRecyclerView.getPaddingRight());
            i = j;
            if (j > 0) {
              break label125;
            }
          }
          i = 0;
          break label125;
          if (this.mDy > 0.0F)
          {
            k = k + this.mSelected.itemView.getHeight() + this.mTmpRect.bottom - (this.mRecyclerView.getHeight() - this.mRecyclerView.getPaddingBottom());
            j = k;
            if (k > 0) {
              break label176;
            }
          }
          j = 0;
          break label176;
        }
        this.mDragScrollStartTimeInMs = Long.MIN_VALUE;
        return false;
      }
    }
  }
  
  private void select(RecyclerView.ViewHolder paramViewHolder, int paramInt)
  {
    if ((paramViewHolder == this.mSelected) && (paramInt == this.mActionState)) {
      return;
    }
    this.mDragScrollStartTimeInMs = Long.MIN_VALUE;
    int k = this.mActionState;
    endRecoverAnimation(paramViewHolder, true);
    this.mActionState = paramInt;
    if (paramInt == 2)
    {
      this.mOverdrawChild = paramViewHolder.itemView;
      addChildDrawingOrderCallback();
    }
    int i = 0;
    int j = 0;
    Object localObject;
    float f1;
    float f2;
    if (this.mSelected != null)
    {
      localObject = this.mSelected;
      if (((RecyclerView.ViewHolder)localObject).itemView.getParent() == null) {
        break label510;
      }
      if (k == 2)
      {
        j = 0;
        releaseVelocityTracker();
      }
    }
    else
    {
      switch (j)
      {
      default: 
        f1 = 0.0F;
        f2 = 0.0F;
        label169:
        if (k == 2)
        {
          i = 8;
          label179:
          getSelectedDxDy(this.mTmpPosition);
          float f3 = this.mTmpPosition[0];
          float f4 = this.mTmpPosition[1];
          localObject = new c(this, (RecyclerView.ViewHolder)localObject, i, k, f3, f4, f1, f2, j, (RecyclerView.ViewHolder)localObject);
          ((b)localObject).a(this.mCallback.getAnimationDuration(this.mRecyclerView, i, f1 - f3, f2 - f4));
          this.mRecoverAnimations.add(localObject);
          ((b)localObject).a();
          i = 1;
          label277:
          this.mSelected = null;
          if (paramViewHolder != null)
          {
            this.mSelectedFlags = ((this.mCallback.getAbsoluteMovementFlags(this.mRecyclerView, paramViewHolder) & (1 << paramInt * 8 + 8) - 1) >> this.mActionState * 8);
            this.mSelectedStartX = paramViewHolder.itemView.getLeft();
            this.mSelectedStartY = paramViewHolder.itemView.getTop();
            this.mSelected = paramViewHolder;
            if (paramInt == 2) {
              this.mSelected.itemView.performHapticFeedback(0);
            }
          }
          paramViewHolder = this.mRecyclerView.getParent();
          if (paramViewHolder != null) {
            if (this.mSelected == null) {
              break label539;
            }
          }
        }
        break;
      }
    }
    label510:
    label539:
    for (boolean bool = true;; bool = false)
    {
      paramViewHolder.requestDisallowInterceptTouchEvent(bool);
      if (i == 0) {
        this.mRecyclerView.getLayoutManager().requestSimpleAnimationsInNextLayout();
      }
      this.mCallback.onSelectedChanged(this.mSelected, this.mActionState);
      this.mRecyclerView.invalidate();
      return;
      j = swipeIfNecessary((RecyclerView.ViewHolder)localObject);
      break;
      f2 = 0.0F;
      f1 = Math.signum(this.mDx) * this.mRecyclerView.getWidth();
      break label169;
      f1 = 0.0F;
      f2 = Math.signum(this.mDy) * this.mRecyclerView.getHeight();
      break label169;
      if (j > 0)
      {
        i = 2;
        break label179;
      }
      i = 4;
      break label179;
      removeChildDrawingOrderCallbackIfNecessary(((RecyclerView.ViewHolder)localObject).itemView);
      this.mCallback.clearView(this.mRecyclerView, (RecyclerView.ViewHolder)localObject);
      i = j;
      break label277;
    }
  }
  
  private void setupCallbacks()
  {
    this.mSlop = ViewConfiguration.get(this.mRecyclerView.getContext()).getScaledTouchSlop();
    this.mRecyclerView.addItemDecoration(this);
    this.mRecyclerView.addOnItemTouchListener(this.mOnItemTouchListener);
    this.mRecyclerView.addOnChildAttachStateChangeListener(this);
    initGestureDetector();
  }
  
  private int swipeIfNecessary(RecyclerView.ViewHolder paramViewHolder)
  {
    if (this.mActionState == 2) {}
    int j;
    int i;
    do
    {
      do
      {
        do
        {
          return 0;
          j = this.mCallback.getMovementFlags(this.mRecyclerView, paramViewHolder);
          i = (this.mCallback.convertToAbsoluteDirection(j, ViewCompat.getLayoutDirection(this.mRecyclerView)) & 0xFF00) >> 8;
        } while (i == 0);
        j = (j & 0xFF00) >> 8;
        if (Math.abs(this.mDx) <= Math.abs(this.mDy)) {
          break;
        }
        k = checkHorizontalSwipe(paramViewHolder, i);
        if (k > 0)
        {
          if ((j & k) == 0) {
            return Callback.convertToRelativeDirection(k, ViewCompat.getLayoutDirection(this.mRecyclerView));
          }
          return k;
        }
        i = checkVerticalSwipe(paramViewHolder, i);
      } while (i <= 0);
      return i;
      int k = checkVerticalSwipe(paramViewHolder, i);
      if (k > 0) {
        return k;
      }
      i = checkHorizontalSwipe(paramViewHolder, i);
    } while (i <= 0);
    if ((j & i) == 0) {
      return Callback.convertToRelativeDirection(i, ViewCompat.getLayoutDirection(this.mRecyclerView));
    }
    return i;
  }
  
  private void updateDxDy(MotionEvent paramMotionEvent, int paramInt1, int paramInt2)
  {
    float f1 = MotionEventCompat.getX(paramMotionEvent, paramInt2);
    float f2 = MotionEventCompat.getY(paramMotionEvent, paramInt2);
    this.mDx = (f1 - this.mInitialTouchX);
    this.mDy = (f2 - this.mInitialTouchY);
    if ((paramInt1 & 0x4) == 0) {
      this.mDx = Math.max(0.0F, this.mDx);
    }
    if ((paramInt1 & 0x8) == 0) {
      this.mDx = Math.min(0.0F, this.mDx);
    }
    if ((paramInt1 & 0x1) == 0) {
      this.mDy = Math.max(0.0F, this.mDy);
    }
    if ((paramInt1 & 0x2) == 0) {
      this.mDy = Math.min(0.0F, this.mDy);
    }
  }
  
  public void attachToRecyclerView(@Nullable RecyclerView paramRecyclerView)
  {
    if (this.mRecyclerView == paramRecyclerView) {}
    do
    {
      return;
      if (this.mRecyclerView != null) {
        destroyCallbacks();
      }
      this.mRecyclerView = paramRecyclerView;
    } while (this.mRecyclerView == null);
    paramRecyclerView = paramRecyclerView.getResources();
    this.mSwipeEscapeVelocity = paramRecyclerView.getDimension(a.a.item_touch_helper_swipe_escape_velocity);
    this.mMaxSwipeVelocity = paramRecyclerView.getDimension(a.a.item_touch_helper_swipe_escape_max_velocity);
    setupCallbacks();
  }
  
  public void getItemOffsets(Rect paramRect, View paramView, RecyclerView paramRecyclerView, RecyclerView.State paramState)
  {
    paramRect.setEmpty();
  }
  
  public void onChildViewAttachedToWindow(View paramView) {}
  
  public void onChildViewDetachedFromWindow(View paramView)
  {
    removeChildDrawingOrderCallbackIfNecessary(paramView);
    paramView = this.mRecyclerView.getChildViewHolder(paramView);
    if (paramView == null) {}
    do
    {
      return;
      if ((this.mSelected != null) && (paramView == this.mSelected))
      {
        select(null, 0);
        return;
      }
      endRecoverAnimation(paramView, false);
    } while (!this.mPendingCleanup.remove(paramView.itemView));
    this.mCallback.clearView(this.mRecyclerView, paramView);
  }
  
  public void onDraw(Canvas paramCanvas, RecyclerView paramRecyclerView, RecyclerView.State paramState)
  {
    float f2 = 0.0F;
    this.mOverdrawChildPosition = -1;
    float f1;
    if (this.mSelected != null)
    {
      getSelectedDxDy(this.mTmpPosition);
      f1 = this.mTmpPosition[0];
      f2 = this.mTmpPosition[1];
    }
    for (;;)
    {
      this.mCallback.onDraw(paramCanvas, paramRecyclerView, this.mSelected, this.mRecoverAnimations, this.mActionState, f1, f2);
      return;
      f1 = 0.0F;
    }
  }
  
  public void onDrawOver(Canvas paramCanvas, RecyclerView paramRecyclerView, RecyclerView.State paramState)
  {
    float f2 = 0.0F;
    float f1;
    if (this.mSelected != null)
    {
      getSelectedDxDy(this.mTmpPosition);
      f1 = this.mTmpPosition[0];
      f2 = this.mTmpPosition[1];
    }
    for (;;)
    {
      this.mCallback.onDrawOver(paramCanvas, paramRecyclerView, this.mSelected, this.mRecoverAnimations, this.mActionState, f1, f2);
      return;
      f1 = 0.0F;
    }
  }
  
  public void startDrag(RecyclerView.ViewHolder paramViewHolder)
  {
    if (!this.mCallback.hasDragFlag(this.mRecyclerView, paramViewHolder))
    {
      Log.e("ItemTouchHelper", "Start drag has been called but swiping is not enabled");
      return;
    }
    if (paramViewHolder.itemView.getParent() != this.mRecyclerView)
    {
      Log.e("ItemTouchHelper", "Start drag has been called with a view holder which is not a child of the RecyclerView which is controlled by this ItemTouchHelper.");
      return;
    }
    obtainVelocityTracker();
    this.mDy = 0.0F;
    this.mDx = 0.0F;
    select(paramViewHolder, 2);
  }
  
  public void startSwipe(RecyclerView.ViewHolder paramViewHolder)
  {
    if (!this.mCallback.hasSwipeFlag(this.mRecyclerView, paramViewHolder))
    {
      Log.e("ItemTouchHelper", "Start swipe has been called but dragging is not enabled");
      return;
    }
    if (paramViewHolder.itemView.getParent() != this.mRecyclerView)
    {
      Log.e("ItemTouchHelper", "Start swipe has been called with a view holder which is not a child of the RecyclerView controlled by this ItemTouchHelper.");
      return;
    }
    obtainVelocityTracker();
    this.mDy = 0.0F;
    this.mDx = 0.0F;
    select(paramViewHolder, 1);
  }
  
  public static abstract class Callback
  {
    private static final int ABS_HORIZONTAL_DIR_FLAGS = 789516;
    public static final int DEFAULT_DRAG_ANIMATION_DURATION = 200;
    public static final int DEFAULT_SWIPE_ANIMATION_DURATION = 250;
    private static final long DRAG_SCROLL_ACCELERATION_LIMIT_TIME_MS = 2000L;
    static final int RELATIVE_DIR_FLAGS = 3158064;
    private static final Interpolator sDragScrollInterpolator = new f();
    private static final Interpolator sDragViewScrollCapInterpolator = new g();
    private static final ItemTouchUIUtil sUICallback = new i.a();
    private int mCachedMaxScrollSpeed = -1;
    
    static
    {
      if (Build.VERSION.SDK_INT >= 21)
      {
        sUICallback = new i.c();
        return;
      }
      if (Build.VERSION.SDK_INT >= 11)
      {
        sUICallback = new i.b();
        return;
      }
    }
    
    public static int convertToRelativeDirection(int paramInt1, int paramInt2)
    {
      int i = paramInt1 & 0xC0C0C;
      if (i == 0) {
        return paramInt1;
      }
      paramInt1 = (i ^ 0xFFFFFFFF) & paramInt1;
      if (paramInt2 == 0) {
        return paramInt1 | i << 2;
      }
      return paramInt1 | i << 1 & 0xFFF3F3F3 | (i << 1 & 0xC0C0C) << 2;
    }
    
    public static ItemTouchUIUtil getDefaultUIUtil()
    {
      return sUICallback;
    }
    
    private int getMaxDragScroll(RecyclerView paramRecyclerView)
    {
      if (this.mCachedMaxScrollSpeed == -1) {
        this.mCachedMaxScrollSpeed = paramRecyclerView.getResources().getDimensionPixelSize(a.a.item_touch_helper_max_drag_scroll_per_frame);
      }
      return this.mCachedMaxScrollSpeed;
    }
    
    private boolean hasDragFlag(RecyclerView paramRecyclerView, RecyclerView.ViewHolder paramViewHolder)
    {
      return (getAbsoluteMovementFlags(paramRecyclerView, paramViewHolder) & 0xFF0000) != 0;
    }
    
    private boolean hasSwipeFlag(RecyclerView paramRecyclerView, RecyclerView.ViewHolder paramViewHolder)
    {
      return (getAbsoluteMovementFlags(paramRecyclerView, paramViewHolder) & 0xFF00) != 0;
    }
    
    public static int makeFlag(int paramInt1, int paramInt2)
    {
      return paramInt2 << paramInt1 * 8;
    }
    
    public static int makeMovementFlags(int paramInt1, int paramInt2)
    {
      return makeFlag(0, paramInt2 | paramInt1) | makeFlag(1, paramInt2) | makeFlag(2, paramInt1);
    }
    
    private void onDraw(Canvas paramCanvas, RecyclerView paramRecyclerView, RecyclerView.ViewHolder paramViewHolder, List<ItemTouchHelper.b> paramList, int paramInt, float paramFloat1, float paramFloat2)
    {
      int j = paramList.size();
      int i = 0;
      while (i < j)
      {
        ItemTouchHelper.b localb = (ItemTouchHelper.b)paramList.get(i);
        localb.c();
        int k = paramCanvas.save();
        onChildDraw(paramCanvas, paramRecyclerView, localb.h, localb.k, localb.l, localb.i, false);
        paramCanvas.restoreToCount(k);
        i += 1;
      }
      if (paramViewHolder != null)
      {
        i = paramCanvas.save();
        onChildDraw(paramCanvas, paramRecyclerView, paramViewHolder, paramFloat1, paramFloat2, paramInt, true);
        paramCanvas.restoreToCount(i);
      }
    }
    
    private void onDrawOver(Canvas paramCanvas, RecyclerView paramRecyclerView, RecyclerView.ViewHolder paramViewHolder, List<ItemTouchHelper.b> paramList, int paramInt, float paramFloat1, float paramFloat2)
    {
      int j = paramList.size();
      int i = 0;
      while (i < j)
      {
        ItemTouchHelper.b localb = (ItemTouchHelper.b)paramList.get(i);
        int k = paramCanvas.save();
        onChildDrawOver(paramCanvas, paramRecyclerView, localb.h, localb.k, localb.l, localb.i, false);
        paramCanvas.restoreToCount(k);
        i += 1;
      }
      if (paramViewHolder != null)
      {
        i = paramCanvas.save();
        onChildDrawOver(paramCanvas, paramRecyclerView, paramViewHolder, paramFloat1, paramFloat2, paramInt, true);
        paramCanvas.restoreToCount(i);
      }
      paramInt = 0;
      i = j - 1;
      if (i >= 0)
      {
        paramCanvas = (ItemTouchHelper.b)paramList.get(i);
        if ((ItemTouchHelper.b.a(paramCanvas)) && (!paramCanvas.j)) {
          paramList.remove(i);
        }
      }
      for (;;)
      {
        i -= 1;
        break;
        if (!ItemTouchHelper.b.a(paramCanvas))
        {
          paramInt = 1;
          continue;
          if (paramInt != 0) {
            paramRecyclerView.invalidate();
          }
          return;
        }
      }
    }
    
    public boolean canDropOver(RecyclerView paramRecyclerView, RecyclerView.ViewHolder paramViewHolder1, RecyclerView.ViewHolder paramViewHolder2)
    {
      return true;
    }
    
    public RecyclerView.ViewHolder chooseDropTarget(RecyclerView.ViewHolder paramViewHolder, List<RecyclerView.ViewHolder> paramList, int paramInt1, int paramInt2)
    {
      int m = paramViewHolder.itemView.getWidth();
      int n = paramViewHolder.itemView.getHeight();
      Object localObject2 = null;
      int i = -1;
      int i1 = paramInt1 - paramViewHolder.itemView.getLeft();
      int i2 = paramInt2 - paramViewHolder.itemView.getTop();
      int i3 = paramList.size();
      int j = 0;
      Object localObject1;
      int k;
      if (j < i3)
      {
        localObject1 = (RecyclerView.ViewHolder)paramList.get(j);
        if (i1 <= 0) {
          break label346;
        }
        k = ((RecyclerView.ViewHolder)localObject1).itemView.getRight() - (paramInt1 + m);
        if ((k >= 0) || (((RecyclerView.ViewHolder)localObject1).itemView.getRight() <= paramViewHolder.itemView.getRight())) {
          break label346;
        }
        k = Math.abs(k);
        if (k <= i) {
          break label346;
        }
        i = k;
        localObject2 = localObject1;
        label143:
        if (i1 >= 0) {
          break label359;
        }
        k = ((RecyclerView.ViewHolder)localObject1).itemView.getLeft() - paramInt1;
        if ((k <= 0) || (((RecyclerView.ViewHolder)localObject1).itemView.getLeft() >= paramViewHolder.itemView.getLeft())) {
          break label359;
        }
        k = Math.abs(k);
        if (k <= i) {
          break label359;
        }
        localObject2 = localObject1;
        i = k;
      }
      label346:
      label359:
      for (;;)
      {
        if (i2 < 0)
        {
          k = ((RecyclerView.ViewHolder)localObject1).itemView.getTop() - paramInt2;
          if ((k > 0) && (((RecyclerView.ViewHolder)localObject1).itemView.getTop() < paramViewHolder.itemView.getTop()))
          {
            k = Math.abs(k);
            if (k > i)
            {
              localObject2 = localObject1;
              i = k;
            }
          }
        }
        for (;;)
        {
          if (i2 > 0)
          {
            k = ((RecyclerView.ViewHolder)localObject1).itemView.getBottom() - (paramInt2 + n);
            if ((k < 0) && (((RecyclerView.ViewHolder)localObject1).itemView.getBottom() > paramViewHolder.itemView.getBottom()))
            {
              k = Math.abs(k);
              if (k > i) {
                i = k;
              }
            }
          }
          for (;;)
          {
            j += 1;
            localObject2 = localObject1;
            break;
            return (RecyclerView.ViewHolder)localObject2;
            break label143;
            localObject1 = localObject2;
          }
        }
      }
    }
    
    public void clearView(RecyclerView paramRecyclerView, RecyclerView.ViewHolder paramViewHolder)
    {
      sUICallback.clearView(paramViewHolder.itemView);
    }
    
    public int convertToAbsoluteDirection(int paramInt1, int paramInt2)
    {
      int i = paramInt1 & 0x303030;
      if (i == 0) {
        return paramInt1;
      }
      paramInt1 = (i ^ 0xFFFFFFFF) & paramInt1;
      if (paramInt2 == 0) {
        return paramInt1 | i >> 2;
      }
      return paramInt1 | i >> 1 & 0xFFCFCFCF | (i >> 1 & 0x303030) >> 2;
    }
    
    final int getAbsoluteMovementFlags(RecyclerView paramRecyclerView, RecyclerView.ViewHolder paramViewHolder)
    {
      return convertToAbsoluteDirection(getMovementFlags(paramRecyclerView, paramViewHolder), ViewCompat.getLayoutDirection(paramRecyclerView));
    }
    
    public long getAnimationDuration(RecyclerView paramRecyclerView, int paramInt, float paramFloat1, float paramFloat2)
    {
      paramRecyclerView = paramRecyclerView.getItemAnimator();
      if (paramRecyclerView == null)
      {
        if (paramInt == 8) {
          return 200L;
        }
        return 250L;
      }
      if (paramInt == 8) {
        return paramRecyclerView.getMoveDuration();
      }
      return paramRecyclerView.getRemoveDuration();
    }
    
    public int getBoundingBoxMargin()
    {
      return 0;
    }
    
    public float getMoveThreshold(RecyclerView.ViewHolder paramViewHolder)
    {
      return 0.5F;
    }
    
    public abstract int getMovementFlags(RecyclerView paramRecyclerView, RecyclerView.ViewHolder paramViewHolder);
    
    public float getSwipeEscapeVelocity(float paramFloat)
    {
      return paramFloat;
    }
    
    public float getSwipeThreshold(RecyclerView.ViewHolder paramViewHolder)
    {
      return 0.5F;
    }
    
    public float getSwipeVelocityThreshold(float paramFloat)
    {
      return paramFloat;
    }
    
    public int interpolateOutOfBoundsScroll(RecyclerView paramRecyclerView, int paramInt1, int paramInt2, int paramInt3, long paramLong)
    {
      float f1 = 1.0F;
      paramInt3 = getMaxDragScroll(paramRecyclerView);
      int i = Math.abs(paramInt2);
      int j = (int)Math.signum(paramInt2);
      float f2 = Math.min(1.0F, i * 1.0F / paramInt1);
      paramInt1 = (int)(paramInt3 * j * sDragViewScrollCapInterpolator.getInterpolation(f2));
      if (paramLong > 2000L) {}
      for (;;)
      {
        f2 = paramInt1;
        paramInt3 = (int)(sDragScrollInterpolator.getInterpolation(f1) * f2);
        paramInt1 = paramInt3;
        if (paramInt3 == 0)
        {
          if (paramInt2 <= 0) {
            break;
          }
          paramInt1 = 1;
        }
        return paramInt1;
        f1 = (float)paramLong / 2000.0F;
      }
      return -1;
    }
    
    public boolean isItemViewSwipeEnabled()
    {
      return true;
    }
    
    public boolean isLongPressDragEnabled()
    {
      return true;
    }
    
    public void onChildDraw(Canvas paramCanvas, RecyclerView paramRecyclerView, RecyclerView.ViewHolder paramViewHolder, float paramFloat1, float paramFloat2, int paramInt, boolean paramBoolean)
    {
      sUICallback.onDraw(paramCanvas, paramRecyclerView, paramViewHolder.itemView, paramFloat1, paramFloat2, paramInt, paramBoolean);
    }
    
    public void onChildDrawOver(Canvas paramCanvas, RecyclerView paramRecyclerView, RecyclerView.ViewHolder paramViewHolder, float paramFloat1, float paramFloat2, int paramInt, boolean paramBoolean)
    {
      sUICallback.onDrawOver(paramCanvas, paramRecyclerView, paramViewHolder.itemView, paramFloat1, paramFloat2, paramInt, paramBoolean);
    }
    
    public abstract boolean onMove(RecyclerView paramRecyclerView, RecyclerView.ViewHolder paramViewHolder1, RecyclerView.ViewHolder paramViewHolder2);
    
    public void onMoved(RecyclerView paramRecyclerView, RecyclerView.ViewHolder paramViewHolder1, int paramInt1, RecyclerView.ViewHolder paramViewHolder2, int paramInt2, int paramInt3, int paramInt4)
    {
      RecyclerView.LayoutManager localLayoutManager = paramRecyclerView.getLayoutManager();
      if ((localLayoutManager instanceof ItemTouchHelper.ViewDropHandler)) {
        ((ItemTouchHelper.ViewDropHandler)localLayoutManager).prepareForDrop(paramViewHolder1.itemView, paramViewHolder2.itemView, paramInt3, paramInt4);
      }
      do
      {
        do
        {
          return;
          if (localLayoutManager.canScrollHorizontally())
          {
            if (localLayoutManager.getDecoratedLeft(paramViewHolder2.itemView) <= paramRecyclerView.getPaddingLeft()) {
              paramRecyclerView.scrollToPosition(paramInt2);
            }
            if (localLayoutManager.getDecoratedRight(paramViewHolder2.itemView) >= paramRecyclerView.getWidth() - paramRecyclerView.getPaddingRight()) {
              paramRecyclerView.scrollToPosition(paramInt2);
            }
          }
        } while (!localLayoutManager.canScrollVertically());
        if (localLayoutManager.getDecoratedTop(paramViewHolder2.itemView) <= paramRecyclerView.getPaddingTop()) {
          paramRecyclerView.scrollToPosition(paramInt2);
        }
      } while (localLayoutManager.getDecoratedBottom(paramViewHolder2.itemView) < paramRecyclerView.getHeight() - paramRecyclerView.getPaddingBottom());
      paramRecyclerView.scrollToPosition(paramInt2);
    }
    
    public void onSelectedChanged(RecyclerView.ViewHolder paramViewHolder, int paramInt)
    {
      if (paramViewHolder != null) {
        sUICallback.onSelected(paramViewHolder.itemView);
      }
    }
    
    public abstract void onSwiped(RecyclerView.ViewHolder paramViewHolder, int paramInt);
  }
  
  public static abstract class SimpleCallback
    extends ItemTouchHelper.Callback
  {
    private int mDefaultDragDirs;
    private int mDefaultSwipeDirs;
    
    public SimpleCallback(int paramInt1, int paramInt2)
    {
      this.mDefaultSwipeDirs = paramInt2;
      this.mDefaultDragDirs = paramInt1;
    }
    
    public int getDragDirs(RecyclerView paramRecyclerView, RecyclerView.ViewHolder paramViewHolder)
    {
      return this.mDefaultDragDirs;
    }
    
    public int getMovementFlags(RecyclerView paramRecyclerView, RecyclerView.ViewHolder paramViewHolder)
    {
      return makeMovementFlags(getDragDirs(paramRecyclerView, paramViewHolder), getSwipeDirs(paramRecyclerView, paramViewHolder));
    }
    
    public int getSwipeDirs(RecyclerView paramRecyclerView, RecyclerView.ViewHolder paramViewHolder)
    {
      return this.mDefaultSwipeDirs;
    }
    
    public void setDefaultDragDirs(int paramInt)
    {
      this.mDefaultDragDirs = paramInt;
    }
    
    public void setDefaultSwipeDirs(int paramInt)
    {
      this.mDefaultSwipeDirs = paramInt;
    }
  }
  
  public static abstract interface ViewDropHandler
  {
    public abstract void prepareForDrop(View paramView1, View paramView2, int paramInt1, int paramInt2);
  }
  
  private class a
    extends GestureDetector.SimpleOnGestureListener
  {
    private a() {}
    
    public boolean onDown(MotionEvent paramMotionEvent)
    {
      return true;
    }
    
    public void onLongPress(MotionEvent paramMotionEvent)
    {
      Object localObject = ItemTouchHelper.this.findChildView(paramMotionEvent);
      if (localObject != null)
      {
        localObject = ItemTouchHelper.this.mRecyclerView.getChildViewHolder((View)localObject);
        if ((localObject != null) && (ItemTouchHelper.this.mCallback.hasDragFlag(ItemTouchHelper.this.mRecyclerView, (RecyclerView.ViewHolder)localObject))) {
          break label57;
        }
      }
      label57:
      do
      {
        do
        {
          return;
        } while (MotionEventCompat.getPointerId(paramMotionEvent, 0) != ItemTouchHelper.this.mActivePointerId);
        int i = MotionEventCompat.findPointerIndex(paramMotionEvent, ItemTouchHelper.this.mActivePointerId);
        float f1 = MotionEventCompat.getX(paramMotionEvent, i);
        float f2 = MotionEventCompat.getY(paramMotionEvent, i);
        ItemTouchHelper.this.mInitialTouchX = f1;
        ItemTouchHelper.this.mInitialTouchY = f2;
        paramMotionEvent = ItemTouchHelper.this;
        ItemTouchHelper.this.mDy = 0.0F;
        paramMotionEvent.mDx = 0.0F;
      } while (!ItemTouchHelper.this.mCallback.isLongPressDragEnabled());
      ItemTouchHelper.this.select((RecyclerView.ViewHolder)localObject, 2);
    }
  }
  
  private class b
    implements AnimatorListenerCompat
  {
    private final ValueAnimatorCompat a;
    private final int b;
    private boolean c = false;
    final float d;
    final float e;
    final float f;
    final float g;
    final RecyclerView.ViewHolder h;
    final int i;
    public boolean j;
    float k;
    float l;
    boolean m = false;
    private float o;
    
    public b(RecyclerView.ViewHolder paramViewHolder, int paramInt1, int paramInt2, float paramFloat1, float paramFloat2, float paramFloat3, float paramFloat4)
    {
      this.i = paramInt2;
      this.b = paramInt1;
      this.h = paramViewHolder;
      this.d = paramFloat1;
      this.e = paramFloat2;
      this.f = paramFloat3;
      this.g = paramFloat4;
      this.a = AnimatorCompatHelper.emptyValueAnimator();
      this.a.addUpdateListener(new h(this, ItemTouchHelper.this));
      this.a.setTarget(paramViewHolder.itemView);
      this.a.addListener(this);
      a(0.0F);
    }
    
    public void a()
    {
      this.h.setIsRecyclable(false);
      this.a.start();
    }
    
    public void a(float paramFloat)
    {
      this.o = paramFloat;
    }
    
    public void a(long paramLong)
    {
      this.a.setDuration(paramLong);
    }
    
    public void b()
    {
      this.a.cancel();
    }
    
    public void c()
    {
      if (this.d == this.f) {}
      for (this.k = ViewCompat.getTranslationX(this.h.itemView); this.e == this.g; this.k = (this.d + this.o * (this.f - this.d)))
      {
        this.l = ViewCompat.getTranslationY(this.h.itemView);
        return;
      }
      this.l = (this.e + this.o * (this.g - this.e));
    }
    
    public void onAnimationCancel(ValueAnimatorCompat paramValueAnimatorCompat)
    {
      a(1.0F);
    }
    
    public void onAnimationEnd(ValueAnimatorCompat paramValueAnimatorCompat)
    {
      if (!this.c) {
        this.h.setIsRecyclable(true);
      }
      this.c = true;
    }
    
    public void onAnimationRepeat(ValueAnimatorCompat paramValueAnimatorCompat) {}
    
    public void onAnimationStart(ValueAnimatorCompat paramValueAnimatorCompat) {}
  }
}


/* Location:              E:\apk\xiaoenai2\classes-dex2jar.jar!\android\support\v7\widget\helper\ItemTouchHelper.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */