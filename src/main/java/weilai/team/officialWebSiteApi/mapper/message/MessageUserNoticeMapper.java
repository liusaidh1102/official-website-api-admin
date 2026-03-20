package weilai.team.officialWebSiteApi.mapper.message;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.data.repository.query.Param;
import weilai.team.officialWebSiteApi.entity.message.DO.MessageUserNotice;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import weilai.team.officialWebSiteApi.entity.message.VO.MessageNoticeVO;
import weilai.team.officialWebSiteApi.entity.post.DTO.PostNoticeDto;

import java.util.Date;
import java.util.List;

/**
* @author someo
* @description 针对表【message_user(公告-用户表)】的数据库操作Mapper
* @createDate 2024-11-18 17:40:43
* @Entity weilai.team.officialWebSiteApi.entity.message.DO.MessageUser
*/
public interface MessageUserNoticeMapper extends BaseMapper<MessageUserNotice> {
    /**
     * 插入所有用户关于此广告的状态
     * @param noticeId 公告ID
     * @return
     */
    int insertAllUserNotice(@Param("noticeId")Long noticeId);

    /**
     * 获取 timeStamp 之后的 n 条时间戳
     * @param limit 条数限制
     * @param timeStamp 时间戳
     * @return
     */
    List<MessageNoticeVO> selectNoticeFromTimeStamp(@Param("limit")int limit, @Param("timeStamp")Date timeStamp);



    MessageNoticeVO selectNoticeByNoticeId(@Param("noticeId")Long noticed);
    /**
     * 根据分页信息查询公告
     * @param pageInfo 分页信息
     * @return
     */
    List<MessageNoticeVO> selectNoticeByPageInfo(Page<MessageNoticeVO> pageInfo);

    /**
     * 获取所有公告的ID
     * @return
     */
    List<Long> getAllNoticeIds();

    /**
     * 将一条公告标记为已读
     * @param userId 接收者用户ID
     * @param noticeId
     * @return
     */
    int markUnReadAsRead(@Param("userId") Long userId,@Param("noticeId") Long noticeId);

    /**
     * 将所有公告标记为已读
     * @param userId 接收者用户ID
     * @return
     */
    int markAllUnReadAsRead(@Param("userId") Long userId);

    /**
     * 将已读标记为未读
     * @param noticeId
     * @return
     */
    int updateStatus(@Param("noticeId") Long noticeId);

    /**
     * 获取一个用户所有未读公告ID列表
     * @param userId 接收者用户ID
     * @return
     */
    List<Long> getUnreadNoticeIds(@Param("userId") Long userId);

    /**
     * 删除
     * @param noticeId 接收者用户ID
     */
    void deleteNotice(@Param("noticeId")Long noticeId);

    /**
     * 分页获取公告，不含个人信息
     * @param pageInfo
     * @return
     */
    List<PostNoticeDto> selectPostNoticeDtoByPageInfo(Page<PostNoticeDto> pageInfo);

    /**
     * 获取公告数量
     * @return
     */
    int getAllNoticeCount();

    /**
     * 获取个人用户未读信息数量
     * @param userId 用户ID
     * @return
     */
    int getUnreadNoticeCount(@Param("userId") Long userId);

    /**
     * 获取一个公告所有已读用户ID列表
     * @param userId 公告ID
     * @return
     */
    List<Long> getReadNoticeIdsByUserId(@Param("userId") Long userId);
}




