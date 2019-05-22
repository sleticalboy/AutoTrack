package com.sleticalboy.autotrack.helper;

import android.app.ActionBar;
import android.app.Activity;
import android.app.Fragment;
import android.content.ContextWrapper;
import android.content.pm.PackageManager;
import android.view.View;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

/**
 * Created on 19-5-11.
 *
 * @author leebin
 */
public final class ActivityHelper {

    private ActivityHelper() {
        throw new AssertionError("Utility class can not be initialized");
    }

    public static Activity findActivity(Object obj) {
        if (obj instanceof Activity) {
            return (Activity) obj;
        } else if (obj instanceof View) {
            return findActivity(((View) obj).getContext());
        } else if (obj instanceof ContextWrapper) {
            return findActivity(((ContextWrapper) obj).getBaseContext());
        } else if (obj instanceof Fragment) {
            return ((Fragment) obj).getActivity();
        } else if (obj instanceof androidx.fragment.app.Fragment) {
            return ((androidx.fragment.app.Fragment) obj).requireActivity();
        }
        return null;
    }

    public static CharSequence getTitle(@NonNull Activity page) {
        CharSequence title;
        try {
            title = getToolbarTitle(page);
            if (title == null) {
                final PackageManager pkgMgr = page.getPackageManager();
                title = pkgMgr.getActivityInfo(page.getComponentName(), 0).loadLabel(pkgMgr);
            }
        } catch (PackageManager.NameNotFoundException e) {
            return page.getClass().getSimpleName();
        }
        return title;
    }

    public static CharSequence getToolbarTitle(@NonNull Activity page) {
        if (page instanceof AppCompatActivity) {
            final androidx.appcompat.app.ActionBar actionBar = ((AppCompatActivity) page).getSupportActionBar();
            if (actionBar != null && actionBar.getTitle() != null) {
                return actionBar.getTitle();
            }
        }
        final ActionBar actionBar = page.getActionBar();
        if (actionBar != null && actionBar.getTitle() != null) {
            return actionBar.getTitle();
        }
        return null;
    }
}
