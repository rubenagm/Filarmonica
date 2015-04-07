package date;

import android.text.format.Time;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.StringTokenizer;

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

    public DateDifference startCountdown()
    {
        int dateYear = 0;
        int dateMonth = 0;
        int dateDay = 0;
        int hourHour = 0;
        int hourMinute = 0;
        int i = 0;

        //Tokenize the date.
        StringTokenizer dateTokenizer = new StringTokenizer(date, "-");
        while(dateTokenizer.hasMoreTokens())
        {
            switch(i)
            {
                case 0:
                {
                    dateYear = Integer.parseInt(dateTokenizer.nextToken());
                    break;
                }
                case 1:
                {
                    dateMonth = Integer.parseInt(dateTokenizer.nextToken());
                    break;
                }
                case 2:
                {
                    dateDay = Integer.parseInt(dateTokenizer.nextToken());
                    break;
                }
            }
            i++;
        }

        //Tokenize the hour.
        i = 0;
        StringTokenizer hourTokenizer = new StringTokenizer(hour, ":");
        while(hourTokenizer.hasMoreTokens())
        {
            switch(i)
            {
                case 0:
                {
                    hourHour = Integer.parseInt(hourTokenizer.nextToken());
                    break;
                }
                case 1:
                {
                    hourMinute = Integer.parseInt(hourTokenizer.nextToken());
                    break;
                }
            }
            i++;
        }

        //Create actual date.
        Time now = new Time();
        now.setToNow();
        Date actualDate = new Date(now.year, now.month + 1, now.monthDay, now.hour, now.minute,
               now.second);

        //Create next concert's date.
        Date goalDate = new Date(dateYear, dateMonth, dateDay, hourHour, hourMinute, 0);

        return actualDate.getDateDifference(goalDate);
    }
}
