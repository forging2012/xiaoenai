package com.alipay.sdk.util;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.Signature;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Build.VERSION;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.WindowManager;
import com.alipay.sdk.cons.a;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class k
{
  static final String a = "com.alipay.android.app";
  public static final String b = "com.eg.android.AlipayGphone";
  private static final String c = "7.0.0";
  
  private static int a(String paramString1, String paramString2)
  {
    ArrayList localArrayList1 = new ArrayList();
    ArrayList localArrayList2 = new ArrayList();
    localArrayList1.addAll(Arrays.asList(paramString1.split("\\.")));
    localArrayList2.addAll(Arrays.asList(paramString2.split("\\.")));
    int j = Math.max(localArrayList1.size(), localArrayList2.size());
    while (localArrayList1.size() < j) {
      localArrayList1.add("0");
    }
    while (localArrayList2.size() < j) {
      localArrayList2.add("0");
    }
    int i = 0;
    while (i < j)
    {
      if (Integer.parseInt((String)localArrayList1.get(i)) != Integer.parseInt((String)localArrayList2.get(i))) {
        return Integer.parseInt((String)localArrayList1.get(i)) - Integer.parseInt((String)localArrayList2.get(i));
      }
      i += 1;
    }
    return 0;
  }
  
  private static a a(Context paramContext, String paramString)
  {
    Iterator localIterator = paramContext.getPackageManager().getInstalledPackages(64).iterator();
    while (localIterator.hasNext())
    {
      paramContext = (PackageInfo)localIterator.next();
      if (paramContext.packageName.equals(paramString))
      {
        paramString = new a();
        paramString.a = paramContext.signatures[0].toByteArray();
        paramString.b = paramContext.versionCode;
        return paramString;
      }
    }
    return null;
  }
  
  public static String a()
  {
    return "Android " + Build.VERSION.RELEASE;
  }
  
  public static String a(byte[] paramArrayOfByte)
  {
    try
    {
      paramArrayOfByte = ((X509Certificate)CertificateFactory.getInstance("X.509").generateCertificate(new ByteArrayInputStream(paramArrayOfByte))).getPublicKey().toString();
      if (paramArrayOfByte.indexOf("modulus") != -1)
      {
        paramArrayOfByte = paramArrayOfByte.substring(paramArrayOfByte.indexOf("modulus") + 8, paramArrayOfByte.lastIndexOf(",")).trim();
        return paramArrayOfByte;
      }
    }
    catch (Exception paramArrayOfByte) {}
    return null;
  }
  
  public static boolean a(Context paramContext)
  {
    try
    {
      paramContext = paramContext.getPackageManager().getPackageInfo("com.alipay.android.app", 128);
      return paramContext != null;
    }
    catch (PackageManager.NameNotFoundException paramContext) {}
    return false;
  }
  
  private static boolean a(PackageInfo paramPackageInfo)
  {
    int i = paramPackageInfo.applicationInfo.flags;
    return ((i & 0x1) == 0) && ((i & 0x80) == 0);
  }
  
  public static boolean a(String paramString)
  {
    return Pattern.compile("^http(s)?://([a-z0-9_\\-]+\\.)*(alipay|taobao)\\.(com|net)(:\\d+)?(/.*)?$").matcher(paramString).matches();
  }
  
  public static String b()
  {
    Object localObject2 = c();
    int i = ((String)localObject2).indexOf("-");
    Object localObject1 = localObject2;
    if (i != -1) {
      localObject1 = ((String)localObject2).substring(0, i);
    }
    i = ((String)localObject1).indexOf("\n");
    localObject2 = localObject1;
    if (i != -1) {
      localObject2 = ((String)localObject1).substring(0, i);
    }
    return "Linux " + (String)localObject2;
  }
  
  public static boolean b(Context paramContext)
  {
    for (;;)
    {
      try
      {
        paramContext = paramContext.getPackageManager().getPackageInfo("com.eg.android.AlipayGphone", 128);
        if (paramContext == null) {
          return false;
        }
        paramContext = paramContext.versionName;
        ArrayList localArrayList1 = new ArrayList();
        ArrayList localArrayList2 = new ArrayList();
        localArrayList1.addAll(Arrays.asList(paramContext.split("\\.")));
        localArrayList2.addAll(Arrays.asList("7.0.0".split("\\.")));
        int j = Math.max(localArrayList1.size(), localArrayList2.size());
        if (localArrayList1.size() < j)
        {
          localArrayList1.add("0");
          continue;
        }
        if (localArrayList2.size() < j)
        {
          localArrayList2.add("0");
          continue;
          if (i < j)
          {
            if (Integer.parseInt((String)localArrayList1.get(i)) != Integer.parseInt((String)localArrayList2.get(i)))
            {
              j = Integer.parseInt((String)localArrayList1.get(i));
              i = Integer.parseInt((String)localArrayList2.get(i));
              i = j - i;
              if (i < 0) {
                return false;
              }
            }
            else
            {
              i += 1;
            }
          }
          else
          {
            i = 0;
            continue;
          }
          return true;
        }
      }
      catch (PackageManager.NameNotFoundException paramContext)
      {
        return false;
      }
      int i = 0;
    }
  }
  
  private static String c()
  {
    try
    {
      Object localObject1 = new BufferedReader(new FileReader("/proc/version"), 256);
      try
      {
        String str2 = ((BufferedReader)localObject1).readLine();
        ((BufferedReader)localObject1).close();
        localObject1 = Pattern.compile("\\w+\\s+\\w+\\s+([^\\s]+)\\s+\\(([^\\s@]+(?:@[^\\s.]+)?)[^)]*\\)\\s+\\((?:[^(]*\\([^)]*\\))?[^)]*\\)\\s+([^\\s]+)\\s+(?:PREEMPT\\s+)?(.+)").matcher(str2);
        if (!((Matcher)localObject1).matches()) {
          return "Unavailable";
        }
      }
      finally
      {
        ((BufferedReader)localObject1).close();
      }
      if (localIOException.groupCount() >= 4) {
        break label75;
      }
    }
    catch (IOException localIOException)
    {
      return "Unavailable";
    }
    return "Unavailable";
    label75:
    String str1 = localIOException.group(1) + "\n" + localIOException.group(2) + " " + localIOException.group(3) + "\n" + localIOException.group(4);
    return str1;
  }
  
  public static String c(Context paramContext)
  {
    String str1 = a();
    String str2 = b();
    String str3 = d(paramContext);
    paramContext = e(paramContext);
    return " (" + str1 + ";" + str2 + ";" + str3 + ";;" + paramContext + ")(sdk android)";
  }
  
  private static String d()
  {
    String str = a.b;
    return str.substring(0, str.indexOf("://"));
  }
  
  public static String d(Context paramContext)
  {
    return paramContext.getResources().getConfiguration().locale.toString();
  }
  
  private static String e()
  {
    return "-1;-1";
  }
  
  public static String e(Context paramContext)
  {
    DisplayMetrics localDisplayMetrics = new DisplayMetrics();
    ((WindowManager)paramContext.getSystemService("window")).getDefaultDisplay().getMetrics(localDisplayMetrics);
    paramContext = new StringBuilder();
    paramContext.append(localDisplayMetrics.widthPixels);
    paramContext.append("*");
    paramContext.append(localDisplayMetrics.heightPixels);
    return paramContext.toString();
  }
  
  private static String f()
  {
    Random localRandom = new Random();
    StringBuilder localStringBuilder = new StringBuilder();
    int i = 0;
    if (i < 24)
    {
      switch (localRandom.nextInt(3))
      {
      }
      for (;;)
      {
        i += 1;
        break;
        localStringBuilder.append(String.valueOf((char)(int)Math.round(Math.random() * 25.0D + 65.0D)));
        continue;
        localStringBuilder.append(String.valueOf((char)(int)Math.round(Math.random() * 25.0D + 97.0D)));
        continue;
        localStringBuilder.append(String.valueOf(new Random().nextInt(10)));
      }
    }
    return localStringBuilder.toString();
  }
  
  public static String f(Context paramContext)
  {
    try
    {
      paramContext = ((ActivityManager)paramContext.getSystemService("activity")).getRunningAppProcesses().iterator();
      while (paramContext.hasNext())
      {
        ActivityManager.RunningAppProcessInfo localRunningAppProcessInfo = (ActivityManager.RunningAppProcessInfo)paramContext.next();
        if (localRunningAppProcessInfo.processName.contains("com.eg.android.AlipayGphone"))
        {
          paramContext = localRunningAppProcessInfo.processName;
          return paramContext;
        }
      }
      return null;
    }
    catch (Throwable paramContext) {}
    return "error";
  }
  
  public static String g(Context paramContext)
  {
    paramContext = paramContext.getPackageManager().getInstalledPackages(0);
    StringBuilder localStringBuilder = new StringBuilder();
    int i = 0;
    if (i < paramContext.size())
    {
      PackageInfo localPackageInfo = (PackageInfo)paramContext.get(i);
      int j = localPackageInfo.applicationInfo.flags;
      if (((j & 0x1) == 0) && ((j & 0x80) == 0))
      {
        j = 1;
        label68:
        if (j != 0)
        {
          if (!localPackageInfo.packageName.equals("com.eg.android.AlipayGphone")) {
            break label119;
          }
          localStringBuilder.append(localPackageInfo.packageName).append(localPackageInfo.versionCode).append("-");
        }
      }
      for (;;)
      {
        i += 1;
        break;
        j = 0;
        break label68;
        label119:
        if (!localPackageInfo.packageName.contains("theme")) {
          localStringBuilder.append(localPackageInfo.packageName).append("-");
        }
      }
    }
    paramContext = localStringBuilder.toString();
    if ((paramContext != null) && (paramContext.length() > 0)) {
      return paramContext.substring(0, paramContext.length() - 1);
    }
    return null;
  }
  
  private static DisplayMetrics h(Context paramContext)
  {
    DisplayMetrics localDisplayMetrics = new DisplayMetrics();
    ((WindowManager)paramContext.getSystemService("window")).getDefaultDisplay().getMetrics(localDisplayMetrics);
    return localDisplayMetrics;
  }
  
  public static final class a
  {
    public byte[] a;
    public int b;
  }
}


/* Location:              E:\apk\xiaoenai2\classes-dex2jar.jar!\com\alipay\sdk\util\k.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */