package com.alipay.android.phone.mrpc.core;

import org.apache.http.client.RedirectHandler;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.ConnectionKeepAliveStrategy;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.BasicHttpProcessor;
import org.apache.http.protocol.HttpContext;

final class d
  extends DefaultHttpClient
{
  d(b paramb, ClientConnectionManager paramClientConnectionManager, HttpParams paramHttpParams)
  {
    super(paramClientConnectionManager, paramHttpParams);
  }
  
  protected final ConnectionKeepAliveStrategy createConnectionKeepAliveStrategy()
  {
    return new f(this);
  }
  
  protected final HttpContext createHttpContext()
  {
    BasicHttpContext localBasicHttpContext = new BasicHttpContext();
    localBasicHttpContext.setAttribute("http.authscheme-registry", getAuthSchemes());
    localBasicHttpContext.setAttribute("http.cookiespec-registry", getCookieSpecs());
    localBasicHttpContext.setAttribute("http.auth.credentials-provider", getCredentialsProvider());
    return localBasicHttpContext;
  }
  
  protected final BasicHttpProcessor createHttpProcessor()
  {
    BasicHttpProcessor localBasicHttpProcessor = super.createHttpProcessor();
    localBasicHttpProcessor.addRequestInterceptor(b.a());
    localBasicHttpProcessor.addRequestInterceptor(new b.a(this.a, (byte)0));
    return localBasicHttpProcessor;
  }
  
  protected final RedirectHandler createRedirectHandler()
  {
    return new e(this);
  }
}


/* Location:              E:\apk\xiaoenai2\classes-dex2jar.jar!\com\alipay\android\phone\mrpc\core\d.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */