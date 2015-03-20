package date;

import android.text.format.Time;

/**
 * Created by natafrank on 2/27/15.
 */
public class Date
{
    //CONSTANTS.
    private static final int JANUARY   = 1;
    private static final int FEBRAURY  = 2;
    private static final int MARCH     = 3;
    private static final int APRIL     = 4;
    private static final int MAY       = 5;
    private static final int JUNE	   = 6;
    private static final int JULY      = 7;
    private static final int AUGUST    = 8;
    private static final int SEPTEMBER = 9;
    private static final int OCTOBER   = 10;
    private static final int NOVEMBER  = 11;
    private static final int DECEMBER  = 12;
    private static final String ARGUMENT_EXCEPTION = "One or more of the arguments are undefined"
            + " to parse a real date.";

    //Fields.
    private int second;
    private int minute;
    private int hour;
    private int day;
    private int month;
    private int year;

    //Flags.
    private boolean leapYear;
    private boolean validDate;

    public Date(int year, int month, int day, int hour, int minute, int second)
    {
        if(second >= 0 && second < 60 && minute >= 0 && minute < 60 && hour >= 0 && hour < 24
                && day > 0 && day <= 31 & month >= JANUARY  && month <= DECEMBER)
        {
            this.year = year;
            leapYear  = isLeapYear();

            switch(month)
            {
                case JANUARY:
                {
                    validDate = true;
                    break;
                }
                case FEBRAURY:
                {
                    if(day < 29 || (day == 29 && leapYear))
                    {
                        validDate = true;
                    }
                    else
                    {
                        validDate = false;
                    }
                    break;
                }
                case MARCH:
                {
                    validDate = true;
                    break;
                }
                case APRIL:
                {
                    if(day <= 30)
                    {
                        validDate = true;
                    }
                    else
                    {
                        validDate = false;
                    }
                    break;
                }
                case MAY:
                {
                    validDate = true;
                    break;
                }
                case JUNE:
                {
                    if(day <= 30)
                    {
                        validDate = true;
                    }
                    else
                    {
                        validDate = false;
                    }
                    break;
                }
                case JULY:
                {
                    validDate = true;
                    break;
                }
                case AUGUST:
                {
                    validDate = true;
                    break;
                }
                case SEPTEMBER:
                {
                    if(day <= 30)
                    {
                        validDate = true;
                    }
                    else
                    {
                        validDate = false;
                    }
                    break;
                }
                case OCTOBER:
                {
                    validDate = true;
                    break;
                }
                case NOVEMBER:
                {
                    if(day <= 30)
                    {
                        validDate = true;
                    }
                    else
                    {
                        validDate = false;
                    }
                    break;
                }
                case DECEMBER:
                {
                    validDate = true;
                    break;
                }
            }//switch (month)
        }
        else
        {
            validDate = false;
        }

        //Trigger to fire exception in case of Illegal arguments.
        if(!validDate)
        {
            throw new IllegalArgumentException(ARGUMENT_EXCEPTION);
        }
    }

    public boolean isLeapYear()
    {
        if( ((year % 400) == 0)
                || ( ((year % 4) == 0) && ((year % 100) != 0) ))
        {
            return true;
        }
        else
        {
            return false;
        }
    }

    public boolean isValidDate()
    {
        return validDate;
    }

    public DateDifference getDateDifference(Date date)
    {
        //Variables to create the DateDifference object.
        int dayDifference;
        int hourDifference;
        int minuteDifference;
        int secondDifference;

        //Get the day of the year.
        Time actualDate = new Time();
        Time goalDate   = new Time();

        //Set the fields of both dates.
        actualDate.set(second, minute, hour, day, month - 1, year);
        goalDate.set(date.second, date.minute, date.hour, date.day, date.month - 1, date.year);

        //Get the differences.
        //Day.
        dayDifference = goalDate.yearDay - actualDate.yearDay;

        //Hour.
        if(hour > date.hour)
        {
            dayDifference--;
            hourDifference = 24 - (hour - date.hour);
        }
        else
        {
            hourDifference = date.hour - hour;
        }

        //Minute.
        if(minute > date.minute)
        {
            hourDifference--;
            minuteDifference = 60 - (minute - date.minute);
        }
        else
        {
            minuteDifference = date.minute - minute;
        }

        //Second.
        if(second > date.second)
        {
            minuteDifference--;
            secondDifference = 60 - (second - date.second);
        }
        else
        {
            secondDifference = date.second - second;
        }

        return new DateDifference(dayDifference, hourDifference, minuteDifference, secondDifference);
    }
}
