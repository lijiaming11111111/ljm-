package com.example.demo.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.demo.dto.MailSendDTO;
import com.example.demo.dto.PageMailDTO;
import com.example.demo.entiy.Mail;
import com.example.demo.enumerate.Status;
import com.example.demo.mapper.MailMapper;
import com.example.demo.result.PageResult;
import com.example.demo.result.Result;
import com.example.demo.service.MailService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;


@Service
@RequiredArgsConstructor
public class MailServiceImpl extends ServiceImpl<MailMapper, Mail> implements MailService {
    private final MailMapper mailMapper;

    @Autowired
    private JavaMailSenderImpl javaMailSender;

    @Value("${spring.mail.username}")
    private String sendMailer;

    /**
     * 发送邮件
     *
     * @param dto
     */
    @Override
    public Result sendTextMailMessage(MailSendDTO dto) {
        SimpleMailMessage message = new SimpleMailMessage();

        //邮件字段赋值
        Mail mail =new Mail();
        mail.setId(IdWorker.getId());
        mail.setSendEmail(sendMailer);
        mail.setSendDate(new Date());
        mail.setContent(dto.getContent());
        mail.setRecipientEmail(dto.getRecipientEmail());
        message.setFrom(sendMailer);
        message.setTo(dto.getRecipientEmail());
        message.setText(dto.getContent());

        //判断是否发送失败
        try {
            javaMailSender.send(message);
            mail.setStatus(Status.SUCCEED);
            mailMapper.insert(mail);
            return Result.success("发送成功");
        }catch (Exception e){
            mail.setStatus(Status.FAILED);
            mailMapper.insert(mail);
            return Result.error("发送失败");
        }

    }

    /**
     * 分页查询邮件历史记录
     *
     * @param  dto
     * @return
     */
    @Override
    public PageResult<Mail> pageMailMessage(PageMailDTO dto) {
        Page<Mail>page=new Page<>(dto.getPage(),dto.getPageSize());

        LambdaQueryWrapper<Mail>queryWrapper=new LambdaQueryWrapper<>();

        //当模糊查询条件的sendEmail不为空的时候
        queryWrapper.like(StringUtils.isNotBlank(dto.getSendEmail()),
                Mail::getSendEmail,dto.getSendEmail());

        //当模糊查询条件的RecipientEmail不为空的时候
        queryWrapper.like(StringUtils.isNotBlank(dto.getRecipientEmail()),
                Mail::getRecipientEmail,dto.getRecipientEmail());

        //当模糊查询条件的content不为空的时候
        queryWrapper.like(StringUtils.isNotBlank(dto.getContent()),
                Mail::getContent,dto.getContent());

        //当模糊查询条件的status不为空的时候
        queryWrapper.eq(dto.getStatus()!=null,Mail::getStatus,dto.getStatus());
        Page<Mail>result=mailMapper.selectPage(page,queryWrapper);
        return new PageResult<>(result.getTotal(),result.getRecords());

    }

    /**
     * 批量删除邮件历史记录
     *
     * @param  ids
     * @return
     */
    @Override
    public Result deleteMail(List<Long> ids) {
        List<Mail>mailList=mailMapper.selectBatchIds(ids);

        //判断删除的邮件历史记录id是否存在
        if (ids.size()== mailList.size()){
            mailMapper.deleteBatchIds(ids);
            return Result.success("删除成功");
        }else {
            return Result.error("删除失败，邮件不存在");
        }
    }
}
