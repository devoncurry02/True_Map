package com.example.truemap

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import com.example.truemap.ui.theme.TrueMapTheme
import org.osmdroid.config.Configuration
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay
import org.osmdroid.util.GeoPoint
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import android.preference.PreferenceManager
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat


class MainActivity : ComponentActivity() {
    private var mapView: MapView? = null
    private var locationOverlay: MyLocationNewOverlay? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Configuration.getInstance().load(
            applicationContext,
            PreferenceManager.getDefaultSharedPreferences(applicationContext)
        )
        Configuration.getInstance().userAgentValue = packageName
        requestLocationPermission()
        enableEdgeToEdge()
        setContent {
            TrueMapTheme {
                OsmMapView { mv ->
                    if (mapView == null) {
                        mapView = mv
                        mapView?.apply {
                            setTileSource(TileSourceFactory.MAPNIK)
                            setMultiTouchControls(true)
                            controller.setZoom(15.0)
                        }
                        locationOverlay = MyLocationNewOverlay(
                            GpsMyLocationProvider(applicationContext),
                            mapView
                        ).apply {
                            enableMyLocation()
                        }
                        mapView?.overlays?.add(locationOverlay)
                    }
                }
            }
        }
    }
    private fun requestLocationPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
            != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), 1001)
        }
    }
    override fun onResume() {
        super.onResume()
        mapView?.onResume()
    }
    override fun onPause() {
        super.onPause()
        mapView?.onPause()
    }
}

@Composable
fun OsmMapView(onMapReady: (MapView) -> Unit) {
    val context = LocalContext.current
    val mapView = remember {
        MapView(context)
    }
    AndroidView(
        factory = {
            onMapReady(mapView)
            mapView
        },
        modifier = Modifier.fillMaxSize()
    )
}