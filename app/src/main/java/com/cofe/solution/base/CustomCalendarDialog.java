package com.cofe.solution.base;

import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.Drawable;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;

import com.cofe.solution.R;
import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.DayViewDecorator;
import com.prolificinteractive.materialcalendarview.DayViewFacade;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;

import java.util.HashSet;

public class CustomCalendarDialog extends Dialog {

    public interface OnDateSelectedListener {
        void onDateSelected(CalendarDay date);
    }

    public CustomCalendarDialog(@NonNull Context context, HashSet<CalendarDay> highlightedDates,
                                OnDateSelectedListener listener) {
        super(context);
        setContentView(R.layout.dialog_calendar);

        MaterialCalendarView calendarView = findViewById(R.id.calendarView);

        // Highlight specific dates
        calendarView.addDecorator(new HighlightedDateDecorator(context, highlightedDates));

        // Set date selection listener
        calendarView.setOnDateChangedListener((widget, date, selected) -> {
            if (listener != null) {
                listener.onDateSelected(date);
                dismiss(); // Close dialog after selection
            }
        });
    }

    static class HighlightedDateDecorator implements DayViewDecorator {
        private final Drawable backgroundDrawable;
        private final HashSet<CalendarDay> dates;

        HighlightedDateDecorator(Context context, HashSet<CalendarDay> dates) {
            this.backgroundDrawable = ContextCompat.getDrawable(context, R.drawable.highlight_background);
            this.dates = dates;
        }

        @Override
        public boolean shouldDecorate(CalendarDay day) {
            return dates.contains(day);
        }

        @Override
        public void decorate(DayViewFacade view) {
            view.setBackgroundDrawable(backgroundDrawable);
        }
    }
}
