package whatsapp.android.com.whatsapp.helper;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.HashMap;

public class Preferences {

    private Context context;
    private SharedPreferences preferences;
    private String NOME_ARQUIVO = "WhastAppPreferences";
    private int MODE = 0;
    private SharedPreferences.Editor editor;

    private String CHAVE_USERID = "userID";
    private String CHAVE_USERNAME = "name";

    public Preferences(Context contextParameter)
    {
        context = contextParameter;
        preferences = context.getSharedPreferences(NOME_ARQUIVO, MODE);
        editor = preferences.edit();
    }

    public void SaveData(String userID, String name)
    {
        editor.putString(CHAVE_USERID, userID);
        editor.putString(CHAVE_USERNAME, name);
        editor.commit();
    }

    public String getIdentifier()
    {
        return preferences.getString(CHAVE_USERID, null);
    }

    public String getName()
    {
        return preferences.getString(CHAVE_USERNAME, null);
    }
}
