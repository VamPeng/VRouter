package com.vam.mydemo.test;

import com.vam.annotation.model.RouterBean;
import com.vam.arouterapi.core.ARouterLoadPath;
import com.vam.order.OrderActivity;

import java.util.HashMap;
import java.util.Map;

/**
 * 模拟
 */
public class ARouter$$Path$$order implements ARouterLoadPath {

    @Override
    public Map<String, RouterBean> loadPath() {

        Map<String, RouterBean> pathMap = new HashMap<>();

        pathMap.put("/order/OrderActivity",
                RouterBean.create(
                        RouterBean.Type.activity,
                        OrderActivity.class,
                        "/order/OrderActivity",
                        "order"));
        return pathMap;
    }
}
