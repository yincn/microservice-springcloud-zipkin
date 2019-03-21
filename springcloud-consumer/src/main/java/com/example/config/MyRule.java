package com.example.config;

import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.example.util.RequestVariableHolder;
import com.netflix.client.config.IClientConfig;
import com.netflix.hystrix.strategy.concurrency.HystrixRequestContext;
import com.netflix.loadbalancer.AbstractLoadBalancerRule;
import com.netflix.loadbalancer.ILoadBalancer;
import com.netflix.loadbalancer.Server;
import com.netflix.niws.loadbalancer.DiscoveryEnabledServer;

public class MyRule extends AbstractLoadBalancerRule{
	
	private AtomicInteger nextServerCyclicCounter;
	
	private static final Logger log = LoggerFactory.getLogger(MyRule.class);
	
	{
		nextServerCyclicCounter = new AtomicInteger(0);
	}
	
	/*
	private int total = 0;
	private int curIndex = 0;
	
	public Server choose(ILoadBalancer lb, Object key) {
		System.out.println("total:" + total + ",curIndex:" + curIndex);
		if (lb == null) {
			return null;
		}
		
		Server server = null;
		while (server == null) {
			if (Thread.interrupted()) {
				return null;
			}
			
			List<Server> upList = lb.getReachableServers();
			List<Server> allList = lb.getAllServers();
			int serverCount = allList.size();
			if (serverCount == 0) {
				return null;
			}
			
			if (total < 5) {
				server = upList.get(curIndex);
				total++;
			} else {
				total = 0;
				curIndex++;
				if (curIndex >= upList.size()) {
					curIndex = 0;
				}
			}
			
			if (server == null) {
				Thread.yield();
				continue;
			}
			if (server.isAlive()) {
				return server;
			}
			
			server = null;
			Thread.yield();
		}
		
		return server;
	}
	*/
	
	public Server choose(ILoadBalancer lb, Object key) {
		if (lb == null) {
			return null;
		}
		
		System.out.println("MyRule 线程:" + Thread.currentThread() + ", id: " + Thread.currentThread().getId());

		if (HystrixRequestContext.isCurrentThreadInitialized()) {
			String version = RequestVariableHolder.echo.get();
            HystrixRequestContext.getContextForCurrentThread().shutdown();
            
            if (!StringUtils.isEmpty(version)) {
    			// 根据参数 version 选择 Server
            	List<Server> upList = lb.getReachableServers();
    			for (Server server : upList) {
    				Map<String, String> metadata = ((DiscoveryEnabledServer)server).getInstanceInfo().getMetadata();
    				String metaVersion = metadata.get("version");
    				log.info("当前Server metaVersion:{}", metaVersion);
    				if (version.equals(metaVersion)) {
    					log.info("version 匹配，返回此 Server");
    					return server;
    				}
    			}
    		} else {
    			Server server = null;
    	        int count = 0;
    	        while (server == null && count++ < 10) {
    	            List<Server> reachableServers = lb.getReachableServers();
    	            List<Server> allServers = lb.getAllServers();
    	            int upCount = reachableServers.size();
    	            int serverCount = allServers.size();

    	            if ((upCount == 0) || (serverCount == 0)) {
    	                log.warn("No up servers available from load balancer: " + lb);
    	                return null;
    	            }

    	            int nextServerIndex = incrementAndGetModulo(serverCount);
    	            server = allServers.get(nextServerIndex);

    	            if (server == null) {
    	                /* Transient. */
    	                Thread.yield();
    	                continue;
    	            }

    	            if (server.isAlive() && (server.isReadyToServe())) {
    	            	Map<String, String> metadata = ((DiscoveryEnabledServer)server).getInstanceInfo().getMetadata();
        				String metaVersion = metadata.get("version");
        				if (metaVersion.equals("8001")) {
        					continue;
    					}
        				
    	            	log.info("返回端口号：{} 的 Server.", server.getHostPort());
    	                return (server);
    	            }

    	            // Next.
    	            server = null;
    	        }

    	        if (count >= 10) {
    	            log.warn("No available alive servers after 10 tries from load balancer: "
    	                    + lb);
    	        }
    	        return server;
    		}
        } else {
        	System.out.println("不是 Hystrix 线程.");
        	return lb.getReachableServers().get(0);
        }
		
		return null;
	}
	
	@Override
	public Server choose(Object key) {
		return choose(getLoadBalancer(), key);
	}

	@Override
	public void initWithNiwsConfig(IClientConfig arg0) {
	}
	
    private int incrementAndGetModulo(int modulo) {
        for (;;) {
            int current = nextServerCyclicCounter.get();
            int next = (current + 1) % modulo;
            if (nextServerCyclicCounter.compareAndSet(current, next))
                return next;
        }
    }

}
