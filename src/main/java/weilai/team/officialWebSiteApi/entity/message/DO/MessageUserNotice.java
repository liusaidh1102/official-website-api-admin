package weilai.team.officialWebSiteApi.entity.message.DO;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import lombok.Data;

/**
 * 公告-用户表
 * @TableName message_user
 */
@TableName(value ="message_user_notice")
@Data
public class MessageUserNotice implements Serializable {
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
     * 公告id
     */
    private Long noticeId;

    /**
     * 消息状态（已读、未读）
     */
    private Integer status;

    /**
     * 是否删除,0:未删除,1:已删除
     */
    private Integer deleteFlag;

    @TableField(exist = false)
    private static final long serialVersionUID = 46239594113L;
}