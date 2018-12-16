package com.cenes.Manager;

import android.content.Context;
import android.content.Intent;
/**
 * Created by puneet on 11/8/17.
 */

public interface AlertManager {
    public void getAlert(final Context context, String message, String title, final Intent intent, final boolean isFinish, String buttonString);
}
