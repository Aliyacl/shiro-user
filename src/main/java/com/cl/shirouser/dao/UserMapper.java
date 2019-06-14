package com.cl.shirouser.dao;

import com.cl.shirouser.entity.User;
import com.cl.shirouser.vo.UserVo;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserMapper {
    int deleteByPrimaryKey(Integer userId);

    int insert(User record);

    int insertSelective(User record);

    User selectByPrimaryKey(Integer userId);

    int updateByPrimaryKeySelective(User record);

    int updateByPrimaryKey(User record);

    User getUserByUserName(String username);

    List<User> getUserByDeptId(Integer deptId);

    List<User> getUser(User userCondition);

    int deleteByUserIds(@Param("userIds") List<Integer> userIds);

    String selectUserNameByUserId(Integer userId);

    List<UserVo> getUserVoByDeptId(Integer deptId);

}