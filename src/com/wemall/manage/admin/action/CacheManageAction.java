package com.wemall.manage.admin.action;

import com.wemall.core.annotation.SecurityMapping;
import com.wemall.core.cache.MemcacheManagerForSpy;
import com.wemall.core.mv.JModelAndView;
import com.wemall.core.tools.CommUtil;
import com.wemall.foundation.service.ISysConfigService;
import com.wemall.foundation.service.IUserConfigService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.*;

/**
 * 缓存管理控制器
 */
@Controller
public class CacheManageAction {
    @Autowired
    private ISysConfigService configService;

    @Autowired
    private IUserConfigService userConfigService;

    @SecurityMapping(display = false, rsequence = 0, title = "缓存列表", value = "/admin/cache_list.htm*", rtype = "admin", rname = "更新缓存", rcode = "cache_manage", rgroup = "工具")
    @RequestMapping({"/admin/cache_list.htm"})
    public ModelAndView cache_list(HttpServletRequest request, HttpServletResponse response){

        ModelAndView mv = new JModelAndView("admin/blue/cache_list.html", configService.getSysConfig(), userConfigService.getUserConfig(), 0, request, response);
        MemcacheManagerForSpy mc = MemcacheManagerForSpy.getInstance();
        Iterator iterSlabs = mc.getStats("items").values().iterator();
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
            for (Iterator iterItems = mc.getStats(commond).values().iterator(); iterItems.hasNext(); list.addAll(items.keySet()))
                items = (Map)iterItems.next();

        }

        int data_cache_size = 0;
        mv.addObject("data_cache_size", Integer.valueOf(list.size()));
        return mv;
    }

    @SecurityMapping(display = false, rsequence = 0, title = "更新缓存", value = "/admin/update_cache.htm*", rtype = "admin", rname = "更新缓存", rcode = "cache_manage", rgroup = "工具")
    @RequestMapping({"/admin/update_cache.htm"})
    public ModelAndView update_cache(HttpServletRequest request, HttpServletResponse response, String data_cache, String page_cache){
        ModelAndView mv = new JModelAndView("admin/blue/success.html", configService.getSysConfig(), userConfigService.getUserConfig(), 0, request, response);
        MemcacheManagerForSpy mc = MemcacheManagerForSpy.getInstance();
        mc.removeAll();
        mv.addObject("list_url", (new StringBuilder(String.valueOf(CommUtil.getURL(request)))).append("/admin/cache_list.htm").toString());
        mv.addObject("op_title", "更新缓存成功");
        return mv;
    }
}




