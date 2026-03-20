package weilai.team.officialWebSiteApi.entity.postComment.DO;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.util.Date;
import lombok.Data;

/**
 * 无极评论
 * @TableName post_comment_all
 */
@TableName(value ="post_comment_all")
@Data
public class PostCommentAll implements Serializable {
    /**
     * 主键自增
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 一级评论id
     */
    private Long commentId;

    /**
     * 评论者(用户)id
     */
    private Long commentUser;

    /**
     * 该评论指向的用户id
     */
    private Long pointUser;

    /**
     * 评论内容(文本 <= 300)
     */
    private String commentTxt;

    /**
     * 评论时间,格式为YYYY-MM-DD HH:MM:SS
     */
    private Date commentTime;

    /**
     * 点赞数量
     */
    private long likeCount;

    /**
     * 是否删除,0:未删除,1:已删除
     */
    private Integer deleteFlag;

    @TableField(exist = false)
    private static final long serialVersionUID = 2904752398749L;
}