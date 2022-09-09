package com.tinypng.source;

import com.google.gson.Gson;

import java.io.*;

public class ConfigUtils {

    private static final String TINY_PROJECT_PATCH = "project_info";

    public static String PROJECT_PATH = "";

    public static TinyPngConfig loadProjectConfig(String configPath) throws Exception {
        String configString = readFileAsString(configPath);
        return new Gson().fromJson(configString, TinyPngConfig.class);
    }

    public static ProjectConfig getProjectConfig() {
        try {
            String config = readFileAsString(TINY_PROJECT_PATCH);
            return new Gson().fromJson(config, ProjectConfig.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void saveProjectConfig(ProjectConfig config) {
        try {
            String configString = new Gson().toJson(config);
            writerFileAsString(TINY_PROJECT_PATCH, configString, false);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static String loadFileMD5Record(String recordPath) throws Exception {
        return readFileAsString(recordPath);
    }

    public static void appendFileMD5Record(String recordPath, String json) throws Exception {
        writerFileAsString(recordPath, json + ",\n", true);
    }

    private static void writerFileAsString(String path, String data, boolean append) throws Exception {
        File writeName = new File(path);
        if (!writeName.exists()) {
            boolean newFile = writeName.createNewFile();
            if (!newFile) {
                return;
            }
        }
        FileWriter writer = new FileWriter(writeName, append);
        BufferedWriter out = new BufferedWriter(writer);
        out.write(data);
        out.flush();
    }

    private static String readFileAsString(String path) throws Exception {
        File file = new File(path);
        if (!file.exists()) {
            boolean newFile = file.createNewFile();
            if (!newFile) {
                return "";
            }
        }
        FileReader reader = new FileReader(file);
        BufferedReader br = new BufferedReader(reader);
        String line;
        StringBuilder sb = new StringBuilder();
        while ((line = br.readLine()) != null) {
            sb.append(line);
        }
        return sb.toString();
    }
}
