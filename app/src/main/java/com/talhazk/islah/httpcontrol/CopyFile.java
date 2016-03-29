package com.talhazk.islah.httpcontrol;

/**
 * Created by Talhazk on 25-Mar-16.
 */

import android.os.Environment;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;

//rg.apache.commons.io.FileUtils.java

public class CopyFile {

    public static void moveFile(File src, File dst) throws IOException {
        FileUtils.copyFile(src, dst);
    }

    public void deleteDir() {

        File dir = new File(Environment.getExternalStorageDirectory()
                + "/.sayittemp");
        if (dir.isDirectory()) {
            String[] children = dir.list();
            for (int i = 0; i < children.length; i++) {
                new File(dir, children[i]).delete();
            }
        }
    }

    public String destinationPath(String fName) {

        File src = new File(fName);
        new CopyFile().deleteDir();
        File des = null;
        try {

            des = createTemporaryFile("sendAudio", ".aac");
            CopyFile.moveFile(src, des);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return des.getAbsolutePath();

    }

    private File createTemporaryFile(String part, String ext) throws Exception {
        File tempDir = Environment.getExternalStorageDirectory();
        tempDir = new File(tempDir.getAbsolutePath() + "/.sayittemp/");
        if (!tempDir.exists()) {
            tempDir.mkdir();
        }
        return File.createTempFile(part, ext, tempDir);
    }

}
