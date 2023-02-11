package tv.danmaku.ijk.media.player.ui;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.util.DisplayMetrics;

public class Utils {

    public static final String USER_AGENT = "User-Agent";

    public static float dp2px(Context context, float dpValue) {
        return Math.round((dpValue * context.getResources().getDisplayMetrics().densityDpi) / DisplayMetrics.DENSITY_DEFAULT);
    }

    public static String getUserAgent(Context context) {
        String versionName;
        try {
            String packageName = context.getPackageName();
            PackageInfo info = context.getPackageManager().getPackageInfo(packageName, 0);
            versionName = info.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            versionName = "?";
        }
        return context.getPackageName() + "/" + versionName + " (Linux;Android " + Build.VERSION.RELEASE + ") " + "IjkPlayerLib/0.8.9";
    }
}
