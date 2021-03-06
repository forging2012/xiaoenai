package android.support.v4.view;

import android.graphics.Rect;
import android.view.View;

class ViewPager$4
  implements OnApplyWindowInsetsListener
{
  private final Rect mTempRect = new Rect();
  
  ViewPager$4(ViewPager paramViewPager) {}
  
  public WindowInsetsCompat onApplyWindowInsets(View paramView, WindowInsetsCompat paramWindowInsetsCompat)
  {
    paramView = ViewCompat.onApplyWindowInsets(paramView, paramWindowInsetsCompat);
    if (paramView.isConsumed()) {
      return paramView;
    }
    paramWindowInsetsCompat = this.mTempRect;
    paramWindowInsetsCompat.left = paramView.getSystemWindowInsetLeft();
    paramWindowInsetsCompat.top = paramView.getSystemWindowInsetTop();
    paramWindowInsetsCompat.right = paramView.getSystemWindowInsetRight();
    paramWindowInsetsCompat.bottom = paramView.getSystemWindowInsetBottom();
    int i = 0;
    int j = this.this$0.getChildCount();
    while (i < j)
    {
      WindowInsetsCompat localWindowInsetsCompat = ViewCompat.dispatchApplyWindowInsets(this.this$0.getChildAt(i), paramView);
      paramWindowInsetsCompat.left = Math.min(localWindowInsetsCompat.getSystemWindowInsetLeft(), paramWindowInsetsCompat.left);
      paramWindowInsetsCompat.top = Math.min(localWindowInsetsCompat.getSystemWindowInsetTop(), paramWindowInsetsCompat.top);
      paramWindowInsetsCompat.right = Math.min(localWindowInsetsCompat.getSystemWindowInsetRight(), paramWindowInsetsCompat.right);
      paramWindowInsetsCompat.bottom = Math.min(localWindowInsetsCompat.getSystemWindowInsetBottom(), paramWindowInsetsCompat.bottom);
      i += 1;
    }
    return paramView.replaceSystemWindowInsets(paramWindowInsetsCompat.left, paramWindowInsetsCompat.top, paramWindowInsetsCompat.right, paramWindowInsetsCompat.bottom);
  }
}


/* Location:              E:\apk\xiaoenai2\classes-dex2jar.jar!\android\support\v4\view\ViewPager$4.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */