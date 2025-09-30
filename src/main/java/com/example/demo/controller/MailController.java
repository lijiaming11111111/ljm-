package com.example.demo.controller;

import com.example.demo.dto.DeleteMailDTO;
import com.example.demo.dto.MailSendDTO;
import com.example.demo.dto.PageMailDTO;
import com.example.demo.entiy.Mail;
import com.example.demo.result.PageResult;
import com.example.demo.result.Result;
import com.example.demo.service.MailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/email")
@CrossOrigin
@Slf4j
@RequiredArgsConstructor
@Validated
public class MailController {

    private final MailService mailService;

    @PostMapping("/sendTextMail")
    public Result sendTextMail(@RequestBody MailSendDTO dto){
        return mailService.sendTextMailMessage(dto);
    }

    @PostMapping("/pageMailMessage")
    public Result<PageResult<Mail>> pageMailMessage(@RequestBody PageMailDTO dto){
        PageResult<Mail>pageResult=mailService.pageMailMessage(dto);
        return Result.success("查询成功",pageResult);
    }

    @PostMapping("/deleteMail")
    public Result deleteMail(@RequestBody DeleteMailDTO dto){
        return mailService.deleteMail(dto.getIdlist());
    }
}
