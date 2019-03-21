package com.example.util;
import com.netflix.hystrix.strategy.concurrency.HystrixRequestVariableDefault;

public class RequestVariableHolder {

	public static final HystrixRequestVariableDefault<String> echo = new HystrixRequestVariableDefault<>();
    
}