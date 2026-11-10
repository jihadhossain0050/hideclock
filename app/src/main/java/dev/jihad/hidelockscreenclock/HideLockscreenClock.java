package dev.jihad.hidelockscreenclock;

import android.content.res.Resources;
import android.view.View;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

/**
 * Hides only the Xiaomi lock-screen views identified in the user's UIAutomator dumps.
 * This module must be scoped to com.android.systemui in LSPosed.
 */
public final class HideLockscreenClock implements IXposedHookLoadPackage {
    private static final String TARGET_PACKAGE = "com.android.systemui";
    private static final String TAG = "[HideLockscreenClock] ";

    // Resource entry names observed on the user's HyperOS 2 / Android 15 device.
    private static final Set<String> TARGET_RESOURCE_NAMES = new HashSet<>(Arrays.asList(
            "time_view",       // Lock-screen clock (observed in first dump)
            "content_area1",   // Month container (contentDescription: November)
            "content_area2",   // Date container (contentDescription: November 10)
            "text_area"        // Carrier label (e.g. Grameenphone / Teletalk)
    ));

    @Override
    public void handleLoadPackage(XC_LoadPackage.LoadPackageParam lpparam) {
        if (!TARGET_PACKAGE.equals(lpparam.packageName)) {
            return;
        }

        // Prevent SystemUI from making one of the target views visible again.
        XposedHelpers.findAndHookMethod(
                View.class,
                "setVisibility",
                int.class,
                new XC_MethodHook() {
                    @Override
                    protected void beforeHookedMethod(MethodHookParam param) {
                        View view = (View) param.thisObject;
                        if (isTargetView(view)) {
                            param.args[0] = View.GONE;
                        }
                    }
                }
        );

        // Hide target views immediately after they are attached to the window.
        XposedHelpers.findAndHookMethod(
                View.class,
                "onAttachedToWindow",
                new XC_MethodHook() {
                    @Override
                    protected void afterHookedMethod(MethodHookParam param) {
                        View view = (View) param.thisObject;
                        if (isTargetView(view) && view.getVisibility() != View.GONE) {
                            view.setVisibility(View.GONE);
                            XposedBridge.log(TAG + "Hidden " + resourceName(view));
                        }
                    }
                }
        );

        XposedBridge.log(TAG + "Hooks installed in com.android.systemui; targets="
                + TARGET_RESOURCE_NAMES);
    }

    private static boolean isTargetView(View view) {
        String name = resourceName(view);
        return name != null && TARGET_RESOURCE_NAMES.contains(name);
    }

    private static String resourceName(View view) {
        if (view == null || view.getId() == View.NO_ID) {
            return null;
        }

        try {
            Resources resources = view.getResources();
            // Compare the entry name rather than a hard-coded numeric resource ID.
            return resources.getResourceEntryName(view.getId());
        } catch (RuntimeException ignored) {
            return null;
        }
    }
}
