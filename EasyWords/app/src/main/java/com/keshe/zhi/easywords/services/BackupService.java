package com.keshe.zhi.easywords.services;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

public class BackupService extends Service {
    public BackupService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }
}
