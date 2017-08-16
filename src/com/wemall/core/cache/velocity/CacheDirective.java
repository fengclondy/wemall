package com.wemall.core.cache.velocity;

import com.wemall.core.cache.MemcacheManagerForSpy;
import com.wemall.core.tools.CommUtil;
import com.wemall.core.tools.Md5Encrypt;
import org.apache.velocity.context.InternalContextAdapter;
import org.apache.velocity.exception.MethodInvocationException;
import org.apache.velocity.exception.ParseErrorException;
import org.apache.velocity.exception.ResourceNotFoundException;
import org.apache.velocity.exception.TemplateInitException;
import org.apache.velocity.runtime.RuntimeServices;
import org.apache.velocity.runtime.directive.Directive;
import org.apache.velocity.runtime.parser.node.Node;
import org.apache.velocity.runtime.parser.node.SimpleNode;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;

public class CacheDirective extends Directive
{

	private static int cachetime = 300;
	private static String region = "wemall";
	private static String mark = "@@";
	private static boolean flush = true;
	private static String prefix = "_old";

	public CacheDirective()
	{
	}

	public String getName()
	{
		return "cache";
	}

	public int getType()
	{
		return 1;
	}

	public void init(RuntimeServices rs, InternalContextAdapter context, Node node)
		throws TemplateInitException
	{
		super.init(rs, context, node);
		cachetime = rs.getInt("userdirective.cache.cachetime", 300);
		region = rs.getString("userdirective.cache.region", "wemall");
		flush = rs.getBoolean("userdirective.cache.flush", true);
	}

	public boolean render(InternalContextAdapter context, Writer writer, Node node)
		throws IOException, ResourceNotFoundException, ParseErrorException, MethodInvocationException
	{
		MemcacheManagerForSpy client = MemcacheManagerForSpy.getInstance();
		int cache_time = cachetime;
		int node_size = node.jjtGetNumChildren();
		String node_type = "html";
		if (node_size == 3)
		{
			SimpleNode timenode = (SimpleNode)node.jjtGetChild(1);
			cache_time = ((Integer)timenode.value(context)).intValue();
		}
		if (node_size == 4)
		{
			SimpleNode timenode = (SimpleNode)node.jjtGetChild(2);
			node_type = CommUtil.null2String(timenode.value(context));
		}
		SimpleNode sn_key = (SimpleNode)node.jjtGetChild(0);
		String key = (String)sn_key.value(context);
		Node body = node.jjtGetChild(node_size - 1);
		String tpl_key = (new StringBuilder(String.valueOf(key))).append(mark).append(region).toString();
		Object cache_html = client.get(tpl_key);
		if (flush)
		{
			String body_tpl = Md5Encrypt.md5(body.literal()).toLowerCase();
			Object old_body_tpl = client.get((new StringBuilder(String.valueOf(tpl_key))).append(prefix).toString());
			if (cache_html == null || !body_tpl.equals(CommUtil.null2String(old_body_tpl)))
			{
				if (node_type.equals("html"))
				{
					StringWriter sw = new StringWriter();
					body.render(context, sw);
					cache_html = sw.toString();
					client.add(tpl_key, cache_html, cache_time);
					client.add((new StringBuilder(String.valueOf(tpl_key))).append(prefix).toString(), body_tpl, cachetime);
				}
				if (node_type.equals("url"))
				{
					cache_html = getHttpContent(key, "UTF-8", "POST");
					client.add(tpl_key, cache_html, cache_time);
					client.add((new StringBuilder(String.valueOf(tpl_key))).append(prefix).toString(), body_tpl, cachetime);
				}
				if (node_type.equals("script"))
				{
					cache_html = (new StringBuilder("<script>")).append(getHttpContent(key, "UTF-8", "POST")).append("</script>").toString();
					client.add(tpl_key, cache_html, cache_time);
					client.add((new StringBuilder(String.valueOf(tpl_key))).append(prefix).toString(), body_tpl, cachetime);
				}
			}
		} else
		if (cache_html == null)
		{
			if (node_type.equals("html"))
			{
				StringWriter sw = new StringWriter();
				body.render(context, sw);
				cache_html = sw.toString();
				client.add(tpl_key, cache_html, cache_time);
			}
			if (node_type.equals("url"))
			{
				cache_html = getHttpContent(key, "UTF-8", "POST");
				client.add(tpl_key, cache_html, cache_time);
			}
			if (node_type.equals("script"))
			{
				cache_html = (new StringBuilder("<script>")).append(getHttpContent(key, "UTF-8", "POST")).append("</script>").toString();
				client.add(tpl_key, cache_html, cache_time);
			}
		}
		writer.write(cache_html.toString());
		return true;
	}

	public static String getHttpContent(String url, String charSet, String method)
	{
		HttpURLConnection connection;
		String content = "";
		URL address_url = null;
		try {
			address_url = new URL(url);
			connection = (HttpURLConnection)address_url.openConnection();
			connection.setRequestMethod("GET");
			connection.setConnectTimeout(3600);
			connection.setReadTimeout(3600);
			int response_code = 0;
			response_code = connection.getResponseCode();
			InputStream in = connection.getInputStream();
			BufferedReader reader = new BufferedReader(new InputStreamReader(in, charSet));
			for (String line = null; (line = reader.readLine()) != null;)
				content = (new StringBuilder(String.valueOf(content))).append(line).toString();
			if (connection != null)
				connection.disconnect();
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (ProtocolException e) {
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return content;
	}
}
