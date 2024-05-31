package com.siyu.server;

import com.siyu.server.generator.EntityControllerGenerator;
import com.siyu.server.generator.EntityVOGenerator;

import java.io.IOException;

public class GeneratorHandler {

    private final static String entity = "D:\\Code\\Project\\campus-assistance-bakcend\\campus-assistance-server\\src\\main\\java\\com\\siyu\\server\\entity\\CampusReportEvent.java";

    public static void main(String[] args) {
        try {
            new EntityVOGenerator().execute(entity);
            new EntityControllerGenerator().execute(entity);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
