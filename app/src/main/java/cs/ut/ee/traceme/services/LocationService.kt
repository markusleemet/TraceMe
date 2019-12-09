package cs.ut.ee.traceme.services

import android.app.Service
import android.content.Intent
import android.os.IBinder

class LocationService : Service() {
    override fun onBind(intent: Intent?): IBinder? {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        return super.onStartCommand(intent, flags, startId)
        // https://stackoverflow.com/questions/11292993/always-show-service-in-notification-bar
    }

    override fun onCreate() {
        super.onCreate()
    }

    override fun onDestroy() {
        super.onDestroy()
    }
}
