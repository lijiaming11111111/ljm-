package com.example.demo.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.demo.context.BaseContext;
import com.example.demo.dto.team.AddTeamMailDTO;
import com.example.demo.dto.team.InsertTeamDTO;
import com.example.demo.dto.team.PageSelectTeamDTO;
import com.example.demo.entiy.Mail;
import com.example.demo.entiy.Team;
import com.example.demo.entiy.User;
import com.example.demo.entiy.UserTeam;
import com.example.demo.enumerate.Status;
import com.example.demo.mapper.MailMapper;
import com.example.demo.mapper.TeamMapper;
import com.example.demo.mapper.UserMapper;
import com.example.demo.mapper.UserTeamMapper;
import com.example.demo.result.PageResult;
import com.example.demo.result.Result;
import com.example.demo.service.TeamService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
public class TeamImpl implements TeamService {

    @Autowired
    private JavaMailSender mailSender;

    private final MailMapper mailMapper;

    private final TeamMapper teamMapper;

    private final UserMapper userMapper;

    private final UserTeamMapper userTeamMapper;

    @Override
    public String insertTeam(InsertTeamDTO insertTeamDTO) {
        //创建对象
        Team team = new Team();
        team.setId(IdWorker.getId());
        team.setTeamName(insertTeamDTO.getTeamName());
        team.setTeamIntroduction(insertTeamDTO.getTeamIntroduction());
        team.setContactInformation(insertTeamDTO.getContactInformation());
        team.setAddress(insertTeamDTO.getAddress());
        teamMapper.insert(team);
        return "新增成功";
    }

    @Override
    public String insertTeamMail(AddTeamMailDTO addTeamMailDTO) {
        User user=userMapper.selectById(BaseContext.getCurrentUserId());
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        List<UserTeam> userTeams=userTeamMapper.selectList(new QueryWrapper<UserTeam>().in("team_id", addTeamMailDTO.getId()));
        // 根据团队ID，提取 用户ID 并返回
        List<Long> userId=userTeams.stream().map(UserTeam::getUserId).collect(Collectors.toList());
        List<User> userList = userMapper.selectList(queryWrapper.in("id", userId));
        // 根据用户ID,提取 用户ID所对应的邮箱
        List<String> emails= userList.stream().map(User::getMail).collect(Collectors.toList());

        //批量发送邮件
        for (String email : emails) {
            Mail mail = new Mail();
            mail.setId(IdWorker.getId());
            mail.setSendEmail(user.getMail());
            mail.setSendDate(new Date());
            mail.setContent(addTeamMailDTO.getContent());

            //尝试发送邮件
            try {
                //发送邮件成功，生成对应的数据库内容
                SimpleMailMessage message = new SimpleMailMessage();
                message.setFrom(user.getMail());
                message.setTo(email);
                message.setText(addTeamMailDTO.getContent());
                mailSender.send(message);
                //修改状态
                mail.setStatus(Status.SUCCEED);
                mail.setRecipientEmail(email);
                mailMapper.insert(mail);
            }catch (Exception e){
                //异常处理
                mail.setStatus(Status.FAILED);
                mail.setRecipientEmail(email);
                mailMapper.insert(mail);
            }
        }
        return "批量新增成功";
    }

    @Override
    public PageResult<Team> pageSelectTeam(PageSelectTeamDTO pageSelectTeamDTO) {
        //创建分页对象，指定页码和每页大小
        Page<Team> page = new Page<>(pageSelectTeamDTO.getPage(), pageSelectTeamDTO.getPageSize());
        //创建 Lambda 形式的查询条件构造器
        LambdaQueryWrapper<Team> queryWrapper = new LambdaQueryWrapper<>();
        //模糊查询团队名称
        queryWrapper.like(StringUtils.isNotBlank(pageSelectTeamDTO.getTeamName()),
                Team::getTeamName, pageSelectTeamDTO.getTeamName());
        //模糊查询团队介绍
        queryWrapper.like(StringUtils.isNotBlank(pageSelectTeamDTO.getTeamIntroduction()),
                Team::getTeamIntroduction, pageSelectTeamDTO.getTeamIntroduction());
        //模糊查询联系方式
        queryWrapper.like(StringUtils.isNotBlank(pageSelectTeamDTO.getContactInformation()),
                Team::getContactInformation, pageSelectTeamDTO.getContactInformation());
        //模糊查询地址
        queryWrapper.like(StringUtils.isNotBlank(pageSelectTeamDTO.getAddress()),
                Team::getAddress, pageSelectTeamDTO.getAddress());
        Page<Team>result=teamMapper.selectPage(page,queryWrapper);
        return new PageResult<>(result.getTotal(),result.getRecords());
    }

    @Override
    public Result deleteTeam(List<Long> ids) {
        List<Team> team=teamMapper.selectBatchIds(ids);
        //判断查询的id与表中id是否存在
        if (ids.size()==team.size()) {
            teamMapper.deleteBatchIds(ids);
            return Result.success("删除成功",null);
        }
        return Result.error("删除失败");
    }

}
