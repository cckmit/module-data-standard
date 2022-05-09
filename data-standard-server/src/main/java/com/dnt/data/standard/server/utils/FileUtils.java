package com.dnt.data.standard.server.utils;

import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.CharEncoding;
import org.apache.commons.lang3.StringUtils;

import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.CipherOutputStream;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;
import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.URLEncoder;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.Base64;
import java.util.List;

/**
 * 文件操作工具类 实现文件的创建、删除、复制、压缩、解压以及目录的创建、删除、复制、压缩解压等功能
 */
@Slf4j
public class FileUtils extends org.apache.commons.io.FileUtils {


    /**
     * 复制单个文件，如果目标文件存在，则不覆盖
     *
     * @param srcFileName  待复制的文件名
     * @param descFileName 目标文件名
     * @return 如果复制成功，则返回true，否则返回false
     */
    public static boolean copyFile(String srcFileName, String descFileName) {
        return FileUtils.copyFileCover(srcFileName, descFileName, false);
    }

    /**
     * 复制单个文件
     *
     * @param srcFileName  待复制的文件名
     * @param descFileName 目标文件名
     * @param coverlay     如果目标文件已存在，是否覆盖
     * @return 如果复制成功，则返回true，否则返回false
     */
    public static boolean copyFileCover(String srcFileName, String descFileName, boolean coverlay) {
        File srcFile = new File(srcFileName);
        // 判断源文件是否存在
        if (!srcFile.exists()) {
            log.debug("复制文件失败，源文件 " + srcFileName + " 不存在!");
            return false;
        }
        // 判断源文件是否是合法的文件
        else if (!srcFile.isFile()) {
            log.debug("复制文件失败，" + srcFileName + " 不是一个文件!");
            return false;
        }
        File descFile = new File(descFileName);
        // 判断目标文件是否存在
        if (descFile.exists()) {
            // 如果目标文件存在，并且允许覆盖
            if (coverlay) {
                log.debug("目标文件已存在，准备删除!");
                if (!FileUtils.delFile(descFileName)) {
                    log.debug("删除目标文件 " + descFileName + " 失败!");
                    return false;
                }
            } else {
                log.debug("复制文件失败，目标文件 " + descFileName + " 已存在!");
                return false;
            }
        } else {
            if (!descFile.getParentFile().exists()) {
                // 如果目标文件所在的目录不存在，则创建目录
                log.debug("目标文件所在的目录不存在，创建目录!");
                // 创建目标文件所在的目录
                if (!descFile.getParentFile().mkdirs()) {
                    log.debug("创建目标文件所在的目录失败!");
                    return false;
                }
            }
        }

        // 准备复制文件
        // 读取的位数
        int readByte;
        InputStream ins = null;
        OutputStream outs = null;
        try {
            // 打开源文件
            ins = new FileInputStream(srcFile);
            // 打开目标文件的输出流
            outs = new FileOutputStream(descFile);
            byte[] buf = new byte[1024];
            // 一次读取1024个字节，当readByte为-1时表示文件已经读取完毕
            while ((readByte = ins.read(buf)) != -1) {
                // 将读取的字节流写入到输出流
                outs.write(buf, 0, readByte);
            }
            log.debug("复制单个文件 " + srcFileName + " 到" + descFileName + "成功!");
            return true;
        } catch (Exception e) {
            log.debug("复制文件失败：" + e.getMessage());
            return false;
        } finally {
            // 关闭输入输出流，首先关闭输出流，然后再关闭输入流
            if (outs != null) {
                try {
                    outs.close();
                } catch (IOException oute) {
                    oute.printStackTrace();
                }
            }
            if (ins != null) {
                try {
                    ins.close();
                } catch (IOException ine) {
                    ine.printStackTrace();
                }
            }
        }
    }

    /**
     * 复制整个目录的内容，如果目标目录存在，则不覆盖
     *
     * @param srcDirName  源目录名
     * @param descDirName 目标目录名
     * @return 如果复制成功返回true，否则返回false
     */
    public static boolean copyDirectory(String srcDirName, String descDirName) {
        return FileUtils.copyDirectoryCover(srcDirName, descDirName, false);
    }

    /**
     * 复制整个目录的内容
     *
     * @param srcDirName  源目录名
     * @param descDirName 目标目录名
     * @param coverlay    如果目标目录存在，是否覆盖
     * @return 如果复制成功返回true，否则返回false
     */
    public static boolean copyDirectoryCover(String srcDirName, String descDirName, boolean coverlay) {
        File srcDir = new File(srcDirName);
        // 判断源目录是否存在
        if (!srcDir.exists()) {
            log.debug("复制目录失败，源目录 " + srcDirName + " 不存在!");
            return false;
        }
        // 判断源目录是否是目录
        else if (!srcDir.isDirectory()) {
            log.debug("复制目录失败，" + srcDirName + " 不是一个目录!");
            return false;
        }
        // 如果目标文件夹名不以文件分隔符结尾，自动添加文件分隔符
        String descDirNames = descDirName;
        if (!descDirNames.endsWith(File.separator)) {
            descDirNames = descDirNames + File.separator;
        }
        File descDir = new File(descDirNames);
        // 如果目标文件夹存在
        if (descDir.exists()) {
            if (coverlay) {
                // 允许覆盖目标目录
                log.debug("目标目录已存在，准备删除!");
                if (!FileUtils.delFile(descDirNames)) {
                    log.debug("删除目录 " + descDirNames + " 失败!");
                    return false;
                }
            } else {
                log.debug("目标目录复制失败，目标目录 " + descDirNames + " 已存在!");
                return false;
            }
        } else {
            // 创建目标目录
            log.debug("目标目录不存在，准备创建!");
            if (!descDir.mkdirs()) {
                log.debug("创建目标目录失败!");
                return false;
            }

        }

        boolean flag = true;
        // 列出源目录下的所有文件名和子目录名
        File[] files = srcDir.listFiles();
        if (files != null) {
            for (File file : files) {
                // 如果是一个单个文件，则直接复制
                if (file.isFile()) {
                    flag = FileUtils.copyFile(file.getAbsolutePath(), descDirName + file.getName());
                    // 如果拷贝文件失败，则退出循环
                    if (!flag) {
                        break;
                    }
                }
                // 如果是子目录，则继续复制目录
                if (file.isDirectory()) {
                    flag = FileUtils.copyDirectory(file.getAbsolutePath(), descDirName + file.getName());
                    // 如果拷贝目录失败，则退出循环
                    if (!flag) {
                        break;
                    }
                }
            }
        }

        if (!flag) {
            log.debug("复制目录 " + srcDirName + " 到 " + descDirName + " 失败!");
            return false;
        }
        log.debug("复制目录 " + srcDirName + " 到 " + descDirName + " 成功!");
        return true;

    }

    /**
     * 删除文件，可以删除单个文件或文件夹
     *
     * @param fileName 被删除的文件名
     * @return 如果删除成功，则返回true，否是返回false
     */
    public static boolean delFile(String fileName) {
        File file = new File(fileName);
        if (!file.exists()) {
            log.debug(fileName + " 文件不存在!");
            return true;
        } else {
            if (file.isFile()) {
                return FileUtils.deleteFile(fileName);
            } else {
                return FileUtils.deleteDirectory(fileName);
            }
        }
    }

    /**
     * 删除单个文件
     *
     * @param fileName 被删除的文件名
     * @return 如果删除成功，则返回true，否则返回false
     */
    public static boolean deleteFile(String fileName) {
        File file = new File(fileName);
        if (file.exists() && file.isFile()) {
            if (file.delete()) {
                log.debug("删除文件 " + fileName + " 成功!");
                return true;
            } else {
                log.debug("删除文件 " + fileName + " 失败!");
                return false;
            }
        } else {
            log.debug(fileName + " 文件不存在!");
            return true;
        }
    }

    /**
     * 删除目录及目录下的文件
     *
     * @param dirName 被删除的目录所在的文件路径
     * @return 如果目录删除成功，则返回true，否则返回false
     */
    public static boolean deleteDirectory(String dirName) {
        String dirNames = dirName;
        if (!dirNames.endsWith(File.separator)) {
            dirNames = dirNames + File.separator;
        }
        File dirFile = new File(dirNames);
        if (!dirFile.exists() || !dirFile.isDirectory()) {
            log.debug(dirNames + " 目录不存在!");
            return true;
        }
        boolean flag = true;
        // 列出全部文件及子目录
        File[] files = dirFile.listFiles();

        if (files != null) {
            for (File file : files) {
                // 删除子文件
                if (file.isFile()) {
                    flag = FileUtils.deleteFile(file.getAbsolutePath());
                    // 如果删除文件失败，则退出循环
                    if (!flag) {
                        break;
                    }
                }
                // 删除子目录
                else if (file.isDirectory()) {
                    flag = FileUtils.deleteDirectory(file.getAbsolutePath());
                    // 如果删除子目录失败，则退出循环
                    if (!flag) {
                        break;
                    }
                }
            }
        }

        if (!flag) {
            log.debug("删除目录失败!");
            return false;
        }
        // 删除当前目录
        if (dirFile.delete()) {
            log.debug("删除目录 " + dirName + " 成功!");
            return true;
        } else {
            log.debug("删除目录 " + dirName + " 失败!");
            return false;
        }

    }

    /**
     * 创建单个文件
     *
     * @param descFileName 文件名，包含路径
     * @return 如果创建成功，则返回true，否则返回false
     */
    public static boolean createFile(String descFileName) {
        File file = new File(descFileName);
        if (file.exists()) {
            log.debug("文件 " + descFileName + " 已存在!");
            return false;
        }
        if (descFileName.endsWith(File.separator)) {
            log.debug(descFileName + " 为目录，不能创建目录!");
            return false;
        }
        if (!file.getParentFile().exists()) {
            // 如果文件所在的目录不存在，则创建目录
            if (!file.getParentFile().mkdirs()) {
                log.debug("创建文件所在的目录失败!");
                return false;
            }
        }

        // 创建文件
        try {
            if (file.createNewFile()) {
                log.debug(descFileName + " 文件创建成功!");
                return true;
            } else {
                log.debug(descFileName + " 文件创建失败!");
                return false;
            }
        } catch (Exception e) {
            log.error("", e);
            log.debug(descFileName + " 文件创建失败!");
            return false;
        }

    }

    /**
     * 创建目录
     *
     * @param descDirName 目录名,包含路径
     * @return 如果创建成功，则返回true，否则返回false
     */
    public static boolean createDirectory(String descDirName) {
        String descDirNames = descDirName;
        if (!descDirNames.endsWith(File.separator)) {
            descDirNames = descDirNames + File.separator;
        }
        File descDir = new File(descDirNames);
        if (descDir.exists()) {
            log.debug("目录 " + descDirNames + " 已存在!");
            return false;
        }
        // 创建目录
        if (descDir.mkdirs()) {
            log.debug("目录 " + descDirNames + " 创建成功!");
            return true;
        } else {
            log.debug("目录 " + descDirNames + " 创建失败!");
            return false;
        }

    }

    /**
     * 写入文件
     *
     * @param fileName 要写入的文件
     * @param content  内容
     * @param append   附加方式
     */
    public static void writeToFile(String fileName, String content, boolean append) {
        try {
            FileUtils.write(new File(fileName), content, "utf-8", append);
            log.debug("文件 " + fileName + " 写入成功!");
        } catch (IOException e) {
            log.debug("文件 " + fileName + " 写入失败! " + e.getMessage());
        }
    }

    /**
     * 写入文件
     *
     * @param fileName 要写入的文件
     * @param content  内容
     * @param encoding 编码
     * @param append   附加方式
     */
    public static void writeToFile(String fileName, String content, String encoding, boolean append) {
        try {
            FileUtils.write(new File(fileName), content, encoding, append);
            log.debug("文件 " + fileName + " 写入成功!");
        } catch (IOException e) {
            log.debug("文件 " + fileName + " 写入失败! " + e.getMessage());
        }
    }

    /**
     * 写入内容到文件
     * @param fileName 完整文件路径，包括文件名
     * @param fileContent 写入的内容
     */
    public static void writeFile(String fileName, String fileContent) {
        try {
            File f = new File(fileName);
            if (!f.exists()) {
                boolean result = f.createNewFile();
                if (!result) {
                    log.error("create new file {} error.", fileName);
                }
            }
            IOUtils.write(fileContent, new FileOutputStream(f), CharEncoding.UTF_8);
        } catch (IOException e) {
            log.error("", e);
        }
    }

    /**
     * 获取文件内容
     * @param fileName 完整文件路径，包括文件名
     * @return 文件内容
     */
    public static String getFileContent(String fileName) {
        String content = "";
        try (FileInputStream fis = new FileInputStream(fileName)) {
            content = IOUtils.toString(fis, getCharset(fileName));
        } catch (IOException e) {
            log.error("", e);
        }
        return content;
    }

    /**
     * 修正路径，将 \\ 或 / 等替换为 File.separator
     *
     * @param path 待修正的路径
     * @return 修正后的路径
     */
    public static String path(String path) {
        String p = StringUtils.replace(path, "\\", "/");
        p = StringUtils.join(StringUtils.split(p, "/"), "/");
        if (!StringUtils.startsWithAny(p, "/") && StringUtils.startsWithAny(path, "\\", "/")) {
            p += "/";
        }
        if (!StringUtils.endsWithAny(p, "/") && StringUtils.endsWithAny(path, "\\", "/")) {
            p = p + "/";
        }
        if (path != null && path.startsWith("/")) {
            p = "/" + p; // linux下路径
        }
        return p;
    }

    /**
     * 文件大小工具类.
     */
    // delete @see
    // byteCountToDisplaySize(size)

    /**
     * 获取文件编码
     * @param fileName 完整文件路径，包括文件名
     * @return 文件编码
     */
    public static String getCharset(String fileName) {
        String code;
        BufferedInputStream bis = null;
        int p = 0;
        try (FileInputStream inputStream = new FileInputStream(fileName)) {
            bis = new BufferedInputStream(inputStream);
            p = (bis.read() << 8) + bis.read();
        } catch (IOException ignored) {
        } finally {
            if (bis != null) {
                try {
                    bis.close();
                } catch (IOException ignored) {
                }
            }
        }

        switch (p) {
            case 0xefbb:
                code = CharEncoding.UTF_8;
                break;
            case 0xfffe:
                code = "Unicode";
                break;
            case 0xfeff:
                code = CharEncoding.UTF_16BE;
                break;
            default:
                code = "GBK";
        }

        return code;
    }


    /**
     * 根据“文件名的后缀”获取文件内容类型（而非根据File.getContentType()读取的文件类型）
     *
     * @param returnFileName 带验证的文件名
     * @return 返回文件类型
     */
    public static String getContentType(String returnFileName) {
        String contentType = "application/octet-stream";
        if (returnFileName.lastIndexOf(".") < 0) {
            return contentType;
        }
        returnFileName = returnFileName.toLowerCase();
        returnFileName = returnFileName.substring(returnFileName.lastIndexOf(".") + 1);
        switch (returnFileName) {
            case "html":
            case "htm":
            case "shtml":
                contentType = "text/html";
                break;
            case "apk":
                contentType = "application/vnd.android.package-archive";
                break;
            case "sis":
                contentType = "application/vnd.symbian.install";
                break;
            case "sisx":
                contentType = "application/vnd.symbian.install";
                break;
            case "exe":
                contentType = "application/x-msdownload";
                break;
            case "msi":
                contentType = "application/x-msdownload";
                break;
            case "css":
                contentType = "text/css";
                break;
            case "xml":
                contentType = "text/xml";
                break;
            case "gif":
                contentType = "image/gif";
                break;
            case "jpeg":
            case "jpg":
                contentType = "image/jpeg";
                break;
            case "js":
                contentType = "application/x-javascript";
                break;
            case "atom":
                contentType = "application/atom+xml";
                break;
            case "rss":
                contentType = "application/rss+xml";
                break;
            case "mml":
                contentType = "text/mathml";
                break;
            case "txt":
                contentType = "text/plain";
                break;
            case "jad":
                contentType = "text/vnd.sun.j2me.app-descriptor";
                break;
            case "wml":
                contentType = "text/vnd.wap.wml";
                break;
            case "htc":
                contentType = "text/x-component";
                break;
            case "png":
                contentType = "image/png";
                break;
            case "tif":
            case "tiff":
                contentType = "image/tiff";
                break;
            case "wbmp":
                contentType = "image/vnd.wap.wbmp";
                break;
            case "ico":
                contentType = "image/x-icon";
                break;
            case "jng":
                contentType = "image/x-jng";
                break;
            case "bmp":
                contentType = "image/x-ms-bmp";
                break;
            case "svg":
                contentType = "image/svg+xml";
                break;
            case "jar":
            case "var":
            case "ear":
                contentType = "application/java-archive";
                break;
            case "doc":
                contentType = "application/msword";
                break;
            case "pdf":
                contentType = "application/pdf";
                break;
            case "rtf":
                contentType = "application/rtf";
                break;
            case "xls":
                contentType = "application/vnd.ms-excel";
                break;
            case "ppt":
                contentType = "application/vnd.ms-powerpoint";
                break;
            case "7z":
                contentType = "application/x-7z-compressed";
                break;
            case "rar":
                contentType = "application/x-rar-compressed";
                break;
            case "swf":
                contentType = "application/x-shockwave-flash";
                break;
            case "rpm":
                contentType = "application/x-redhat-package-manager";
                break;
            case "der":
            case "pem":
            case "crt":
                contentType = "application/x-x509-ca-cert";
                break;
            case "xhtml":
                contentType = "application/xhtml+xml";
                break;
            case "zip":
                contentType = "application/zip";
                break;
            case "mid":
            case "midi":
            case "kar":
                contentType = "audio/midi";
                break;
            case "mp3":
                contentType = "audio/mpeg";
                break;
            case "ogg":
                contentType = "audio/ogg";
                break;
            case "m4a":
                contentType = "audio/x-m4a";
                break;
            case "ra":
                contentType = "audio/x-realaudio";
                break;
            case "3gpp":
            case "3gp":
                contentType = "video/3gpp";
                break;
            case "mp4":
                contentType = "video/mp4";
                break;
            case "mpeg":
            case "mpg":
                contentType = "video/mpeg";
                break;
            case "mov":
                contentType = "video/quicktime";
                break;
            case "flv":
                contentType = "video/x-flv";
                break;
            case "m4v":
                contentType = "video/x-m4v";
                break;
            case "mng":
                contentType = "video/x-mng";
                break;
            case "asx":
            case "asf":
                contentType = "video/x-ms-asf";
                break;
            case "wmv":
                contentType = "video/x-ms-wmv";
                break;
            case "avi":
                contentType = "video/x-msvideo";
                break;
            case "wav":
                contentType = "video/x-ms-wav";
                break;
        }
        return contentType;
    }

    /**
     * 向浏览器发送文件下载，支持断点续传
     *
     * @param file     要下载的文件
     * @param request  请求对象
     * @param response 响应对象
     * @return 返回错误信息，无错误信息返回null
     */
    public static String downFile(File file, HttpServletRequest request, HttpServletResponse response) {
        return downFile(file, request, response, null);
    }

    /**
     * 向浏览器发送文件下载，支持断点续传
     *
     * @param file     要下载的文件
     * @param request  请求对象
     * @param response 响应对象
     * @param fileName 指定下载的文件名
     * @return 返回错误信息，无错误信息返回null
     */
    public static String downFile(File file, HttpServletRequest request, HttpServletResponse response, String fileName) {
        String error = null;
        if (file != null && file.exists()) {
            if (file.isFile()) {
                if (file.length() <= 0) {
                    error = "该文件是一个空文件。";
                }
                if (!file.canRead()) {
                    error = "该文件没有读取权限。";
                }
            } else {
                error = "该文件是一个文件夹。";
            }
        } else {
            error = "文件已丢失或不存在！";
        }
        if (error != null) {
            log.debug("---------------" + file + " " + error);
            return error;
        }

        long fileLength = file.length(); // 记录文件大小
        long pastLength = 0;    // 记录已下载文件大小
        int rangeSwitch = 0;    // 0：从头开始的全文下载；1：从某字节开始的下载（bytes=27000-）；2：从某字节开始到某字节结束的下载（bytes=27000-39000）
        long toLength;          // 记录客户端需要下载的字节段的最后一个字节偏移量（比如bytes=27000-39000，则这个值是为39000）
        long contentLength;     // 客户端请求的字节总量
        String rangeBytes = ""; // 记录客户端传来的形如“bytes=27000-”或者“bytes=27000-39000”的内容
        RandomAccessFile raf = null; // 负责读取数据
        OutputStream os;        // 写出数据
        OutputStream out = null;      // 缓冲
        byte b[] = new byte[1024];    // 暂存容器

        if (request.getHeader("Range") != null) { // 客户端请求的下载的文件块的开始字节
            response.setStatus(HttpServletResponse.SC_PARTIAL_CONTENT);
            log.debug("request.getHeader(\"Range\") = " + request.getHeader("Range"));
            rangeBytes = request.getHeader("Range").replaceAll("bytes=", "");
            if (rangeBytes.indexOf('-') == rangeBytes.length() - 1) {// bytes=969998336-
                rangeSwitch = 1;
                rangeBytes = rangeBytes.substring(0, rangeBytes.indexOf('-'));
                pastLength = Long.parseLong(rangeBytes.trim());
                contentLength = fileLength - pastLength; // 客户端请求的是 969998336  之后的字节
            } else { // bytes=1275856879-1275877358
                rangeSwitch = 2;
                String temp0 = rangeBytes.substring(0, rangeBytes.indexOf('-'));
                String temp2 = rangeBytes.substring(rangeBytes.indexOf('-') + 1, rangeBytes.length());
                pastLength = Long.parseLong(temp0.trim()); // bytes=1275856879-1275877358，从第 1275856879 个字节开始下载
                toLength = Long.parseLong(temp2); // bytes=1275856879-1275877358，到第 1275877358 个字节结束
                contentLength = toLength - pastLength; // 客户端请求的是 1275856879-1275877358 之间的字节
            }
        } else { // 从开始进行下载
            contentLength = fileLength; // 客户端要求全文下载
        }

        // 如果设设置了Content-Length，则客户端会自动进行多线程下载。如果不希望支持多线程，则不要设置这个参数。 响应的格式是:
        // Content-Length: [文件的总大小] - [客户端请求的下载的文件块的开始字节]
        // ServletActionContext.getResponse().setHeader("Content- Length", new Long(file.length() - p).toString());
        response.reset(); // 告诉客户端允许断点续传多线程连接下载,响应的格式是:Accept-Ranges: bytes
        if (pastLength != 0) {
            response.setHeader("Accept-Ranges", "bytes");// 如果是第一次下,还没有断点续传,状态是默认的 200,无需显式设置;响应的格式是:HTTP/1.1 200 OK
            // 不是从最开始下载, 响应的格式是: Content-Range: bytes [文件块的开始字节]-[文件的总大小 - 1]/[文件的总大小]
            log.debug("---------------不是从开始进行下载！服务器即将开始断点续传...");
            switch (rangeSwitch) {
                case 1: { // 针对 bytes=27000- 的请求
                    String contentRange = new StringBuilder("bytes ")
                            .append(Long.toString(pastLength))
                            .append("-")
                            .append(Long.toString(fileLength - 1))
                            .append("/")
                            .append(Long.toString(fileLength)).toString();
                    response.setHeader("Content-Range", contentRange);
                    break;
                }
                case 2: { // 针对 bytes=27000-39000 的请求
                    String contentRange = rangeBytes + "/" + Long.toString(fileLength);
                    response.setHeader("Content-Range", contentRange);
                    break;
                }
                default: {
                    break;
                }
            }
        } else {
            // 是从开始下载
            log.debug("---------------是从开始进行下载！");
        }

        try {
            response.addHeader("Content-Disposition", "attachment; filename=\"" +
                    Encodes.urlEncode(StringUtils.isBlank(fileName) ? file.getName() : fileName) + "\"");
            response.setContentType(getContentType(file.getName())); // set the MIME type.
            response.addHeader("Content-Length", String.valueOf(contentLength));
            os = response.getOutputStream();
            out = new BufferedOutputStream(os);
            raf = new RandomAccessFile(file, "r");
            try {
                switch (rangeSwitch) {
                    case 0: { // 普通下载，或者从头开始的下载 同1
                    }
                    case 1: { // 针对 bytes=27000- 的请求
                        raf.seek(pastLength); // 形如 bytes=969998336- 的客户端请求，跳过 969998336 个字节
                        int n;
                        while ((n = raf.read(b, 0, 1024)) != -1) {
                            out.write(b, 0, n);
                        }
                        break;
                    }
                    case 2: { // 针对 bytes=27000-39000 的请求
                        raf.seek(pastLength); // 形如 bytes=1275856879-1275877358 的客户端请求，找到第 1275856879 个字节
                        int n;
                        long readLength = 0; // 记录已读字节数
                        while (readLength <= contentLength - 1024) {// 大部分字节在这里读取
                            n = raf.read(b, 0, 1024);
                            readLength += 1024;
                            out.write(b, 0, n);
                        }
                        if (readLength <= contentLength) { // 余下的不足 1024 个字节在这里读取
                            n = raf.read(b, 0, (int) (contentLength - readLength));
                            out.write(b, 0, n);
                        }
                        break;
                    }
                    default: {
                        break;
                    }
                }
                out.flush();
                log.debug("---------------下载完成！");
            } catch (IOException ie) {
                /**
                 * 在写数据的时候， 对于 ClientAbortException 之类的异常，
                 * 是因为客户端取消了下载，而服务器端继续向浏览器写入数据时， 抛出这个异常，这个是正常的。
                 * 尤其是对于迅雷这种吸血的客户端软件， 明明已经有一个线程在读取 bytes=1275856879-1275877358，
                 * 如果短时间内没有读取完毕，迅雷会再启第二个、第三个。。。线程来读取相同的字节段， 直到有一个线程读取完毕，迅雷会 KILL
                 * 掉其他正在下载同一字节段的线程， 强行中止字节读出，造成服务器抛 ClientAbortException。
                 * 所以，我们忽略这种异常
                 */
                log.debug("提醒：向客户端传输时出现IO异常，但此异常是允许的，有可能客户端取消了下载，导致此异常，不用关心！");
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        } finally {
            if (out != null) {
                try {
                    out.close();
                } catch (IOException e) {
                    log.error(e.getMessage(), e);
                }
            }
            if (raf != null) {
                try {
                    raf.close();
                } catch (IOException e) {
                    log.error(e.getMessage(), e);
                }
            }
        }
        return null;
    }

    /**
     * 获目录下的文件列表
     *
     * @param dir        搜索目录
     * @param searchDirs 是否是搜索目录
     * @return 文件列表
     */
    public static List<String> findChildrenList(File dir, boolean searchDirs) {
        List<String> files = Lists.newArrayList();
        for (String subFiles : dir.list()) {
            File file = new File(dir + "/" + subFiles);
            if (((searchDirs) && (file.isDirectory())) || ((!searchDirs) && (!file.isDirectory()))) {
                files.add(file.getName());
            }
        }
        return files;
    }

    /**
     * 获取文件扩展名(返回小写)
     *
     * @param fileName 文件名
     * @return 例如：test.jpg  返回：  jpg
     */
    public static String getFileExtension(String fileName) {
        if ((fileName == null) || (fileName.lastIndexOf(".") == -1) || (fileName.lastIndexOf(".") == fileName.length() - 1)) {
            return null;
        }
        return StringUtils.lowerCase(fileName.substring(fileName.lastIndexOf(".") + 1));
    }

    /**
     * 获取文件名，不包含扩展名
     *
     * @param fileName 文件名
     * @return 例如：d:\files\test.jpg  返回：d:\files\test
     */
    public static String getFileNameWithoutExtension(String fileName) {
        if ((fileName == null) || (fileName.lastIndexOf(".") == -1)) {
            return null;
        }
        return fileName.substring(0, fileName.lastIndexOf("."));
    }

    /**
     * 下载文件
     *
     * @param filePath 文件路径
     * @throws IOException
     */
    public static void downFile(HttpServletResponse response, String filePath) throws IOException {
        File file = new File(filePath);
        if (!file.exists()) {
            throw new IllegalArgumentException("下载的文件路径无效，请检查路径：" + filePath);
        }
        downFile(response, filePath, file.getName(), false);
    }

    /**
     * 下载文件
     *
     * @param filePath 文件路径
     * @throws IOException
     */
    public static void downFile(HttpServletResponse response, String filePath, boolean direct) throws IOException {
        File file = new File(filePath);
        if (!file.exists()) {
            throw new IllegalArgumentException("下载的文件路径无效，请检查路径：" + filePath);
        }
        downFile(response, filePath, file.getName(), direct);
    }

    /**
     * 下载文件, 解决fileName和文件名不一致的情况
     */
    public static void downFile(HttpServletResponse response, String filePath, String fileName, boolean direct) throws IOException {
        File file = new File(filePath);
        if (!file.exists()) {
            throw new IllegalArgumentException("下载的文件路径无效，请检查路径：" + filePath);
        }
        response.setHeader("content-type", "application/octet-stream");
        response.setContentType("application/octet-stream");
        /* 如果文件名有中文的话，进行URL编码，让中文正常显示*/
        if (direct) {
            response.addHeader("Content-Disposition", "attachment; filename=" + new String(fileName.getBytes(), "ISO-8859-1"));
        } else {
            response.addHeader("Content-Disposition", "attachment; filename=" + URLEncoder.encode(fileName, "UTF-8"));
        }
        // response.addHeader("Content-Disposition", "attachment; filename=" + new String(fileName.getBytes(), "ISO-8859-1"));

        InputStream inputStream = null;
        OutputStream outputStream = null;
        try {
            inputStream = new FileInputStream(file);
            outputStream = response.getOutputStream();
            byte[] buffer = new byte[1024];
            int len;
            while ((len = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, len);
            }
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException ignored) {
                }
            }
            if (outputStream != null) {
                try {
                    outputStream.close();
                } catch (IOException ignored) {
                }
            }
        }
    }

    /**
     * 文件file进行加密并保存目标文件destFile中
     *
     * @param file     要加密的文件 如c:/test/srcFile.txt
     * @param destFile 加密后存放的文件名 如c:/加密后文件.txt
     */
    public static void encrypt(File file, String destFile, String strKey) throws Exception {
        InputStream is = null;
        OutputStream out = null;
        CipherInputStream cis = null;
        try {
            Cipher cipher = Cipher.getInstance("DES");
            cipher.init(Cipher.ENCRYPT_MODE, getKey(strKey));
            is = new FileInputStream(file);
            out = new FileOutputStream(destFile);
            cis = new CipherInputStream(is, cipher);
            byte[] buffer = new byte[1024];
            int r;
            while ((r = cis.read(buffer)) > 0) {
                out.write(buffer, 0, r);
            }
        } finally {
            if (cis != null) {
                try {
                    cis.close();
                } catch (IOException ignored) {
                }
            }
            if (is != null) {
                try {
                    is.close();
                } catch (IOException ignored) {
                }
            }
            if (out != null) {
                try {
                    out.close();
                } catch (IOException ignored) {
                }
            }
        }
    }

    /**
     * 文件采用DES算法解密文件
     *
     * @param file 已加密的文件 如c:/加密后文件.txt *
     * @param dest 解密后存放的文件名 如c:/ test/解密后文件.txt
     */
    public static void decrypt(File file, String dest, String strKey) throws Exception {
        InputStream is = null;
        OutputStream out = null;
        CipherOutputStream cos = null;
        try {
            Cipher cipher = Cipher.getInstance("DES");
            cipher.init(Cipher.DECRYPT_MODE, getKey(strKey));
            is = new FileInputStream(file);
            out = new FileOutputStream(dest);
            cos = new CipherOutputStream(out, cipher);
            byte[] buffer = new byte[1024];
            int r;
            while ((r = is.read(buffer)) >= 0) {
                cos.write(buffer, 0, r);
            }
        } finally {
            if (cos != null) {
                try {
                    cos.close();
                } catch (IOException ignored) {
                }
            }
            if (out != null) {
                try {
                    out.close();
                } catch (IOException ignored) {
                }
            }
            if (is != null) {
                try {
                    is.close();
                } catch (IOException ignored) {
                }
            }
        }
    }

    /**
     * 将BASE64加密字符串转换为图片.
     * @param base64String 图片字符流
     * @param imagePath 图片生成路径.
     * @param format 图片格式.
     */
    public static void convertByteToImage(String base64String, String imagePath, String format) {
        byte[] bytes = null;
        ByteArrayInputStream bais = null;
        BufferedImage bi = null;
        File file = null;
        try {

            bytes = Base64.getDecoder().decode(base64String);
            bais = new ByteArrayInputStream(bytes);
            bi = ImageIO.read(bais);
            file = new File(imagePath);

            File dir = file.getParentFile();
            if (!dir.exists()) {
                dir.mkdirs();
            }

            ImageIO.write(bi, format == null ? "jpg" : format, file);
            log.info("将BASE64加密字符串转换为图片成功！");
        } catch (IOException e) {
            log.error("将BASE64加密字符串转换为图片失败: " + e);
        } finally {
            try {
                if (bais != null) {
                    bais.close();
                    bais = null;
                }
            } catch (Exception e) {
                log.error("关闭文件流发生异常: " + e);
            }
        }
    }

    /**
     * 根据参数生成Key
     */
    private static Key getKey(String strKey) throws NoSuchAlgorithmException, InvalidKeyException, InvalidKeySpecException {
        SecretKeyFactory factory = SecretKeyFactory.getInstance("DES");
        return factory.generateSecret(new DESKeySpec(strKey.getBytes()));
    }

}
