package com.siyu.server;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.generator.FastAutoGenerator;
import com.baomidou.mybatisplus.generator.config.OutputFile;
import com.baomidou.mybatisplus.generator.config.TemplateType;
import com.baomidou.mybatisplus.generator.config.rules.DateType;
import com.baomidou.mybatisplus.generator.config.rules.NamingStrategy;
import org.junit.jupiter.api.Test;

import java.util.Collections;

public class CodeGenerator {

    private final String tableName = "notification";
    private final String parent = "com.siyu";
    private final String moduleName = "server";
    private final String host = "localhost";
    private final String port = "3306";
    private final String database = "campus";

    private final String username = "siyu";
    private final String password = "123456";
    private final String url = "jdbc:mysql://" + host + ":" + port + "/" + database + "?serverTimezone=UTC&useUnicode=true&characterEncoding=utf-8";


    @Test
    public void mybatisGenerator() {
        FastAutoGenerator.create(url, username, password)

                .globalConfig(builder -> {
                    builder.author("MybatisPlusGenerator")
                            .commentDate("yyyy-MM-dd hh:mm:ss")
                            .outputDir(System.getProperty("user.dir") + "/src/main/java")
                            .enableSwagger()
                            .dateType(DateType.TIME_PACK)
                            .disableOpenDir()
                    ;
                })
                .packageConfig(builder -> {
                    builder.parent(parent)
                            .moduleName(moduleName)
                            .service("service")
                            .controller("controller")
                            .mapper("mapper")
                            .entity("entity")
                            .pathInfo(Collections.singletonMap(OutputFile.xml, System.getProperty("user.dir") + "\\src\\main\\resources\\mapper")); // 设置mapperXml生成路径;
                })
                // 策略配置
                .strategyConfig(builder -> {
                    builder.addInclude(tableName) //表名
//                            .addTablePrefix("sys_")
                            // Entity 策略配置
                            .entityBuilder()
                            .superClass("com.siyu.common.domain.BaseEntity")
                            .idType(IdType.ASSIGN_ID) //主键分配
                            .enableLombok()
                            .enableFileOverride()
                            .naming(NamingStrategy.underline_to_camel)  //数据库表映射到实体的命名策略：下划线转驼峰命
                            .columnNaming(NamingStrategy.underline_to_camel)    //数据库表字段映射到实体的命名策略：下划线转驼峰命

                            // Mapper 策略配置
                            .mapperBuilder()
                            .enableBaseResultMap() //通用ResultMap
                            .superClass(BaseMapper.class)
                            .enableFileOverride()
                            .formatMapperFileName("%sMapper")
                            .formatXmlFileName("%sMapper")

                            // Service 策略配置
                            .serviceBuilder()
                            .enableFileOverride()
                            .formatServiceFileName("%sService")
                            .formatServiceImplFileName("%sServiceImpl")

                            // Controller 策略配置
                            .controllerBuilder()
                            .enableFileOverride()
                            .formatFileName("%sController")
                    ;
                })
                .templateConfig(config -> {
                    //不生成controller
                    config.disable(TemplateType.CONTROLLER);
                })
                .execute();

    }
}
