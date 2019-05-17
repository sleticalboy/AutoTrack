package com.sleticalboy.autotrack.helper;

import android.content.res.Resources;
import com.sleticalboy.autotrack.AutoTrack;

/**
 * Created on 19-5-16.
 *
 * @author leebin
 */
public final class ResHelper {

    private ResHelper() {
        throw new AssertionError();
    }

    public static String getEntryName(int id) {
        try {
            return AutoTrack.sApp.getResources().getResourceEntryName(id);
        } catch (Resources.NotFoundException e) {
            return null;
        }
    }
}
