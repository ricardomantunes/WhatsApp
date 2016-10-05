package whatsapp.android.com.whatsapp.helper;

import android.util.Base64;

/**
 * Created by ricar on 03/10/2016.
 */

public class Base64Custom {
    public static String convertBase64(String text)
    {
        String convertedText = Base64.encodeToString(text.getBytes(), Base64.DEFAULT);
        return convertedText.trim();
    }

    public static String decodeBase64(String text)
    {
        byte[] decodeText = Base64.decode(text, Base64.DEFAULT);
        return new String(decodeText);
    }
}
