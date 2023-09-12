package com.example.springdatajpa.controller;


import com.example.springdatajpa.common.util.DocxUtil;
import com.example.springdatajpa.common.util.FileUtil;
import com.example.springdatajpa.entity.Email;
import com.example.springdatajpa.service.EmailService;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfWriter;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

import javax.mail.*;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.List;
import java.util.UUID;

import static com.example.springdatajpa.common.util.PDFBinaryUtil.base64StringToPDF;

@RestController
@RequestMapping("/email")
public class EmailController {
    /*BASE64Encoder和BASE64Decoder这两个方法是sun公司的内部方法，并没有在java api中公开过，所以使用这些方法是不安全的，
     * 将来随时可能会从中去除，所以相应的应该使用替代的对象及方法，建议使用apache公司的API---可引用 import org.apache.commons.codec.binary.Base64;进行替换*/
    static BASE64Encoder encoder = new sun.misc.BASE64Encoder();
    static BASE64Decoder decoder = new sun.misc.BASE64Decoder();

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
    public Email detail(Long id) {
        System.out.println(111);
        return emailService.detailById(id);
    }

    /*pdf下载*/
    @GetMapping("/download/{id}")
    public void downLoad(@PathVariable Long id,HttpServletResponse response) throws DocumentException, IOException {
        //获取pdf二进制数据
        Email email = emailService.detailById(id);
        if (email == null)
            return;
        byte[] pdfAttachment = email.getPdfAttachment();

        // 设置响应头，指示文件类型和下载方式
        response.setContentType("application/pdf");
        response.setHeader("Content-Disposition", "attachment; filename=email.pdf");

        // 获取响应输出流
        OutputStream outputStream = response.getOutputStream();

        try {
            // 将PDF二进制数据写入响应输出流
            outputStream.write(pdfAttachment);
            outputStream.flush();
        } finally {
            // 关闭输出流
            outputStream.close();
        }

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
