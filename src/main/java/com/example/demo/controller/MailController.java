package com.example.demo.controller;

import com.example.demo.dto.mail.DeleteMailDTO;
import com.example.demo.dto.mail.MailSendDTO;
import com.example.demo.dto.mail.PageMailDTO;
import com.example.demo.entiy.Mail;
import com.example.demo.result.PageResult;
import com.example.demo.result.Result;
import com.example.demo.service.MailService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/email")
@CrossOrigin
@Slf4j
@RequiredArgsConstructor
@Validated
@Tag(name = "邮件管理")
public class MailController {

    private final MailService mailService;


    /**
     * 发送邮件
     *
     * @param  dto
     * @return
     */
    @PostMapping("/sendTextMail")
    @Operation(summary = "发送邮件")
    public Result sendTextMail(@RequestBody MailSendDTO dto){
        return mailService.sendTextMailMessage(dto);
    }

    /**
     * 分页查询邮件历史记录
     *
     * @param  dto
     * @return
     */
    @PostMapping("/pageMailMessage")
    @Operation(summary = "分页查询邮件历史记录")
    public Result<PageResult<Mail>> pageMailMessage(@RequestBody PageMailDTO dto){
        PageResult<Mail>pageResult=mailService.pageMailMessage(dto);
        return Result.success("查询成功",pageResult);
    }

    /**
     * 批量删除邮件历史记录
     *
     * @param  dto
     * @return
     */
    @PostMapping("/deleteMail")
    @Operation(summary = "批量删除邮件历史记录")
    public Result deleteMail(@RequestBody DeleteMailDTO dto){
        return mailService.deleteMail(dto.getIdlist());
    }
}
