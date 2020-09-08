package com.example.moveyourfeet.fragments


import android.Manifest
import android.content.Context.LOCATION_SERVICE

import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.example.moveyourfeet.R
import com.example.moveyourfeet.classes.GPSLocationOnClick
import com.example.moveyourfeet.database.PoiDataEntity
import com.example.moveyourfeet.database.PoiDatabase
import com.example.moveyourfeet.database.SpeedDataEntity
import com.example.moveyourfeet.database.SpeedDatabase
import kotlinx.android.synthetic.main.home_fragment.view.*
import kotlinx.android.synthetic.main.home_fragment.view.btn_homeFragment_createPoi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.overlay.ItemizedIconOverlay
import org.osmdroid.views.overlay.OverlayItem
import java.util.*
import kotlin.collections.ArrayList
import kotlin.math.round

////Alexander Schröder 4schoa24///
class HomeFragment : Fragment(), LocationListener {

    //permission request
    private val REQUEST_PERMISSIONS_REQUEST_CODE = 1

    ///Location variables
    var currentLocation: Location? = null
    var isRoutingStarted: Boolean = false

    //region Database
    //database variables
    lateinit var poiDatabase: PoiDatabase
    lateinit var speedDatabase: SpeedDatabase
    lateinit var  locMgr: LocationManager
    //map marker variables
    var poiList: List<PoiDataEntity>? = null

    //endregion
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        var homeFragmentView = inflater.inflate(R.layout.home_fragment, container, false)

        //region mapView configuration preference
        Configuration.getInstance()
            .load(
                context,
                androidx.preference.PreferenceManager.getDefaultSharedPreferences(context)
            )
        //endregion

        //region Permission handling
        permissionRequest(
            arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            )
        )
        //endregion

        //region Initialize variables
        currentLocation = GPSLocationOnClick().getCurrentLocation(context, activity)
        poiDatabase = PoiDatabase.getDatabase(requireActivity().application)
        speedDatabase = SpeedDatabase.getDatabase((requireActivity().application))
        locMgr = context?.getSystemService(LOCATION_SERVICE) as LocationManager
        //endregion

        //region functions
        //configuring the initial map view
        mapViewConfig(homeFragmentView)
        //handling all map markers
        handleMapMarker(homeFragmentView)
        //endregion



        //region Button handling
        //adding markers
        homeFragmentView.btn_homeFragment_createPoi.setOnClickListener {
            addMapMarker(homeFragmentView)
            Toast.makeText(activity,"Map marker created!",Toast.LENGTH_LONG).show()
        }

        //start location updates with permission check
        homeFragmentView.btn_homeFragment_startRoute.setOnClickListener {
            handleLocationRouting()
            isRoutingStarted=true
            Toast.makeText(activity,"Routing started!",Toast.LENGTH_LONG).show()

        }

        //stop location manager from updating
        homeFragmentView.btn_homeFragment_stopRoute.setOnClickListener {
            locMgr.removeUpdates(this)
            isRoutingStarted=false
            Toast.makeText(activity,"Routing stopped!",Toast.LENGTH_LONG).show()

        }
        //endregion

        return homeFragmentView
    }

    //region private functions
    //add data for the graph in the statistics fragment
    private fun addStatisticData(location: Location) {
        lifecycleScope.launch {
            val c = Calendar.getInstance()
            val minute = c.get(Calendar.MINUTE)
            val hour = c.get(Calendar.HOUR)


            withContext(Dispatchers.IO) {
                speedDatabase.speedDataDao().insertSpeedValue(
                    SpeedDataEntity(
                        0,
                        hour.toDouble(),
                        minute.toDouble(),
                        round(location.speed * 3.6f).toDouble()
                    )
                )
            }
        }
    }

    //add map markers to the marker list
    private fun addMapMarker(homeFragmentView: View?) {

        lifecycleScope.launch {

            val poiTitle = homeFragmentView!!.et_homeFragment_poiName.text.toString()
            withContext(Dispatchers.IO) {
                poiDatabase.PoiDao().insertPoi(
                    PoiDataEntity(
                        0,
                        poiTitle,
                        "Blank",
                        currentLocation!!.latitude,
                        currentLocation!!.longitude
                    )
                )
            }
        }
    }

    //delete map markers
    private fun deleteMapMarker(item: OverlayItem) {
        lifecycleScope.launch {


            withContext(Dispatchers.IO) {
                poiDatabase.PoiDao()
                    .deleteByTitleAndLoc(item.title, item.point.latitude, item.point.longitude)
            }
        }

    }

    //handle gesture events on map markers and fill map with markers from marker list
    private fun handleMapMarker(homeFragmentView: View) {
        val markerGestureListener =
            object : ItemizedIconOverlay.OnItemGestureListener<OverlayItem> {
                override fun onItemLongPress(i: Int, item: OverlayItem): Boolean {
                    deleteMapMarker(item)
                    Toast.makeText(activity, item.title + " was deleted.", Toast.LENGTH_SHORT)
                        .show();
                    return true;
                }

                override fun onItemSingleTapUp(i: Int, item: OverlayItem): Boolean {
                    Toast.makeText(activity, item.title, Toast.LENGTH_SHORT).show();
                    return true;
                }
            }
        val marker = ItemizedIconOverlay<OverlayItem>(
            activity,
            arrayListOf<OverlayItem>(),
            markerGestureListener
        )

        lifecycleScope.launch {
            withContext(Dispatchers.IO) {
                poiList = poiDatabase.PoiDao().getAllPoi()
            }
            if (poiList != null) {
                for (pois in poiList!!) {
                    val tempPoi = OverlayItem(
                        pois.title,
                        pois.snippet,
                        GeoPoint(pois.latitude, pois.longitude)
                    )
                    marker.addItem(tempPoi)
                }
            }
        }
        homeFragmentView.mv_homeFragment.overlays.add(marker)
    }

    //map view configuration and center map
    private fun mapViewConfig(view: View) {

        view.mv_homeFragment.setMultiTouchControls(true)
        view.mv_homeFragment.controller.setZoom(19.0)
        view.mv_homeFragment.controller.setCenter(
            GeoPoint(
                currentLocation!!.latitude,
                currentLocation!!.longitude
            )
        )
        view.mv_homeFragment.setTileSource(TileSourceFactory.MAPNIK)
    }

    //location handling from button
    private fun handleLocationRouting() {
        if (ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {

            locMgr.requestLocationUpdates(LocationManager.GPS_PROVIDER, 6000, 10f, this)


        } else {
            activity?.requestPermissions(
                arrayOf<String>(Manifest.permission.ACCESS_FINE_LOCATION),
                0
            )
        }
    }

    //endregion

    //region permission handling
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String?>,
        grantResults: IntArray
    ) {
        val permissionsToRequest: ArrayList<String?> = ArrayList()
        for (i in grantResults.indices) {
            permissionsToRequest.add(permissions[i])
        }
        if (permissionsToRequest.size > 0) {
            ActivityCompat.requestPermissions(
                requireActivity(),
                permissionsToRequest.toArray(arrayOfNulls(0)),
                REQUEST_PERMISSIONS_REQUEST_CODE
            )
        }
    }

    private fun permissionRequest(permissions: Array<String>) {
        val permissionsToRequest: ArrayList<String> = ArrayList()
        for (permission in permissions) {
            if (ContextCompat.checkSelfPermission(requireContext(), permission)
                != PackageManager.PERMISSION_GRANTED
            ) {
                // Permission is not granted
                permissionsToRequest.add(permission)
            }
        }
        if (permissionsToRequest.size > 0) {
            ActivityCompat.requestPermissions(
                requireActivity(),
                permissionsToRequest.toArray(arrayOfNulls(0)),
                REQUEST_PERMISSIONS_REQUEST_CODE
            )
        }
    }

    //endregion

    //region location handling functions
    override fun onLocationChanged(location: Location?) {
        location?.apply {
            addStatisticData(location)
        }
    }

    override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {

    }

    override fun onProviderEnabled(provider: String?) {

    }

    override fun onProviderDisabled(provider: String?) {

    }
    //endregion

    override fun onDestroy() {
        super.onDestroy()
        locMgr.removeUpdates(this)
    }

    override fun onPause() {
        super.onPause()
        locMgr.removeUpdates(this)
    }

    override fun onResume() {
        super.onResume()
        if (isRoutingStarted)
        {
            handleLocationRouting()
        }
    }
}