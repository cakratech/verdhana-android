package com.bcasekuritas.mybest.app.feature.calendar

import android.os.Build
import android.os.Bundle
import androidx.annotation.RequiresApi
import androidx.core.view.isGone
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.bcasekuritas.mybest.BR
import com.bcasekuritas.mybest.R
import com.bcasekuritas.mybest.app.base.BaseFragment
import com.bcasekuritas.mybest.app.domain.dto.response.CalDividenSaham
import com.bcasekuritas.mybest.app.domain.dto.response.CalEvent
import com.bcasekuritas.mybest.app.domain.dto.response.CalIpo
import com.bcasekuritas.mybest.app.domain.dto.response.CalPubExp
import com.bcasekuritas.mybest.app.domain.dto.response.CalReverseStock
import com.bcasekuritas.mybest.app.domain.dto.response.CalRightIssue
import com.bcasekuritas.mybest.app.domain.dto.response.CalRups
import com.bcasekuritas.mybest.app.domain.dto.response.CalSahamBonus
import com.bcasekuritas.mybest.app.domain.dto.response.CalStockSplit
import com.bcasekuritas.mybest.app.domain.dto.response.CalWarrant
import com.bcasekuritas.mybest.app.domain.dto.response.CalendarData
import com.bcasekuritas.mybest.app.feature.calendar.adapter.CalendarAdapter
import com.bcasekuritas.mybest.app.feature.calendar.adapter.CalendarEventAdapter
import com.bcasekuritas.mybest.databinding.FragmentCalendarBinding
import com.bcasekuritas.mybest.ext.activity.autoCleaned
import com.bcasekuritas.mybest.ext.common.convertMillisToDate
import com.bcasekuritas.mybest.ext.common.getDateMillis
import com.bcasekuritas.mybest.ext.common.getDayOfMonthFromMillis
import com.bcasekuritas.mybest.ext.common.getMonthFromMillis
import com.bcasekuritas.mybest.ext.constant.Args
import com.bcasekuritas.mybest.ext.delegate.ShowDialogCalendarDetailImpl
import com.bcasekuritas.mybest.ext.delegate.ShowDialogCalendarDetailInterface
import com.bcasekuritas.mybest.ext.listener.OnClickAny
import com.bcasekuritas.mybest.ext.listener.OnClickAnyStr
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.scopes.FragmentScoped
import java.time.LocalDate
import java.time.YearMonth
import java.time.ZoneId
import java.time.format.DateTimeFormatter

@FragmentScoped
@AndroidEntryPoint
class CalendarFragment : BaseFragment<FragmentCalendarBinding, CalendarViewModel>(), OnClickAny, OnClickAnyStr
    , ShowDialogCalendarDetailInterface by ShowDialogCalendarDetailImpl() {

    @FragmentScoped
    override val bindingVariable: Int = BR.vmCalendar
    override val viewModel: CalendarViewModel by viewModels()
    override val binding: FragmentCalendarBinding by autoCleaned {
        (FragmentCalendarBinding.inflate(layoutInflater))
    }

    private val calendarAdapter: CalendarAdapter by autoCleaned {
        CalendarAdapter(
            requireContext(),
            this,
            binding.rcvCalendar
        )
    }
    private val calendarEventAdapter: CalendarEventAdapter by autoCleaned {
        CalendarEventAdapter(
            requireContext(),
            this
        )
    }

    private var calendarList = ArrayList<Any>()
    private var calendarDataList = ArrayList<CalendarData>()
    private lateinit var selectedDate: LocalDate


    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        selectedDate = LocalDate.now()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun setupObserver() {
        super.setupObserver()

        viewModel.getCalendarDataResults.observe(viewLifecycleOwner) {
            calendarDataList = arrayListOf()
            val calDataList = arrayListOf<CalendarData>()
            val daysInMonth = daysInMonthArray(selectedDate)

            val selectedMonth = selectedDate
                .atStartOfDay()
                .atZone(ZoneId.systemDefault())
                .toInstant()
                .toEpochMilli()

            it.forEach { (date, corpAction) ->
                calDataList.add(CalendarData(calDateData = date, calItem = corpAction))
            }

            for (days in daysInMonth) {
                val filterData =
                    calDataList.filter {
                        val targetDay = days.first.toString()
                        val targetMonth = days.second
                        val month = getMonthFromMillis(it.calDateData ?: 0)
                        val day = getDayOfMonthFromMillis(it.calDateData ?: 0)

                        targetDay == day &&  targetMonth == month
                    }

                if (filterData.isNotEmpty()) {
                    val filterDataEntries =  filterData.map {
                        CalendarData(
                            calItem = it.calItem,
                            calDate = days,
                            calDateData = it.calDateData,
                            selectedMonth = selectedMonth
                        )
                    }

                    calendarDataList.addAll(filterDataEntries)
                } else {
                    var dateMillis = 0L

                    if (days.second == selectedDate.monthValue){
                        dateMillis = getDateMillis(days.first, selectedDate.monthValue, selectedDate.year)
                    } else {
                        dateMillis = getDateMillis(days.first, days.second, selectedDate.year)
                    }

                    calendarDataList.add(
                        CalendarData(
                            calItem = listOf(),
                            calDate = days,
                            calDateData = dateMillis,
                            selectedMonth = selectedMonth
                        )
                    )

                }
            }

                calendarAdapter.setData(calendarDataList)
        }


    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun isSameDate(date: String, time: Long): Boolean {
        return date == getDayOfMonthFromMillis(time)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun initOnClick() {
        super.initOnClick()


        binding.lyToolbar.ivLayoutToolbarMasterIconLeft.setOnClickListener {
            requireActivity().onBackPressed()
        }

        binding.btnCalendarBack.setOnClickListener {
            previousMonthAction()
        }

        binding.btnCalendarForward.setOnClickListener {
            nextMonthAction()
        }
    }


    @RequiresApi(Build.VERSION_CODES.O)
    private fun daysInMonthArray(date: LocalDate): ArrayList<Pair<Int, Int>> {
        val daysInMonthArray = ArrayList<Pair<Int, Int>>()
        val yearMonth = YearMonth.from(date)
        val daysInMonth = yearMonth.lengthOfMonth()

        // First day of the selected month
        val firstOfMonth = date.withDayOfMonth(1)
        val dayOfWeek = (firstOfMonth.dayOfWeek.value % 7).plus(1)

        // Previous month's details
        val previousMonth = date.minusMonths(1)
        val prevYearMonth = YearMonth.from(previousMonth)
        val daysInPrevMonth = prevYearMonth.lengthOfMonth()
        val prevMonthValue = previousMonth.monthValue

        // Fill the calendar grid with previous month's days
        for (i in dayOfWeek - 1 downTo 1) {
            daysInMonthArray.add(Pair(daysInPrevMonth - i + 1, prevMonthValue))
        }

        // Fill the calendar grid with the current month's days
        val currentMonthValue = date.monthValue
        for (i in 1..daysInMonth) {
            daysInMonthArray.add(Pair(i, currentMonthValue))
        }

        // Calculate remaining days to be filled by next month
        val nextMonth = date.plusMonths(1)
        val nextMonthValue = nextMonth.monthValue
        val remainingDays = 42 - daysInMonthArray.size
        for (i in 1..remainingDays) {
            daysInMonthArray.add(Pair(i, nextMonthValue))
        }

        return daysInMonthArray
    }


    @RequiresApi(Build.VERSION_CODES.O)
    private fun dateForTextView(date: LocalDate, pattern: String): String {
        val formatter = DateTimeFormatter.ofPattern(pattern)
        date.format(formatter)
        return date.format(formatter)
    }


    @RequiresApi(Build.VERSION_CODES.O)
    private fun setMonthView() {
        val daysInMonth = daysInMonthArray(selectedDate)
        daysInMonth.map {
            calendarDataList.add(CalendarData(arrayListOf(), it))
        }
        calendarAdapter.setData(calendarDataList)
        calendarDataList.clear()
        viewModel.getCalendar(
            prefManager.userId,
            prefManager.sessionId,
            selectedDate.year.toString(),
            selectedDate.monthValue.toString().padStart(2, '0')
        )

    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun previousMonthAction() {
        calendarDataList.clear()
        selectedDate = selectedDate.minusMonths(1)
        binding.tvCalDate.text = dateForTextView(selectedDate, "MMMM yyyy")
        setMonthView()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun nextMonthAction() {
        calendarDataList.clear()
        selectedDate = selectedDate.plusMonths(1)
        binding.tvCalDate.text = dateForTextView(selectedDate, "MMMM yyyy")
        setMonthView()
    }

    override fun setupAdapter() {
        super.setupAdapter()
        binding.rcvCalendar.adapter = calendarAdapter
        binding.rcvEventList.adapter = calendarEventAdapter
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun setupComponent() {
        super.setupComponent()

        binding.lyToolbar.tvLayoutToolbarMasterTitle.text = "Calendar"

        binding.tvCalDate.text = dateForTextView(selectedDate, "MMMM yyyy")

        setMonthView()
    }

    override fun onClickAny(valueAny: Any?) {
        valueAny as CalendarData
        val eventList: ArrayList<Any> = arrayListOf()

        if (isAdded && !isDetached) {
            binding.tvDateList.text = convertMillisToDate(valueAny.calDateData ?: 0L, "dd MMM yyyy")
            val sortedList: List<Any> = valueAny.calItem
                .sortedBy { it.toString() }
                .toList()

            if (sortedList.isNotEmpty()) {

                sortedList.forEach {
                    when (it) {
                        is CalWarrant -> {
                            eventList.add(it.type)
                            eventList.add(it)
                        }

                        is CalRightIssue -> {
                            eventList.add(it.type)
                            eventList.add(it)
                        }

                        is CalStockSplit -> {
                            eventList.add(it.type)
                            eventList.add(it)
                        }

                        is CalSahamBonus -> {
                            eventList.add(it.type)
                            eventList.add(it)
                        }

                        is CalDividenSaham -> {
                            eventList.add(it.type)
                            eventList.add(it)
                        }

                        is CalPubExp -> {
                            eventList.add(it.type)
                            eventList.add(it)
                        }

                        is CalRups -> {
                            eventList.add(it.type)
                            eventList.add(it)
                        }

                        is CalIpo -> {
                            eventList.add(it.type)
                            eventList.add(it)
                        }

                        is CalReverseStock -> {
                            eventList.add(it.type)
                            eventList.add(it)
                        }
                    }
                }

                binding.rcvEventList.isGone = false
                binding.groupEmptyList.isGone = true
                binding.dividerCalendarDetail.setBackgroundResource(R.color.bgWhite)
                calendarEventAdapter.setData(eventList.toSet().toList())
            } else {
                binding.rcvEventList.isGone = true
                binding.dividerCalendarDetail.setBackgroundResource(R.color.bgSecondary)
                binding.groupEmptyList.isGone = false
            }
        }
    }

    override fun onClickAnyStr(valueAny: Any?, valueString: String) {
        val eventData = valueAny as CalEvent
        when(valueString){
            "Warrant" -> {
                showDialogCalendarWarrant(parentFragmentManager, eventData, onClicked = {boolean ->
                    if (boolean){
                        val bundle = Bundle().apply {
                            putString(Args.EXTRA_PARAM_STR_ONE, valueAny.stockCode)
                        }
                        findNavController().navigate(R.id.stock_detail_fragment, bundle)
                    }

                })
            }

            "Right Issue" -> {
                showDialogCalendarRightIssue(parentFragmentManager, eventData, onClicked = {boolean ->
                    if (boolean){
                        val bundle = Bundle().apply {
                            putString(Args.EXTRA_PARAM_STR_ONE, valueAny.stockCode)
                        }
                        findNavController().navigate(R.id.stock_detail_fragment, bundle)
                    }

                })
            }

            "Reverse Split" -> {
                showDialogCalendarReverseSplit(parentFragmentManager, eventData, onClicked = {boolean ->
                    if (boolean){
                        val bundle = Bundle().apply {
                            putString(Args.EXTRA_PARAM_STR_ONE, valueAny.stockCode)
                        }
                        findNavController().navigate(R.id.stock_detail_fragment, bundle)
                    }

                })
            }

            "Stock Split" -> {
                showDialogCalendarStockSplit(parentFragmentManager, eventData, onClicked = {boolean ->
                    if (boolean){
                        val bundle = Bundle().apply {
                            putString(Args.EXTRA_PARAM_STR_ONE, valueAny.stockCode)
                        }
                        findNavController().navigate(R.id.stock_detail_fragment, bundle)
                    }
                })
            }

            "Bonus" -> {
                showDialogCalendarBonus(parentFragmentManager, eventData, onClicked = {boolean ->
                    if (boolean){
                        val bundle = Bundle().apply {
                            putString(Args.EXTRA_PARAM_STR_ONE, valueAny.stockCode)
                        }
                        findNavController().navigate(R.id.stock_detail_fragment, bundle)
                    }
                })
            }

            "Dividend" -> {
                showDialogCalendarDividend(parentFragmentManager, eventData, onClicked = {string ->
                    if (string == "stock"){
                        val bundle = Bundle().apply {
                            putString(Args.EXTRA_PARAM_STR_ONE, valueAny.stockCode)
                        }
                        findNavController().navigate(R.id.stock_detail_fragment, bundle)
                    } else {
                        // TODO RK: To History
                    }
                })
            }

            "Public Expose" -> {
                showDialogCalendarPublic(parentFragmentManager, eventData, onClicked = {boolean ->
                    if (boolean){
                        val bundle = Bundle().apply {
                            putString(Args.EXTRA_PARAM_STR_ONE, valueAny.stockCode)
                        }
                        findNavController().navigate(R.id.stock_detail_fragment, bundle)
                    }
                })
            }

            "RUPS" -> {
                showDialogCalendarRups(parentFragmentManager, eventData, onClicked = {boolean ->
                    if (boolean){
                        val bundle = Bundle().apply {
                            putString(Args.EXTRA_PARAM_STR_ONE, valueAny.stockCode)
                        }
                        findNavController().navigate(R.id.stock_detail_fragment, bundle)
                    }
                })
            }

            "E-IPO" -> {
                showDialogCalendarIpo(parentFragmentManager, eventData, onClicked = {boolean ->

                })
            }
        }
    }
}