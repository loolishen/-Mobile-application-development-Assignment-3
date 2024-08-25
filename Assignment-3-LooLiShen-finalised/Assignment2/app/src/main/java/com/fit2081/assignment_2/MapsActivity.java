package com.fit2081.assignment_2;

import androidx.fragment.app.FragmentActivity;

import android.location.Address;
import android.location.Geocoder;
import android.os.Build;
import android.os.Bundle;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.fit2081.assignment_2.databinding.ActivityMapsBinding;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private ActivityMapsBinding binding;

    public String countryToFocus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMapsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        // Retrieve eventLocation from intent extras
        countryToFocus = getIntent().getStringExtra("eventLocation");
        if (countryToFocus != null) {
            findCountryMoveCamera();
        }
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

    }

    private void findCountryMoveCamera() {
        // initialise Geocode to search location using String
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());

        // getFromLocationName method works for API 33 and above
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            /**
             * countryToFocus: String value, any string we want to search
             * maxResults: how many results to return if search was successful
             * successCallback method: if results are found, this method will be executed
             * runs in a background thread
             */
            geocoder.getFromLocationName(countryToFocus, 1, addresses -> {
                // if there are results, this condition would return true
                if (!addresses.isEmpty()) {
                    // run on UI thread as the user interface will update once set map location
                    runOnUiThread(() -> {
                        // define new LatLng variable using the first address from list of addresses
                        LatLng newAddressLocation = new LatLng(
                                addresses.get(0).getLatitude(),
                                addresses.get(0).getLongitude()
                        );

                        // move the camera to the middle of the country
                        mMap.moveCamera(CameraUpdateFactory.newLatLng(newAddressLocation));

                        // add a new Marker with title as the countryToFocus
                        mMap.addMarker(
                                new MarkerOptions()
                                        .position(newAddressLocation)
                                        .title(countryToFocus)
                        );

                        // set zoom level to 10
                        mMap.animateCamera(CameraUpdateFactory.zoomTo(10));

                        // Adjust the camera to ensure the marker appears in the middle of the screen
                        mMap.getUiSettings().setScrollGesturesEnabled(false);
                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(newAddressLocation, 10));

                        // Add click listener to the map
                        mMap.setOnMapClickListener(latLng -> {
                            // Perform reverse geocoding to get the address information for the clicked location
                            Geocoder reverseGeocoder = new Geocoder(MapsActivity.this, Locale.getDefault());
                            try {
                                List<Address> addressesList = reverseGeocoder.getFromLocation(latLng.latitude, latLng.longitude, 1);
                                if (!addressesList.isEmpty()) {
                                    Address address = addressesList.get(0);
                                    String countryName = address.getCountryName();
                                    // Show a Toast message with the name of the country
                                    Toast.makeText(MapsActivity.this, "Clicked on: " + countryName, Toast.LENGTH_SHORT).show();
                                } else {
                                    // Show a Toast message if the address is not found
                                    Toast.makeText(MapsActivity.this, "Country not found for clicked location", Toast.LENGTH_SHORT).show();
                                }
                            } catch (IOException e) {
                                e.printStackTrace();
                                // Show a Toast message if an error occurs during reverse geocoding
                                Toast.makeText(MapsActivity.this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
                    });
                } else {
                    // Show a Toast message if the address is not found
                    runOnUiThread(() -> {
                        Toast.makeText(MapsActivity.this, "Category address not found", Toast.LENGTH_SHORT).show();
                    });
                }
            });
        }
    }



}