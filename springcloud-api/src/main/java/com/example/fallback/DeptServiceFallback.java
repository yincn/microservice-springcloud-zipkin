package com.example.fallback;

import org.springframework.stereotype.Component;

import com.example.model.Result;
import com.example.service.DeptService;

import feign.hystrix.FallbackFactory;

@Component
public class DeptServiceFallback implements FallbackFactory<DeptService> {
	@Override
	public DeptService create(Throwable arg0) {
		return new DeptService() {
			
			@Override
			public Result get(Integer id) {
				Result result = new Result();
				result.setCode(-1);
				result.setInfo("Dept server is down");
				return result;
			}
		};
	}
}
