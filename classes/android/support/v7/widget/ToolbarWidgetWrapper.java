package android.support.v7.widget;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Parcelable;
import android.support.v4.view.ViewCompat;
import android.support.v4.view.ViewPropertyAnimatorCompat;
import android.support.v7.a.a.a;
import android.support.v7.a.a.e;
import android.support.v7.a.a.f;
import android.support.v7.a.a.i;
import android.support.v7.a.a.k;
import android.support.v7.view.menu.f;
import android.support.v7.view.menu.f.a;
import android.support.v7.view.menu.m.a;
import android.text.TextUtils;
import android.util.Log;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.Window.Callback;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;

public class ToolbarWidgetWrapper
  implements DecorToolbar
{
  private static final int AFFECTS_LOGO_MASK = 3;
  private static final long DEFAULT_FADE_DURATION_MS = 200L;
  private static final String TAG = "ToolbarWidgetWrapper";
  private ActionMenuPresenter mActionMenuPresenter;
  private View mCustomView;
  private int mDefaultNavigationContentDescription = 0;
  private Drawable mDefaultNavigationIcon;
  private int mDisplayOpts;
  private final AppCompatDrawableManager mDrawableManager;
  private CharSequence mHomeDescription;
  private Drawable mIcon;
  private Drawable mLogo;
  private boolean mMenuPrepared;
  private Drawable mNavIcon;
  private int mNavigationMode = 0;
  private Spinner mSpinner;
  private CharSequence mSubtitle;
  private View mTabView;
  private CharSequence mTitle;
  private boolean mTitleSet;
  private Toolbar mToolbar;
  private Window.Callback mWindowCallback;
  
  public ToolbarWidgetWrapper(Toolbar paramToolbar, boolean paramBoolean)
  {
    this(paramToolbar, paramBoolean, a.i.abc_action_bar_up_description, a.e.abc_ic_ab_back_mtrl_am_alpha);
  }
  
  public ToolbarWidgetWrapper(Toolbar paramToolbar, boolean paramBoolean, int paramInt1, int paramInt2)
  {
    this.mToolbar = paramToolbar;
    this.mTitle = paramToolbar.getTitle();
    this.mSubtitle = paramToolbar.getSubtitle();
    boolean bool;
    if (this.mTitle != null)
    {
      bool = true;
      this.mTitleSet = bool;
      this.mNavIcon = paramToolbar.getNavigationIcon();
      if (!paramBoolean) {
        break label495;
      }
      paramToolbar = TintTypedArray.obtainStyledAttributes(paramToolbar.getContext(), null, a.k.ActionBar, a.a.actionBarStyle, 0);
      Object localObject = paramToolbar.getText(a.k.ActionBar_title);
      if (!TextUtils.isEmpty((CharSequence)localObject)) {
        setTitle((CharSequence)localObject);
      }
      localObject = paramToolbar.getText(a.k.ActionBar_subtitle);
      if (!TextUtils.isEmpty((CharSequence)localObject)) {
        setSubtitle((CharSequence)localObject);
      }
      localObject = paramToolbar.getDrawable(a.k.ActionBar_logo);
      if (localObject != null) {
        setLogo((Drawable)localObject);
      }
      localObject = paramToolbar.getDrawable(a.k.ActionBar_icon);
      if ((this.mNavIcon == null) && (localObject != null)) {
        setIcon((Drawable)localObject);
      }
      localObject = paramToolbar.getDrawable(a.k.ActionBar_homeAsUpIndicator);
      if (localObject != null) {
        setNavigationIcon((Drawable)localObject);
      }
      setDisplayOptions(paramToolbar.getInt(a.k.ActionBar_displayOptions, 0));
      int i = paramToolbar.getResourceId(a.k.ActionBar_customNavigationLayout, 0);
      if (i != 0)
      {
        setCustomView(LayoutInflater.from(this.mToolbar.getContext()).inflate(i, this.mToolbar, false));
        setDisplayOptions(this.mDisplayOpts | 0x10);
      }
      i = paramToolbar.getLayoutDimension(a.k.ActionBar_height, 0);
      if (i > 0)
      {
        localObject = this.mToolbar.getLayoutParams();
        ((ViewGroup.LayoutParams)localObject).height = i;
        this.mToolbar.setLayoutParams((ViewGroup.LayoutParams)localObject);
      }
      i = paramToolbar.getDimensionPixelOffset(a.k.ActionBar_contentInsetStart, -1);
      int j = paramToolbar.getDimensionPixelOffset(a.k.ActionBar_contentInsetEnd, -1);
      if ((i >= 0) || (j >= 0)) {
        this.mToolbar.setContentInsetsRelative(Math.max(i, 0), Math.max(j, 0));
      }
      i = paramToolbar.getResourceId(a.k.ActionBar_titleTextStyle, 0);
      if (i != 0) {
        this.mToolbar.setTitleTextAppearance(this.mToolbar.getContext(), i);
      }
      i = paramToolbar.getResourceId(a.k.ActionBar_subtitleTextStyle, 0);
      if (i != 0) {
        this.mToolbar.setSubtitleTextAppearance(this.mToolbar.getContext(), i);
      }
      i = paramToolbar.getResourceId(a.k.ActionBar_popupTheme, 0);
      if (i != 0) {
        this.mToolbar.setPopupTheme(i);
      }
      paramToolbar.recycle();
    }
    for (;;)
    {
      this.mDrawableManager = AppCompatDrawableManager.get();
      setDefaultNavigationContentDescription(paramInt1);
      this.mHomeDescription = this.mToolbar.getNavigationContentDescription();
      setDefaultNavigationIcon(this.mDrawableManager.getDrawable(getContext(), paramInt2));
      this.mToolbar.setNavigationOnClickListener(new ce(this));
      return;
      bool = false;
      break;
      label495:
      this.mDisplayOpts = detectDisplayOptions();
    }
  }
  
  private int detectDisplayOptions()
  {
    int i = 11;
    if (this.mToolbar.getNavigationIcon() != null) {
      i = 15;
    }
    return i;
  }
  
  private void ensureSpinner()
  {
    if (this.mSpinner == null)
    {
      this.mSpinner = new AppCompatSpinner(getContext(), null, a.a.actionDropDownStyle);
      Toolbar.LayoutParams localLayoutParams = new Toolbar.LayoutParams(-2, -2, 8388627);
      this.mSpinner.setLayoutParams(localLayoutParams);
    }
  }
  
  private void setTitleInt(CharSequence paramCharSequence)
  {
    this.mTitle = paramCharSequence;
    if ((this.mDisplayOpts & 0x8) != 0) {
      this.mToolbar.setTitle(paramCharSequence);
    }
  }
  
  private void updateHomeAccessibility()
  {
    if ((this.mDisplayOpts & 0x4) != 0)
    {
      if (TextUtils.isEmpty(this.mHomeDescription)) {
        this.mToolbar.setNavigationContentDescription(this.mDefaultNavigationContentDescription);
      }
    }
    else {
      return;
    }
    this.mToolbar.setNavigationContentDescription(this.mHomeDescription);
  }
  
  private void updateNavigationIcon()
  {
    Toolbar localToolbar;
    if ((this.mDisplayOpts & 0x4) != 0)
    {
      localToolbar = this.mToolbar;
      if (this.mNavIcon == null) {
        break label32;
      }
    }
    label32:
    for (Drawable localDrawable = this.mNavIcon;; localDrawable = this.mDefaultNavigationIcon)
    {
      localToolbar.setNavigationIcon(localDrawable);
      return;
    }
  }
  
  private void updateToolbarLogo()
  {
    Drawable localDrawable = null;
    if ((this.mDisplayOpts & 0x2) != 0)
    {
      if ((this.mDisplayOpts & 0x1) == 0) {
        break label49;
      }
      if (this.mLogo == null) {
        break label41;
      }
      localDrawable = this.mLogo;
    }
    for (;;)
    {
      this.mToolbar.setLogo(localDrawable);
      return;
      label41:
      localDrawable = this.mIcon;
      continue;
      label49:
      localDrawable = this.mIcon;
    }
  }
  
  public void animateToVisibility(int paramInt)
  {
    ViewPropertyAnimatorCompat localViewPropertyAnimatorCompat = setupAnimatorToVisibility(paramInt, 200L);
    if (localViewPropertyAnimatorCompat != null) {
      localViewPropertyAnimatorCompat.start();
    }
  }
  
  public boolean canShowOverflowMenu()
  {
    return this.mToolbar.canShowOverflowMenu();
  }
  
  public void collapseActionView()
  {
    this.mToolbar.collapseActionView();
  }
  
  public void dismissPopupMenus()
  {
    this.mToolbar.dismissPopupMenus();
  }
  
  public Context getContext()
  {
    return this.mToolbar.getContext();
  }
  
  public View getCustomView()
  {
    return this.mCustomView;
  }
  
  public int getDisplayOptions()
  {
    return this.mDisplayOpts;
  }
  
  public int getDropdownItemCount()
  {
    if (this.mSpinner != null) {
      return this.mSpinner.getCount();
    }
    return 0;
  }
  
  public int getDropdownSelectedPosition()
  {
    if (this.mSpinner != null) {
      return this.mSpinner.getSelectedItemPosition();
    }
    return 0;
  }
  
  public int getHeight()
  {
    return this.mToolbar.getHeight();
  }
  
  public Menu getMenu()
  {
    return this.mToolbar.getMenu();
  }
  
  public int getNavigationMode()
  {
    return this.mNavigationMode;
  }
  
  public CharSequence getSubtitle()
  {
    return this.mToolbar.getSubtitle();
  }
  
  public CharSequence getTitle()
  {
    return this.mToolbar.getTitle();
  }
  
  public ViewGroup getViewGroup()
  {
    return this.mToolbar;
  }
  
  public int getVisibility()
  {
    return this.mToolbar.getVisibility();
  }
  
  public boolean hasEmbeddedTabs()
  {
    return this.mTabView != null;
  }
  
  public boolean hasExpandedActionView()
  {
    return this.mToolbar.hasExpandedActionView();
  }
  
  public boolean hasIcon()
  {
    return this.mIcon != null;
  }
  
  public boolean hasLogo()
  {
    return this.mLogo != null;
  }
  
  public boolean hideOverflowMenu()
  {
    return this.mToolbar.hideOverflowMenu();
  }
  
  public void initIndeterminateProgress()
  {
    Log.i("ToolbarWidgetWrapper", "Progress display unsupported");
  }
  
  public void initProgress()
  {
    Log.i("ToolbarWidgetWrapper", "Progress display unsupported");
  }
  
  public boolean isOverflowMenuShowPending()
  {
    return this.mToolbar.isOverflowMenuShowPending();
  }
  
  public boolean isOverflowMenuShowing()
  {
    return this.mToolbar.isOverflowMenuShowing();
  }
  
  public boolean isTitleTruncated()
  {
    return this.mToolbar.isTitleTruncated();
  }
  
  public void restoreHierarchyState(SparseArray<Parcelable> paramSparseArray)
  {
    this.mToolbar.restoreHierarchyState(paramSparseArray);
  }
  
  public void saveHierarchyState(SparseArray<Parcelable> paramSparseArray)
  {
    this.mToolbar.saveHierarchyState(paramSparseArray);
  }
  
  public void setBackgroundDrawable(Drawable paramDrawable)
  {
    this.mToolbar.setBackgroundDrawable(paramDrawable);
  }
  
  public void setCollapsible(boolean paramBoolean)
  {
    this.mToolbar.setCollapsible(paramBoolean);
  }
  
  public void setCustomView(View paramView)
  {
    if ((this.mCustomView != null) && ((this.mDisplayOpts & 0x10) != 0)) {
      this.mToolbar.removeView(this.mCustomView);
    }
    this.mCustomView = paramView;
    if ((paramView != null) && ((this.mDisplayOpts & 0x10) != 0)) {
      this.mToolbar.addView(this.mCustomView);
    }
  }
  
  public void setDefaultNavigationContentDescription(int paramInt)
  {
    if (paramInt == this.mDefaultNavigationContentDescription) {}
    do
    {
      return;
      this.mDefaultNavigationContentDescription = paramInt;
    } while (!TextUtils.isEmpty(this.mToolbar.getNavigationContentDescription()));
    setNavigationContentDescription(this.mDefaultNavigationContentDescription);
  }
  
  public void setDefaultNavigationIcon(Drawable paramDrawable)
  {
    if (this.mDefaultNavigationIcon != paramDrawable)
    {
      this.mDefaultNavigationIcon = paramDrawable;
      updateNavigationIcon();
    }
  }
  
  public void setDisplayOptions(int paramInt)
  {
    int i = this.mDisplayOpts ^ paramInt;
    this.mDisplayOpts = paramInt;
    if (i != 0)
    {
      if ((i & 0x4) != 0)
      {
        if ((paramInt & 0x4) == 0) {
          break label115;
        }
        updateNavigationIcon();
        updateHomeAccessibility();
      }
      if ((i & 0x3) != 0) {
        updateToolbarLogo();
      }
      if ((i & 0x8) != 0)
      {
        if ((paramInt & 0x8) == 0) {
          break label126;
        }
        this.mToolbar.setTitle(this.mTitle);
        this.mToolbar.setSubtitle(this.mSubtitle);
      }
    }
    for (;;)
    {
      if (((i & 0x10) != 0) && (this.mCustomView != null))
      {
        if ((paramInt & 0x10) == 0) {
          break label145;
        }
        this.mToolbar.addView(this.mCustomView);
      }
      return;
      label115:
      this.mToolbar.setNavigationIcon(null);
      break;
      label126:
      this.mToolbar.setTitle(null);
      this.mToolbar.setSubtitle(null);
    }
    label145:
    this.mToolbar.removeView(this.mCustomView);
  }
  
  public void setDropdownParams(SpinnerAdapter paramSpinnerAdapter, AdapterView.OnItemSelectedListener paramOnItemSelectedListener)
  {
    ensureSpinner();
    this.mSpinner.setAdapter(paramSpinnerAdapter);
    this.mSpinner.setOnItemSelectedListener(paramOnItemSelectedListener);
  }
  
  public void setDropdownSelectedPosition(int paramInt)
  {
    if (this.mSpinner == null) {
      throw new IllegalStateException("Can't set dropdown selected position without an adapter");
    }
    this.mSpinner.setSelection(paramInt);
  }
  
  public void setEmbeddedTabView(ScrollingTabContainerView paramScrollingTabContainerView)
  {
    if ((this.mTabView != null) && (this.mTabView.getParent() == this.mToolbar)) {
      this.mToolbar.removeView(this.mTabView);
    }
    this.mTabView = paramScrollingTabContainerView;
    if ((paramScrollingTabContainerView != null) && (this.mNavigationMode == 2))
    {
      this.mToolbar.addView(this.mTabView, 0);
      Toolbar.LayoutParams localLayoutParams = (Toolbar.LayoutParams)this.mTabView.getLayoutParams();
      localLayoutParams.width = -2;
      localLayoutParams.height = -2;
      localLayoutParams.gravity = 8388691;
      paramScrollingTabContainerView.setAllowCollapse(true);
    }
  }
  
  public void setHomeButtonEnabled(boolean paramBoolean) {}
  
  public void setIcon(int paramInt)
  {
    if (paramInt != 0) {}
    for (Drawable localDrawable = this.mDrawableManager.getDrawable(getContext(), paramInt);; localDrawable = null)
    {
      setIcon(localDrawable);
      return;
    }
  }
  
  public void setIcon(Drawable paramDrawable)
  {
    this.mIcon = paramDrawable;
    updateToolbarLogo();
  }
  
  public void setLogo(int paramInt)
  {
    if (paramInt != 0) {}
    for (Drawable localDrawable = this.mDrawableManager.getDrawable(getContext(), paramInt);; localDrawable = null)
    {
      setLogo(localDrawable);
      return;
    }
  }
  
  public void setLogo(Drawable paramDrawable)
  {
    this.mLogo = paramDrawable;
    updateToolbarLogo();
  }
  
  public void setMenu(Menu paramMenu, m.a parama)
  {
    if (this.mActionMenuPresenter == null)
    {
      this.mActionMenuPresenter = new ActionMenuPresenter(this.mToolbar.getContext());
      this.mActionMenuPresenter.a(a.f.action_menu_presenter);
    }
    this.mActionMenuPresenter.a(parama);
    this.mToolbar.setMenu((f)paramMenu, this.mActionMenuPresenter);
  }
  
  public void setMenuCallbacks(m.a parama, f.a parama1)
  {
    this.mToolbar.setMenuCallbacks(parama, parama1);
  }
  
  public void setMenuPrepared()
  {
    this.mMenuPrepared = true;
  }
  
  public void setNavigationContentDescription(int paramInt)
  {
    if (paramInt == 0) {}
    for (Object localObject = null;; localObject = getContext().getString(paramInt))
    {
      setNavigationContentDescription((CharSequence)localObject);
      return;
    }
  }
  
  public void setNavigationContentDescription(CharSequence paramCharSequence)
  {
    this.mHomeDescription = paramCharSequence;
    updateHomeAccessibility();
  }
  
  public void setNavigationIcon(int paramInt)
  {
    if (paramInt != 0) {}
    for (Drawable localDrawable = AppCompatDrawableManager.get().getDrawable(getContext(), paramInt);; localDrawable = null)
    {
      setNavigationIcon(localDrawable);
      return;
    }
  }
  
  public void setNavigationIcon(Drawable paramDrawable)
  {
    this.mNavIcon = paramDrawable;
    updateNavigationIcon();
  }
  
  public void setNavigationMode(int paramInt)
  {
    int i = this.mNavigationMode;
    if (paramInt != i)
    {
      switch (i)
      {
      }
      for (;;)
      {
        this.mNavigationMode = paramInt;
        switch (paramInt)
        {
        default: 
          throw new IllegalArgumentException("Invalid navigation mode " + paramInt);
          if ((this.mSpinner != null) && (this.mSpinner.getParent() == this.mToolbar))
          {
            this.mToolbar.removeView(this.mSpinner);
            continue;
            if ((this.mTabView != null) && (this.mTabView.getParent() == this.mToolbar)) {
              this.mToolbar.removeView(this.mTabView);
            }
          }
          break;
        }
      }
      ensureSpinner();
      this.mToolbar.addView(this.mSpinner, 0);
    }
    do
    {
      return;
    } while (this.mTabView == null);
    this.mToolbar.addView(this.mTabView, 0);
    Toolbar.LayoutParams localLayoutParams = (Toolbar.LayoutParams)this.mTabView.getLayoutParams();
    localLayoutParams.width = -2;
    localLayoutParams.height = -2;
    localLayoutParams.gravity = 8388691;
  }
  
  public void setSubtitle(CharSequence paramCharSequence)
  {
    this.mSubtitle = paramCharSequence;
    if ((this.mDisplayOpts & 0x8) != 0) {
      this.mToolbar.setSubtitle(paramCharSequence);
    }
  }
  
  public void setTitle(CharSequence paramCharSequence)
  {
    this.mTitleSet = true;
    setTitleInt(paramCharSequence);
  }
  
  public void setVisibility(int paramInt)
  {
    this.mToolbar.setVisibility(paramInt);
  }
  
  public void setWindowCallback(Window.Callback paramCallback)
  {
    this.mWindowCallback = paramCallback;
  }
  
  public void setWindowTitle(CharSequence paramCharSequence)
  {
    if (!this.mTitleSet) {
      setTitleInt(paramCharSequence);
    }
  }
  
  public ViewPropertyAnimatorCompat setupAnimatorToVisibility(int paramInt, long paramLong)
  {
    ViewPropertyAnimatorCompat localViewPropertyAnimatorCompat = ViewCompat.animate(this.mToolbar);
    if (paramInt == 0) {}
    for (float f = 1.0F;; f = 0.0F) {
      return localViewPropertyAnimatorCompat.alpha(f).setDuration(paramLong).setListener(new cf(this, paramInt));
    }
  }
  
  public boolean showOverflowMenu()
  {
    return this.mToolbar.showOverflowMenu();
  }
}


/* Location:              E:\apk\xiaoenai2\classes-dex2jar.jar!\android\support\v7\widget\ToolbarWidgetWrapper.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */