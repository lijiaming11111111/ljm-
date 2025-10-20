package com.example.demo.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.demo.bo.UserLoginVerifyData;
import com.example.demo.dto.user.PageUserDTO;
import com.example.demo.entiy.File;
import com.example.demo.entiy.User;
import com.example.demo.vo.user.PageUserVO;
import com.github.pagehelper.Page;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface UserMapper extends BaseMapper<User> {
    // 1. 根据账号查询用户登录校验数据
    // 返回 UserLoginVerifyData（需提前定义的 DTO，封装登录校验所需字段，如账号、密码等）
    // 常用于登录流程：通过账号查询密码等信息做校验
    UserLoginVerifyData getUserLoginDataByAccount(String account);

    // 2. 忘记密码（重置密码）方法
    // 入参 User 对象，通常包含账号、新密码等信息
    // 返回 int 类型（影响行数，1 表示修改成功，0 表示无匹配记录）
    int forgetPassword(User user);

    // 3. 分页查询用户列表
    // 入参 PageUserDTO（封装分页条件、查询参数，如页码、页大小、用户名等）
    // 返回 Page<PageUserVO>（MyBatis-Plus 的分页对象，包含分页信息 + PageUserVO 数据列表）
    // PageUserVO 是前端展示用的视图对象，按需封装用户信息
    Page<PageUserVO> pageUser(PageUserDTO dto);

    // 4. 根据文件 ID 查询文件信息（注解方式写 SQL）
    // @Select 注解直接写 SQL：从 file 表查询 id 匹配的记录
    // 返回 File 实体类（封装文件表字段，如文件名、路径等）
    @Select("select * from file where id=#{id}")
    File selectFileName(Long id);
}
