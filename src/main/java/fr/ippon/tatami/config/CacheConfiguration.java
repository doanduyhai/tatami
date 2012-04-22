package fr.ippon.tatami.config;

import org.springframework.cache.CacheManager;
import org.springframework.cache.ehcache.EhCacheCacheManager;
import org.springframework.context.annotation.Bean;

/**
 * 
 * @author Julien Dubois
 */
public class CacheConfiguration
{

	@Bean
	public CacheManager cacheManager()
	{
		EhCacheCacheManager cacheManager = new EhCacheCacheManager();
		cacheManager.setCacheManager(ehCacheManager());
		return cacheManager;
	}

	@Bean
	public net.sf.ehcache.CacheManager ehCacheManager()
	{
		return new net.sf.ehcache.CacheManager();
	}

}