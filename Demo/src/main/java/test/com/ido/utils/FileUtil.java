package test.com.ido.utils;

/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.Writer;
import java.nio.channels.FileChannel;

/*
 * FileUtils copied from org.apache.commons.io.FileUtils
 */
public class FileUtil {
    /**
     * Construct a file from the set of name elements.
     *
     * @param directory
     *            the parent directory
     * @param names
     *            the name elements
     * @return the file
     */
    public static File getFile(File directory, String... names) {
        if (directory == null) {
            throw new NullPointerException(
                    "directorydirectory must not be null");
        }
        if (names == null) {
            throw new NullPointerException("names must not be null");
        }
        File file = directory;
        for (String name : names) {
            file = new File(file, name);
        }
        return file;
    }

    /**
     * Construct a file from the set of name elements.
     *
     * @param names
     *            the name elements
     * @return the file
     */
    public static File getFile(String... names) {
        if (names == null) {
            throw new NullPointerException("names must not be null");
        }
        File file = null;
        for (String name : names) {
            if (file == null) {
                file = new File(name);
            }
            else {
                file = new File(file, name);
            }
        }
        return file;
    }

    /**
     * Opens a {@link FileInputStream} for the specified file, providing better
     * error messages than simply calling <code>new FileInputStream(file)</code>
     * .
     * <p>
     * At the end of the method either the stream will be successfully opened,
     * or an exception will have been thrown.
     * <p>
     * An exception is thrown if the file does not exist. An exception is thrown
     * if the file object exists but is a directory. An exception is thrown if
     * the file exists but cannot be read.
     *
     * @param file
     *            the file to open for input, must not be {@code null}
     * @return a new {@link FileInputStream} for the specified file
     * @throws FileNotFoundException
     *             if the file does not exist
     * @throws IOException
     *             if the file object is a directory
     * @throws IOException
     *             if the file cannot be read
     */
    public static FileInputStream openInputStream(File file) throws IOException {
        if (file.exists()) {
            if (file.isDirectory()) {
                throw new IOException("File '" + file
                        + "' exists but is a directory");
            }
            if (file.canRead() == false) {
                throw new IOException("File '" + file + "' cannot be read");
            }
        }
        else {
            throw new FileNotFoundException("File '" + file
                    + "' does not exist");
        }
        return new FileInputStream(file);
    }

    /**
     * Opens a {@link FileOutputStream} for the specified file, checking and
     * creating the parent directory if it does not exist.
     * <p>
     * At the end of the method either the stream will be successfully opened,
     * or an exception will have been thrown.
     * <p>
     * The parent directory will be created if it does not exist. The file will
     * be created if it does not exist. An exception is thrown if the file
     * object exists but is a directory. An exception is thrown if the file
     * exists but cannot be written to. An exception is thrown if the parent
     * directory cannot be created.
     *
     * @param file
     *            the file to open for output, must not be {@code null}
     * @param append
     *            if {@code true}, then bytes will be added to the
     *            end of the file rather than overwriting
     * @return a new {@link FileOutputStream} for the specified file
     * @throws IOException
     *             if the file object is a directory
     * @throws IOException
     *             if the file cannot be written to
     * @throws IOException
     *             if a parent directory needs creating but that fails
     */
    public static FileOutputStream openOutputStream(File file, boolean append)
            throws IOException {
        if (file.exists()) {
            if (file.isDirectory()) {
                throw new IOException("File '" + file
                        + "' exists but is a directory");
            }
            if (file.canWrite() == false) {
                throw new IOException("File '" + file
                        + "' cannot be written to");
            }
        }
        else {
            File parent = file.getParentFile();
            if (parent != null) {
                if (!parent.mkdirs() && !parent.isDirectory()) {
                    throw new IOException("Directory '" + parent
                            + "' could not be created");
                }
            }
        }
        return new FileOutputStream(file, append);
    }

    public static FileOutputStream openOutputStream(File file)
            throws IOException {
        return openOutputStream(file, false);
    }

    /**
     * Cleans a directory without deleting it.
     *
     * @param directory
     *            directory to clean
     * @throws IOException
     *             in case cleaning is unsuccessful
     */
    public static void cleanDirectory(File directory) throws IOException {
        if (!directory.exists()) {
            String message = directory + " does not exist";
//            throw new IllegalArgumentException(message);
            return;
        }

        if (!directory.isDirectory()) {
            String message = directory + " is not a directory";
//            throw new IllegalArgumentException(message);
            return;
        }

        File[] files = directory.listFiles();
        if (files == null) { // null if security restricted
            throw new IOException("Failed to list contents of " + directory);
        }

        IOException exception = null;
        for (File file : files) {
            try {
                forceDelete(file);
            }
            catch (IOException ioe) {
                exception = ioe;
            }
        }

        if (null != exception) {
            throw exception;
        }
    }

    // -----------------------------------------------------------------------
    /**
     * Deletes a directory recursively.
     *
     * @param directory
     *            directory to delete
     * @throws IOException
     *             in case deletion is unsuccessful
     */
    public static void deleteDirectory(File directory) throws IOException {
        if (!directory.exists()) {
            return;
        }

        cleanDirectory(directory);

        if (!directory.delete()) {
            String message = "Unable to delete directory " + directory + ".";
            throw new IOException(message);
        }
    }

    /**
     * Deletes a file. If file is a directory, delete it and all
     * sub-directories.
     * <p>
     * The difference between File.delete() and this method are:
     * <ul>
     * <li>A directory to be deleted does not have to be empty.</li>
     * <li>You get exceptions when a file or directory cannot be deleted.
     * (java.io.File methods returns a boolean)</li>
     * </ul>
     *
     * @param file
     *            file or directory to delete, must not be {@code null}
     *             if the directory is {@code null}
     * @throws FileNotFoundException
     *             if the file was not found
     * @throws IOException
     *             in case deletion is unsuccessful
     */
    public static void forceDelete(File file) throws IOException {
        if (file.isDirectory()) {
            deleteDirectory(file);
        }
        else {
            boolean filePresent = file.exists();
            if (!file.delete()) {
                if (!filePresent) {
                    throw new FileNotFoundException("File does not exist: "
                            + file);
                }
                String message = "Unable to delete file: " + file;
                throw new IOException(message);
            }
        }
    }

    /**
     * Deletes a file, never throwing an exception. If file is a directory,
     * delete it and all sub-directories.
     * <p>
     * The difference between File.delete() and this method are:
     * <ul>
     * <li>A directory to be deleted does not have to be empty.</li>
     * <li>No exceptions are thrown when a file or directory cannot be deleted.</li>
     * </ul>
     *
     * @param file
     *            file or directory to delete, can be {@code null}
     * @return {@code true} if the file or directory was deleted, otherwise
     *         {@code false}
     *
     */
    public static boolean deleteQuietly(File file) {
        if (file == null) {
            return false;
        }
        try {
            if (file.isDirectory()) {
                cleanDirectory(file);
            }
        }
        catch (Exception ignored) {
        }

        try {
            return file.delete();
        }
        catch (Exception ignored) {
            return false;
        }
    }

    /**
     * Makes a directory, including any necessary but nonexistent parent
     * directories. If a file already exists with specified name but it is
     * not a directory then an IOException is thrown.
     * If the directory cannot be created (or does not already exist)
     * then an IOException is thrown.
     *
     * @param directory
     *            directory to create, must not be {@code null}
     *             if the directory is {@code null}
     * @throws IOException
     *             if the directory cannot be created or the file already exists
     *             but is not a directory
     */
    public static void forceMkdir(File directory) throws IOException {
        if (directory.exists()) {
            if (!directory.isDirectory()) {
                String message = "File " + directory + " exists and is "
                        + "not a directory. Unable to create directory.";
                throw new IOException(message);
            }
        }
        else {
            if (!directory.mkdirs()) {
                // Double-check that some other thread or process hasn't made
                // the directory in the background
                if (!directory.isDirectory()) {
                    String message = "Unable to create directory " + directory;
                    throw new IOException(message);
                }
            }
        }
    }

    /**
     * Returns the size of the specified file or directory. If the provided
     * {@link File} is a regular file, then the file's length is returned.
     * If the argument is a directory, then the size of the directory is
     * calculated recursively. If a directory or subdirectory is security
     * restricted, its size will not be included.
     *
     * @param file
     *            the regular file or directory to return the size
     *            of (must not be {@code null}).
     *
     * @return the length of the file, or recursive size of the directory,
     *         provided (in bytes).
     *
     */
    public static long sizeOf(File file) {

        if (!file.exists()) {
            String message = file + " does not exist";
            throw new IllegalArgumentException(message);
        }

        if (file.isDirectory()) {
            return sizeOfDirectory(file);
        }
        else {
            return file.length();
        }

    }

    /**
     * Counts the size of a directory recursively (sum of the length of all
     * files).
     *
     * @param directory
     *            directory to inspect, must not be {@code null}
     * @return size of directory in bytes, 0 if directory is security
     *         restricted, a negative number when the real total
     */
    public static long sizeOfDirectory(File directory) {
        checkDirectory(directory);

        final File[] files = directory.listFiles();
        if (files == null) { // null if security restricted
            return 0L;
        }
        long size = 0;

        for (final File file : files) {

            size += sizeOf(file);
            if (size < 0) {
                break;

            }

        }

        return size;
    }

    /**
     * Checks that the given {@code File} exists and is a directory.
     *
     * @param directory
     *            The {@code File} to check.
     *             if the given {@code File} does not exist or is not a
     *             directory.
     */
    private static void checkDirectory(File directory) {
        if (!directory.exists()) {
            throw new IllegalArgumentException(directory + " does not exist");
        }
        if (!directory.isDirectory()) {
            throw new IllegalArgumentException(directory
                    + " is not a directory");
        }
    }

    public static String readStringFromFile(String path){
        File file = new File(path);
        if (!file.exists()){
            return null;
        }
        try {
            FileReader fileReader = new FileReader(file);
            BufferedReader bufferedReader = new BufferedReader(fileReader);
            StringBuilder stringBuilder = new StringBuilder();
            String line;
            while ((line = bufferedReader.readLine()) != null){
                stringBuilder.append(line);
            }

            bufferedReader.close();
            return stringBuilder.toString();

        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }
    public static boolean writeStringToFile(String path, String data){
        boolean isSuccess = true;
        File file = new File(path);
        if (!file.exists()){
            try {
                isSuccess = file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
                isSuccess = false;
            }
        }
        if (isSuccess) {
            try {
                FileWriter writer = new FileWriter(path);
                writer.write(data);
                writer.flush();
                writer.close();
            } catch (IOException e) {
                e.printStackTrace();
                isSuccess = false;
            }
        }

        return isSuccess;
    }
    /**
     * 保存对象到文件中
     *
     * @param obj
     */
    public static boolean writeObjectToFile(String path, Object obj) {
        boolean isSuccess = true;
        File file = new File(path);
        if (!file.exists()){
            try {
                isSuccess = file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
                isSuccess = false;
            }
        }
        if (isSuccess) {
            FileOutputStream out = null;
            try {
                out = new FileOutputStream(file);
                ObjectOutputStream objOut = new ObjectOutputStream(out);
                objOut.writeObject(obj);
                objOut.flush();
                objOut.close();
            } catch (IOException e) {
                e.printStackTrace();
                isSuccess = false;
            } finally {
                closeStream(out);
            }
        }

        return isSuccess;
    }

    /**
     * 判断文件是否存在
     *
     * @param filePath
     * @return
     */
    public static boolean fileExists(String filePath) {
        File file = new File(filePath);
        return file.exists();
    }

    /**
     * 保存对象到文件中
     *
     */
    public static Object readObjectFromFile(String path) {
        File file = new File(path);
        Object temp = null;
        FileInputStream in=null;
        try {
            in = new FileInputStream(file);
            ObjectInputStream objIn = new ObjectInputStream(in);
            temp = objIn.readObject();
            objIn.close();
        }
        catch (Exception e) {
            e.printStackTrace();
        }finally {
            closeStream(in);
        }
        return temp;
    }

    /**
     * 删除文件
     *
     * @param path
     */
    public static void deleteFile(String path) {
        File file = new File(path);
        if (file.exists()) {
            boolean result = file.delete();
        }
    }
    public static void closeStream(Object stream){
        if(stream==null){return;}
        try {
            if(stream instanceof Reader){
                ((Reader)stream).close();
            }else if(stream instanceof Writer){
                ((Writer)stream).close();
            }else if(stream instanceof InputStream){
                ((InputStream)stream).close();
            }else if(stream instanceof OutputStream){
                ((OutputStream)stream).close();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void fileCopy(File in, File out) {
        FileChannel inChannel = null;
        FileChannel outChannel = null;
        try {
            inChannel = new FileInputStream(in).getChannel();
            outChannel = new FileOutputStream(out).getChannel();
            int maxCount = (64 * 1024 * 1024) - (32 * 1024);
            long size = inChannel.size();
            long position = 0;
            while (position < size) {
                position += inChannel.transferTo(position, maxCount, outChannel);
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (inChannel != null) {
                try {
                    inChannel.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (outChannel != null) {

                try {
                    outChannel.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }

        }

    }

    private static double SIZE_KB = 1024;
    private static double SIZE_MB = SIZE_KB * 1024;
    private static double SIZE_GB = SIZE_MB * 1024;
    private static double SIZE_TB = SIZE_GB * 1024;

}

