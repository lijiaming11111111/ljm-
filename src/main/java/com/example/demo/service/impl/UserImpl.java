package com.example.demo.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.demo.bo.UserLoginData;
import com.example.demo.bo.UserLoginVerifyData;
import com.example.demo.dto.user.*;
import com.example.demo.entiy.File;
import com.example.demo.entiy.User;
import com.example.demo.enumerate.StatusEnum;
import com.example.demo.exception.BaseException;
import com.example.demo.mapper.FileMapper;
import com.example.demo.mapper.UserMapper;
import com.example.demo.redis.RedisCode;
import com.example.demo.redis.RedisPrefix;
import com.example.demo.result.PageResult;
import com.example.demo.result.Result;
import com.example.demo.service.UserService;
import com.example.demo.util.CodeUtil;
import com.example.demo.util.FileUtil;
import com.example.demo.util.JwtUtil;
import com.example.demo.util.SaltUtil;
import com.example.demo.vo.file.FileDataVO;
import com.example.demo.vo.file.FileUrlVO;
import com.example.demo.vo.user.PageUserVO;
import com.example.demo.vo.user.UserLoginVO;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.Duration;
import java.util.*;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class UserImpl extends ServiceImpl<UserMapper, User>implements UserService {
    private final UserMapper userMapper;

    private final RedisTemplate<String, String> redisTemplate;

    private final ObjectMapper objectMapper;

    @Value("${jwt.secretKey}")
    private String jwtSecretKey;

    @Value("${jwt.expiration}")
    private Long jwtExpiration;

    @Autowired
    private JavaMailSenderImpl javaMailSender;

    @Value("${spring.mail.username}")
    private String sendMailer;

    // 保存收件人邮箱
    private String mail;

    private final FileUtil fileUtil;

    private final FileMapper fileMapper;
    /**
     * 新增用户
     *
     * @param addUserDTO
     * @return
     */
    @Override
    public String addUser(AddUserDTO addUserDTO,MultipartFile face) throws IOException {

        //创建用户
        User user = new User();
        BeanUtils.copyProperties(addUserDTO,user);
        user.setId(IdWorker.getId());
        user.setStatusEnum(StatusEnum.START);

        QueryWrapper<User>queryWrapper=new QueryWrapper<>();
        queryWrapper.eq("mail",addUserDTO.getMail());
        Long count=userMapper.selectCount(queryWrapper);
        if (count!=0){
            throw new BaseException("邮箱已存在");
        }

        QueryWrapper<User>mobileQueryWrapper=new QueryWrapper<>();
        mobileQueryWrapper.eq("mobile",addUserDTO.getMobile());
        Long mobileCount=userMapper.selectCount(queryWrapper);
        if (mobileCount!=0){
            throw new BaseException("手机号已存在");
        }
        //调用获取上传文件预签名接口，获取上传预签名地址，和文件唯一ID
        FileUrlVO vo=fileUtil.url(face);
        //通过预签名上传文件
        fileUtil.uploadFileUrl(String.valueOf(vo.getUrl()),face);
        //用户的头像字段使用第一步获取的文件ID
        user.setFace(vo.getId());
        //生成盐值和加密密码
        String salt= SaltUtil.generateSalt(16);
        user.setSalt(salt);
        String password=addUserDTO.getPassword()+salt;
        user.setPassword(DigestUtils.md5DigestAsHex(password.getBytes()));
        if (userMapper.insert(user)!=1){
            throw new BaseException("新增失败");
        }
        return user.getId().toString();
    }

    /**
     * 批量删除用户
     *
     * @param  ids
     * @return
     */
    @Override
    public Result deleteUser(List<Long> ids) {
        List<User>userList=userMapper.selectBatchIds(ids);
        //判断删除的邮件历史记录id是否存在
        if (ids.size()== userList.size()){
            userMapper.deleteBatchIds(ids);
            return Result.success("删除成功");
        }else {
            return Result.error("删除失败，邮件不存在");
        }
    }

    /**
     * 登录
     *
     * @param  dto
     * @return
     */
    @Override
    public UserLoginVO login(UserLoginDTO dto) throws JsonProcessingException {
        UserLoginVerifyData user = userMapper.getUserLoginDataByAccount(dto.getMail());
        if (user == null){
            throw new BaseException("用户不存在");
        }
        // 创建用户登录验证数据对象，用于处理登录验证相关信息
        UserLoginVerifyData data=new UserLoginVerifyData();
        BeanUtils.copyProperties(user,data);
        // 构建待加密的密码字符串：用户输入的密码 + 数据库中存储的盐值
        String password=dto.getPassword()+data.getSalt();
        // 对密码进行MD5加密处理
        password=DigestUtils.md5DigestAsHex(password.getBytes());
        // 验证加密后的密码与数据库中存储的密码是否一致
        if (!password.equals(user.getPassword())) {
            //密码错误
            throw new BaseException("密码错误");
        }
        // 创建JWT声明（claims）对象，用于存储自定义负载信息
        Map<String, Object> claims = new HashMap<>();
        // 将用户ID存入声明中，用于生成令牌
        claims.put("id", data.getId());
        //生成token
        String token = JwtUtil.createJWT(
                jwtSecretKey,
                jwtExpiration * 3600 * 1000,
                claims);
        //返回用户信息
        UserLoginData userLoginData = new UserLoginData();
        userLoginData.setId(user.getId());
        userLoginData.setToken(token);
        userLoginData.setRoleIds(user.getRoleIds());
        redisTemplate.opsForValue().set(RedisPrefix.USER_LOGIN_DATA.getPrefix() + user.getId(), objectMapper.writeValueAsString(userLoginData), jwtExpiration, TimeUnit.HOURS);
        return UserLoginVO
                .builder()
                .id(data.getId())
                .userName(data.getUserName())
                .token(token).build();
    }

    @Override
    public Result sendVerificationCode(SendVerificationCodeDTO dto) {
        SimpleMailMessage message = new SimpleMailMessage();
        // 生成随机验证码
        String code = CodeUtil.generateCode(6);
        // 构建邮件内容，包含验证码信息和提示
        String text = "您的验证码为：" + code + ",请勿泄露给他人。";
        // 设置邮件发送者（发件人邮箱
        message.setFrom(sendMailer);
        // 设置邮件接收者
        message.setTo(dto.getMail());
        // 设置邮件正文内容
        message.setText(text);
        // 设置邮件发送时间为当前时间
        message.setSentDate(new Date());
        // 设置邮件主题
        message.setSubject("登录验证码");
        // 保存收件人邮箱
        mail=dto.getMail();
        try {
            javaMailSender.send(message);
            redisTemplate.opsForValue().set(RedisCode.CODE.getPrefix()+ mail, String.valueOf(code), Duration.ofMinutes(5));
            return Result.success("发送成功",null);

        }catch (Exception e){
            return Result.error("发送失败");
        }

    }
    /**
     * 忘记密码
     *
     * @param dto
     */
    @Override
    public void forgetPassword(ForgetPasswordDTO dto) {
        String key = dto.getMail() != null ? dto.getMail() : dto.getMobile();
        if (CodeUtil.checkCode(key, dto.getCode())) {
            QueryWrapper<User>queryWrapper=new QueryWrapper<>();
            queryWrapper.eq("mail",dto.getMail());
            User oldUser=userMapper.selectOne(queryWrapper);
            //判断用户是否存在，不存在则返回错误信息
            if (oldUser==null){
                throw new BaseException("用户不存在");
            }
            String salt = SaltUtil.generateSalt(16);
            oldUser.setSalt(salt);
            String newPassword = dto.getNewPassword() + salt;
            oldUser.setPassword(DigestUtils.md5DigestAsHex(newPassword.getBytes()));
            User newUser = new User();
            BeanUtils.copyProperties(oldUser,newUser);
            userMapper.updateById(newUser);
        }
    }

    /**
     * 修改密码
     *
     * @param  dto
     * @return
     */
    @Override
    public Result updatePassword(UpdatePasswordDTO dto) {
        QueryWrapper<User>queryWrapper=new QueryWrapper<>();
        // 设置查询条件：根据邮箱查询用户
        queryWrapper.eq("mail",dto.getMail());
        //  根据邮箱查询用户信息
        User oldUser=userMapper.selectOne(queryWrapper);
        //判断用户是否存在，不存在则返回错误信息
        if (oldUser==null){
            return Result.error("用户不存在");
        }
        //生成16位随机盐值，用于密码加密
        String salt = SaltUtil.generateSalt(16);
        //将盐值设置到用户对象中
        oldUser.setSalt(salt);
        //构建新密码：新密码明文 + 盐值
        String newPassword = dto.getNewPassword() + salt;
        //对新密码进行MD5加密处理，并设置到用户对象中
        oldUser.setPassword(DigestUtils.md5DigestAsHex(newPassword.getBytes()));
        User newUser = new User();
        BeanUtils.copyProperties(oldUser,newUser);
        userMapper.updateById(newUser);
        return Result.success("修改成功 ");
    }

    /**
     * 分页查询邮件历史记录
     *
     * @param dto
     * @return
     */
    @Override
    public PageResult<PageUserVO> pageUser(PageUserDTO dto) {
        PageHelper.startPage(dto.getPage(), dto.getPageSize());
        Page<PageUserVO> page=userMapper.pageUser(dto);
        List<PageUserVO>vos=page.getResult();
                for (PageUserVO vo:vos){
            FileDataVO fileDataVO=new FileDataVO();
            File file=userMapper.selectFileName(vo.getFace());
            fileDataVO.setId(file.getId());
            fileDataVO.setName(file.getFileName());
            String url=fileUtil.generateDownloadUrl(file.getObjectName());
            fileDataVO.setUrl(url);
            vo.setFaceUrl(fileDataVO);
        }
                page.setTotal(vos.size());
        return new PageResult<>(page.getTotal(),page.getResult());
    }

    /**
     * 修改用户
     *
     * @param dto
     * @param face
     * @return
     */
    @Override
    public Result updateUser(UpdateUserDTO dto, MultipartFile face) throws IOException {
        QueryWrapper<User>queryWrapper=new QueryWrapper<>();
        queryWrapper.eq("id",dto.getId());
        User oldUser=userMapper.selectOne(queryWrapper);
        if (oldUser==null){
            return Result.error("用户不存在");
        }
        User user=new User();
        BeanUtils.copyProperties(oldUser,user);
        BeanUtils.copyProperties(dto,user);
        QueryWrapper<User>oldQueryWrapper=new QueryWrapper<>();
        oldQueryWrapper.eq("id",dto.getId());
        if (face!=null){
            File file=userMapper.selectFileName(user.getFace());
            fileUtil.deleteFile(file.getObjectName());
            String objectName=fileUtil.uploadFile(face);
            user.setFace(fileMapper.selectFileId(objectName));
        }
        userMapper.update(user,oldQueryWrapper);
        return Result.success("修改成功",null);
    }
}
