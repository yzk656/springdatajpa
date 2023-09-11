package com.example.springdatajpa.config;

import com.example.springdatajpa.Repository.EmailRepository;
import com.example.springdatajpa.entity.Email;
import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeUtility;
import javax.mail.search.FlagTerm;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Properties;


/**
 * 定时获取邮件信息
 */
@Configuration
@EnableScheduling
public class ScheduledTasksConfig {
    public static List<String> emailTitles = null;

    @Autowired
    private EmailRepository emailRepository;

    @Scheduled(fixedRate = 60000) // 定时任务的执行频率（每60秒执行一次）
    public void myScheduledTask() throws SchedulerException {
        // 在这里编写您的定时任务逻辑
        // 可以是获取邮件、数据处理等操作

        /*存放未读邮件的标题*/
        emailTitles = new ArrayList<>();

        // 邮箱配置
        /*POP3协议*/
//        Properties props = new Properties();
//        props.put("mail.smtp.auth", "true");
//        props.put("mail.smtp.starttls.enable", "true");
//        props.put("mail.smtp.host", "smtp.163.com");
//        props.put("mail.smtp.port", "25");

        /*imap协议*/
        /*可以获取邮件状态信息，即是否被读过*/
        Properties props = new Properties();
        props.put("mail.store.protocol", "imap"); // 使用IMAP协议
        props.put("mail.imap.host", "imap.126.com"); // IMAP服务器地址
        props.put("mail.imap.port", "993"); // IMAP端口（使用SSL）
        /*打开SSL*/
        props.put("mail.imap.ssl.enable", "true");

        // 请将下面的用户名和密码替换为您的实际用户名和密码
        String username = "yzk15325824503@126.com";
        String password = "IMPRDKCGULNMNBCN";

        // 创建会话
        Session session = Session.getInstance(props, new Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(username, password);
            }
        });

        try {
            // 连接到邮箱
            Store store = session.getStore("imap");
            store.connect("imap.126.com", username, password);

            // 打开收件箱
            Folder inbox = store.getFolder("INBOX");
            /*注意你要修改状态，因此要改为读写权限*/
            inbox.open(Folder.READ_WRITE);

            // 等待一段时间，以确保收件箱完全打开（可以根据需要调整等待时间）
            Thread.sleep(2000);

            // 创建搜索条件，过滤未读邮件
            Flags seen = new Flags(Flags.Flag.SEEN);
            FlagTerm unseenFlagTerm = new FlagTerm(seen, false);

            // 获取邮件消息
            Message[] unreadMessages = inbox.search(unseenFlagTerm);
            for (Message message : unreadMessages) {
                String subject = message.getSubject();
                if (subject != null) {
                    /*获取邮件标题*/
                    String title = message.getSubject();
                    // 获取发件人信息
                    String senderName=null;
                    Address[] senders = message.getFrom();
                    if (senders != null && senders.length > 0) {
                        for (Address sender : senders) {
                            senderName = ((InternetAddress) sender).getPersonal();
                            if (senderName != null) {
                                System.out.println("发件人: " + senderName);
                            } else {
                                System.out.println("发件人: " + sender.toString());
                            }
                        }
                    }

                    /*判断标题是否包含关键词*/
                    if (!(title != null && title.contains("发票") && senderName.contains("诺诺") || (title != null && title.contains("发票") && senderName.contains("票通")))) {
                        break;
                    }

                    // 获取邮件内容
                    String content="";
                    String contentType = message.getContentType();
                    if (contentType.contains("text/plain") || contentType.contains("text/html")) {
                        // 邮件内容是纯文本或HTML格式
                        content = message.getContent().toString();
                        System.out.println("邮件内容:\n" + content);
                    } else if (contentType.contains("multipart")) {
                        // 邮件内容是多部分（可能包含附件）
                        Multipart multipart = (Multipart) message.getContent();
                        for (int i = 0; i < multipart.getCount(); i++) {
                            BodyPart bodyPart = multipart.getBodyPart(i);
                            content = bodyPart.getContent().toString();
                            System.out.println("邮件内容:\n" + content);
                        }
                    }
                    /*获取发送时间*/
                    Date sentDate = message.getSentDate();

                    /*进行组装*/
                    Email email = new Email();
                    email.setTitle(title);
                    email.setSender(senderName);
                    email.setContent(content);
                    email.setCreateTime(sentDate);

                    System.out.println(title);
                    System.out.println(senderName);
                    System.out.println(content);
                    System.out.println(sentDate);

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
            throw new RuntimeException(e);
        }

        //打印标题
        for (String emailTitle : emailTitles) {
            System.out.println(emailTitle);
        }


        System.out.println("定时获取邮件任务执行了");
    }

    // 解码编码后的文本
    public static String decodeText(String text) throws Exception {
        if (text == null) {
            return "";
        }
        if (text.startsWith("=?") && text.endsWith("?=")) {
            String[] parts = text.split("\\?");
            if (parts.length >= 5 && "B".equalsIgnoreCase(parts[1])) {
                return new String(MimeUtility.decodeText(parts[3]).getBytes(), parts[2]);
            }
        }
        return text;
    }
}
