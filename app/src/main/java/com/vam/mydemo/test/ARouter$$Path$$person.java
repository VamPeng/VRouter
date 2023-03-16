package com.vam.mydemo.test;

import com.vam.annotation.model.RouterBean;
import com.vam.arouterapi.core.ARouterLoadPath;
import com.vam.person.PersonActivity;

import java.util.HashMap;
import java.util.Map;

/**
 * 模拟
 */
public class ARouter$$Path$$person implements ARouterLoadPath {

    @Override
    public Map<String, RouterBean> loadPath() {

        Map<String, RouterBean> pathMap = new HashMap<>();

        pathMap.put("/person/PersonActivity",
                RouterBean.create(
                        RouterBean.Type.activity,
                        PersonActivity.class,
                        "/person/PersonActivity",
                        "person"));
        return pathMap;
    }
}
