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
    /**
     * 根据账号查询
     * @param account
     */
    UserLoginVerifyData getUserLoginDataByAccount(String account);

    /**
     * 更新用户
     * @param user
     */
    int forgetPassword(User user);

    /**
     * @param dto
     * 分页查询用户
     */
    Page<PageUserVO> pageUser(PageUserDTO dto);

    /**
     * @param id
     * 根据文件id查询
     */
    @Select("select * from file where id=#{id}")
    File selectFileName(Long id);
}
