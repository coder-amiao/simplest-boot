package cn.soboys.simplest.ip2region;

import lombok.extern.slf4j.Slf4j;
import org.dromara.hutool.core.exception.ExceptionUtil;
import org.dromara.hutool.core.io.file.FileUtil;
import org.dromara.hutool.extra.spring.SpringUtil;
import org.lionsoul.ip2region.xdb.Searcher;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;

import java.io.File;

/**
 * @author kenx
 * @version 1.0
 * @date 2023/8/10 22:39
 */
@Slf4j
public class Ip2regionUtil {
    private static final String UNKNOWN = "unknown";


    private static Ip2regionProperties ip2regionProperties = SpringUtil.getBean(Ip2regionProperties.class);


    /**
     * 根据ip获取城市地理位置信息
     *
     * @param ip
     * @return
     */
    public static String getIpToCityInfo(String ip) {
        try {

            ResourceLoader resourceLoader = new DefaultResourceLoader();
            Resource resource = resourceLoader.getResource(ip2regionProperties.getLocation());
            String dbPath = "";
            if (resource.getURI().getScheme().equals("jar")) {

                File file=new File("src/main/resources/"+((ClassPathResource) resource).getPath());
                FileUtil.writeFromStream(resource.getInputStream(),file);
                dbPath=file.getAbsolutePath();
            } else {
                dbPath = resource.getFile().getPath();
            }

//            if (ip2regionProperties.isExternal()) {
//                ClassPathResource resource = new ClassPathResource(ip2regionProperties.getLocation());
//                dbPath = resource.getFile().getAbsolutePath();
//            } else {
//                // 获取当前默认记录地址位置的文件
//                ResourceLoader resourceLoader = new DefaultResourceLoader();
//                Resource resource = resourceLoader.getResource("classpath:resource.txt");
//
//
//                URL u =null; //HttpUserAgent.class.getClass().getResource(dbPath);
//
//                dbPath= HttpUserAgent.class.getClassLoader().getResource("ip2region/ip2region.xdb").getPath();
////                if(u==null||u.getPath().contains("file")){
////                    dbPath =    System.getProperty("user.dir") + "/ip.db";
////                    HttpUserAgent.class.getClassLoader().getResource("ip2region/ip2region.xdb").getPath();
////                    File file=new File(dbPath);
////                    FileUtil.writeFromStream(HttpUserAgent.class.getClassLoader().getResourceAsStream("ip2region/ip2region.xdb"),file);
////                }else {
////                    dbPath=u.getPath();
////                }
//            }

            File file = new File(dbPath);
            //如果当前文件不存在，则从缓存中复制一份
            if (!file.exists()) {
                log.error("ip2region.xdb文件找不到请填写类路径");
                return "UNKNOWN";
            }
            //创建查询对象
            Searcher searcher = Searcher.newWithFileOnly(dbPath);
            //开始查询
            return searcher.search(ip);
        } catch (Exception e) {
            log.error("Ip查询城市地址解析失败{}", ExceptionUtil.stacktraceToString(e));
            e.printStackTrace();
        }
        //默认返回空字符串
        return "UNKNOWN";

    }
}
