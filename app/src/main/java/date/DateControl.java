package date;

import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by natafrank on 2/27/15.
 */
public class DateControl
{
    //Fields.
    private TextView lblClock;
    private String date;
    private String hour;

    public DateControl(ArrayList<String> dateHourArray)
    {
        date = dateHourArray.get(0);
        hour = dateHourArray.get(1);
    }

    public void startCountdown()
    {
        int dateYear;
        int dateMonth;
        int dateDay;
        int hourHour;
        int hourMinute;


    }
}
