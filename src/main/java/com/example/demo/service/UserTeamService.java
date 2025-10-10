package com.example.demo.service;

import com.example.demo.dto.user.InsertUserTeamDTO;
import com.example.demo.dto.user.PageSelectUserTeamDTO;
import com.example.demo.entiy.UserTeam;
import com.example.demo.result.PageResult;
import com.example.demo.result.Result;


import java.util.List;

public interface UserTeamService {
    /**
     * 新增用户与团队的关联关系
     * @param insertUserTeamDTO 用户团队关联信息DTO对象
     * @return 操作结果信息
     */
    String insertUserTeam(InsertUserTeamDTO insertUserTeamDTO);

    /**
     * 批量删除用户与团队的关联关系
     * @param ids 待删除关联记录的ID列表
     * @return 操作结果对象
     */
    Result deleteUserTeam(List<Long> ids);

    /**
     * 分页查询用户与团队的关联关系
     * @param pageSelectUserTeamDTO 分页查询条件DTO对象
     * @return 分页查询结果，包含用户团队关联列表及分页信息
     */
    PageResult<UserTeam> pageSelectUserTeam(PageSelectUserTeamDTO pageSelectUserTeamDTO);
}
