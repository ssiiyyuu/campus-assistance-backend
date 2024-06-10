package com.siyu.server;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class FindViewByIdGenerator {

    private final static String initPrefix = "view.";

    private final static String path = "D:\\Code\\Android\\campus-assistance-frontend\\app\\src\\main\\res\\layout\\fragment_notification_load.xml";

    public static void main(String[] args) throws IOException {
        List<String> ids = new ArrayList<>();
        List<String> types = new ArrayList<>();
        List<String> fields = new ArrayList<>();
        File file = new File(path);
        BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8));
        String start = "android:id=\"@+id/";
        String currentType = "";
        String line;
        while ((line = reader.readLine()) != null) {
            if(line.contains("<")) {
                currentType = line.substring(line.indexOf("<") + 1);
                continue;
            }
            if(line.contains(start)) {
                String id = line.substring(line.indexOf(start) + start.length(), line.lastIndexOf("\""));
                ids.add(id);
                String[] split = id.split("_");
                if(split.length == 1) {
                    fields.add(split[0]);
                    types.add(currentType);
                } else {
                    StringBuilder stringBuilder = new StringBuilder();
                    for(int i = 1; i < split.length; i++) {
                        stringBuilder.append(i == 1 ? split[i] : changeFirstCharacterUpper(split[i]));
                    }
                    stringBuilder.append(changeFirstCharacterUpper(split[0]));
                    fields.add(stringBuilder.toString());
                    types.add(currentType);
                }
                continue;
            }
        }
        if(types.size() != fields.size()) {
            System.out.println("generate error");
            return;
        }

        String importPrefix = "import android.widget.";
        StringBuilder importBuilder = new StringBuilder();
        StringBuilder filedBuilder = new StringBuilder();
        StringBuilder initBuilder = new StringBuilder();
        StringBuilder viewHolderSetterBuilder = new StringBuilder();
        StringBuilder viewHolderGetterBuilder = new StringBuilder();

        for(int i = 0; i < types.size(); i++) {
            filedBuilder.append("private ").append(types.get(i)).append(" ").append(fields.get(i)).append(";\n");
            initBuilder.append(fields.get(i)).append(" = ").append(initPrefix).append("findViewById(R.id.").append(ids.get(i)).append(");\n");
            viewHolderSetterBuilder.append("viewHolder.set").append(changeFirstCharacterUpper(fields.get(i))).append("(convertView.findViewById(R.id.").append(ids.get(i)).append("));\n");
            viewHolderGetterBuilder.append("viewHolder.get").append(changeFirstCharacterUpper(fields.get(i))).append("().setText(null);\n");
        }
        types.stream().distinct().forEach(type -> {
            importBuilder.append(importPrefix).append(type).append(";\n");
        });
        System.out.println(importBuilder);
        System.out.println(filedBuilder);
        System.out.println(initBuilder);
        System.out.println(viewHolderSetterBuilder);
        System.out.println(viewHolderGetterBuilder);
        reader.close();
    }


    private static String changeFirstCharacterUpper(String str) {
        return Character.toUpperCase(str.charAt(0)) + str.substring(1);
    }
    private static String changeFirstCharacterLower(String str) {
        return Character.toLowerCase(str.charAt(0)) + str.substring(1);
    }
}
