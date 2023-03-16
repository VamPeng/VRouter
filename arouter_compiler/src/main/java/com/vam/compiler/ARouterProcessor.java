package com.vam.compiler;

import com.google.auto.service.AutoService;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;
import com.squareup.javapoet.WildcardTypeName;
import com.vam.annotation.ARouter;
import com.vam.annotation.model.RouterBean;
import com.vam.compiler.utils.Constants;
import com.vam.compiler.utils.EmptyUtils;

import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedOptions;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;
import javax.tools.Diagnostic;
import javax.tools.JavaFileObject;

@AutoService(Processor.class) // javax.annotation.processing.Processor
@SupportedAnnotationTypes({Constants.AROUTER_ANNOTATION_TYPES})
@SupportedSourceVersion(SourceVersion.RELEASE_8)
@SupportedOptions({Constants.MODULE_NAME, Constants.APT_PACKAGE})
public class ARouterProcessor extends AbstractProcessor {

    private Elements elementUtils;

    // 类信息工具类
    private Types typeUtils;

    // 输出日志
    private Messager messager;

    // 文件生成器
    private Filer filer;
    private String moduleName;
    private String packageNameForAPT;

    private Map<String, List<RouterBean>> tempPathMap = new HashMap<>();

    private Map<String, String> tempGroupMap = new HashMap<>();

    // 初始化
    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);
        elementUtils = processingEnv.getElementUtils();
        typeUtils = processingEnv.getTypeUtils();
        messager = processingEnv.getMessager();
        filer = processingEnv.getFiler();


        Map<String, String> options = processingEnv.getOptions();
        if (!EmptyUtils.isEmpty(options)) {

            moduleName = options.get(Constants.MODULE_NAME);
            packageNameForAPT = options.get(Constants.APT_PACKAGE);

            // 有坑，不能像android中Log.e的写法
            log("module: " + moduleName + ", packageForAPT: " + packageNameForAPT);
        }

        if (EmptyUtils.isEmpty(moduleName) || EmptyUtils.isEmpty(packageNameForAPT)) {
            throw new RuntimeException("注解处理器需要的参数 moduleName 或 packageName 为空,请在对应的 build.gradle 中配置");
        }

    }

    /**
     * @param annotations 处理的注解集合
     * @param roundEnv    运行时环境
     * @return true 后续处理结束，不会再处理。 false 处理失败
     */
    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        messager.printMessage(Diagnostic.Kind.NOTE, "annotations start process");
        if (!EmptyUtils.isEmpty(annotations)) {

            Set<? extends Element> elements = roundEnv.getElementsAnnotatedWith(ARouter.class);

            if (!EmptyUtils.isEmpty(elements)) {
                try {
                    parseElements(elements);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }

            return true;
        }

        // 原始写法
//        origin(annotations,roundEnv);
        // javapoet 初步使用
//        javapoet(annotations, roundEnv);

        return false;
    }

    /**
     * 解析所有被@ARouter注解的元素集合
     *
     * @param elements
     */
    private void parseElements(Set<? extends Element> elements) throws IOException {

        TypeElement activityType = elementUtils.getTypeElement(Constants.ACTIVITY);
        TypeMirror activityMirror = activityType.asType();

        for (Element element : elements) {

            TypeMirror elementMirror = element.asType();

            log("遍历的元素信息为: " + elementMirror.toString());

            ARouter aRouter = element.getAnnotation(ARouter.class);

            // 路由详细信息封装到实体类
            RouterBean bean = new RouterBean.Builder()
                    .setGroup(aRouter.group())
                    .setPath(aRouter.path())
                    .setElement(element)
                    .build();

            // 判断说明，@ARouter仅仅只能用在类智商，并且只能是Activity
            if (typeUtils.isSubtype(elementMirror, activityMirror)) {
                bean.setType(RouterBean.Type.activity);
            } else {
                throw new RuntimeException("@ARouter 注解目前仅限于Activity之上");
            }

            // 赋值临时 map 存储以上信息，用来遍历时生成代码
            valueOfPathMap(bean);

        }

        TypeElement groupLoadType = elementUtils.getTypeElement(Constants.AROUTER_GROUP);
        TypeElement pathLoadType = elementUtils.getTypeElement(Constants.AROUTER_PATH);

        createPathFile(pathLoadType);
        createGroupFile(groupLoadType, pathLoadType);

    }

    private void createGroupFile(TypeElement groupLoadType, TypeElement pathLoadType) throws IOException {

        if (EmptyUtils.isEmpty(tempGroupMap) || EmptyUtils.isEmpty(tempPathMap)) return;

        ParameterizedTypeName methodReturns = ParameterizedTypeName.get(
                ClassName.get(Map.class),
                ClassName.get(String.class),

                // 第二个参数: Class<? extends ARouterLoadPath>
                // 某某Class是否属于ARouterLoadPath接口的实现类
                ParameterizedTypeName.get(ClassName.get(Class.class),
                        WildcardTypeName.subtypeOf(ClassName.get(pathLoadType)))
        );

        MethodSpec.Builder methodBuilder = MethodSpec.methodBuilder(Constants.GROUP_METHOD_NAME)
                .addAnnotation(Override.class)
                .addModifiers(Modifier.PUBLIC)
                .returns(methodReturns);

        methodBuilder.addStatement("$T<$T, $T> $N = new $T<>()",
                ClassName.get(Map.class),
                ClassName.get(String.class),
                ParameterizedTypeName.get(ClassName.get(Class.class),
                        WildcardTypeName.subtypeOf(ClassName.get(pathLoadType))),
                Constants.GROUP_PARAMETER_NAME,
                HashMap.class);

        for (Map.Entry<String, String> stringStringEntry : tempGroupMap.entrySet()) {
            methodBuilder.addStatement("$N.put($S,$T.class)",
                    Constants.GROUP_PARAMETER_NAME,
                    stringStringEntry.getKey(),
                    ClassName.get(packageNameForAPT, stringStringEntry.getValue()));

        }

        methodBuilder.addStatement("return $N", Constants.GROUP_PARAMETER_NAME);

        String finalClassName = Constants.GROUP_FILE_NAME + moduleName;
        log("APT生成路由组 Group 类文件: " + packageNameForAPT + "." + finalClassName);

        JavaFile.builder(packageNameForAPT,
                        TypeSpec.classBuilder(finalClassName)
                                .addModifiers(Modifier.PUBLIC)
                                .addSuperinterface(ClassName.get(groupLoadType))
                                .addMethod(methodBuilder.build())
                                .build())
                .build()
                .writeTo(filer);


    }

    private void createPathFile(TypeElement pathLoadType) throws IOException {

        if (EmptyUtils.isEmpty(tempPathMap)) return;

        TypeName methodReturns = ParameterizedTypeName.get(
                ClassName.get(Map.class),
                ClassName.get(String.class),
                ClassName.get(RouterBean.class));

        for (Map.Entry<String, List<RouterBean>> entry : tempPathMap.entrySet()) {
            MethodSpec.Builder methodBuilder = MethodSpec
                    .methodBuilder(Constants.PATH_METHOD_NAME)
                    .addAnnotation(Override.class)
                    .addModifiers(Modifier.PUBLIC)
                    .returns(methodReturns);

            methodBuilder.addStatement("$T<$T,$T> $N = new $T<>()",
                    ClassName.get(Map.class),
                    ClassName.get(String.class),
                    ClassName.get(RouterBean.class),
                    Constants.PATH_PARAMETER_NAME,
                    HashMap.class);

            List<RouterBean> pathList = entry.getValue();
            /*
             * pathMap.put("/app/MainActivity",
             *                 RouterBean.create(
             *                         RouterBean.Type.activity,
             *                         MainActivity.class,
             *                         "/app/MainActivity",
             *                         "app"));
             */

            for (RouterBean bean : pathList) {

                methodBuilder
                        .addStatement(
                                "$N.put($S,$T.create($T.$L, $T.class, $S, $S))",
                                Constants.PATH_PARAMETER_NAME,
                                bean.getPath(),
                                ClassName.get(RouterBean.class),
                                ClassName.get(RouterBean.Type.class),
                                bean.getType(),
                                ClassName.get((TypeElement) bean.getElement()),
                                bean.getPath(),
                                bean.getGroup());

            }

            methodBuilder
                    .addStatement("return $N", Constants.PATH_PARAMETER_NAME);

            // 生成类文件
            String finalClassName = Constants.PATH_FILE_NAME + entry.getKey();
            log("APT 生成路由 Path 类文件 为: " + packageNameForAPT + "." + finalClassName);

            JavaFile.builder(packageNameForAPT,
                            TypeSpec.classBuilder(finalClassName)
                                    .addSuperinterface(ClassName.get(pathLoadType))
                                    .addModifiers(Modifier.PUBLIC)
                                    .addMethod(methodBuilder.build())
                                    .build())
                    .build()
                    .writeTo(filer);


            tempGroupMap.put(entry.getKey(), finalClassName);
        }


    }

    /**
     * 1、生成路由的详细 path 文件
     * 2、生成路由组 Group 类文件
     *
     * @param bean
     */
    private void valueOfPathMap(RouterBean bean) {

        if (checkRouterPath(bean)) {

            log("RouterBean >>> " + bean);

            List<RouterBean> routerBeans = tempPathMap.get(bean.getGroup());
            if (EmptyUtils.isEmpty(routerBeans)) {
                routerBeans = new ArrayList<>();
                tempPathMap.put(bean.getGroup(), routerBeans);
            }

            boolean isContains = false;
            for (RouterBean routerBean : routerBeans) {
                if (routerBean.getPath().equalsIgnoreCase(bean.getPath())) {
                    isContains = true;
                    break;
                }
            }

            if (!isContains) {
                routerBeans.add(bean);
            }
        } else {
            logE("@ARouter注解未按规范设置， 如: /app/MainActivity");
        }

    }

    private boolean checkRouterPath(RouterBean bean) {
        String group = bean.getGroup();
        String path = bean.getPath();

        if (EmptyUtils.isEmpty(path) || !path.startsWith("/")) {
            logE("@ARouter注解未按规范设置， 如: /app/MainActivity");
            return false;
        }

        if (path.lastIndexOf("/") == 0) {
            logE("@ARouter注解未按规范设置， 如: /app/MainActivity");
            return false;
        }

        String finalGroup = path.substring(1, path.indexOf("/", 1));

        if (finalGroup.contains("/")) {
            logE("@ARouter注解未按规范设置， 如: /app/MainActivity");
            return false;
        }

        if (!EmptyUtils.isEmpty(group) && !group.equals(moduleName)) {
            logE("@ARouter注解中的group值必须和当前子模块名相同");
            return false;
        } else {
            bean.setGroup(finalGroup);
        }

        return true;
    }

    private void log(String content) {
        // 有坑，不能像android中Log.e的写法
        messager.printMessage(Diagnostic.Kind.NOTE, content);
    }

    private void logE(String content) {
        // 有坑，不能像android中Log.e的写法
        messager.printMessage(Diagnostic.Kind.ERROR, content);
    }

    private void javapoet(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {

        Set<? extends Element> elements = roundEnv.getElementsAnnotatedWith(ARouter.class);


        for (Element element : elements) {
            String packageName = elementUtils.getPackageOf(element).getQualifiedName().toString();
            String className = element.getSimpleName().toString();
            log("被注解的类有: " + className);

            String finalName = className + "$$ARouter";

            ARouter aRouter = element.getAnnotation(ARouter.class);

            MethodSpec methodSpec = MethodSpec.methodBuilder("findTargetClass")
                    .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
                    .returns(Class.class)
                    .addParameter(String.class, "path")
//                    .addStatement("$T.out.println($S)", System.class, "Hello, JavaPoet!")
                    .addStatement(
                            "return path.equals($S) ? $T.class : null",
                            aRouter.path(),
                            ClassName.get((TypeElement) element))
                    .build();

            TypeSpec typeSpec = TypeSpec.classBuilder(finalName)
                    .addModifiers(Modifier.PUBLIC, Modifier.FINAL)
                    .addMethod(methodSpec)
                    .build();

            JavaFile javaFile = JavaFile.builder(packageName, typeSpec)
                    .build();

            try {
                javaFile.writeTo(filer);
                log("生成文件成功");
            } catch (IOException e) {
                log("生成文件异常");
                throw new RuntimeException(e);
            }

        }

    }

    // 原生手写
    private void origin(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {

        Set<? extends Element> elements = roundEnv.getElementsAnnotatedWith(ARouter.class);

        for (Element element : elements) {
            String packageName = elementUtils.getPackageOf(element).getQualifiedName().toString();
            String className = element.getSimpleName().toString();


            // 最终我们想要生成的类文件
            String finalClassName = className + "$$ARouter";


            try {
                JavaFileObject sourceFile = filer.createSourceFile(packageName + "." + finalClassName);

                Writer writer = sourceFile.openWriter();


                writer.write("package " + packageName + ";\n");
                writer.write("public class " + finalClassName + "{\n");
                writer.write(" public static Class<?> findTargetClass(String path) {\n");

                ARouter aRouter = element.getAnnotation(ARouter.class);

                writer.write("if (path.equalsIgnoreCase(\"" + aRouter.path() + "\")) {\n");
                writer.write("return " + className + ".class;\n}\n");
                writer.write("return null;\n");
                writer.write("}\n}");

                writer.close();


            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

}
