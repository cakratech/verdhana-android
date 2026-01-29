package com.bcasekuritas.mybest.app.feature.news.rssconverter

import okhttp3.ResponseBody
import org.xml.sax.InputSource
import retrofit2.Converter
import javax.xml.parsers.SAXParserFactory

internal class RssResponseBodyConverter : Converter<ResponseBody, RssFeed> {

    override fun convert(value: ResponseBody): RssFeed {
        val rssFeed = RssFeed()
        try {
            val parser = XMLParser()
            val parserFactory = SAXParserFactory.newInstance()
            val saxParser = parserFactory.newSAXParser()
            val xmlReader = saxParser.xmlReader
            xmlReader.contentHandler = parser
            val inputSource = InputSource(value.charStream())
            xmlReader.parse(inputSource)
            val items = parser.items
            rssFeed.items = items
            rssFeed.link = parser.headerLink
            rssFeed.image = parser.headerImage
            rssFeed.title = parser.headerTitle
            rssFeed.lastBuildDate = parser.headerDate
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return rssFeed
    }

}