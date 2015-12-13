package com.linjiaxiaohai.sendkeys.utils;

import android.content.Context;

/**
 * Created by Meng on 15/12/13.
 */
public class FloatKeeper {

    public static final String SP_FLOAT_TOP = "sp_float_top";

    public static boolean isFloatTop(Context context) {
        return SharedPreferenceUtils.getBooleanValue(context, SP_FLOAT_TOP, false);
    }

    public static void setFloatTop(Context context, boolean isTop) {
        SharedPreferenceUtils.saveBooleanValue(context, SP_FLOAT_TOP, isTop);
    }

}
