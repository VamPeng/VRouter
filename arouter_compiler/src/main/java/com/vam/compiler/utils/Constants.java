package com.vam.compiler.utils;

import javax.annotation.processing.Filer;
import javax.annotation.processing.Messager;
import javax.lang.model.util.Types;

public class Constants {

    public static final String AROUTER_ANNOTATION_TYPES = "com.vam.annotation.ARouter";

    public static final String MODULE_NAME = "moduleName";

    public static final String APT_PACKAGE = "packageNameForAPT";

    public static final String STRING = "java.lang.String";

    public static final String ACTIVITY = "android.app.Activity";

    public static final String BASE_PACKAGE = "com.vam.arouterapi";

    public static final String AROUTER_GROUP = BASE_PACKAGE + ".core.ARouterLoadGroup";

    public static final String AROUTER_PATH = BASE_PACKAGE + ".core.ARouterLoadPath";

    public static final String PATH_METHOD_NAME = "loadPath";

    public static final String GROUP_METHOD_NAME = "loadGroup";

    public static final String PATH_PARAMETER_NAME = "pathMap";
    public static final String GROUP_PARAMETER_NAME = "groupMap";

    public static final String PATH_FILE_NAME = "ARouter$$Path$$";

    public static final String GROUP_FILE_NAME = "ARouter$$Group$$";

}
