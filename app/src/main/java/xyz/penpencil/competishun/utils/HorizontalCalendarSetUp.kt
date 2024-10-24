package xyz.penpencil.competishun.utils
import android.content.Context
import android.os.Build
import android.util.Log
import android.widget.ImageView
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import xyz.penpencil.competishun.data.model.CalendarDate
import xyz.penpencil.competishun.ui.adapter.CalendarDateAdapter
import java.text.SimpleDateFormat
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.util.*

class HorizontalCalendarSetUp {

    private var currentMonth = Calendar.getInstance()

    fun scrollToSpecificDate(
        recyclerView: RecyclerView,
        zonedDateTime: ZonedDateTime,
    ) {
        val dates = getDatesForMonth(currentMonth)
        val adapter = recyclerView.adapter as? CalendarDateAdapter

        val position = dates.indexOfFirst { it.zonedDateTime?.toLocalDate() == zonedDateTime.toLocalDate() }
        if (position != -1) {
            (recyclerView.layoutManager as LinearLayoutManager).scrollToPositionWithOffset(position, 0)
            adapter?.setSelectedPosition(position)
        }
    }

    fun setUpCalendarAdapter(
        recyclerView: RecyclerView,
        context: Context,
        onDateSelected: (CalendarDate) -> Unit,
        hasScheduleList: MutableList<String>,
    ): String {
        val dates = getDatesForMonth(currentMonth)
        val adapter = CalendarDateAdapter(dates, onDateSelected, hasScheduleList)
        recyclerView.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        recyclerView.adapter = adapter

        selectCurrentDate(dates, adapter, recyclerView)

        return getCurrentMonthYear(currentMonth)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun setUpScheduleAvailable(
        recyclerView: RecyclerView,
        hasScheduleList: MutableList<String>,
        context: Context,
        onDateSelected: (CalendarDate) -> Unit
    ) {
        val adapter = recyclerView.adapter as? CalendarDateAdapter
            ?: CalendarDateAdapter(getDatesForMonth(currentMonth), onDateSelected, hasScheduleList).also {
                recyclerView.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
                recyclerView.adapter = it
            }
        adapter.updateHasScheduleList(hasScheduleList)

        selectCurrentDate(adapter.dates, adapter, recyclerView) // Ensure current date is selected
    }

    fun setUpCalendarPrevNextClickListener(
        recyclerView: RecyclerView,
        nextButton: ImageView,
        prevButton: ImageView,
        context: Context,
        onMonthChange: (String) -> Unit,
        onDateSelected: (CalendarDate) -> Unit
    ) {
        nextButton.setOnClickListener {
            currentMonth.add(Calendar.MONTH, 1)
            val newMonth = setUpCalendarAdapter(
                recyclerView, context, onDateSelected,
                mutableListOf()
            )
            onMonthChange(newMonth)
        }
        prevButton.setOnClickListener {
            currentMonth.add(Calendar.MONTH, -1)
            val newMonth = setUpCalendarAdapter(
                recyclerView, context, onDateSelected,
                mutableListOf()
            )
            onMonthChange(newMonth)
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun getDatesForMonth(calendar: Calendar): List<CalendarDate> {
        val dates = mutableListOf<CalendarDate>()
        val month = calendar.get(Calendar.MONTH)
        val year = calendar.get(Calendar.YEAR)
        val dateFormat = SimpleDateFormat("dd", Locale.getDefault())
        val dayFormat = SimpleDateFormat("EEE", Locale.getDefault())

        calendar.set(Calendar.DAY_OF_MONTH, 1)
        while (calendar.get(Calendar.MONTH) == month) {
            val date = dateFormat.format(calendar.time)
            val day = dayFormat.format(calendar.time)
            val zonedDateTime = ZonedDateTime.ofInstant(calendar.toInstant(), ZoneId.systemDefault())
            dates.add(CalendarDate(date, day, null, zonedDateTime))
            calendar.add(Calendar.DAY_OF_MONTH, 1)
        }

        calendar.set(Calendar.DAY_OF_MONTH, 1)
        calendar.set(Calendar.MONTH, month)
        calendar.set(Calendar.YEAR, year)
        return dates
    }

    private fun selectCurrentDate(dates: List<CalendarDate>, adapter: CalendarDateAdapter, recyclerView: RecyclerView) {
        val currentDate = getCurrentDate()
        val currentPosition = dates.indexOfFirst { it.date == currentDate.date }
        if (currentPosition != -1) {
            (recyclerView.layoutManager as LinearLayoutManager).scrollToPositionWithOffset(currentPosition, 0)
            adapter.setSelectedPosition(currentPosition)
        }
    }

    private fun getCurrentMonthYear(calendar: Calendar): String {
        val dateFormat = SimpleDateFormat("MMMM yyyy", Locale.getDefault())
        return dateFormat.format(calendar.time)
    }

    private fun getCurrentDate(): CalendarDate {
        val zonedDateTime = ZonedDateTime.now(ZoneId.systemDefault())
        val date = zonedDateTime.format(DateTimeFormatter.ofPattern("dd", Locale.getDefault()))
        val day = zonedDateTime.format(DateTimeFormatter.ofPattern("EEE", Locale.getDefault()))
        return CalendarDate(date, day, null, zonedDateTime)
    }
}

