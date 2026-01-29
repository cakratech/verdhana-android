package com.bcasekuritas.mybest.app.domain.repositories

import com.bcasekuritas.mybest.app.domain.dto.request.CorporateActionTabRequest
import com.bcasekuritas.mybest.app.domain.dto.request.NewsFeedByStockRequest
import com.bcasekuritas.mybest.app.domain.dto.request.NewsFeedRequest
import com.bcasekuritas.mybest.app.domain.dto.request.NewsFeedSearchRequest
import com.bcasekuritas.mybest.app.domain.dto.request.NewsPromoRequest
import com.bcasekuritas.mybest.app.domain.dto.request.NewsResearchContentReq
import com.bcasekuritas.mybest.app.domain.dto.request.PromoBannerReq
import com.bcasekuritas.mybest.app.domain.dto.request.StockPickReportReq
import com.bcasekuritas.mybest.app.domain.dto.request.StockPickRequest
import com.bcasekuritas.mybest.app.domain.subscribers.Resource
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
import kotlinx.coroutines.flow.Flow

interface NewsRepo {
//    suspend fun getNewsInfo(newsRequest: NewsRequest?): Flow<Resource<List<NewsInfoResponse?>>>
    suspend fun getNewsPromoInfo(newsPromoRequest: NewsPromoRequest): Flow<Resource<NewsInfoResponse?>>
    suspend fun getPromoBanner(promotionBannerRequest: PromoBannerReq): Flow<Resource<PromotionBannerResponse?>>
    suspend fun getStockPick(stockPickRequest: StockPickRequest): Flow<Resource<NewsStockPickSingleResponse?>>
    suspend fun getNewsRssFeed(link: String): Flow<Resource<RssFeed?>>
    suspend fun getNewsBannerLogin(): Flow<Resource<LoginBannerResponse?>>
    suspend fun getResearchNews(newsResearchContentReq: NewsResearchContentReq): Flow<Resource<NewsResearchContentResponse?>>
    suspend fun getStockPickReport(stockPickReportReq: StockPickReportReq): Flow<Resource<ViewStockPickResearchReportResponse?>>
    suspend fun getNewsFeed(newsFeedRequest: NewsFeedRequest): Flow<Resource<NewsInfoFeedResponse?>>
    suspend fun getNewsFeedByStock(newsFeedByStockRequest: NewsFeedByStockRequest): Flow<Resource<NewsInfoFeedSearchByStockResponse?>>
    suspend fun getNewsFeedSearch(newsFeedSearchRequest: NewsFeedSearchRequest): Flow<Resource<NewsInfoFeedSearchResponse?>>
    suspend fun getResearchContentSearch(newsFeedSearchRequest: NewsFeedSearchRequest): Flow<Resource<SearchNewsResearchContentResponse?>>
    suspend fun getCorporateActionTabByStockCode(corporateActionTabRequest: CorporateActionTabRequest): Flow<Resource<CorporateActionCalendarGetByStockResponse?>>
}