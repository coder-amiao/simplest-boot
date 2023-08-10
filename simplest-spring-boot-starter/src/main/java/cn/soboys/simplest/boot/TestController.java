package cn.soboys.simplest.boot;

import cn.soboys.simplest.cache.RedisTempUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * @author kenx
 * @version 1.0
 * @date 2023/8/10 17:51
 */
@RestController
public class TestController {

    @Autowired
    RedisTempUtil redisTempUtil;


    @GetMapping("/test")
    public Map ts() {
        Map m = new HashMap<>();
        m.put("time", new Date());
        return m;
    }
}
