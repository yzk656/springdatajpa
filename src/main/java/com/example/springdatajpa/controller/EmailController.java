package com.example.springdatajpa.controller;


import com.alibaba.fastjson.JSON;
import com.example.springdatajpa.entity.Email;
import com.example.springdatajpa.entity.EmailAttachment;
import com.example.springdatajpa.service.EmailService;
import com.itextpdf.text.DocumentException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/email")
public class EmailController {

    /*存放邮箱标题、发件人、发件时间信息*/
    private List<String> emailTitles;

    @Autowired
    private EmailService emailService;

    /**
     * 查询全部email数据
     *
     * @return
     */
    @GetMapping("/listEmail")
    @ResponseBody
    public String list() {
        List<Email> list = emailService.list();
        System.out.println(list);
        String jsonString = JSON.toJSONString(list);
        return jsonString;
    }

    /**
     * 查询详情
     *
     * @param id
     * @return
     */
    @GetMapping("/detail")
    @ResponseBody
    public String detail(Long id) {
        Email email = emailService.detailById(id);
        /*转换成json数据*/
        String jsonString = JSON.toJSONString(email);
        /*返回给调用者*/
        return jsonString;
    }

    /**
     * pdf文件下载
     *
     * @param id
     * @param attachmentIndex
     * @param response
     * @throws DocumentException
     * @throws IOException
     */
    @GetMapping("/download/{id}")
    public void downLoad(@PathVariable Long id, @RequestParam(name = "attachmentIndex", defaultValue = "0") int attachmentIndex, HttpServletResponse response) throws DocumentException, IOException {
        // 获取邮件信息
        Email email = emailService.detailById(id);
        System.out.println(email.getAttachments().size());
        if (email.getAttachments() == null || email.getAttachments().isEmpty()) {
            // 如果邮件不存在或没有附件，则返回
            return;
        }

        /*指定下载的附件*/
        List<EmailAttachment> attachments = email.getAttachments();
        if (attachmentIndex < 0 || attachmentIndex >= attachments.size()) {
            // 如果指定的附件索引无效，则返回
            return;
        }

        /*获取*/
        EmailAttachment selectedAttachment = attachments.get(attachmentIndex);
        byte[] pdfAttachment = selectedAttachment.getData();

        // 生成随机文件名
        String randomFileName = "email" + UUID.randomUUID() + ".pdf";

        // 设置响应头，指示文件类型和下载方式
        response.setContentType("application/pdf");
        response.setHeader("Content-Disposition", "attachment;filename= " + randomFileName);

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


}
