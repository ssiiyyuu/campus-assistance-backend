package com.siyu.server.generator;

import java.io.*;
import java.nio.charset.StandardCharsets;

public class EntityControllerGenerator extends BaseGenerator{
    @Override
    protected void generate() throws IOException {
        String packageName = basePackage + ".controller";
        String className = packageName + "." + entityName + "Controller";
        String filePath = moduleBasePath + className.replaceAll("\\.", "/") + ".java";
        File file = new File(filePath);
        if(file.exists()) {
            System.out.println(className + "已存在");
            return;
        }
        if(file.getParentFile().mkdirs()) {
            System.out.println("正在创建: " + file.getParentFile());
        };
        BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(entityPath), StandardCharsets.UTF_8));
        StringBuilder conditionBuilder = new StringBuilder();
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
                continue;
            }
            //类结束或遇到方法: end
            if (start && (line.startsWith("}") || (line.contains("(") && line.contains(")")))) {
                break;
            }
            //非static与final的字段
            if (start && check(line)) {
                String fieldName = getFieldName(line);
                String fieldNameUpper = changeFirstCharacterUpper(fieldName);
                String format = String.format(".eq(StringUtils.hasText(condition.get%s()), %s::get%s, condition.get%s())"
                        , fieldNameUpper, entityName, fieldNameUpper, fieldNameUpper);
                conditionBuilder.append("\n\t\t\t").append(format);
                continue;
            }
        }
        conditionBuilder.append(";");
        reader.close();
        String content = getContent("EntityController.tmp");
        content = content.replaceAll("\\$\\{entityName}", changeFirstCharacterLower(entityName));
        content = content.replaceAll("\\$\\{EntityName}", entityName);
        content = content.replaceAll("\\$\\{basePackage}", basePackage);
        content = content.replaceAll("\\$\\{EntityCondition}", conditionBuilder.toString());

        FileOutputStream out = new FileOutputStream(file);
        out.write(content.getBytes(StandardCharsets.UTF_8));
        out.flush();
        out.close();

        System.out.println(className + "已创建");
    }
}
