package com.example.springdatajpa.config;

import com.example.springdatajpa.Repository.EmailRepository;
import com.example.springdatajpa.entity.Email;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMultipart;
import javax.mail.search.FlagTerm;
import java.io.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Properties;

/**
 * 定时获取邮件信息（邮件正文、pdf附件）
 * 注意：读取邮件后会将未读邮件标记为已读邮件
 */
@Configuration
@EnableScheduling
public class ScheduledTasksConfig {

    // 请将下面的用户名和密码替换为您的实际用户名和授权密码
    String username = "yzk15325824503@126.com";
    String password = "IMPRDKCGULNMNBCN";

    @Autowired
    private EmailRepository emailRepository;

    @Scheduled(fixedRate = 60000) // 定时任务的执行频率（每60秒执行一次）
    public void myScheduledTask() {
        /*邮箱配置 IMAP 协议*/
        Properties props = Properties();

        // 创建会话
        Session session = getSession(props);

        try {
            // 连接到邮箱
            Store store = getStore(session);

            // 打开收件箱
            Folder inbox = getFolder(store);

            // 等待一段时间，以确保收件箱完全打开（可以根据需要调整等待时间）
            Thread.sleep(2000);

            // 创建搜索条件，过滤未读邮件
            FlagTerm unseenFlagTerm = getUnseenFlagTerm();

            // 获取邮件消息
            Message[] unreadMessages = inbox.search(unseenFlagTerm);
            for (Message message : unreadMessages) {
                String subject = message.getSubject();
                if (subject != null) {
                    /*获取邮件标题*/
                    String title = message.getSubject();
                    // 获取发件人信息
                    String senderName = getSenderName(message);

                    /*判断标题和发件人是否包含关键词*/
                    if (filter(title, senderName)) break;

                    // 获取邮件内容
                    StringBuilder content = new StringBuilder();
                    getHtmlMailContent(message, content);

                    // 保存PDF附件
                    byte[] pdfData = saveAttachment(message); //保存附件

                    /*获取发送时间*/
                    Date sentDate = message.getSentDate();

                    /*组装获得的信息*/
                    Email email = getEmail(title, senderName, content, pdfData, sentDate);

                    /*保存到数据库中*/
                    emailRepository.save(email);

                    // 将邮件标记为已读
                    message.setFlag(Flags.Flag.SEEN, true);
                }
            }

            // 关闭连接
            inbox.close(false);
            store.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        System.out.println("定时获取邮件任务执行了");
    }

    /**
     * 获取发件人信息
     * @param message
     * @return
     * @throws MessagingException
     */
    private static String getSenderName(Message message) throws MessagingException {
        String senderName = null;
        Address[] senders = message.getFrom();
        if (senders != null && senders.length > 0) {
            for (Address sender : senders) {
                senderName = ((InternetAddress) sender).getPersonal();
            }
        }
        return senderName;
    }

    /**
     * 判断标题和发件人是否包含关键词
     * @param title
     * @param senderName
     * @return
     */
    private static boolean filter(String title, String senderName) {
        /*判断标题是否包含关键词*/
        if (!(title != null && title.contains("发票") && senderName.contains("诺诺网") || (title != null && title.contains("发票") && senderName.contains("票通")))) {
            return true;
        }
        return false;
    }

    private static Email getEmail(String title, String senderName, StringBuilder content, byte[] pdfData, Date sentDate) {
        /*进行组装*/
        Email email = new Email();
        email.setTitle(title);
        email.setSender(senderName);
        email.setContent(content.toString());
        email.setCreateTime(sentDate);
        email.setPdfAttachment(pdfData);
        return email;
    }

    /**
     * 过滤出未读邮件
     *
     * @return
     */
    private static FlagTerm getUnseenFlagTerm() {
        Flags seen = new Flags(Flags.Flag.SEEN);
        FlagTerm unseenFlagTerm = new FlagTerm(seen, false);
        return unseenFlagTerm;
    }

    /**
     * 打开收件箱
     *
     * @param store
     * @return
     * @throws MessagingException
     */
    private static Folder getFolder(Store store) throws MessagingException {
        Folder inbox = store.getFolder("INBOX");
        /*注意你要修改状态，因此要改为读写权限*/
        inbox.open(Folder.READ_WRITE);
        return inbox;
    }

    /**
     * 连接到邮箱
     *
     * @param session
     * @return
     * @throws MessagingException
     */
    private Store getStore(Session session) throws MessagingException {
        Store store = session.getStore("imap");
        store.connect("imap.126.com", username, password);
        return store;
    }

    /**
     * 创建会话
     *
     * @param props
     * @return
     */
    private Session getSession(Properties props) {
        Session session = Session.getInstance(props, new Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(username, password);
            }
        });
        return session;
    }

    /**
     * 获取邮件所需配置信息
     *
     * @return
     */
    private Properties Properties() {
        Properties props = new Properties();
        props.put("mail.store.protocol", "imap"); // 使用 IMAP 协议
        props.put("mail.imap.host", "imap.126.com"); // IMAP 服务器地址
        props.put("mail.imap.port", "993"); // IMAP 端口（使用 SSL）
        /*打开 SSL*/
        props.put("mail.imap.ssl.enable", "true");

        return props;
    }

    /**
     * 保存附件
     */
    private byte[] saveAttachment(Part part) throws Exception {
        byte[] pdfData = null;

        if (part.isMimeType("multipart/*")) {
            Multipart multipart = (Multipart) part.getContent(); //复杂体邮件
            //复杂体邮件包含多个邮件体
            int partCount = multipart.getCount();
            for (int i = 0; i < partCount; i++) {
                //获得复杂体邮件中其中一个邮件体
                BodyPart bodyPart = multipart.getBodyPart(i);
                //某一个邮件体也有可能是由多个邮件体组成的复杂体
                String disp = bodyPart.getDisposition();
                if (disp != null && (disp.equalsIgnoreCase(Part.ATTACHMENT) || disp.equalsIgnoreCase(Part.INLINE))) {
                    pdfData = readPDFData(bodyPart.getInputStream());
                } else if (bodyPart.isMimeType("multipart/*")) {
                    pdfData = saveAttachment(bodyPart);
                } else {
                    String contentType = bodyPart.getContentType();
                    if (contentType.indexOf("name") != -1 || contentType.indexOf("application") != -1) {
                        pdfData = readPDFData(bodyPart.getInputStream());
                    }
                }
            }
        } else if (part.isMimeType("message/rfc822")) {
            pdfData = saveAttachment((Part) part.getContent());
        }

        return pdfData;
    }

    /**
     * 读取 PDF 附件的二进制数据
     */
    private byte[] readPDFData(InputStream inputStream) throws Exception {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        byte[] buffer = new byte[4096];
        int bytesRead;

        while ((bytesRead = inputStream.read(buffer)) != -1) {
            byteArrayOutputStream.write(buffer, 0, bytesRead);
        }

        return byteArrayOutputStream.toByteArray();
    }

    /**
     * 获得邮件文本内容
     */
    public void getHtmlMailContent(Part part, StringBuilder htmlContent) throws MessagingException, IOException {
        /*如果还想把文本内容拼接到邮件内容上就将html替换为**/
        if (part.isMimeType("text/html")) {
            // 如果是 HTML 类型的部分，将其内容添加到 StringBuilder 中
            htmlContent.append(part.getContent().toString());
        } else if (part.isMimeType("message/rfc822")) {
            // 如果是嵌套消息，则递归处理嵌套消息中的 HTML 邮件正文
            getHtmlMailContent((Part) part.getContent(), htmlContent);
        } else if (part.isMimeType("multipart/*")) {
            Multipart multipart = (Multipart) part.getContent();
            int partCount = multipart.getCount();
            for (int i = 0; i < partCount; i++) {
                BodyPart bodyPart = multipart.getBodyPart(i);
                // 递归处理多部分内容，查找 HTML 邮件正文
                getHtmlMailContent(bodyPart, htmlContent);
            }
        }
    }

}

