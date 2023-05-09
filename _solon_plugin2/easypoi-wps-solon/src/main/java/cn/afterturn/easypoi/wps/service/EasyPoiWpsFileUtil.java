package cn.afterturn.easypoi.wps.service;

/**
 * @author jueyue on 20-5-8.
 */
public class EasyPoiWpsFileUtil {

    private static String[] OFFICE = {"word", "excel", "ppt"};

    private static String[] EXCEL_EXTS = {"et", "xls", "xlt", "xlsx", "xlsm", "xltx", "xltm", "csv"};

    private static String[] WORD_EXTS = {"doc", "docx", "txt", "dot", "wps", "wpt", "dotx", "docm", "dotm"};

    private static String[] PPT_EXTS = {"ppt", "pptx", "pptm", "pptm", "ppsm", "pps", "potx", "potm", "dpt", "dps"};

    private static String[] PDF_EXTS = {"pdf"};

    public static String getFileTypeCode(String fileType) {
        for (String et : EXCEL_EXTS) {
            if (et.equalsIgnoreCase(fileType)) {
                return "s";
            }
        }
        for (String et : WORD_EXTS) {
            if (et.equalsIgnoreCase(fileType)) {
                return "w";
            }
        }
        for (String et : PPT_EXTS) {
            if (et.equalsIgnoreCase(fileType)) {
                return "p";
            }
        }
        for (String et : PDF_EXTS) {
            if (et.equalsIgnoreCase(fileType)) {
                return "f";
            }
        }
        return null;
    }

    public static boolean checkCode(String fileType) {
        for (String et : OFFICE) {
            if (et.equalsIgnoreCase(fileType)) {
                return true;
            }
        }
        return false;
    }

    public static String getTypeCode(String fileType) {
        if ("word".equalsIgnoreCase(fileType)) {
            return "w";
        }
        if ("excel".equalsIgnoreCase(fileType)) {
            return "s";
        }
        if ("ppt".equalsIgnoreCase(fileType)) {
            return "p";
        }
        return null;
    }

    public static String getFileName(String filePath) {
        String[] pathArr = filePath.split("/");
        String   fileName;
        if (pathArr.length > 1) {
            fileName = pathArr[pathArr.length - 1];
        } else {
            fileName = filePath;
        }
        return fileName;
    }

    public static String getFileTypeByPath(String filePath) {
        String   fileName = getFileName(filePath);
        String[] arr      = fileName.split("\\.");
        return arr[arr.length - 1];
    }

    public static String getFileTypeByName(String fileName) {
        String[] arr = fileName.split("\\.");
        return arr[arr.length - 1];
    }
}
