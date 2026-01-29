package com.bcasekuritas.mybest.app.feature.news.rssconverter

data class RssFeed(
    var title: String? = "",
    var lastBuildDate: String? = "",
    var link: String? = "",
    var image: String? = "",
    var items: List<RssItem>? = null
)

