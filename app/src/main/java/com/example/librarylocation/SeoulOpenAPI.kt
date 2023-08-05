package com.example.librarylocation

import com.example.librarylocation.data.Library
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path

object SeoulOpenAPI {
    val DOMAIN = "http://openapi.seoul.go.kr:8088" //서울시 공공 도서관 url
    val API_KEY = "Your API Key" //서울 공공 데이터 홈페이지에서 발급 받은 인증키
}


interface SeoulOpenService{
    //공공 도서관 정보 요청
    @GET("/{key}/json/SeoulPublicLibraryInfo/1/200/")
    fun getLibrary(@Path("key") key:String) : Call<Library>
}

