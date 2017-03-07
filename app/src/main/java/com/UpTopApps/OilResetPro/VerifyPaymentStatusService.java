package com.UpTopApps.OilResetPro;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

public class VerifyPaymentStatusService extends Service {
    public VerifyPaymentStatusService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }
}
