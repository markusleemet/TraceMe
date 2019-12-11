package cs.ut.ee.traceme.services

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.IBinder
import android.os.Looper
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import cs.ut.ee.traceme.R
import cs.ut.ee.traceme.activities.TraceActivity


class LocationService : Service() {
    private val channelId = "234"
    private val notificationId = 345
    private lateinit var notificationManagerCompat: NotificationManagerCompat
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var locationCallback: LocationCallback
    private lateinit var locationRequest: LocationRequest

    override fun onBind(intent: Intent?): IBinder? {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        //open channel for notifications
        createNotificationChannel()

        // Create an explicit intent for an Activity in your app
        val notificationIntent = Intent(this, TraceActivity::class.java).apply {
            this.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent: PendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0)

        //create notification
        val notification = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(R.drawable.ic_launcher_background)
            .setContentTitle("TraceMe")
            .setContentText("Your location sharing is turned on")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(pendingIntent)
            .setAutoCancel(false)
            .setOngoing(true)


        notificationManagerCompat = NotificationManagerCompat.from(this)
        with(notificationManagerCompat) {
            // notificationId is a unique int for each notification that you must define
            notify(notificationId, notification.build())
        }

        //create location request, callback and start location updates
        fusedLocationClient = FusedLocationProviderClient(this)
        locationRequest = createLocationRequest()!!
        locationCallback = createLocationCallback()
        startLocationUpdates(locationRequest, locationCallback)


        return super.onStartCommand(notificationIntent, flags, startId)
    }


    override fun onDestroy() {
        Log.i("lüliti", "service is killed")
        notificationManagerCompat.cancel(notificationId)
        endLocationUpdates()
        super.onDestroy()
    }

    private fun createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = getString(R.string.channel_name)
            val descriptionText = getString(R.string.channel_description)
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(channelId, name, importance).apply {
                description = descriptionText
            }
            // Register the channel with the system
            val notificationManager: NotificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    private fun createLocationRequest():LocationRequest? {
        Log.i("lüliti", "Created location request")
        return LocationRequest.create()?.apply {
            interval = 5000
            fastestInterval = 3000
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        }
    }

    private fun createLocationCallback(): LocationCallback{
        Log.i("lüliti", "Created location callback")
        return object : LocationCallback(){
            override fun onLocationResult(p0: LocationResult?) {
                super.onLocationResult(p0)
                Log.i("lüliti", "got location... I think")
            }
        }
    }

    private fun startLocationUpdates(locationRequest: LocationRequest?, locationCallback: LocationCallback) {
        Log.i("lüliti", "Location updates are started")
        fusedLocationClient.requestLocationUpdates(locationRequest,
            locationCallback,
            Looper.getMainLooper())
    }

    private fun endLocationUpdates(){
        Log.i("lüliti", "Location updates are stopped")
        fusedLocationClient.removeLocationUpdates(locationCallback)
    }
}
