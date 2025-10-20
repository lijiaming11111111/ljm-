package com.example.demo.mapper;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.demo.entiy.File;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface FileMapper extends BaseMapper<File> {
    /**
     * 根据文件对象名称（objectName）查询文件记录的 ID
     */
    @Select("select id from file where object_name=#{objectName} ")
    Long selectFileId(String objectName);
}
