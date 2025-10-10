package com.example.demo.controller;

import com.example.demo.dto.user.*;
import com.example.demo.entiy.User;
import com.example.demo.result.PageResult;
import com.example.demo.result.Result;
import com.example.demo.service.UserService;
import com.example.demo.vo.UserLoginVO;
import com.fasterxml.jackson.core.JsonProcessingException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user")
@CrossOrigin
@Slf4j
@RequiredArgsConstructor
@Validated
@Tag(name = "用户管理")
public class UserController {

    private final UserService userService;

    /**
     * 新增用户
     *
     * @param  dto
     * @return
     */
    @PostMapping("/addUser")
    @Operation(summary = "新增用户")
    public Result<String>addUser(@Valid @RequestBody AddUserDTO dto){
        return Result.success("新增成功",userService.addUser(dto));
    }

    /**
     * 批量删除用户
     *
     * @param  dto
     * @return
     */
    @PostMapping("/deleteUser")
    @Operation(summary = "批量删除用户")
    public Result deleteUser(@RequestBody DeleteUserDTO dto){
         return userService.deleteUser(dto.getIdlist());
    }

    /**
     * 登录
     *
     * @param  dto
     * @return
     */
    @PostMapping("/login")
    @Operation(summary = "登录")
    public Result<UserLoginVO> login(@Valid @RequestBody UserLoginDTO dto) throws JsonProcessingException {
        return Result.success("登陆成功",userService.login(dto));
    }

    /**
     * 发送验证码
     *
     * @param  dto
     * @return
     */
    @PostMapping("/sendVerificationCode")
    @Operation(summary = "发送验证码")
    public Result sendVerificationCode(@RequestBody SendVerificationCodeDTO dto){
        return userService.sendVerificationCode(dto);
    }

    /**
     * 验证码验证
     *
     * @param  dto
     * @return
     */
    @PostMapping("/verificationCodeValidation")
    @Operation(summary = "验证码验证")
    public Result verificationCodeValidation(@RequestBody VerificationCodeValidationDTO dto){
        return userService.verificationCodeValidation(dto);
    }

    /**
     * 忘记密码
     *
     * @param  dto
     * @return
     */
    @PostMapping("/forgetPassword")
    @Operation(summary = "忘记密码")
    public Result forgetPassword(@RequestBody ForgetPasswordDTO dto){
        return userService.forgetPassword(dto);
    }

    /**
     * 修改密码
     *
     * @param  dto
     * @return
     */
    @PostMapping("/updatePassword")
    @Operation(summary = "修改密码")
    public Result updatePassword(@RequestBody UpdatePasswordDTO dto){
        return userService.updatePassword(dto);
    }

    /**
     * 分页查询用户
     *
     * @param  dto
     * @return
     */
    @PostMapping("/pageUser")
    @Operation(summary = "分页查询用户")
    public Result<PageResult<User>>pageUser(@RequestBody PageUserDTO dto){
        PageResult<User>pageResult=userService.pageUser(dto);
        return Result.success("查询成功",pageResult);
    }

    /**
     * 修改用户
     *
     * @param  updateUserDTO
     * @return
     */
    @PostMapping("/updateUser")
    @Operation(summary = "修改用户")
    public Result updateUser(@RequestBody UpdateUserDTO updateUserDTO){
        return userService.updateUser(updateUserDTO);
    }
}
