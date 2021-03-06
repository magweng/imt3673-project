package com.imt3673.project.sensors;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Vibrator;

import com.imt3673.project.graphics.Constants;
import com.imt3673.project.main.R;
import com.imt3673.project.utils.Utils;

/**
 * Haptic Feedback Manager
 */
public class HapticFeedbackManager {

    private final Vibrator vibrator;
    private boolean isTurnedOn;

    /**
     * Sets up haptic feedback using the vibrator if supported by the device.
     * @param context Context
     */
    public HapticFeedbackManager(final Context context) {
        this.vibrator = (Vibrator)context.getSystemService(Context.VIBRATOR_SERVICE);

        // Tell the user if the device does not support a vibrator
        if ((this.vibrator == null) || !this.vibrator.hasVibrator()) {
            Utils.alertMessage(context.getString(R.string.error_no_vibrator), context);
            this.isTurnedOn = false;
        } else {
            // Get user vibration preference
            SharedPreferences settings = context.getSharedPreferences(Constants.PREFERENCE_FILE, 0);
            this.isTurnedOn =  settings.getBoolean(Constants.PREFERENCE_VIBRATE,true);
        }
    }

    /**
     * Vibrates for the specified duration.
     * @param duration Duration in milliseconds
     */
    public void vibrate(final long duration) {
        if (this.vibrator != null && this.isTurnedOn)
            this.vibrator.vibrate(duration);
    }
}
