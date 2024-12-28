package com.sky.service.impl;

import com.fasterxml.jackson.databind.JsonSerializable.Base;
import com.sky.constant.MessageConstant;
import com.sky.constant.PasswordConstant;
import com.sky.constant.StatusConstant;
import com.sky.context.BaseContext;
import com.sky.dto.EmployeeDTO;
import com.sky.dto.EmployeeLoginDTO;
import com.sky.entity.Employee;
import com.sky.exception.AccountLockedException;
import com.sky.exception.AccountNotFoundException;
import com.sky.exception.PasswordErrorException;
import com.sky.mapper.EmployeeMapper;
import com.sky.service.EmployeeService;

import java.sql.Date;
import java.time.LocalDateTime;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

@Service // 用Service注解标记这是在业务逻辑层的类
public class EmployeeServiceImpl implements EmployeeService {

    @Autowired
    private EmployeeMapper employeeMapper;

    /**
     * 员工登录
     *
     * @param employeeLoginDTO
     * @return
     */
    public Employee login(EmployeeLoginDTO employeeLoginDTO) {
        String username = employeeLoginDTO.getUsername();
        String password = employeeLoginDTO.getPassword();

        // 1、根据用户名查询数据库中的数据
        Employee employee = employeeMapper.getByUsername(username);

        // 2、处理各种异常情况（用户名不存在、密码不对、账号被锁定）
        if (employee == null) {
            // 账号不存在
            throw new AccountNotFoundException(MessageConstant.ACCOUNT_NOT_FOUND); // 抛出自定义异常：账号不存在
        }

        // 对前端传过来的明文密码进行md5加密处理，然后和数据库中的密码进行比对。
        // 数据库存密文即可，MD5加密不可逆，谁都不知道明文了，数据库泄露了也不会知道用户名对应的密码是什么。
        password = DigestUtils.md5DigestAsHex(password.getBytes());
        if (!password.equals(employee.getPassword())) {
            // 密码错误
            throw new PasswordErrorException(MessageConstant.PASSWORD_ERROR);
        }

        // 账号状态检查，如果账号被锁定，抛出异常
        if (employee.getStatus() == StatusConstant.DISABLE) {
            throw new AccountLockedException(MessageConstant.ACCOUNT_LOCKED);
        }

        // 3、返回实体对象
        return employee;
    }

    /**
     * 新建员工
     *
     * @param employeeDTO
     */
    @Override
    public void save(EmployeeDTO employeeDTO) {
        Employee employee = new Employee();
        // 对象属性拷贝
        BeanUtils.copyProperties(employeeDTO, employee);

        // 对密码进行MD5加密，默认密码：123456，基于常量类PasswordConstant
        employee.setPassword(DigestUtils.md5DigestAsHex(PasswordConstant.DEFAULT_PASSWORD.getBytes()));

        // 设置账号状态，默认启用为1，禁用为0
        employee.setStatus(StatusConstant.ENABLE);

        // 设置创建时间
        employee.setCreateTime(LocalDateTime.now());

        // 设置更新时间
        employee.setUpdateTime(LocalDateTime.now());

        // 设置创建人ID和设置更新人ID
        Long Userid = BaseContext.getCurrentId();
        employee.setCreateUser(Userid);
        employee.setUpdateUser(Userid);

        BaseContext.removeCurrentId();// 清除当前线程的数据

        // 保存员工，调用Mapper接口的方法到数据库
        employeeMapper.insert(employee);

        // 当前线程的ID
        //System.out.println("当前线程ID：" + Thread.currentThread().getId());
    }

}
