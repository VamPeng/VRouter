package com.vam.arouterapi.core;

import java.util.Map;

/**
 * 路由组group加载数据对外接口
 */
public interface ARouterLoadGroup {

    Map<String, Class<? extends ARouterLoadPath>> loadGroup();

}
