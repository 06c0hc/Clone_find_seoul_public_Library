package com.example.librarylocation.data

//서버에서 응답받은 서울시 공공도서관 정보
data class SeoulPublicLibraryInfo(
    val RESULT: RESULT,
    val list_total_count: Int,
    val row: List<Row>
)