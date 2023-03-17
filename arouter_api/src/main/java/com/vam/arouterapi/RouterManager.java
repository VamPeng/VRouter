package com.vam.arouterapi;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.vam.arouterapi.core.ARouterLoadPath;

import java.util.HashMap;
import java.util.Map;

public class RouterManager {

    public static Map<String, ARouterLoadPath> pathMap = new HashMap<>();

    public static void navi(Context context, String path) {

        String group = path.substring(1, path.indexOf("/", 1));

        String className = Constants.PATH_FILE_NAME + group;
        Log.i("Vam", className);


        ARouterLoadPath loadPath = null;
        if (pathMap.containsKey(className)) {
            loadPath = pathMap.get(className);
        }

        if (loadPath == null) {

            try {
                loadPath = (ARouterLoadPath) Class.forName(className).newInstance();
            } catch (IllegalAccessException | InstantiationException | ClassNotFoundException e) {
                throw new RuntimeException(e);
            }

            pathMap.put(className, loadPath);

        }

        Class<?> clazz = loadPath.loadPath().get(path).getClazz();
        Log.i("Vam", loadPath.toString());
        Intent intent = new Intent(context, clazz);
        context.startActivity(intent);

    }

}
