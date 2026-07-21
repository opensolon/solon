/*
 * Copyright 2017-2025 noear.org and authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.noear.solon.net.http;

import org.noear.solon.net.http.impl.HttpUtilsFactoryDefault;

import java.util.*;

/**
 * Http 配置类
 * <p>
 * 连接池、并发与重定向等参数对实现通用：OkHttp 与 JDK 路径按能力映射使用。
 * 连接池/调度器相关配置须在首次请求前设置（懒加载后仅首次生效）。
 *
 * @author noear
 * @since 2.9
 * @since 3.9 增加连接池/并发/重定向等配置
 */
public class HttpConfiguration {
    private static volatile Set<HttpExtension> extensions = new LinkedHashSet<>();
    private static volatile HttpUtilsFactory factory = new HttpUtilsFactoryDefault();

    /**
     * 最大空闲连接数（默认 50）
     * <p>OkHttp：ConnectionPool；JDK：参考系统属性 http.maxConnections</p>
     */
    private static volatile int maxIdleConnections = 50;
    /**
     * 空闲连接保活分钟数（默认 5）
     * <p>OkHttp：ConnectionPool keep-alive；JDK：依赖 JVM keep-alive</p>
     */
    private static volatile long keepAliveMinutes = 5;
    /**
     * 全局最大在途请求数 / 异步并发上限（默认 256）
     * <p>OkHttp：Dispatcher.maxRequests；JDK：异步调度线程池 maxThreads</p>
     */
    private static volatile int maxRequests = 256;
    /**
     * 单 host 最大在途请求数（默认 64）
     * <p>OkHttp：Dispatcher.maxRequestsPerHost；JDK：无对等控制（忽略）</p>
     */
    private static volatile int maxRequestsPerHost = 64;
    /**
     * 关闭响应流时是否强制断开连接（默认 false，利于 keep-alive 复用）
     * <p>主要影响 JDK HttpURLConnection；OkHttp 由 response/body 关闭归还连接池</p>
     */
    private static volatile boolean forceDisconnectOnClose = false;
    /**
     * 最大重定向次数（默认 20）
     */
    private static volatile int maxRedirects = 20;

    /**
     * 添加扩展
     */
    public static void addExtension(HttpExtension extension) {
        extensions.add(extension);
    }

    /**
     * 移除扩展
     */
    public static void removeExtension(HttpExtension extension) {
        extensions.remove(extension);
    }

    /**
     * 获取所有扩展
     */
    public static Collection<HttpExtension> getExtensions() {
        return extensions;
    }

    /**
     * 获取工厂
     */
    public static HttpUtilsFactory getFactory() {
        return factory;
    }

    /**
     * 设置工厂
     */
    public static void setFactory(HttpUtilsFactory factory) {
        HttpConfiguration.factory = factory;
    }

    // --------------- 连接与并发 ---------------

    public static int getMaxIdleConnections() {
        return maxIdleConnections;
    }

    /**
     * 设置最大空闲连接数（须在首次请求前配置）
     */
    public static void setMaxIdleConnections(int maxIdleConnections) {
        HttpConfiguration.maxIdleConnections = maxIdleConnections;
    }

    public static long getKeepAliveMinutes() {
        return keepAliveMinutes;
    }

    /**
     * 设置空闲连接保活分钟数（须在首次请求前配置）
     */
    public static void setKeepAliveMinutes(long keepAliveMinutes) {
        HttpConfiguration.keepAliveMinutes = keepAliveMinutes;
    }

    public static int getMaxRequests() {
        return maxRequests;
    }

    /**
     * 设置全局最大在途请求数 / 异步并发上限（须在首次请求前配置）
     * <p>OkHttp 映射 Dispatcher.maxRequests；JDK 映射异步线程池最大线程数</p>
     */
    public static void setMaxRequests(int maxRequests) {
        HttpConfiguration.maxRequests = maxRequests;
    }

    public static int getMaxRequestsPerHost() {
        return maxRequestsPerHost;
    }

    /**
     * 设置单 host 最大在途请求数（须在首次请求前配置）
     * <p>当前主要作用于 OkHttp；JDK 实现忽略</p>
     */
    public static void setMaxRequestsPerHost(int maxRequestsPerHost) {
        HttpConfiguration.maxRequestsPerHost = maxRequestsPerHost;
    }

    public static boolean isForceDisconnectOnClose() {
        return forceDisconnectOnClose;
    }

    /**
     * 设置关闭响应流时是否强制断开连接。
     * <p>false（默认）：仅关闭流，利于 keep-alive 复用；true：每次 close 都 disconnect（兼容旧行为，主要影响 JDK）</p>
     */
    public static void setForceDisconnectOnClose(boolean forceDisconnectOnClose) {
        HttpConfiguration.forceDisconnectOnClose = forceDisconnectOnClose;
    }

    // --------------- 通用 ---------------

    public static int getMaxRedirects() {
        return maxRedirects;
    }

    /**
     * 设置最大重定向次数
     */
    public static void setMaxRedirects(int maxRedirects) {
        HttpConfiguration.maxRedirects = maxRedirects;
    }
}
