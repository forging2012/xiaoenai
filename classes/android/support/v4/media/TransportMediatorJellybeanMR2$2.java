package android.support.v4.media;

import android.view.ViewTreeObserver.OnWindowFocusChangeListener;

class TransportMediatorJellybeanMR2$2
  implements ViewTreeObserver.OnWindowFocusChangeListener
{
  TransportMediatorJellybeanMR2$2(TransportMediatorJellybeanMR2 paramTransportMediatorJellybeanMR2) {}
  
  public void onWindowFocusChanged(boolean paramBoolean)
  {
    if (paramBoolean)
    {
      this.this$0.gainFocus();
      return;
    }
    this.this$0.loseFocus();
  }
}


/* Location:              E:\apk\xiaoenai2\classes-dex2jar.jar!\android\support\v4\media\TransportMediatorJellybeanMR2$2.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */