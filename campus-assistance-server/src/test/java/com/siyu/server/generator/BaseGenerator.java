package com.siyu.server.generator;

import java.io.*;
import java.nio.charset.StandardCharsets;

public abstract class BaseGenerator {
    private static final String TEMPLATE_DIR = "/code-template/";

    private static final String MODULE_NAME = "campus-assistance-server";

    //项目目录 example: D:/Code/Project/campus-assistance-backend
    protected String basePath;
    
    //类基础创建路径 example: D:/Code/Project/campus-assistance-backend/campus-assistance-server/src/main/java/
    protected String moduleBasePath;
    
    //模块包名 example: com.siyu.server
    protected String basePackage;

    //template目录 example: D:/Code/Project/campus-assistance-backend/code-template/
    protected String templatePath;

    //实体类名 example: SysUser
    protected String entityName;

    //实体类包名 example: com.siyu.server.entity.SysUser
    protected String entityClassName;

    //实体类路径 example: D:/Code/Project/campus-assistance-backend/campus-assistance-server/src/main/java/com/siyu/server/entity/SysUser.java
    protected String entityPath;

    public void execute(String path) throws IOException {
        try {
            entityPath = path.replace("\\", "/");
            if(!entityPath.endsWith(".java")) {
                throw new IllegalArgumentException("目标文件必须是java文件");
            }
            init();
            generate();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    protected void init() throws IOException {
        basePath = new File("").getCanonicalPath().replace("\\", "/");
        templatePath = basePath + TEMPLATE_DIR;
        moduleBasePath = basePath + "/" + MODULE_NAME + "/src/main/java/";
        if (!entityPath.startsWith(moduleBasePath) || !entityPath.substring(moduleBasePath.length()).contains("entity")) {
            throw new IllegalArgumentException("目标类必须在[ " + moduleBasePath + "../entity/.. ]目录下");
        }
        //partEntityPath example: com/siyu/server/entity/SysUser.java
        String partEntityPath = entityPath.substring(moduleBasePath.length());
        entityClassName = partEntityPath.split("\\.")[0].replaceAll("/", ".");
        entityName = entityClassName.substring(entityClassName.lastIndexOf(".") + 1);
        basePackage = entityClassName.substring(0,  entityClassName.indexOf(".entity"));
    }

    protected String getContent(String name) throws IOException {
        StringBuilder builder = new StringBuilder();
        File file = new File(templatePath + name);
        BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8));
        String line;
        while ((line = reader.readLine()) != null) {
            builder.append(line).append("\n");
        }
        reader.close();
        return builder.toString();
    }

    protected static String getFieldName(String line) {
        line = line.substring(0, line.indexOf(";")).trim();
        String[] words = line.split(" ");
        return words[words.length-1];
    }

    protected boolean check(String line) {
        line = line.trim();
        if(line.isEmpty()) {
            return false;
        }
        for (String s : line.split(" ")) {
            if (s.equals("static") || s.equals("final")) {
                return false;
            }
        }
        return true;
    }
    protected String changeFirstCharacterLower(String str) {
        return Character.toLowerCase(str.charAt(0)) + str.substring(1);
    }

    protected String changeFirstCharacterUpper(String str) {
        return Character.toUpperCase(str.charAt(0)) + str.substring(1);
    }

    protected abstract void generate() throws IOException;

}
