package weilai.team.officialWebSiteApi.entity.admin.DO;

import com.baomidou.mybatisplus.annotation.*;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import lombok.Data;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import weilai.team.officialWebSiteApi.mapper.admin.UserPermissionMapper;
import weilai.team.officialWebSiteApi.service.admin.impl.LoginServiceImpl;

import javax.annotation.Resource;

/**
 * 用户表
 * @TableName user
 */
@TableName(value ="user")
@Data
public class User implements Serializable, UserDetails {
    /**
     * 主键自增
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 对应钉钉中的用户id
     */
    private String dingUserId;

    /**
     * 学号[账号]
     */
    private String username;

    /**
     * 邮箱
     */
    private String email;

    /**
     * 密码,最大10位
     * 用于替换保留字
     */
    @TableField(value = "`password`")
    private String password;

    /**
     * 姓名 <= 5
     */
    @TableField(value = "`name`")
    private String name;

    /**
     * 性别
     */
    private String sex;

    /**
     * 班级<= 10
     */
    private String clazz;

    /**
     * 年级 <= 5
     */
    private String grade;

    /**
     * 组别<= 5
     */
    @TableField(value = "`group`")
    private String group;

    /**
     * 方向<= 10
     */
    private String direction;

    /**
     * 学号<= 15
     */
    private String studyId;

    /**
     * 头像
     */
    private String headPortrait;

    /**
     * 手机号
     */
    private String phone;

    /**
     * qq号
     */
    private String qq;

    /**
     * 毕业去向
     */
    private String graduationDestination;

    /**
     * 个性签名
     */
    private String userDestination;

    /**
     * 生活照(文本为json格式的集合)
     */
    private String lifePhoto;

    /**
     * 最后登录时间,格式为YYYY-MM-DD HH:MM:SS
     */
    private Date lastLoginTime;


    /**
     * 当前登录时间,格式为YYYY-MM-DD HH:MM:SS
     */
    private Date currentLoginTime;

    /**
     * 是否是面试官,0:不是,1:是
     */
    private Integer isInterview;

    /**
     * 是否删除,0:未删除,1:已删除
     */
    @TableLogic
    private Integer deleteFlag;

    /**
     * 蓝桥杯获奖数量
     */
    private Integer lanQiaoCount;

    /**
     * 软件著作数量
     */
    private Integer copyrightCount;

    /**
     * 年薪
     */
    private Integer salaryYear;

    /**
     * 地区，邮政编码
     */
    private String area;

    /**
     * 权限（连表查询）
     */
    @TableField(exist = false)
    private List<String> auth;

    @TableField(exist = false)
    private static final long serialVersionUID = 785462356473748392L;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        if(auth != null)
            return AuthorityUtils.createAuthorityList(auth.toArray(new String[0]));
        return new ArrayList<>();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}