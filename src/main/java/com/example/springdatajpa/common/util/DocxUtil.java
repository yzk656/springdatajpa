package com.example.springdatajpa.common.util;

import com.deepoove.poi.XWPFTemplate;
import org.apache.commons.io.FileUtils;


import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * word文档模板导出工具
 */
public class DocxUtil {
    private final static String TplRootPath;
    public final static String Tpl_CommOrder = "/emailTitle.docx";

    public final static String TempDirPath = FileUtils.getTempDirectoryPath() + "/test";

    static {
        TplRootPath = DocxUtil.class.getClassLoader().getResource("docx_templates").getPath();
        File tempPath = new File(TempDirPath);
        if (!tempPath.exists()) {
            tempPath.mkdirs();
        }
    }

    /*到处邮件标题*/
    public static void exportEmailTitle(List<String> emailTitle, String resFilePath) throws IOException {
        XWPFTemplate.compile(TplRootPath + Tpl_CommOrder).render(emailTitle).writeToFile(resFilePath);
    }
}
