package android.support.v4.media;

import android.os.IBinder;
import android.support.v4.util.ArrayMap;

class MediaBrowserServiceCompat$ServiceImpl$2
  implements Runnable
{
  MediaBrowserServiceCompat$ServiceImpl$2(MediaBrowserServiceCompat.ServiceImpl paramServiceImpl, MediaBrowserServiceCompat.ServiceCallbacks paramServiceCallbacks) {}
  
  public void run()
  {
    IBinder localIBinder = this.val$callbacks.asBinder();
    if ((MediaBrowserServiceCompat.ConnectionRecord)MediaBrowserServiceCompat.access$500(this.this$1.this$0).remove(localIBinder) != null) {}
  }
}


/* Location:              E:\apk\xiaoenai2\classes-dex2jar.jar!\android\support\v4\media\MediaBrowserServiceCompat$ServiceImpl$2.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */