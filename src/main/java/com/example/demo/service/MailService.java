package com.example.demo.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.demo.dto.MailSendDTO;
import com.example.demo.dto.PageMailDTO;
import com.example.demo.entiy.Mail;
import com.example.demo.result.PageResult;
import com.example.demo.result.Result;

import java.util.List;

public interface MailService extends IService<Mail> {

    Result sendTextMailMessage(MailSendDTO dto);

    PageResult<Mail>pageMailMessage(PageMailDTO dto);

    Result deleteMail(List<Long>ids);
}
