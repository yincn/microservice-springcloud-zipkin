package com.example.fallback;

import org.springframework.stereotype.Component;

import com.example.model.Result;
import com.example.service.UserService;

import feign.hystrix.FallbackFactory;

@Component
public class UserServiceFallback implements FallbackFactory<UserService> {

	@Override
	public UserService create(Throwable arg0) {
		// TODO Auto-generated method stub
		return new UserService() {
			
			@Override
			public Result get(Integer id) {
				Result result = new Result();
				result.setCode(0);
				result.setInfo("User server is down");
				return result;
			}
		};
	}

}
