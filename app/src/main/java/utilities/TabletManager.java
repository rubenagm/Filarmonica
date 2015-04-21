package utilities;

import android.content.Context;
import android.content.res.Configuration;

/**
 * Created by natafrank on 4/22/15.
 */
public class TabletManager
{
    public static boolean esTablet(Context contexto)
    {
        //Trigger para poner la tablet en horizontal.
        return(contexto.getResources().getConfiguration().screenLayout &
                Configuration.SCREENLAYOUT_SIZE_XLARGE) == Configuration.SCREENLAYOUT_SIZE_XLARGE;
    }
}
