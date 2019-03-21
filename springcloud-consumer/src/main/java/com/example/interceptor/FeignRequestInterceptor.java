package com.example.interceptor;

import com.example.util.RequestVariableHolder;

import feign.RequestInterceptor;
import feign.RequestTemplate;

//@Component
public class FeignRequestInterceptor implements RequestInterceptor {

    @Override
    public void apply(RequestTemplate template) {
        System.out.println("Hystrix 线程池线程:" + Thread.currentThread() + ", id: " + Thread.currentThread().getId() + ",name:" + RequestVariableHolder.echo.get());
    }
}