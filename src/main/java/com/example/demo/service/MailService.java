package com.example.demo.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.demo.dto.mail.MailSendDTO;
import com.example.demo.dto.mail.PageMailDTO;
import com.example.demo.entiy.Mail;
import com.example.demo.result.PageResult;
import com.example.demo.result.Result;

import java.util.List;

public interface MailService extends IService<Mail> {

    /**
     * 发送邮件
     *
     * @param dto
     */
    Result sendTextMailMessage(MailSendDTO dto);

    /**
     * 分页查询邮件历史记录
     *
     * @param  dto
     * @return
     */
    PageResult<Mail>pageMailMessage(PageMailDTO dto);


    /**
     * 批量删除邮件历史记录
     *
     * @param  ids
     * @return
     */
    Result deleteMail(List<Long>ids);
}
