package com.vam.mydemo.test;

import com.vam.annotation.model.RouterBean;
import com.vam.arouterapi.core.ARouterLoadPath;
import com.vam.mydemo.MainActivity;

import java.util.HashMap;
import java.util.Map;

/**
 * 模拟
 */
public class ARouter$$Path$$app implements ARouterLoadPath {

    @Override
    public Map<String, RouterBean> loadPath() {

        Map<String, RouterBean> pathMap = new HashMap<>();

        pathMap.put("/app/MainActivity",
                RouterBean.create(
                        RouterBean.Type.activity,
                        MainActivity.class,
                        "/app/MainActivity",
                        "app"));
        return pathMap;
    }
}
