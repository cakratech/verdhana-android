package com.bcasekuritas.mybest.app.data.repositoriesimpl

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
import com.bcasekuritas.mybest.app.domain.repositories.NewsRepo
import com.bcasekuritas.mybest.app.domain.subscribers.DataSource
import com.bcasekuritas.mybest.app.domain.subscribers.Resource
import com.bcasekuritas.mybest.app.feature.news.rssconverter.RssFeed
import com.bcasekuritas.mybest.ext.other.ErrorCodesMapper
import com.bcasekuritas.mybest.ext.provider.SchedulerProvider
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
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class NewsRepoImpl @Inject constructor(
    private val remoteSource: NewsDataSource,
    private val schedulerProvider: SchedulerProvider,
    private val errorCodesMapper: ErrorCodesMapper
) : NewsRepo {

    override suspend fun getNewsPromoInfo(newsPromoRequest: NewsPromoRequest): Flow<Resource<NewsInfoResponse?>> = flow {
        emit(Resource.Success(data = remoteSource.getNewsPromoInfo(newsPromoRequest), DataSource.REMOTE))
    }

    override suspend fun getPromoBanner(promotionBannerRequest: PromoBannerReq): Flow<Resource<PromotionBannerResponse?>> = flow {
        emit(Resource.Success(data = remoteSource.getPromoBanner(promotionBannerRequest), DataSource.REMOTE))
    }

    override suspend fun getStockPick(stockPickRequest: StockPickRequest): Flow<Resource<NewsStockPickSingleResponse?>> = flow {
        emit(Resource.Success(data = remoteSource.getStockPick(stockPickRequest), DataSource.REMOTE))
    }

    override suspend fun getNewsRssFeed(link: String): Flow<Resource<RssFeed?>> = flow {
        emit(Resource.Success(data = remoteSource.getNewsRssFeed(link), DataSource.REMOTE))
    }

    override suspend fun getNewsBannerLogin(): Flow<Resource<LoginBannerResponse?>> = flow {
        emit(Resource.Success(data = remoteSource.getNewsBannerLogin(), DataSource.REMOTE))
    }

    override suspend fun getResearchNews(newsResearchContentReq: NewsResearchContentReq): Flow<Resource<NewsResearchContentResponse?>> = flow {
        emit(Resource.Success(data = remoteSource.getResearchNews(newsResearchContentReq), DataSource.REMOTE))
    }

    override suspend fun getStockPickReport(stockPickReportReq: StockPickReportReq): Flow<Resource<ViewStockPickResearchReportResponse?>> = flow {
        emit(Resource.Success(data = remoteSource.getStockPickReport(stockPickReportReq), DataSource.REMOTE))
    }

    override suspend fun getNewsFeed(newsFeedRequest: NewsFeedRequest): Flow<Resource<NewsInfoFeedResponse?>> = flow {
        emit(Resource.Success(data = remoteSource.getNewsFeed(newsFeedRequest), DataSource.REMOTE))
    }

    override suspend fun getNewsFeedByStock(newsFeedByStockRequest: NewsFeedByStockRequest): Flow<Resource<NewsInfoFeedSearchByStockResponse?>> = flow {
        emit(Resource.Success(data = remoteSource.getNewsFeedByStock(newsFeedByStockRequest), DataSource.REMOTE))
    }

    override suspend fun getNewsFeedSearch(newsFeedSearchRequest: NewsFeedSearchRequest): Flow<Resource<NewsInfoFeedSearchResponse?>> = flow {
        emit(Resource.Success(data = remoteSource.getNewsFeedSearch(newsFeedSearchRequest), DataSource.REMOTE))
    }

    override suspend fun getResearchContentSearch(newsFeedSearchRequest: NewsFeedSearchRequest): Flow<Resource<SearchNewsResearchContentResponse?>> = flow{
        emit(Resource.Success(data = remoteSource.getResearchContentSearch(newsFeedSearchRequest), DataSource.REMOTE))
    }

    override suspend fun getCorporateActionTabByStockCode(corporateActionTabRequest: CorporateActionTabRequest): Flow<Resource<CorporateActionCalendarGetByStockResponse?>> = flow {
        emit(Resource.Success(data = remoteSource.getCorporateActionTabByStockCode(corporateActionTabRequest), DataSource.REMOTE))
    }
}