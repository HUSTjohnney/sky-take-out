package com.sky.mapper;

import com.github.pagehelper.Page;
import com.sky.dto.EmployeePageQueryDTO;
import com.sky.entity.Employee;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper // 用Mapper注解标记这是一个MyBatis的Mapper接口
public interface EmployeeMapper {

    /**
     * 根据用户名查询员工
     * 
     * @param username
     * @return
     */
    @Select("select * from employee where username = #{username}") // 用Select注解指定SQL语句，也可以用XML来进行SQL语句的编写，这里是一个简单的例子。
    Employee getByUsername(String username);

    /**
     * 新建一个员工的信息，直接基于注解来写SQL语句
     * 
     * @param employee
     */
    @Insert("insert into employee(name, username,password,phone,sex,id_number,create_time,update_time,create_user,update_user)"
            + "values"
            + "(#{name},#{username},#{password},#{phone},#{sex},#{idNumber},#{createTime},#{updateTime},#{createUser},#{updateUser})")
    void insert(Employee employee);

    /**
     * 数据库查询方法，分页查询员工；比较复杂，所以用XML来写SQL语句
     * 
     * @param employeePageQueryDTO
     * @return
     */
    Page<Employee> pageQuery(EmployeePageQueryDTO employeePageQueryDTO);

}
