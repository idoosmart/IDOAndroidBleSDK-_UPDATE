package test.com.ido.utils;

import android.text.TextUtils;
import android.util.Log;


import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.zip.CRC32;
import java.util.zip.CheckedOutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

/**
 * 压缩工具类
 */

public class ZipUtils {

    private static final int BUFF_SIZE = 4 * 1024;
    private Zip zip;

    /**
     * 递归压缩文件夹
     *
     * @param srcRootDir 压缩文件夹根目录的子路径
     * @param file       当前递归压缩的文件或目录对象
     * @param zos        压缩文件存储对象
     * @throws Exception
     */
    private static void zip(String srcRootDir, File file, ZipOutputStream zos) throws Exception {
        if (file == null) {
            return;
        }

        //如果是文件，则直接压缩该文件
        if (file.isFile()) {
            int count, bufferLen = 1024;
            byte[] data = new byte[bufferLen];

            //获取文件相对于压缩文件夹根目录的子路径
            String subPath = file.getAbsolutePath();
            int index = subPath.indexOf(srcRootDir);
            if (index != -1) {
                subPath = subPath.substring(srcRootDir.length());
            }
            ZipEntry entry = new ZipEntry(subPath);
            zos.putNextEntry(entry);
            BufferedInputStream bis = new BufferedInputStream(new FileInputStream(file));
            while ((count = bis.read(data, 0, bufferLen)) != -1) {
                zos.write(data, 0, count);
            }
            bis.close();
            zos.closeEntry();
        }
        //如果是目录，则压缩整个目录
        else {
            //压缩目录中的文件或子目录
            File[] childFileList = file.listFiles();
            for (int n = 0; n < childFileList.length; n++) {
                childFileList[n].getAbsolutePath().indexOf(file.getAbsolutePath());
                zip(srcRootDir, childFileList[n], zos);
            }
        }
    }

    /**
     * 对文件或文件目录进行压缩
     *
     * @param srcPath     要压缩的源文件路径。如果压缩一个文件，则为该文件的全路径；如果压缩一个目录，则为该目录的顶层目录路径
     * @param zipPath     压缩文件保存的路径。注意：zipPath不能是srcPath路径下的子文件夹
     * @param zipFileName 压缩文件名
     * @throws Exception
     */
    public static void zip(String srcPath, String zipPath, String zipFileName) throws Exception {
        if (TextUtils.isEmpty(srcPath) || TextUtils.isEmpty(zipPath) || TextUtils.isEmpty(zipFileName)) {
            throw new Exception("para is empty");
        }
        CheckedOutputStream cos = null;
        ZipOutputStream zos = null;
        try {
            File srcFile = new File(srcPath);

            //判断压缩文件保存的路径是否为源文件路径的子文件夹，如果是，则抛出异常（防止无限递归压缩的发生）
            if (srcFile.isDirectory() && zipPath.indexOf(srcPath) != -1) {
                throw new Exception("zipPath must not be the child directory of srcPath.");
            }

            //判断压缩文件保存的路径是否存在，如果不存在，则创建目录
            File zipDir = new File(zipPath);
            if (!zipDir.exists() || !zipDir.isDirectory()) {
                zipDir.mkdirs();
            }

            //创建压缩文件保存的文件对象
            String zipFilePath = zipPath + File.separator + zipFileName;
            File zipFile = new File(zipFilePath);
            if (zipFile.exists()) {
                //检测文件是否允许删除，如果不允许删除，将会抛出SecurityException
                SecurityManager securityManager = new SecurityManager();
                securityManager.checkDelete(zipFilePath);
                //删除已存在的目标文件
                zipFile.delete();
            }

            cos = new CheckedOutputStream(new FileOutputStream(zipFile), new CRC32());
            zos = new ZipOutputStream(cos);

            //如果只是压缩一个文件，则需要截取该文件的父目录
            String srcRootDir = srcPath;
            if (srcFile.isFile()) {
                int index = srcPath.lastIndexOf(File.separator);
                if (index != -1) {
                    srcRootDir = srcPath.substring(0, index);
                }
            }
            //调用递归压缩方法进行目录或文件压缩
            zip(srcRootDir, srcFile, zos);
            zos.flush();
        } catch (Exception e) {
            throw e;
        } finally {
            try {
                if (zos != null) {
                    zos.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 解压
     *
     * @param zipFilePath 压缩文件
     * @param unzipPath   解压路径
     * @return return true if success
     */
    public static boolean unzip(String zipFilePath, String unzipPath) {
        try {
            ZipFile zipFile = new ZipFile(zipFilePath);
            Enumeration emu = zipFile.entries();
            while (emu.hasMoreElements()) {
                ZipEntry entry = (ZipEntry) emu.nextElement();
                String zipFileName = entry.getName().replace("../", "");
                if (entry.isDirectory()) {
                    new File(unzipPath + "/" + zipFileName).mkdirs();
                    continue;
                }

                BufferedInputStream bis = new BufferedInputStream(zipFile.getInputStream(entry));
                File file = new File(unzipPath + "/" + zipFileName);
                File parent = file.getParentFile();
                if (parent != null && (!parent.exists())) {
                    parent.mkdirs();
                }

                FileOutputStream fos = new FileOutputStream(file);
                BufferedOutputStream bos = new BufferedOutputStream(fos);

                int count;
                byte[] data = new byte[BUFF_SIZE];
                while ((count = bis.read(data, 0, BUFF_SIZE)) != -1) {
                    bos.write(data, 0, count);
                }
                bos.flush();
                bos.close();
                bis.close();
            }

            zipFile.close();

            return true;
        } catch (Exception e) {
            Log.e("ZipUtil", "unzip error! zip file:" + zipFilePath + " unzip to path:" + unzipPath);
            e.printStackTrace();
            return false;
        }
    }

    public static boolean unzip(InputStream inputStream, String outPathString) {
        try (ZipInputStream inZip = new ZipInputStream(inputStream)) {
            ZipEntry zipEntry;
            String szName = "";
            while ((zipEntry = inZip.getNextEntry()) != null) {
                szName = zipEntry.getName().replace("../", "");
                if (zipEntry.isDirectory()) {
                    //获取部件的文件夹名
                    szName = szName.substring(0, szName.length() - 1);
                    File folder = new File(outPathString + File.separator + szName);
                    folder.mkdirs();
                } else {
                    File file = new File(outPathString + File.separator + szName);
                    if (!file.exists()) {
                        file.getParentFile().mkdirs();
                        file.createNewFile();
                    }
                    // 获取文件的输出流
                    FileOutputStream out = new FileOutputStream(file);
                    int len;
                    byte[] buffer = new byte[1024];
                    // 读取（字节）字节到缓冲区
                    while ((len = inZip.read(buffer)) != -1) {
                        // 从缓冲区（0）位置写入（字节）字节
                        out.write(buffer, 0, len);
                        out.flush();
                    }
                    //关闭流
                    IOUtil.close(out);
                }
            }
            //关闭流
            IOUtil.close(inputStream);
            IOUtil.close(inZip);
            return true;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * @throws Exception
     */
    public void ZipFolder(String srcFileString, String zipFileString) throws Exception {
        Log.v("XZip", "ZipFolder(String, String)");

        ZipOutputStream outZip = new ZipOutputStream(new FileOutputStream(zipFileString));

        File file = new File(srcFileString);

        ZipFiles(file.getParent() + File.separator, file.getName(), outZip);

        outZip.finish();
        outZip.close();
        this.zip.onFinish();
    }// run_go of func


    /**
     * @param folderString
     * @param fileString
     * @param zipOutputSteam
     * @throws Exception
     */
    private static void ZipFiles(String folderString, String fileString, ZipOutputStream zipOutputSteam) throws Exception {
        Log.v("XZip", "ZipFiles(String, String, ZipOutputStream)");

        if (zipOutputSteam == null)
            return;

        File file = new File(folderString + fileString);

        if (file.isFile()) {

            ZipEntry zipEntry = new ZipEntry(fileString);
            FileInputStream inputStream = new FileInputStream(file);
            zipOutputSteam.putNextEntry(zipEntry);

            int len;
            byte[] buffer = new byte[4096];
            while ((len = inputStream.read(buffer)) != -1) {
                zipOutputSteam.write(buffer, 0, len);
            }

            zipOutputSteam.closeEntry();
        } else {

            String[] fileList = file.list();

            if (fileList.length <= 0) {
                ZipEntry zipEntry = new ZipEntry(fileString + File.separator);
                zipOutputSteam.putNextEntry(zipEntry);
                zipOutputSteam.closeEntry();
            }

            for (int i = 0; i < fileList.length; i++) {
                ZipFiles(folderString, fileString + File.separator + fileList[i], zipOutputSteam);
            }// run_go of for

        }// run_go of if

    }// run_go of func

    public void OnZip(Zip zip) {
        this.zip = zip;
    }


    public interface Zip {
        void onFinish();
    }
}
