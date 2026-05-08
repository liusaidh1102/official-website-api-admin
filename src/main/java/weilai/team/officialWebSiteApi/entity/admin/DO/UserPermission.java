package weilai.team.officialWebSiteApi.entity.admin.DO;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import lombok.Data;

/**
 * 角色表
 * @TableName user_permission
 */
@TableName(value ="user_permission")
@Data
public class UserPermission implements Serializable {
    /**
     * 主键自增
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 用户id
     */
    private Long userId;

    /**
     * 最高级管理员(老师):admin_plus |普通用户(学生):user |面试官:interviewer |管理面试官:admin_inter
     */
    private String authority;

    @TableField(exist = false)
    private static final long serialVersionUID = 3927402387502383L;
}