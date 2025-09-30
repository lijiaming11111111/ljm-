package com.example.demo.entiy;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.example.demo.enumerate.Status;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Mail {

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    private String sendEmail;

    private String recipientEmail;

    private Date sendDate;

    private Status status;

    private String content;
}
