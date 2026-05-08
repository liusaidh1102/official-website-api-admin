package weilai.team.officialWebSiteApi.entity.post.DO;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Date;

/**
 * 用户发的贴子内容
 * @TableName post
 */
@TableName(value ="post")
@Data
public class Post implements Serializable {
    /**
     * 主键自增
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 发帖用户id
     */
    private Long userId;

    /**
     * 点赞数
     */
    private Integer likeCount;

    /**
     * 评论数
     */
    private Integer commentCount;

    /**
     * 访问量
     */
    private Integer viewCount;

    /**
     * 发布时间,格式为YYYY-MM-DD HH:MM:SS
     */
    private Date postTime;

    /**
     * 贴子标题(字符 <= 30)
     */
    private String title;

    /**
     * 贴子文本内容(文本 <= 65535)
     */
    private String postTxt;

    /**
     * 贴子摘要(字符 <= 100)
     */
    private String postAbstract;

    /**
     * 类型(1博客|2公告|3交流 |4头脑风暴)
     */
    private Integer type;

    /**
     * 是否删除,0:未删除,1:已删除
     */
    private Integer deleteFlag;

    @TableField(exist = false)
    private static final long serialVersionUID = 15645625L;
}