package com.example.demo.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import com.example.demo.dto.user.InsertUserTeamDTO;
import com.example.demo.dto.user.PageSelectUserTeamDTO;
import com.example.demo.entiy.UserTeam;
import com.example.demo.mapper.UserTeamMapper;
import com.example.demo.result.PageResult;
import com.example.demo.result.Result;
import com.example.demo.service.UserTeamService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

import static com.baomidou.mybatisplus.extension.toolkit.Db.saveBatch;

@Service
@RequiredArgsConstructor
public class UserTeamTeamImpl implements UserTeamService {

    private final UserTeamMapper userTeamMapper;

    @Override
    public String insertUserTeam(InsertUserTeamDTO insertUserTeamDTO) {
        // 创建集合用于存储用户与团队的关联关系对象
        List<UserTeam> relList = new ArrayList<>();

        // 遍历所有待关联的团队ID
        for (Long teamId : insertUserTeamDTO.getTeamId()) {
            // 遍历所有待关联的用户ID
            for (Long userId : insertUserTeamDTO.getUserId()) {
                // 创建用户与团队的关联关系对象
                UserTeam rel = new UserTeam();
                // 设置关联的团队ID
                rel.setTeamId(teamId);
                // 设置关联的用户ID
                rel.setUserId(userId);
                // 将关联关系对象添加到集合中
                relList.add(rel);
            }
        }

        saveBatch(relList);
        return "新增成功";
    }

    @Override
    public Result deleteUserTeam(List<Long> ids) {
        List<UserTeam> mail=userTeamMapper.selectBatchIds(ids);
        //判断查询的id与表中id是否存在
        if (ids.size()==mail.size()) {
            userTeamMapper.deleteBatchIds(ids);
            return Result.success("删除成功",null);
        }
        return Result.error("删除失败");
    }

    @Override
    public PageResult<UserTeam> pageSelectUserTeam(PageSelectUserTeamDTO pageSelectUserTeamDTO) {
        // 创建分页对象，指定页码和每页大小
        Page<UserTeam> page = new Page<>(pageSelectUserTeamDTO.getPage(), pageSelectUserTeamDTO.getPageSize());
        // 创建 Lambda 形式的查询条件构造器
        LambdaQueryWrapper<UserTeam> queryWrapper = new LambdaQueryWrapper<>();
        // 构建“团队 ID”模糊查询条件，当团队 ID 列表不为空时生效
        if (pageSelectUserTeamDTO.getTeamId() != null && !pageSelectUserTeamDTO.getTeamId().isEmpty()) {
            queryWrapper.in(UserTeam::getTeamId, pageSelectUserTeamDTO.getTeamId());
        }
        // 构建“用户 ID”模糊查询条件，当用户 ID 列表不为空时生效
        if (pageSelectUserTeamDTO.getUserId() != null && !pageSelectUserTeamDTO.getUserId().isEmpty()) {
            queryWrapper.in(UserTeam::getUserId, pageSelectUserTeamDTO.getUserId());
        }
        // 执行分页查询
        Page<UserTeam> result = userTeamMapper.selectPage(page, queryWrapper);
        // 封装并返回分页结果，包含总记录数和当前页记录列表
        return new PageResult<>(result.getTotal(), result.getRecords());
    }


}
