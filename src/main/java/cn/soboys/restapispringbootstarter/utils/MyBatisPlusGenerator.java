package cn.soboys.restapispringbootstarter.utils;

import cn.soboys.restapispringbootstarter.config.GenerateCodeConfig;
import com.baomidou.mybatisplus.core.exceptions.MybatisPlusException;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.generator.AutoGenerator;
import com.baomidou.mybatisplus.generator.config.DataSourceConfig;
import com.baomidou.mybatisplus.generator.config.GlobalConfig;
import com.baomidou.mybatisplus.generator.config.InjectionConfig;
import com.baomidou.mybatisplus.generator.config.PackageConfig;
import cn.hutool.v7.core.text.StrUtil;

import java.util.Scanner;

/**
 * @author 公众号 程序员三时
 * @version 1.0
 * @date 2023/6/27 23:52
 * @webSite https://github.com/coder-amiao
 */

public class MyBatisPlusGenerator {


    /**
     * <p>
     * 读取控制台内容
     * </p>
     */
    public static String scanner(String tip) {
        Scanner scanner = new Scanner(System.in);
        StringBuilder help = new StringBuilder();
        help.append("请输入" + tip + "：");
        System.out.println(help.toString());
        if (scanner.hasNext()) {
            String ipt = scanner.next();
            if (StringUtils.isNotBlank(ipt)) {
                return ipt;
            }
        }
        throw new MybatisPlusException("请输入正确的" + tip + "！");
    }

    /**
     * 代码生成配置
     *
     * @param config
     */
    public static void generate(GenerateCodeConfig config) {
        // 代码生成器
        GlobalConfig.Builder gc = new GlobalConfig.Builder();
        // 全局配置
        final String[] projectPath = new String[1];
        projectPath[0] = System.getProperty("user.dir");
        //String projectPath = System.getProperty("user.dir");

        if (StrUtil.isNotEmpty(config.getProjectPath())) {
            projectPath[0] = config.getProjectPath();
        }
        gc.outputDir(projectPath[0] + "/src/main/java");
        gc.author("公众号 程序员三时");
        gc.disableOpenDir();
        // gc.setSwagger2(true); 实体属性 Swagger2 注解
        // 数据源配置
        DataSourceConfig.Builder dsc = new DataSourceConfig.Builder(config.getUrl(), config.getUsername(), config.getPassword());
//        dsc.driverClassName(config.getDriverName());
        AutoGenerator mpg = new AutoGenerator(dsc.build());
        mpg.global(gc.build());

        // 包配置
        PackageConfig.Builder pc = new PackageConfig.Builder();
        //pc.setModuleName(scanner("模块名"));
        pc.parent(config.getPackages());
        mpg.packageInfo(pc.build());

        // 自定义配置
        InjectionConfig.Builder cfg = new InjectionConfig.Builder();
        // 如果模板引擎是 freemarker
        String templatePath = "/templates/mapper.xml.ftl";
        // 如果模板引擎是 velocity
        // String templatePath = "/templates/mapper.xml.vm";

//        // 自定义输出配置
//        List<FileOutConfig> focList = new ArrayList<>();
//        // 自定义配置会被优先输出
//        focList.add(new FileOutConfig(templatePath) {
//            @Override
//            public String outputFile(TableInfo tableInfo) {
//                // 自定义输出文件名 ， 如果你 Entity 设置了前后缀、此处注意 xml 的名称会跟着发生变化！！
//                return projectPath[0] + "/src/main/resources/mapper/" + pc.getModuleName()
//                        + "/" + tableInfo.getEntityName() + "Mapper" + StringPool.DOT_XML;
//            }
//        });
//        /*
//        cfg.setFileCreate(new IFileCreate() {
//            @Override
//            public boolean isCreate(ConfigBuilder configBuilder, FileType fileType, String filePath) {
//                // 判断自定义文件夹是否需要创建
//                checkDir("调用默认方法创建的目录，自定义目录用");
//                if (fileType == FileType.MAPPER) {
//                    // 已经生成 mapper 文件判断存在，不想重新生成返回 false
//                    return !new File(filePath).exists();
//                }
//                // 允许生成模板文件
//                return true;
//            }
//        });
//        */
//        cfg.setFileOutConfigList(focList);
//        mpg.setCfg(cfg);
//
//        // 配置模板
//        TemplateConfig templateConfig = new TemplateConfig();
//
//        // 配置自定义输出模板
//        //指定自定义模板路径，注意不要带上.ftl/.vm, 会根据使用的模板引擎自动识别
//        // templateConfig.setEntity("templates/entity2.java");
//        // templateConfig.setService();
//        // templateConfig.setController();
//
//        templateConfig.setXml(null);
//        mpg.setTemplate(templateConfig);
//
//        // 策略配置
//        StrategyConfig strategy = new StrategyConfig();
//        strategy.setNaming(NamingStrategy.underline_to_camel);
//        strategy.setColumnNaming(NamingStrategy.underline_to_camel);
//        //strategy.setSuperEntityClass("你自己的父类实体,没有就不用设置!");
//        strategy.setEntityLombokModel(true);
//        strategy.setRestControllerStyle(true);
//        // 公共父类
//        //strategy.setSuperControllerClass("你自己的父类控制器,没有就不用设置!");
//        // 写于父类中的公共字段
//        strategy.setTablePrefix("tz");
//        strategy.setSuperEntityColumns("id");
//        strategy.setInclude(scanner("表名，多个英文逗号分割").split(","));
//        strategy.setControllerMappingHyphenStyle(true);
//        strategy.setTablePrefix(pc.getModuleName() + "_");
//        mpg.setStrategy(strategy);
//        mpg.setTemplateEngine(new FreemarkerTemplateEngine());
        mpg.execute();
    }
}
