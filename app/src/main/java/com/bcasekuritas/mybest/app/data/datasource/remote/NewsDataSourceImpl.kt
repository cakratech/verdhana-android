package com.bcasekuritas.mybest.app.data.datasource.remote

import com.bcasekuritas.mybest.app.domain.datasource.remote.NewsDataSource
import com.bcasekuritas.mybest.app.domain.dto.request.CorporateActionTabRequest
import com.bcasekuritas.mybest.app.domain.dto.request.NewsFeedByStockRequest
import com.bcasekuritas.mybest.app.domain.dto.request.NewsFeedRequest
import com.bcasekuritas.mybest.app.domain.dto.request.NewsFeedSearchRequest
import com.bcasekuritas.mybest.app.domain.dto.request.NewsPromoRequest
import com.bcasekuritas.mybest.app.domain.dto.request.NewsResearchContentReq
import com.bcasekuritas.mybest.app.domain.dto.request.PromoBannerReq
import com.bcasekuritas.mybest.app.domain.dto.request.StockPickReportReq
import com.bcasekuritas.mybest.app.domain.dto.request.StockPickRequest
import com.bcasekuritas.mybest.app.feature.news.rssconverter.RssConverterFactory
import com.bcasekuritas.mybest.app.feature.news.rssconverter.RssFeed
import com.bcasekuritas.mybest.app.feature.news.rssconverter.RssService
import com.bcasekuritas.rabbitmq.network.OLTService
import com.bcasekuritas.rabbitmq.proto.news.CorporateActionCalendarGetByStockRequest
import com.bcasekuritas.rabbitmq.proto.news.CorporateActionCalendarGetByStockResponse
import com.bcasekuritas.rabbitmq.proto.news.LoginBannerResponse
import com.bcasekuritas.rabbitmq.proto.news.NewsInfoFeedRequest
import com.bcasekuritas.rabbitmq.proto.news.NewsInfoFeedResponse
import com.bcasekuritas.rabbitmq.proto.news.NewsInfoFeedSearchByStockRequest
import com.bcasekuritas.rabbitmq.proto.news.NewsInfoFeedSearchByStockResponse
import com.bcasekuritas.rabbitmq.proto.news.NewsInfoFeedSearchRequest
import com.bcasekuritas.rabbitmq.proto.news.NewsInfoFeedSearchResponse
import com.bcasekuritas.rabbitmq.proto.news.NewsInfoPromoRequest
import com.bcasekuritas.rabbitmq.proto.news.NewsInfoResponse
import com.bcasekuritas.rabbitmq.proto.news.NewsResearchContentRequest
import com.bcasekuritas.rabbitmq.proto.news.NewsResearchContentResponse
import com.bcasekuritas.rabbitmq.proto.news.NewsStockPickSingleRequest
import com.bcasekuritas.rabbitmq.proto.news.NewsStockPickSingleResponse
import com.bcasekuritas.rabbitmq.proto.news.PromotionBannerRequest
import com.bcasekuritas.rabbitmq.proto.news.PromotionBannerResponse
import com.bcasekuritas.rabbitmq.proto.news.SearchNewsResearchContentRequest
import com.bcasekuritas.rabbitmq.proto.news.SearchNewsResearchContentResponse
import com.bcasekuritas.rabbitmq.proto.news.ViewStockPickResearchReportRequest
import com.bcasekuritas.rabbitmq.proto.news.ViewStockPickResearchReportResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.Retrofit
import retrofit2.awaitResponse
import timber.log.Timber
import javax.inject.Inject

class NewsDataSourceImpl @Inject constructor(
    private val oltService: OLTService
) : NewsDataSource {
    /*override suspend fun getNewsInfo(newsRequest: NewsRequest?): NewsInfoResponse? {
        val newsInfoRequest = NewsInfoRequest
            .newBuilder()
            .setUserId(newsRequest?.userId)
            .setSessionId(newsRequest?.sessionId)
            .setTags(newsRequest?.tags)
            .setPublishDateStart(newsRequest!!.publishDateStart)
            .setPublishDateEnd(newsRequest!!.publishDateEnd)
            .build()

        return oltService.getNewsInfo(newsInfoRequest)
    }*/

    override suspend fun getNewsPromoInfo(newsPromoRequest: NewsPromoRequest): NewsInfoResponse? {
        val newsPromoReq = NewsInfoPromoRequest
            .newBuilder()
            .setUserId(newsPromoRequest.userId)
            .setSessionId(newsPromoRequest.sessionId)
//            .setPromo(newsPromoRequest.promo)
            .build()

        return oltService.getNewsPromo(newsPromoReq)
    }

    override suspend fun getPromoBanner(promotionBannerRequest: PromoBannerReq): PromotionBannerResponse? {

        val promotionBannerRequest = PromotionBannerRequest
            .newBuilder()
            .setUserId(promotionBannerRequest.userId)
            .setSessionId(promotionBannerRequest.sessionId)
            .build()

        return oltService.getPromoBanner(promotionBannerRequest)
    }

    override suspend fun getStockPick(stockPickRequest: StockPickRequest): NewsStockPickSingleResponse? {
        val stockPickReq = NewsStockPickSingleRequest
            .newBuilder()
            .setUserId(stockPickRequest.userId)
            .setSessionId(stockPickRequest.sessionId)
            .setStatus(stockPickRequest.status)
            .build()

        return oltService.getStockPick(stockPickReq)
    }

    override suspend fun getNewsRssFeed(link: String): RssFeed? {
        return withContext(Dispatchers.IO) {
            val retrofit = Retrofit.Builder()
                .baseUrl("https://www.cnbcindonesia.com/")
                .addConverterFactory(RssConverterFactory.create())
                .build()
            var response: RssFeed? = null
            response = try {
                val service = retrofit.create(RssService::class.java)
                service.getRss(link).awaitResponse().body()
            } catch (e: Exception) {
                Timber.d("rssfeed " + e.message)
                null
            }
            response
        }
    }

    override suspend fun getNewsBannerLogin(): LoginBannerResponse? {
        val stockPickReq = com.bcasekuritas.rabbitmq.proto.news.LoginBannerRequest
            .newBuilder()
            .setSessionId("")
            .build()

        return oltService.getNewsBannerLogin(stockPickReq)
    }

    override suspend fun getResearchNews(newsResearchContentReq: NewsResearchContentReq): NewsResearchContentResponse? {
        val newsResearchContentRequest = NewsResearchContentRequest.newBuilder()
            .setUserId(newsResearchContentReq.userId)
            .setSessionId(newsResearchContentReq.sessionId)
            .setPage(newsResearchContentReq.page)
            .setSize(newsResearchContentReq.size)
            .build()

        return oltService.getResearchNews(newsResearchContentRequest)
    }

    override suspend fun getStockPickReport(stockPickReportReq: StockPickReportReq): ViewStockPickResearchReportResponse? {
        val stockPickReport = ViewStockPickResearchReportRequest.newBuilder()
            .setUserId(stockPickReportReq.userId)
            .setSessionId(stockPickReportReq.sessionId)
            .build()

        return oltService.getStockPickReport(stockPickReport)
    }

    override suspend fun getNewsFeed(newsFeedRequest: NewsFeedRequest): NewsInfoFeedResponse? {
        val request = NewsInfoFeedRequest.newBuilder()
            .setUserId(newsFeedRequest.userId)
            .setSessionId(newsFeedRequest.sessionId)
            .setPage(newsFeedRequest.page)
            .setSize(newsFeedRequest.size)
            .setNewsStatus(newsFeedRequest.newsStatus)
            .build()

        return oltService.getNewsFeed(request)
    }

    override suspend fun getNewsFeedByStock(newsFeedByStockRequest: NewsFeedByStockRequest): NewsInfoFeedSearchByStockResponse? {
        val request = NewsInfoFeedSearchByStockRequest.newBuilder()
            .setUserId(newsFeedByStockRequest.userId)
            .setSessionId(newsFeedByStockRequest.sessionId)
            .setPage(newsFeedByStockRequest.page)
            .setSize(newsFeedByStockRequest.size)
            .addAllStockCodes(newsFeedByStockRequest.stockCode)
            .build()

        return oltService.getNewsFeedByStock(request)
    }

    override suspend fun getNewsFeedSearch(newsFeedSearchRequest: NewsFeedSearchRequest): NewsInfoFeedSearchResponse? {
        val request = NewsInfoFeedSearchRequest.newBuilder()
            .setUserId(newsFeedSearchRequest.userId)
            .setSessionId(newsFeedSearchRequest.sessionId)
            .setPage(newsFeedSearchRequest.page)
            .setSize(newsFeedSearchRequest.size)
            .setSearchKey(newsFeedSearchRequest.searchKey)
            .build()

        return oltService.getNewsFeedSearch(request)
    }

    override suspend fun getResearchContentSearch(newsFeedSearchRequest: NewsFeedSearchRequest): SearchNewsResearchContentResponse? {
        val request = SearchNewsResearchContentRequest.newBuilder()
            .setUserId(newsFeedSearchRequest.userId)
            .setSessionId(newsFeedSearchRequest.sessionId)
            .setPage(newsFeedSearchRequest.page)
            .setSize(newsFeedSearchRequest.size)
            .setSearchKey(newsFeedSearchRequest.searchKey)
            .setReport(newsFeedSearchRequest.report)
            .build()

        return oltService.getResearchContentSearch(request)
    }

    override suspend fun getCorporateActionTabByStockCode(corporateActionTabRequest: CorporateActionTabRequest): CorporateActionCalendarGetByStockResponse? {
        val request = CorporateActionCalendarGetByStockRequest.newBuilder()
            .setUserId(corporateActionTabRequest.userId)
            .setSessionId(corporateActionTabRequest.sessionId)
            .setStockCode(corporateActionTabRequest.stockCode)
            .setCaType(corporateActionTabRequest.calType)
            .build()

        return oltService.getCorporateActionCalendarByStockCode(request)
    }
}