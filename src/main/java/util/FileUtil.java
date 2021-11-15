package util;

import org.mozilla.universalchardet.UniversalDetector;

import java.io.*;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

public class FileUtil {

    public static String readFileInSameFolder(String fileName) {
        try {
            String jarpath = System.getProperty("java.class.path");
            int firstIndex = jarpath.lastIndexOf(System.getProperty("path.separator")) + 1;
            int lastIndex = jarpath.lastIndexOf(File.separator) + 1;
            jarpath = jarpath.substring(firstIndex, lastIndex);

            File conffile =new File(jarpath + fileName);
            if(conffile.exists() != true) {
                return "false";
            }
           if(getFileDetector(jarpath + fileName).equals("UTF-8")) {
                InputStreamReader confrd = new InputStreamReader (new FileInputStream(conffile),"UTF-8");
                BufferedReader confbf = new BufferedReader(confrd);
                int ch = 0;
                StringBuffer sb = new StringBuffer();
                while((ch = confbf.read()) != -1) {
                    sb.append((char) ch);
                }

                return sb.toString();
           }

            UnicodeReader confrd = new UnicodeReader(new FileInputStream(jarpath + fileName), Charset.defaultCharset().name());
            BufferedReader confbf = new BufferedReader(confrd);
            int ch = 0;
            StringBuffer sb = new StringBuffer();
            while((ch = confbf.read()) != -1) {
                sb.append((char) ch);
            }

            return sb.toString();

        }catch (Exception e) {
            e.printStackTrace();
            return "false";
        }
    }


    public static String getFileDetector(String filePath) {
        try {
            byte[] buf = new byte[4096];
            FileInputStream fis = new FileInputStream(filePath);

            // (1)
            UniversalDetector detector = new UniversalDetector(null);

            // (2)
            int nread;
            while ((nread = fis.read(buf)) > 0 && !detector.isDone()) {
                detector.handleData(buf, 0, nread);
            }
            // (3)
            detector.dataEnd();

            // (4)
            String encoding = detector.getDetectedCharset();

            // (5)
            detector.reset();

            if(encoding.equals("null"))
                return "false";
            return encoding;
        }catch (Exception e) {
            return "false";
        }
    }


    public static List<String> listFileNameInDir(String dirPath) {
        try {
            File f = new File(dirPath);
            if (!f.exists()) {
                System.out.println(dirPath + " not exists");
                return null;
            }

            File List[] = f.listFiles();

            List<String> fileList = new ArrayList<>();
            for (File item : List) {
                fileList.add(item.getName());
            }

            return fileList;

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String readFileToString(String path) {
        try {
            File conffile =new File(path);
            if(conffile.exists() != true) {
                return "false";
            }
            if(getFileDetector(path).equals("UTF-8")) {
                InputStreamReader confrd = new InputStreamReader (new FileInputStream(conffile),"UTF-8");
                BufferedReader confbf = new BufferedReader(confrd);
                int ch = 0;
                StringBuffer sb = new StringBuffer();
                while((ch = confbf.read()) != -1) {
                    sb.append((char) ch);
                }

                return sb.toString();
            }

            UnicodeReader confrd = new UnicodeReader(new FileInputStream(path), Charset.defaultCharset().name());
            BufferedReader confbf = new BufferedReader(confrd);
            int ch = 0;
            StringBuffer sb = new StringBuffer();
            while((ch = confbf.read()) != -1) {
                sb.append((char) ch);
            }

            return sb.toString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "false";
    }

    public static void saveFileInSameDir(String filename, String dirname, String temp) {
        try {
            String jarpath = System.getProperty("java.class.path");
            int firstIndex = jarpath.lastIndexOf(System.getProperty("path.separator")) + 1;
            int lastIndex = jarpath.lastIndexOf(File.separator) + 1;
            jarpath = jarpath.substring(firstIndex, lastIndex);

            String dirPath = jarpath + "\\" + dirname;
            File file=new File(dirPath);
            if(!file.exists()){//如果文件夹不存在
                file.mkdir();//创建文件夹
            }

            FileWriter fwriter = null;
            fwriter = new FileWriter(dirPath + "\\" + filename);
            fwriter.write(temp);
            fwriter.flush();
            fwriter.close();

        }catch (Exception e) {
            e.printStackTrace();
        }
    }
}
