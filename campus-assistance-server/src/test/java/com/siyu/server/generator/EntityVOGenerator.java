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
        StringBuilder filedValidateBuilder = new StringBuilder();
        boolean start = false;
        String line;
        while ((line = reader.readLine()) != null) {
            line = line.trim();
            //不要更改顺序
            //class ClassName: start
            if (line.contains("class") && line.contains(entityName)) {
                start = true;
                continue;
            }
            //@Swagger注解
            if (start && line.contains("@Api")) {
                filedBuilder.append("\t\t").append(line).append("\n");
                filedValidateBuilder.append("\t\t").append(line).append("\n");
                continue;
            }
            //类结束或遇到方法: end
            if (start && (line.startsWith("}") || (line.contains("(") && line.contains(")")))) {
                break;
            }
            //非static与final的字段
            if (start && check(line)) {
                filedBuilder.append("\t\t").append(line).append("\n\n");
                filedValidateBuilder.append("\t\t@NotBlank\n");
                filedValidateBuilder.append("\t\t").append(line).append("\n\n");
                continue;
            }
        }
        System.out.println("content = " + content);
        System.out.println("filedValidateBuilder.toString() = \n" + filedValidateBuilder.toString());
        reader.close();
        content = content.replaceAll("\\$\\{EntityName}", entityName);
        content = content.replaceAll("\\$\\{basePackage}", basePackage);
        content = content.replaceAll("\\t*\\$\\{VOFields}", filedBuilder.toString());
        content = content.replaceAll("\\t*\\$\\{VOValidateFields}", filedValidateBuilder.toString());

        System.out.println("content = " + content);

        FileOutputStream out = new FileOutputStream(file);
        out.write(content.getBytes(StandardCharsets.UTF_8));
        out.flush();
        out.close();

        System.out.println(className + "已创建");
    }
}
