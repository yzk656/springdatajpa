package com.example.springdatajpa.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.Date;

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
    @Column(name = "createTime",updatable = false)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date createTime;
}
