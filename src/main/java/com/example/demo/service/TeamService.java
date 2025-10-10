package com.example.demo.service;

import com.example.demo.dto.team.AddTeamMailDTO;
import com.example.demo.dto.team.InsertTeamDTO;
import com.example.demo.dto.team.PageSelectTeamDTO;
import com.example.demo.entiy.Team;
import com.example.demo.result.PageResult;
import com.example.demo.result.Result;

import java.util.List;

public interface TeamService {
    /**
     * 新增团队信息
     * @param insertTeamDTO 团队信息DTO对象
     * @return 操作结果信息
     */
    String insertTeam(InsertTeamDTO insertTeamDTO);

    /**
     * 为团队添加邮件信息
     * @param addTeamMailDTO 团队邮件关联信息DTO对象
     * @return 操作结果信息
     */
    String insertTeamMail(AddTeamMailDTO addTeamMailDTO);

    /**
     * 分页查询团队信息
     * @param pageSelectTeamDTO 分页查询条件DTO对象
     * @return 分页查询结果，包含团队列表及分页信息
     */
    PageResult<Team> pageSelectTeam(PageSelectTeamDTO pageSelectTeamDTO);

    /**
     * 批量删除团队
     * @param ids 待删除团队的ID列表
     * @return 操作结果对象
     */
    Result deleteTeam(List<Long> ids);
}
