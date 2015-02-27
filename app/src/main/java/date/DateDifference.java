package date;

/**
 * Created by natafrank on 2/27/15.
 */
public class DateDifference extends Thread
{
    //Constants.
    private static final int SLEEP_SECOND = 1000;

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

    @Override
    public void run()
    {
        while(!completedTime)
        {
            substractSecond();

            try
            {
                sleep(SLEEP_SECOND);
            }
            catch (InterruptedException e)
            {
                e.printStackTrace();
            }
        }
    }
}
