package com.lingfeishengtian.utils;

import java.io.File;

public class Cleaner {
    public static void clean(String binPath) {
        File file = new File(binPath + File.separator + "profiles");
        if (file.exists()) {
            for (File child : file.listFiles()) {
                File db1 = new File(child.getAbsolutePath() + File.separator + "db.1");
                if (db1.exists())
                    for (int i = db1.listFiles().length - 1; i >= 0; i--) {
                        String name = db1.listFiles()[i].getName();
                        if (name.matches(".*\\d.*") && (name.contains("runlist") || name.contains("settings"))) {
                            db1.setWritable(true);
                            db1.listFiles()[i].delete();
                        }
                    }
            }
        } else {
            System.out.println("Cannot clean, profiles do not exist.");
        }
    }
}
