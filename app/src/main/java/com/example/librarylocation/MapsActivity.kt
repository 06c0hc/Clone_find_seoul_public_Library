package com.example.librarylocation

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.librarylocation.data.Library
import com.example.librarylocation.data.Row

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.example.librarylocation.databinding.ActivityMapsBinding
import com.google.android.gms.maps.model.LatLngBounds
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private lateinit var binding: ActivityMapsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMapsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
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
    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        mMap.setOnMarkerClickListener{ it ->
            val libraryObject = it.tag as Row //tag에 저장된 Library를 Row로 변환해서 사용
            var homePage = libraryObject.HMPG_URL
            if(!homePage.startsWith("http")){
                homePage = "http://${homePage}"
            }
            Intent(Intent.ACTION_VIEW, Uri.parse(homePage)).run{
                startActivity(this)
            }
            true
        }
        // Add a marker in Sydney and move the camera
        //val sydney = LatLng(-34.0, 151.0)
        //mMap.addMarker(MarkerOptions().position(sydney).title("Marker in Sydney"))
        //mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney))
        loadLibrary()
    }

    fun loadLibrary(){
        val retrofit = Retrofit.Builder()
            .baseUrl(SeoulOpenAPI.DOMAIN)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        //SeoulOpenService 서비스 인터페이스를 구현한 클래스의 객체를 얻음
        val service = retrofit.create(SeoulOpenService::class.java)

        //서비스 객체를 이용하여 네트워크 통신 시도
       service.getLibrary(SeoulOpenAPI.API_KEY).enqueue(object : Callback<Library> {
            override fun onResponse(call: Call<Library>, response: Response<Library>) {
                showLibraries(response.body())
            }

            override fun onFailure(call: Call<Library>, t: Throwable) {
                t.printStackTrace()
            }

        })
    }

    fun showLibraries(libraries: Library?){
        val latLngBounds = LatLngBounds.builder()

        for(library in libraries?.SeoulPublicLibraryInfo?.row?: listOf()){
            val position = LatLng(library.XCNTS.toDouble(),library.YDNTS.toDouble())
            val marker = MarkerOptions().position(position)

            //마커를 누르면 해당 도서관의 정보가 뜨도록 함
            val markerObj = mMap.addMarker(marker)
            markerObj.tag = library

            //마커들이 보이는 뷰로 지도 좌표를 이동시키기 위한 작업
            latLngBounds.include(marker.position)
        }

        val bounds = latLngBounds.build()
        val padding = 0
        val updatedCamera = CameraUpdateFactory.newLatLngBounds(bounds, padding)
        mMap.moveCamera(updatedCamera)//위치가 갱신된 카메라를 던져줌

    }
}