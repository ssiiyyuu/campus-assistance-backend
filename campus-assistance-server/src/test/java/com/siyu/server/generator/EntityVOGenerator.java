package com.siyu.server.generator;

import java.io.*;
import java.nio.charset.StandardCharsets;

public class EntityVOGenerator extends BaseGenerator{
    @Override
    protected void generate() throws IOException {
        String packageName = basePackage + ".entity.vo";
        String className = packageName + "." + entityName + "VO";
        String filePath = moduleBasePath + className.replaceAll("\\.", "/") + ".java";
        File file = new File(filePath);
        if(file.exists()) {
            System.out.println(className + "已存在");
            return;
        }
        if(file.getParentFile().mkdirs()) {
            System.out.println("正在创建: " + file.getParentFile());
        };
        String content = getContent("EntityVO.tmp");

        BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(entityPath), StandardCharsets.UTF_8));
        StringBuilder filedBuilder = new StringBuilder();
        boolean start = false;
        String line;
        while ((line = reader.readLine()) != null) {
            //不要更改顺序
            //class ClassName: start
            if (line.contains("class") && line.contains(entityName)) {
                start = true;
                continue;
            }
            //@Swagger注解
            if (start && line.contains("@Api")) {
                filedBuilder.append("\n\t").append(line).append("\n");
                continue;
            }
            //类结束或遇到方法: end
            if (start && (line.startsWith("}") || (line.contains("(") && line.contains(")")))) {
                break;
            }
            //.eq(StringUtils.hasText(condition.getUsername()), SysUser::getUsername, condition.getUsername())
            //非static与final的字段
            if (start && check(line)) {
                filedBuilder.append("\t").append(line).append("\n");
                continue;
            }
        }
        reader.close();
        content = content.replaceAll("\\$\\{EntityName}", entityName);
        content = content.replaceAll("\\$\\{basePackage}", basePackage);
        content = content.replaceAll("\\$\\{VOFields}", filedBuilder.toString());

        FileOutputStream out = new FileOutputStream(file);
        out.write(content.getBytes(StandardCharsets.UTF_8));
        out.flush();
        out.close();

        System.out.println(className + "已创建");
    }
}
