package com.wemall.core.cache;

import com.wemall.core.tools.CommUtil;
import net.spy.memcached.MemcachedClient;
import org.apache.commons.lang.StringUtils;

import java.io.IOException;
import java.io.InputStream;
import java.net.InetSocketAddress;
import java.util.*;

public class MemcacheManagerForSpy
{
	private MemcachedClient memcacheClient;
	private static MemcacheManagerForSpy INSTANCE = new MemcacheManagerForSpy();

	private MemcacheManagerForSpy()
	{
		Properties prop = new Properties();
		InputStream in = MemcacheManagerForSpy.class.getResourceAsStream("/memcached.properties");
		try
		{
			prop.load(in);
			String host = prop.getProperty("host").trim();
			String port = prop.getProperty("port").trim();
			System.out.println(host);
			if (StringUtils.isBlank(host))
				host = "127.0.0.1";
			if (StringUtils.isBlank(port))
				host = "11211";
			memcacheClient = new MemcachedClient(new InetSocketAddress[] {
				new InetSocketAddress(host, CommUtil.null2Int(port))
			});
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}

	public static MemcacheManagerForSpy getInstance()
	{
		return INSTANCE;
	}

	public void add(String key, Object value, int milliseconds)
	{
		memcacheClient.add(key, milliseconds, value);
	}

	public void add(String key, Object value)
	{
		memcacheClient.add(key, 3600, value);
	}

	public void remove(String key, int milliseconds)
	{
		memcacheClient.delete(key);
	}

	public void remove(String key)
	{
		memcacheClient.delete(key);
	}

	public void removeAll()
	{
		Iterator iterSlabs = memcacheClient.getStats("items").values().iterator();
		Set set = new HashSet();
		while (iterSlabs.hasNext()) 
		{
			Map slab = (Map)iterSlabs.next();
			String index;
			for (Iterator iterator = slab.keySet().iterator(); iterator.hasNext(); set.add(index))
			{
				String key = (String)iterator.next();
				index = key.split(":")[1];
			}
		}
		List list = new LinkedList();
		for (Iterator iterator1 = set.iterator(); iterator1.hasNext();)
		{
			String v = (String)iterator1.next();
			String commond = "cachedump ".concat(v).concat(" 0");
			Map items;
			for (Iterator iterItems = memcacheClient.getStats(commond).values().iterator(); iterItems.hasNext(); list.addAll(items.keySet()))
				items = (Map)iterItems.next();
		}

		String key;
		for (Iterator iterator2 = list.iterator(); iterator2.hasNext(); remove(key))
			key = (String)iterator2.next();
	}

	public void update(String key, Object value, int milliseconds)
	{
		memcacheClient.replace(key, milliseconds, value);
	}

	public void update(String key, Object value)
	{
		memcacheClient.replace(key, 3600, value);
	}

	public Object get(String key)
	{
		return memcacheClient.get(key);
	}

	public void flush()
	{
		memcacheClient.flush();
	}

	public Map getStats(String key)
	{
		return memcacheClient.getStats(key);
	}

	public Map getBulk(List list)
	{
		return memcacheClient.getBulk(list);
	}
}
