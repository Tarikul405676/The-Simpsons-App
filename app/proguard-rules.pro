-repackageclasses
-keepattributes Signature
-keep class sun.misc.Unsafe { *; }
-keep class com.google.gson.stream.** { *; }
-keep class com.google.gson.examples.android.model.** { *; }
-keep public class * extends android.app.Activity { public void *(...); }

-keep class com.techbdhost.support.NetworkService.** { *; }
-keep class com.android.volley.** { *; }
-keep class org.apache.commons.logging.** { *; }
