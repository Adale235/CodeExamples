package com.example.moveyourfeet.classes

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Context.LOCATION_SERVICE
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import androidx.core.app.ActivityCompat.requestPermissions
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.checkSelfPermission
////Alexander Schröder 4schoa24///
//class to get the current location
class GPSLocationOnClick() : LocationListener {

    fun getCurrentLocation(context: Context?, activity: Activity?) :Location? {

        val mgr = context?.getSystemService(LOCATION_SERVICE) as LocationManager

        if (checkSelfPermission(
                context,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {

            mgr.requestLocationUpdates(LocationManager.GPS_PROVIDER,6000,10f,this)
            var loc = mgr.getLastKnownLocation(LocationManager.GPS_PROVIDER)
            return loc
        } else {
            activity?.requestPermissions(
                arrayOf<String>(Manifest.permission.ACCESS_FINE_LOCATION),
                0
            )
            return null
        }


    }


    override fun onLocationChanged(location: Location?) {

    }

    override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {

    }

    override fun onProviderEnabled(provider: String?) {

    }

    override fun onProviderDisabled(provider: String?) {

    }


}