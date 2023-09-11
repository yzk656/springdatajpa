package com.example.springdatajpa.controller;


import com.example.springdatajpa.common.util.DocxUtil;
import com.example.springdatajpa.common.util.FileUtil;
import com.example.springdatajpa.entity.Email;
import com.example.springdatajpa.service.EmailService;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.mail.*;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.OutputStream;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/email")
public class EmailController {

    private List<String> emailTitles;

    @Autowired
    private EmailService emailService;

    /**
     * 查询全部email数据
     *
     * @return
     */
    @GetMapping("/listEmail")
    public List<Email> list() {
        return emailService.list();
    }

    /**
     * 查询详情
     *
     * @param id
     * @return
     */
    @GetMapping("/detail")
    public Email detail(@Param("id") Long id) {
        return emailService.detailById(id);
    }


    @GetMapping("/exportDocx")
    public void exportDocx(HttpServletResponse response) throws MessagingException {
//        /*先获取邮箱数据*/
//        readEmailAndExport();
//        System.out.println(emailTitles);


        // 导出文件所在临时目录
        String uuid = UUID.randomUUID().toString();
        String dirPath = DocxUtil.TempDirPath + "/" + "邮箱" + "/" + uuid;
        File tempPath = new File(dirPath);
        if (!tempPath.exists()) {
            tempPath.mkdirs();
        }

        try {
            String res = dirPath + DocxUtil.Tpl_CommOrder;
            DocxUtil.exportEmailTitle(emailTitles, res);

            String fileName = new String(("邮箱标题" + uuid + ".docx").getBytes(), "ISO8859-1");
            response.setContentType("application/octet-stream;charset=ISO8859-1");
            response.setHeader("Content-Disposition", "attachment;filename=" + fileName);
            response.addHeader("Pargam", "no-cache");
            response.addHeader("Cache-Control", "no-cache");

            OutputStream os = response.getOutputStream();
            FileInputStream in = new FileInputStream(res);
            IOUtils.copy(in, os);
            in.close();
            os.flush();
            os.close();
            FileUtil.deleteDir(tempPath);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
