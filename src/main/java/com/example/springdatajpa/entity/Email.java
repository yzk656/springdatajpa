package com.example.springdatajpa.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.Date;
import java.util.List; // 引入List

@Getter
@Setter
@Entity
public class Email {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 邮件标题
     */
    @Column(name = "title")
    private String title;

    /**
     * 邮件发送人
     */
    @Column(name = "sender")
    private String sender;

    /**
     * 邮件内容
     */
    @Column(name = "content")
    @Lob
    private String content;

    /**
     * 邮件发送时间
     */
    @Column(name = "createTime", updatable = false)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date createTime;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "email") // 使用OneToMany来表示一封邮件可以有多个附件
    private List<EmailAttachment> attachments; // 存储多个PDF附件的二进制数据的集合
}
