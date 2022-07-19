package com.imooc.mall.service.impl;

import com.imooc.mall.exception.ImoocMailExceptionEnum;
import com.imooc.mall.exception.ImoocMallException;
import com.imooc.mall.model.dao.UserMapper;
import com.imooc.mall.model.pojo.User;
import com.imooc.mall.service.UserService;
import com.imooc.mall.util.MD5Utils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.security.NoSuchAlgorithmException;

@Service("userService")
public class UserServiceImpl implements UserService {
    @Autowired
    private UserMapper userMapper;
    @Override
    public User getUser() {
        return userMapper.selectByPrimaryKey(1);
    }

    @Override
    public void register(String userName, String password) throws ImoocMallException {
        // 查询用户名是否存在
        User result = userMapper.selectByName(userName);
        if(result != null){ // 对象已经存在
            throw new ImoocMallException(ImoocMailExceptionEnum.NAME_DUPLICATED);
        }
        // 把信息写入数据库
        User user = new User();
        try{
            user.setPassword(MD5Utils.getMd5String(password));
        } catch (Exception e){
            e.printStackTrace();
        }
        user.setUsername(userName);
        int count = userMapper.insertSelective(user);
        if(count == 0){ // 插入失败
            throw new ImoocMallException(ImoocMailExceptionEnum.INSERT_FAILED);
        }
    }

    @Override
    public User login(String userName, String password) throws ImoocMallException {
        String md5Password = "";
        try {
            md5Password = MD5Utils.getMd5String(password);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
        User user = userMapper.selectLogin(userName, md5Password);
        if (user == null) {
            throw new ImoocMallException(ImoocMailExceptionEnum.WRONG_PASSWORD);
        }
        return user;
    }

    /**
     * 更新个性签名
     * @param user
     */
    @Override
    public void updateInformation(User user) throws ImoocMallException {
        int count = userMapper.updateByPrimaryKeySelective(user);
        if(count > 1){
            throw new ImoocMallException(ImoocMailExceptionEnum.UPDATE_FAILED);
        }
    }

    @Override
    public boolean checkAdminRole(User user){
        // 1 是普通用户, 2 是管理员
        return user.getRole().equals(2);
    }
}
