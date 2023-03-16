package com.vam.arouterapi.core;

import com.vam.annotation.model.RouterBean;

import java.util.Map;

/**
 * 路由组Group对应的详细Path加载数据接口
 */
public interface ARouterLoadPath {

    /**
     * @return key:"/app/MainActivity", value:MainActivity信息
     */
    Map<String, RouterBean> loadPath();

}
