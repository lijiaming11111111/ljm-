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

    UserLoginVerifyData getUserLoginDataByAccount(String account);

    int forgetPassword(User user);

    Page<PageUserVO> pageUser(PageUserDTO dto);

    @Select("select * from file where id=#{id}")
    File selectFileName(Long id);
}
