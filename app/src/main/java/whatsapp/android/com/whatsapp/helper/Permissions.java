package whatsapp.android.com.whatsapp.helper;

import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ricar on 29/09/2016.
 */

public class Permissions {

    public static boolean validatePermissions(int requestCode, Activity activity, String[] permissions)
    {
        if(Build.VERSION.SDK_INT >= 23)
        {
            List<String> permissionsList = new ArrayList<>();

            for(String permission : permissions)
            {
                Boolean validPermission = ContextCompat.checkSelfPermission(activity, permission) == PackageManager.PERMISSION_GRANTED;
                if (!validPermission) permissionsList.add(permission);
            }

            if (permissionsList.isEmpty()) return true;

            String[] newPermissions = new String[permissionsList.size()];
            permissionsList.toArray(newPermissions);

            ActivityCompat.requestPermissions(activity, newPermissions, requestCode);
        }

        return true;
    }
}
