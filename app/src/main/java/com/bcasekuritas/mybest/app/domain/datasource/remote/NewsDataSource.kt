package com.bcasekuritas.mybest.app.domain.datasource.remote

import com.bcasekuritas.mybest.app.domain.dto.request.CorporateActionTabRequest
import com.bcasekuritas.mybest.app.domain.dto.request.NewsFeedByStockRequest
import com.bcasekuritas.mybest.app.domain.dto.request.NewsFeedRequest
import com.bcasekuritas.mybest.app.domain.dto.request.NewsFeedSearchRequest
import com.bcasekuritas.mybest.app.domain.dto.request.NewsPromoRequest
import com.bcasekuritas.mybest.app.domain.dto.request.NewsResearchContentReq
import com.bcasekuritas.mybest.app.domain.dto.request.PromoBannerReq
import com.bcasekuritas.mybest.app.domain.dto.request.StockPickReportReq
import com.bcasekuritas.mybest.app.domain.dto.request.StockPickRequest
import com.bcasekuritas.mybest.app.feature.news.rssconverter.RssFeed
import com.bcasekuritas.rabbitmq.proto.news.CorporateActionCalendarGetByStockResponse
import com.bcasekuritas.rabbitmq.proto.news.LoginBannerResponse
import com.bcasekuritas.rabbitmq.proto.news.NewsInfoFeedResponse
import com.bcasekuritas.rabbitmq.proto.news.NewsInfoFeedSearchByStockResponse
import com.bcasekuritas.rabbitmq.proto.news.NewsInfoFeedSearchResponse
import com.bcasekuritas.rabbitmq.proto.news.NewsInfoResponse
import com.bcasekuritas.rabbitmq.proto.news.NewsResearchContentResponse
import com.bcasekuritas.rabbitmq.proto.news.NewsStockPickSingleResponse
import com.bcasekuritas.rabbitmq.proto.news.PromotionBannerResponse
import com.bcasekuritas.rabbitmq.proto.news.SearchNewsResearchContentResponse
import com.bcasekuritas.rabbitmq.proto.news.ViewStockPickResearchReportResponse

interface NewsDataSource {
//    suspend fun getNewsInfo(newsRequest: NewsRequest?): NewsInfoResponse?
    suspend fun getStockPick(stockPickRequest: StockPickRequest): NewsStockPickSingleResponse?

    suspend fun getNewsPromoInfo(newsPromoRequest: NewsPromoRequest): NewsInfoResponse?

    suspend fun getPromoBanner(promotionBannerRequest: PromoBannerReq): PromotionBannerResponse?

    suspend fun getNewsRssFeed(link: String): RssFeed?

    suspend fun getNewsBannerLogin(): LoginBannerResponse?

    suspend fun getResearchNews(newsResearchContentReq: NewsResearchContentReq): NewsResearchContentResponse?
    suspend fun getStockPickReport(stockPickReportReq: StockPickReportReq): ViewStockPickResearchReportResponse?
    suspend fun getNewsFeed(newsFeedRequest: NewsFeedRequest): NewsInfoFeedResponse?
    suspend fun getNewsFeedByStock(newsFeedByStockRequest: NewsFeedByStockRequest): NewsInfoFeedSearchByStockResponse?
    suspend fun getNewsFeedSearch(newsFeedSearchRequest: NewsFeedSearchRequest): NewsInfoFeedSearchResponse?
    suspend fun getResearchContentSearch(newsFeedSearchRequest: NewsFeedSearchRequest): SearchNewsResearchContentResponse?
    suspend fun getCorporateActionTabByStockCode(corporateActionTabRequest: CorporateActionTabRequest): CorporateActionCalendarGetByStockResponse?

}