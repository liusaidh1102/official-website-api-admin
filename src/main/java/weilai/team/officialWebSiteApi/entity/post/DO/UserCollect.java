package weilai.team.officialWebSiteApi.entity.post.DO;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Date;
import lombok.Data;

/**
 * 用户的收藏[贴子]
 * @TableName user_collect
 */
@TableName(value ="user_collect")
@Data
public class UserCollect implements Serializable {
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
     * 收藏的贴子id
     */
    private Long collectPost;

    /**
     * 收藏时间,格式为YYYY-MM-DD HH:MM:SS
     */
    private Date collectTime;

    /**
     * 是否删除,0:未删除,1:已删除
     */
    private Integer deleteFlag;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}