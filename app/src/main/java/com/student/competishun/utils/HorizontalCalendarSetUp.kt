
import android.content.Context
import android.widget.ImageView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.student.competishun.data.model.CalendarDate
import com.student.competishun.ui.adapter.CalendarDateAdapter
import java.text.SimpleDateFormat
import java.util.*

class HorizontalCalendarSetUp {

    private var currentMonth = Calendar.getInstance()

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
            dates.add(CalendarDate(date, day, null))
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

    fun getCurrentDate(): CalendarDate {
        val calendar = Calendar.getInstance()
        val dateFormat = SimpleDateFormat("dd", Locale.getDefault())
        val dayFormat = SimpleDateFormat("EEE", Locale.getDefault())

        val date = dateFormat.format(calendar.time)
        val day = dayFormat.format(calendar.time)

        return CalendarDate(date, day, null)
    }
}

