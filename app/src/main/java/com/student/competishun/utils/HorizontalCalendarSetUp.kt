
import android.content.Context
import android.os.Build
import android.widget.ImageView
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.student.competishun.data.model.CalendarDate
import com.student.competishun.ui.adapter.CalendarDateAdapter
import java.text.SimpleDateFormat
import java.time.ZoneId
import java.time.ZonedDateTime
import java.util.*

class HorizontalCalendarSetUp {

    private var currentMonth = Calendar.getInstance()

    @RequiresApi(Build.VERSION_CODES.O)
    fun scrollToSpecificDate(
        recyclerView: RecyclerView,
        zonedDateTime: ZonedDateTime
    ) {
        val dates = getDatesForMonth(currentMonth)
        val adapter = recyclerView.adapter as? CalendarDateAdapter

        val position = dates.indexOfFirst { it.zonedDateTime?.toLocalDate() == zonedDateTime.toLocalDate() }
        if (position != -1) {
            (recyclerView.layoutManager as LinearLayoutManager).scrollToPositionWithOffset(position, 0)
            adapter?.setSelectedPosition(position)
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun setUpCalendarAdapter(
        recyclerView: RecyclerView,
        context: Context,
        onDateSelected: (CalendarDate) -> Unit
    ): String {
        val dates = getDatesForMonth(currentMonth)
        val adapter = CalendarDateAdapter(dates, onDateSelected)
        recyclerView.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        recyclerView.adapter = adapter


        val currentDate = getCurrentDate()
        val currentPosition = dates.indexOfFirst { it.date == currentDate.date }
        if (currentPosition != -1) {
            (recyclerView.layoutManager as LinearLayoutManager).scrollToPositionWithOffset(currentPosition, 0)
            adapter.setSelectedPosition(currentPosition)
        }

        return getCurrentMonthYear(currentMonth)
    }

    @RequiresApi(Build.VERSION_CODES.O)
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
            val newMonth = setUpCalendarAdapter(recyclerView, context, onDateSelected)
            onMonthChange(newMonth)
        }
        prevButton.setOnClickListener {
            currentMonth.add(Calendar.MONTH, -1)
            val newMonth = setUpCalendarAdapter(recyclerView, context, onDateSelected)
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
            dates.add(CalendarDate(date,day ,null,zonedDateTime))
            calendar.add(Calendar.DAY_OF_MONTH, 1)
        }

        calendar.set(Calendar.DAY_OF_MONTH, 1)
        calendar.set(Calendar.MONTH, month)
        calendar.set(Calendar.YEAR, year)
        return dates
    }

    private fun getCurrentMonthYear(calendar: Calendar): String {
        val dateFormat = SimpleDateFormat("MMMM yyyy", Locale.getDefault())
        return dateFormat.format(calendar.time)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun getCurrentDate(): CalendarDate {
        val calendar = Calendar.getInstance()
        val dateFormat = SimpleDateFormat("dd", Locale.getDefault())
        val dayFormat = SimpleDateFormat("EEE", Locale.getDefault())
        val zonedDateTime = ZonedDateTime.ofInstant(calendar.toInstant(), ZoneId.systemDefault())
        val date = dateFormat.format(calendar.time)
        val day = dayFormat.format(calendar.time)

        return CalendarDate(date, day, null,zonedDateTime)
    }


}

