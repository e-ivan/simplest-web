package cn.soboys.restapispringbootstarter.utils;

import cn.soboys.restapispringbootstarter.config.RestApiProperties;
import lombok.extern.slf4j.Slf4j;
import cn.hutool.v7.core.io.file.FileUtil;
import cn.hutool.v7.extra.spring.SpringUtil;
import org.lionsoul.ip2region.service.Config;
import org.lionsoul.ip2region.service.Ip2Region;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;

import java.io.File;
import java.io.IOException;
import java.util.Objects;

/**
 * @author E_Ivan
 * @date 2026/3/18 20:36
 */
@Slf4j
public class Ip2RegionUtil {

    private static volatile Ip2Region instance;

    private static Ip2Region getIp2Region() {
        if (instance == null) {
            synchronized (Ip2RegionUtil.class) {
                if (instance == null) {
                    try {
                        RestApiProperties.Ip2regionProperties ip2regionProperties = SpringUtil.getBean(RestApiProperties.Ip2regionProperties.class);
                        // 1, 创建 v4 的配置：指定缓存策略和 v4 的 xdb 文件路径
                        Config v4Config = Config.custom()
                                .setCachePolicy(Config.VIndexCache)     // 指定缓存策略:  NoCache / VIndexCache / BufferCache
                                .setSearchers(15)                       // 设置初始化的查询器数量
                                // .setCacheSliceBytes(int)             // 设置缓存的分片字节数，默认为 50MiB
                                // .setXdbInputStream(InputStream)      // 设置 v4 xdb 文件的 inputstream 对象
                                // .setXdbFile(File)                    // 设置 v4 xdb File 对象
                                .setXdbPath(getResourcePatch(ip2regionProperties.getLocation()))    // 设置 v4 xdb 文件的路径
                                .asV4();    // 指定为 v4 配置

                        // 2, 创建 v6 的配置：指定缓存策略和 v6 的 xdb 文件路径
                        Config v6Config = Config.custom()
                                .setCachePolicy(Config.VIndexCache)     // 指定缓存策略: NoCache / VIndexCache / BufferCache
                                .setSearchers(15)                       // 设置初始化的查询器数量
                                // .setCacheSliceBytes(int)             // 设置缓存的分片字节数，默认为 50MiB
                                // .setXdbInputStream(InputStream)      // 设置 v6 xdb 文件的 inputstream 对象
                                // .setXdbFile(File)                    // 设置 v6 xdb File 对象
                                .setXdbPath(getResourcePatch(ip2regionProperties.getLocationV6()))    // 设置 v6 xdb 文件的路径
                                .asV6();    // 指定为 v6 配置
                        instance = Ip2Region.create(v4Config, v6Config);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        return instance;
    }


    public static String getIpToCityInfo(String ip) {
        Ip2Region ip2Region = getIp2Region();
        if (Objects.nonNull(ip2Region)) {
            try {
                return ip2Region.search(ip);
            } catch (Exception e) {
                log.error("获取地址信息异常:{}", ip, e);
            }
        }
        return null;
    }

    public static String getResourcePatch(String location) throws IOException {
        ResourceLoader resourceLoader = new DefaultResourceLoader();
        Resource resource = resourceLoader.getResource(location);
        String dbPath;
        if (resource.getURI().getScheme().equals("jar")) {
            File file = new File("src/main/resources/" + ((ClassPathResource) resource).getPath());
            FileUtil.writeFromStream(resource.getInputStream(), file);
            dbPath = file.getAbsolutePath();
        } else {
            dbPath = resource.getFile().getPath();
        }
        return dbPath;
    }
}
