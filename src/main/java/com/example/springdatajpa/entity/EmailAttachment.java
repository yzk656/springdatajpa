package com.example.springdatajpa.entity;

import javax.persistence.*;
import lombok.*;

@Getter
@Setter
@Entity
public class EmailAttachment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Lob
    private byte[] data; // 存储PDF附件的二进制数据

    @ManyToOne
    @JoinColumn(name = "email_id")
    private Email email; // 关联到邮件的外键
}
