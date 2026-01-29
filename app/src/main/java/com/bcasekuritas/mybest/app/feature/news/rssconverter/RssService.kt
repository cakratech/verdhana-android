package com.bcasekuritas.mybest.app.feature.news.rssconverter

import retrofit2.Call
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Url

interface RssService {

    /**
     * No baseUrl defined. Each RSS Feed will be consumed by it's Url
     * @param url RSS Feed Url
     * @return Retrofit Call
     */
    @GET
    fun getRss(@Url url: String): Call<RssFeed>
}