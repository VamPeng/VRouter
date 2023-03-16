package com.vam.mydemo.test;

import com.vam.arouterapi.core.ARouterLoadGroup;
import com.vam.arouterapi.core.ARouterLoadPath;

import java.util.HashMap;
import java.util.Map;

/**
 * 模拟ARouter路由器族文件
 */
public class ARouter$$Group$$order implements ARouterLoadGroup {
    @Override
    public Map<String, Class<? extends ARouterLoadPath>> loadGroup() {

        Map<String, Class<? extends ARouterLoadPath>> groupMap = new HashMap<>();

        groupMap.put("order", ARouter$$Path$$order.class);

        return groupMap;
    }
}
