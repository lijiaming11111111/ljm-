package com.example.demo.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.demo.dto.user.*;
import com.example.demo.entiy.User;
import com.example.demo.result.PageResult;
import com.example.demo.result.Result;
import com.example.demo.vo.user.PageUserVO;
import com.example.demo.vo.user.UserLoginVO;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface UserService extends IService<User> {
    /**
     * 新增用户
     *
     * @param  addUserDTO
     * @return
     */
    String addUser(AddUserDTO addUserDTO, MultipartFile face) throws IOException;

    /**
     * 批量删除用户
     *
     * @param  ids
     * @return
     */
    Result deleteUser(List<Long>ids);

    /**
     * 登录
     *
     * @param  dto
     * @return
     */
    UserLoginVO login(UserLoginDTO dto) throws JsonProcessingException;

    /**
     * 发送验证码
     *
     * @param  dto
     * @return
     */
    Result sendVerificationCode(SendVerificationCodeDTO dto);

    /**
     * 忘记密码
     *
     * @param dto
     */
    void   forgetPassword(ForgetPasswordDTO dto);

    /**
     * 修改密码
     *
     * @param  dto
     * @return
     */
    Result updatePassword(UpdatePasswordDTO dto);

    /**
     * 分页查询邮件历史记录
     *
     * @param dto
     * @return
     */
    PageResult<PageUserVO>pageUser(PageUserDTO dto);

    /**
     * 修改用户
     *
     * @param dto
     * @param face
     * @return
     */
    Result updateUser(UpdateUserDTO dto,MultipartFile face) throws IOException;
}
