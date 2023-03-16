package com.vam.annotation.model;

import javax.lang.model.element.Element;

/**
 *
 */
public class RouterBean {

    public enum Type {
        activity,

        // 还没写，暂时不能用
//        fragment;
    }
    private Type type;

    private Element element;

    private Class<?> clazz;

    private String group;

    private String path;

    public Type getType() {
        return type;
    }

    public Element getElement() {
        return element;
    }

    public Class<?> getClazz() {
        return clazz;
    }

    public String getGroup() {
        return group;
    }

    public String getPath() {
        return path;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public void setGroup(String group) {
        this.group = group;
    }

    private RouterBean(Builder builder) {
        this.path = builder.path;
        this.element = builder.element;
        this.group = builder.group;
    }

    public RouterBean(Type type, Class<?> clazz, String path, String group) {
        this.type = type;
        this.clazz = clazz;
        this.path = path;
        this.group = group;
    }

    public static RouterBean create(Type type, Class<?> clazz, String path, String group) {
        return new RouterBean(type,clazz,path,group);
    }

    @Override
    public String toString() {
        return "RouterBean{" +
                "group='" + group + '\'' +
                ", path='" + path + '\'' +
                '}';
    }

    public final static class Builder {
        private Element element;

        private String group;

        private String path;

        public Builder setElement(Element element) {
            this.element = element;
            return this;
        }

        public Builder setGroup(String group) {
            this.group = group;
            return this;
        }

        public Builder setPath(String path) {
            this.path = path;
            return this;
        }

        // 参数校验或者初始化
        public RouterBean build() {
            if (path == null || path.length() == 0) {
                throw new IllegalStateException("path必须不为空, 如: /app/MainActivity");
            }
            return new RouterBean(this);
        }
    }

}
