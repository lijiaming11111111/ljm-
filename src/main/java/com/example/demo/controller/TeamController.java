package com.example.demo.controller;

import com.example.demo.dto.team.AddTeamMailDTO;
import com.example.demo.dto.team.DeleteTeamDTO;
import com.example.demo.dto.team.InsertTeamDTO;
import com.example.demo.dto.team.PageSelectTeamDTO;
import com.example.demo.entiy.Team;
import com.example.demo.result.PageResult;
import com.example.demo.result.Result;
import com.example.demo.service.TeamService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/team")
@CrossOrigin
@Slf4j
@RequiredArgsConstructor
@Tag(name = "团队接口")
@Validated
public class TeamController {

    private final TeamService teamService;

    /**
     * team 新增的团队信息
     * @param insertTeamDTO 包含新增团队信息的数据传输对象
     * @return 新增团队操作结果
     */
    @PostMapping("/addTeam")
    @Operation(summary = "新增团队")
    public Result<String> addTeam(@RequestBody InsertTeamDTO insertTeamDTO) {
        String team=teamService.insertTeam(insertTeamDTO);
        return Result.success(team,null);
    }

    /**
     * 批量发送团队邮件
     * @param addTeamMailDTO 包含批量发送团队邮件信息的数据传输对象
     * @return 批量发送团队邮件操作结果
     */
    @PostMapping("/addTeamMail")
    @Operation(summary = "批量发送团队邮件")
    public Result<String> addTeamMail(@RequestBody AddTeamMailDTO addTeamMailDTO) {
        String team=teamService.insertTeamMail(addTeamMailDTO);
        return Result.success(team,null);
    }

    /**
     * 批量删除团队
     * @param deleteTeamDTO 包含要删除团队标识的数据传输对象
     * @return 批量删除团队操作结果
     */
    @PostMapping("/deleteTeam")
    @Operation(summary = "批量删除团队")
    public Result deleteUserTeam(@RequestBody DeleteTeamDTO deleteTeamDTO) {
        return teamService.deleteTeam(deleteTeamDTO.getIds());
    }

    /**
     * 分页查询团队
     * @param pageSelectTeamDTO 包含分页查询团队条件的数据传输对象
     * @return 分页查询到的团队结果
     */
    @PostMapping("/pageSelectTeam")
    @Operation(summary = "分页查询团队")
    public Result<PageResult<Team>> pageSelectUserTeam(@RequestBody PageSelectTeamDTO pageSelectTeamDTO) {
        PageResult<Team> vo =teamService.pageSelectTeam(pageSelectTeamDTO);
        return Result.success("查询成功",vo);
    }
}
