package com.duoshine.douyin.meishe.sdkdemo.utils;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;
import android.text.TextUtils;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * Created by sensetime on 16-11-16.
 */

public class FileUtils {

    public static boolean copyFileIfNeed(Context context, String fileName, String className) {
        String path = getFilePath(context, className + File.separator + fileName);
        if (path != null) {
            File file = new File(path);
            if (!file.exists()) {
                /*
                 * 模型文件不存在
                 * The model file does not exist
                 * */
                try {
                    String folderpath = null;
                    File dataDir = context.getApplicationContext().getExternalFilesDir(null);
                    if (dataDir != null) {
                        folderpath = dataDir.getAbsolutePath() + File.separator + className;
                    }
                    File folder = new File(folderpath);

                    if (!folder.exists()) {
                        folder.mkdirs();
                    }

                    if (file.exists())
                        file.delete();

                    file.createNewFile();

                    InputStream in = context.getAssets().open(className + File.separator + fileName);
                    if (in == null) {
                        return false;
                    }
                    OutputStream out = new FileOutputStream(file);
                    byte[] buffer = new byte[4096];
                    int n;
                    while ((n = in.read(buffer)) > 0) {
                        out.write(buffer, 0, n);
                    }
                    in.close();
                    out.close();
                } catch (IOException e) {
                    file.delete();
                    return false;
                }
            }
        }
        return true;
    }

    public static String getFilePath(Context context, String fileName) {
        String path = null;
        File dataDir = context.getApplicationContext().getExternalFilesDir(null);
        if (dataDir != null) {
            path = dataDir.getAbsolutePath() + File.separator + fileName;
        }
        return path;
    }

    public static List<String> copyStickerZipFiles(Context context, String className) {
        String files[] = null;
        ArrayList<String> modelFiles = new ArrayList<String>();

        try {
            files = context.getAssets().list(className);
        } catch (IOException e) {
            e.printStackTrace();
        }

        String folderpath = null;
        File dataDir = context.getExternalFilesDir(null);
        if (dataDir != null) {
            folderpath = dataDir.getAbsolutePath() + File.separator + className;

            File folder = new File(folderpath);

            if (!folder.exists()) {
                folder.mkdir();
            }
        }
        for (int i = 0; i < files.length; i++) {
            String str = files[i];
            if (str.indexOf(".zip") != -1) {
                copyFileIfNeed(context, str, className);
            }
        }

        File file = new File(folderpath);
        File[] subFile = file.listFiles();

        for (int i = 0; i < subFile.length; i++) {
            /*
             * 判断是否为文件夹
             * Determine if it is a folder
             * */
            if (!subFile[i].isDirectory()) {
                String filename = subFile[i].getAbsolutePath();
                String path = subFile[i].getPath();
                /*
                 * 判断是否为model结尾
                 * Determine if it is the end of the model
                 * */
                if (filename.trim().toLowerCase().endsWith(".zip")) {
                    modelFiles.add(filename);
                }
            }
        }

        return modelFiles;
    }

    public static List<String> getStickerZipFilesFromSd(Context context, String className) {
        ArrayList<String> modelFiles = new ArrayList<String>();

        String folderpath = null;
        File dataDir = context.getExternalFilesDir(null);
        if (dataDir != null) {
            folderpath = dataDir.getAbsolutePath() + File.separator + className;

            File folder = new File(folderpath);

            if (!folder.exists()) {
                folder.mkdir();
            }
        }

        File file = new File(folderpath);
        File[] subFile = file.listFiles();

        for (int i = 0; i < subFile.length; i++) {
            /*
             * 判断是否为文件夹
             * Determine if it is a folder
             * */
            if (!subFile[i].isDirectory()) {
                String filename = subFile[i].getAbsolutePath();
                String path = subFile[i].getPath();
                /*
                 * 判断是否为model结尾
                 * Determine if it is the end of the model
                 * */
                if (filename.trim().toLowerCase().endsWith(".zip")) {
                    modelFiles.add(filename);
                }
            }
        }

        return modelFiles;
    }

    public static String getStickerZipFileFromSd(Context context, String pathName) {
        ArrayList<String> modelFiles = new ArrayList<String>();

        String stickerpath = null;
        File dataDir = context.getExternalFilesDir(null);
        if (dataDir != null) {
            stickerpath = dataDir.getAbsolutePath() + File.separator + pathName;

            File zipFile = new File(stickerpath);

            if (!zipFile.exists()) {
                return null;
            }
        }
        return stickerpath;
    }

    public static Map<String, Bitmap> copyStickerIconFiles(Context context, String className) {
        String files[] = null;
        TreeMap<String, Bitmap> iconFiles = new TreeMap<String, Bitmap>();

        try {
            files = context.getAssets().list(className);
        } catch (IOException e) {
            e.printStackTrace();
        }

        String folderpath = null;
        File dataDir = context.getExternalFilesDir(null);
        if (dataDir != null) {
            folderpath = dataDir.getAbsolutePath() + File.separator + className;

            File folder = new File(folderpath);

            if (!folder.exists()) {
                folder.mkdir();
            }
        }
        for (int i = 0; i < files.length; i++) {
            String str = files[i];
            if (str.indexOf(".png") != -1) {
                copyFileIfNeed(context, str, className);
            }
        }

        File file = new File(folderpath);
        File[] subFile = file.listFiles();

        for (int i = 0; i < subFile.length; i++) {
            /*
             * 判断是否为文件夹
             * Determine if it is a folder
             * */
            if (!subFile[i].isDirectory()) {
                String filename = subFile[i].getAbsolutePath();
                String path = subFile[i].getPath();
                /*
                 * 判断是否为png结尾
                 * Determine if it is the end of png
                 * */
                if (filename.trim().toLowerCase().endsWith(".png") && filename.indexOf("mode_") == -1) {
                    String name = subFile[i].getName();
                    iconFiles.put(getFileNameNoEx(name), BitmapFactory.decodeFile(filename));
                }
            }
        }

        return iconFiles;
    }

    public static Map<String, Bitmap> getStickerIconFilesFromSd(Context context, String className) {
        TreeMap<String, Bitmap> iconFiles = new TreeMap<String, Bitmap>();

        String folderpath = null;
        File dataDir = context.getExternalFilesDir(null);
        if (dataDir != null) {
            folderpath = dataDir.getAbsolutePath() + File.separator + className;

            File folder = new File(folderpath);

            if (!folder.exists()) {
                folder.mkdir();
            }
        }

        File file = new File(folderpath);
        File[] subFile = file.listFiles();

        for (int i = 0; i < subFile.length; i++) {
            /*
             * 判断是否为文件夹
             * Determine if it is a folder
             * */
            if (!subFile[i].isDirectory()) {
                String filename = subFile[i].getAbsolutePath();
                String path = subFile[i].getPath();
                /*
                 * 判断是否为png结尾
                 * Determine if it is the end of png
                 * */
                if (filename.trim().toLowerCase().endsWith(".png") && filename.indexOf("mode_") == -1) {
                    String name = subFile[i].getName();
                    iconFiles.put(getFileNameNoEx(name), BitmapFactory.decodeFile(filename));
                }
            }
        }

        return iconFiles;
    }

    public static List<String> getStickerNames(Context context, String className) {
        ArrayList<String> modelNames = new ArrayList<String>();
        String folderpath = null;
        File dataDir = context.getExternalFilesDir(null);
        if (dataDir != null) {
            folderpath = dataDir.getAbsolutePath() + File.separator + className;
            File folder = new File(folderpath);

            if (!folder.exists()) {
                folder.mkdir();
            }
        }

        File file = new File(folderpath);
        File[] subFile = file.listFiles();

        for (int i = 0; i < subFile.length; i++) {
            /*
             * 判断是否为文件夹
             * Determine if it is a folder
             * */
            if (!subFile[i].isDirectory()) {
                String filename = subFile[i].getAbsolutePath();
                /*
                 * 判断是否为model结尾
                 * Determine if it is the end of the model
                 * */
                if (filename.trim().toLowerCase().endsWith(".zip") && filename.indexOf("filter") == -1) {
                    String name = subFile[i].getName();
                    modelNames.add(getFileNameNoEx(name));
                }
            }
        }

        return modelNames;
    }

    public static String getFileNameNoEx(String filename) {
        if ((filename != null) && (filename.length() > 0)) {
            int dot = filename.lastIndexOf('.');
            if ((dot > -1) && (dot < (filename.length()))) {
                return filename.substring(0, dot);
            }
        }
        return filename;
    }

    public static ArrayList<String> GetAllFilesFromSDWithExtension(String Path, String Extension, boolean IsIterative)  //搜索目录，扩展名，是否进入子文件夹
    {
        ArrayList<String> path_list = new ArrayList<String>();

        File[] files = new File(Path).listFiles();
        if (files == null)
            return null;

        for (int i = 0; i < files.length; i++) {
            File f = files[i];
            if (f.isFile()) {
                if (f.getPath().substring(f.getPath().length() - Extension.length()).equals(Extension))  //判断扩展名
                    path_list.add(f.getPath());

                if (!IsIterative)
                    break;
            } else if (f.isDirectory() && f.getPath().indexOf("/.") == -1)  //忽略点文件（隐藏文件/文件夹）
            {
                ArrayList<String> sub_path_list = GetAllFilesFromSDWithExtension(f.getPath(), Extension, IsIterative);

                path_list.addAll(sub_path_list);
            }
        }

        return path_list;
    }


    /**
     * content路径转绝对路径
     *
     * @param contentResolver
     * @param path
     * @return
     */
    public static String contentPath2AbsPath(ContentResolver contentResolver, String path) {
        Uri uri = Uri.parse(path);
        return getFilePathFromContentUri(uri, contentResolver);
    }


    public static String getFilePathFromContentUri(Uri selectedVideoUri,
                                                   ContentResolver contentResolver) {
        String filePath;
        String[] filePathColumn = {MediaStore.MediaColumns.DATA};

        Cursor cursor = contentResolver.query(selectedVideoUri, filePathColumn, null, null, null);
        cursor.moveToFirst();

        int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
        filePath = cursor.getString(columnIndex);
        cursor.close();
        return filePath;

    }

    public static boolean isContent(String url) {
        if (TextUtils.isEmpty(url)) {
            return false;
        }
        return url.startsWith("content://");
    }


    public static void JPG2PNG(String path) {
        Bitmap bitmap = openImage(path);
        savePNG_After(bitmap, path);
    }

    /**
     * 将本地图片转成Bitmap
     *
     * @param path 已有图片的路径
     * @return
     */
    private static Bitmap openImage(String path) {
        Bitmap bitmap = null;
        try {
            //1.无压缩
            BufferedInputStream bis = new BufferedInputStream(new FileInputStream(path));
            bitmap = BitmapFactory.decodeStream(bis);

            //2.压缩
//            BitmapFactory.Options options = new BitmapFactory.Options();
//
//            options.inPreferredConfig = Bitmap.Config.RGB_565;
//
//            options.inSampleSize = 2;
//
//            bitmap = BitmapFactory.decodeFile(path,options);

            bis.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return bitmap;

    }

    private static void savePNG_After(Bitmap bitmap, String name) {
        name = name.replace("jpg", "png");
        File file = new File(name);
        try {
            FileOutputStream out = new FileOutputStream(file);
            //质量压缩，不改变bitmap大小，只牵涉到磁盘文件大小。100表示不压缩
            if (bitmap.compress(Bitmap.CompressFormat.PNG, 100, out)) {
                out.flush();
                out.close();
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
