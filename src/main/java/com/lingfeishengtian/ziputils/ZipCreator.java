package com.lingfeishengtian.ziputils;

import java.io.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class ZipCreator {
    private static final int BUFFER_SIZE = 4096;

    public static void unzipTo(String fileLoc) throws IOException {
        File destination = new File(fileLoc);
        if (destination.isDirectory()) {
            unzip("resources/pc2-9.6.0_server.zip", destination.getAbsolutePath());
        } else {
            System.out.println("Path provided is not a directory and cannot be unzipped to.");
        }
    }

    private static void unzip(String zipFilePath, String destDirectory) throws IOException {
        File destDir = new File(destDirectory);
        if (!destDir.exists()) {
            destDir.mkdir();
        }
        ZipInputStream zipIn = new ZipInputStream(new FileInputStream(zipFilePath));
        ZipEntry entry = zipIn.getNextEntry();
        while (entry != null) {
            String filePath = destDirectory + File.separator + entry.getName();
            if (!filePath.contains("__MACOSX")) {
                if (!entry.isDirectory()) {
                    File filePathFile = new File(filePath);
                    File parent = new File(filePathFile.getParent());
                    parent.mkdirs();
                    extractFile(zipIn, filePath);
                    if (filePathFile.getName().toLowerCase().startsWith("pc2") && !filePathFile.getName().contains(".")) {
                        filePathFile.setExecutable(true);
                    }
                } else {
                    File dir = new File(filePath);
                    dir.mkdirs();
                }
            }
            zipIn.closeEntry();
            entry = zipIn.getNextEntry();
        }
        zipIn.close();
    }

    private static void extractFile(ZipInputStream zipIn, String filePath) throws IOException {
        BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(filePath));
        byte[] bytesIn = new byte[BUFFER_SIZE];
        int read = 0;
        while ((read = zipIn.read(bytesIn)) != -1) {
            bos.write(bytesIn, 0, read);
        }
        bos.close();
    }
}