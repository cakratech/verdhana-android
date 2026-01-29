package com.bcasekuritas.mybest.app.data.mapper

import com.bcasekuritas.mybest.app.domain.dto.response.IpoData
import com.bcasekuritas.mybest.app.domain.dto.response.StatusEipoList
import com.bcasekuritas.mybest.app.domain.dto.response.TradeSummary
import com.bcasekuritas.mybest.ext.common.getCurrentTimeInMillis
import com.bcasekuritas.mybest.ext.converter.GET_PIPELINE_STATUS_EIPO
import com.bcasekuritas.mybest.ext.date.DateUtils
import com.bcasekuritas.rabbitmq.proto.bcas.PipelinesIpoListData
import com.bcasekuritas.rabbitmq.proto.datafeed.CurrentMessageInfo


fun List<CurrentMessageInfo>.toTradeSummary() = map {
    TradeSummary(
        secCode = it.tradeSummary.secCode,
        change = it.tradeSummary.change,
        changePct = it.tradeSummary.changePct,
        last = it.tradeSummary.last,
        close = it.tradeSummary.close,
        tradeFreq = it.tradeSummary.tradeFreq,
        open = it.tradeSummary.open,
        tradeVolumeLot = it.tradeSummary.tradeVolumeLot,
        high = it.tradeSummary.high,
        low = it.tradeSummary.low,
        tradeValue = it.tradeSummary.tradeValue,
        bestBidPrice = it.tradeSummary.bestBidPrice,
        bestOfferPrice = it.tradeSummary.bestOfferPrice,
        avgPrice = it.tradeSummary.avgPrice,
        iep = it.tradeSummary.theoreticalPrice,
        iev = it.tradeSummary.theoreticalVolume
    )
}

fun List<PipelinesIpoListData>.toIpoListData() = map {item ->
    IpoData(
        code =  item.code,
        companyName =  item.companyName,
        sector =  item.sector,
        subSector =  item.subSector,
        address =  item.address,
        website =  item.website,
        overview = item.overview,
        businessLine =  item.bussinesLine,
        targetLots =  item.targetLots,
        sharePercent =  item.sharePercent,
        bookPriceFrom =  item.bookPriceFrom,
        bookPriceTo =  item.bookPriceTo,
        bookPeriodStart =  item.bookPeriodStart,
        bookPeriodEnd =  item.bookPeriodEnd,
        offeringPrice =  item.offeringPrice,
        offeringPeriodStart =  item.offeringPeriodStart,
        offeringPeriodEnd =  item.offeringPeriodEnd,
        allotmentDate =  item.allotmentDate,
        distDate =  item.distDate,
        listingDate =  item.listingDate,
        logoLink =  item.logoLink,
        statusId = getStatusId(item.statusId, item.distDate, item.listingDate),
        isBookBuilding =  item.isBookBuilding,
        prospectusLink =  item.prospectusLink,
        summaryProspectusLink =  item.summaryProspectusLink,
        underWriters =  item.underwriters,
        status =  GET_PIPELINE_STATUS_EIPO(item.bookPeriodStart, item.deadlineBookBuilding, item.offeringPeriodStart, item.deadlineOffering, item.allotmentDate, item.distDate, item.listingDate),
        participantAdmin = item.luBroker,
        deadlineAllocDate = item.deadlinePublishAllotmentDate,
        deadlineBookDate = item.deadlineBookBuilding,
        deadlineOfferDate = item.deadlineOffering,
        statusNew = StatusEipoList.fromString(getStatusId(item.statusId, item.distDate, item.listingDate)) ?: StatusEipoList.UNKNOWN
    )
}

// for status Distribution/ipo
fun getStatusId(statusId: String, distDate: String, listDate: String): String {
    val listingDate = DateUtils.getTimeInMillisFromDateStringFormat(listDate, "")
    val distributionDate = DateUtils.getTimeInMillisFromDateStringFormat(distDate, "")
    val currentDate = getCurrentTimeInMillis()

    return when {
        listingDate != 0L && currentDate >= listingDate -> "IPO"
        distributionDate != 0L && currentDate >= distributionDate -> "Distribution"
        else -> statusId
    }
}

fun PipelinesIpoListData.toIpoData() = IpoData(
    code =  this.code,
    companyName =  this.companyName,
    sector =  this.sector,
    subSector =  this.subSector,
    address =  this.address,
    website =  this.website,
    overview = this.overview,
    businessLine =  this.bussinesLine,
    targetLots =  this.targetLots,
    sharePercent =  this.sharePercent,
    bookPriceFrom =  this.bookPriceFrom,
    bookPriceTo =  this.bookPriceTo,
    bookPeriodStart =  this.bookPeriodStart,
    bookPeriodEnd =  this.bookPeriodEnd,
    offeringPrice =  this.offeringPrice,
    offeringPeriodStart =  this.offeringPeriodStart,
    offeringPeriodEnd =  this.offeringPeriodEnd,
    allotmentDate =  this.allotmentDate,
    distDate =  this.distDate,
    listingDate =  this.listingDate,
    logoLink =  this.logoLink,
    statusId =  this.statusId,
    isBookBuilding =  this.isBookBuilding,
    prospectusLink =  this.prospectusLink,
    summaryProspectusLink =  this.summaryProspectusLink,
    underWriters =  this.underwriters,
    status =  GET_PIPELINE_STATUS_EIPO(this.bookPeriodStart, this.deadlineBookBuilding, this.offeringPeriodStart, this.deadlineOffering, this.allotmentDate, this.distDate, this.listingDate),
    participantAdmin = this.luBroker,
    deadlineAllocDate = this.deadlinePublishAllotmentDate,
    deadlineBookDate = this.deadlineBookBuilding,
    deadlineOfferDate = this.deadlineOffering,
    statusNew = StatusEipoList.UNKNOWN
    )
