package date;

/**
 * Created by natafrank on 2/27/15.
 */
public class DateDifference
{
    //Fields.
    private int second;
    private int minute;
    private int hour;
    private int day;

    //Flags.
    private boolean completedTime;

    public DateDifference(int day, int hour, int minute, int second)
    {
        this.second = second;
        this.minute = minute;
        this.hour   = hour;
        this.day    = day;
        completedTime = false;
    }

    public DateDifference(boolean completedTime)
    {
        this.completedTime = completedTime;
    }

    public void substractSecond()
    {
        if(second > 0)
        {
            second--;
        }
        else
        {
            if(minute > 0)
            {
                minute--;
                second = 59;
            }
            else
            {
                if(hour > 0)
                {
                    hour--;
                    minute = 59;
                    second = 59;
                }
                else
                {
                    if(day > 0)
                    {
                        day--;
                        hour = 23;
                        minute = 59;
                        second = 59;
                    }

                    //Completed time.
                    completedTime = true;
                }
            }
        }
    }

    public static DateDifference getDateDifferenceCompletedTime()
    {
        return new DateDifference(true);
    }

    public int getDay()
    {
        return day;
    }

    public int getHour()
    {
        return hour;
    }

    public int getMinute()
    {
        return minute;
    }

    public int getSecond()
    {
        return second;
    }

    public boolean isCompletedTime()
    {
        return completedTime;
    }
}
