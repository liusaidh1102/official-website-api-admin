package weilai.team.officialWebSiteApi.entity.admin.DO;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import lombok.Data;

/**
 * 
 * @TableName user_group_leader
 */
@TableName(value ="user_group_leader")
@Data
public class UserGroupLeader implements Serializable {
    /**
     * 主键自增
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 组长id
     */
    private Long groupLeaderId;

    /**
     * 组的位置(那一组)
     */
    private String groupPostiton;

    @TableField(exist = false)
    private static final long serialVersionUID = 73480184590L;
}