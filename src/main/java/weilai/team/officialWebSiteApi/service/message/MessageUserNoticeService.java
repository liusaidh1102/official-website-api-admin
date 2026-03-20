package weilai.team.officialWebSiteApi.service.message;

import org.springframework.stereotype.Service;
import weilai.team.officialWebSiteApi.entity.message.DTO.UpdateNoticeDTO;
import weilai.team.officialWebSiteApi.entity.post.DTO.UpdatePostDto;
import weilai.team.officialWebSiteApi.util.ResponseResult;
import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
* @author someo
* @description 针对表【message_user(公告-用户表)】的数据库操作Service
* @createDate 2024-11-18 17:40:43
*/
@Service
public interface MessageUserNoticeService {

    /**
     * 获取公告列表
     * @param page 当前页
     * @param size 每页的数量
     * @param request
     * @return
     */
    ResponseResult<?> getNotice(int page, int size, HttpServletRequest request);

    /**
     *将单个公告标记为已读
     * @param noticeId 公告ID
     * @param request
     * @return
     */
    ResponseResult<?> markUnReadNoticeAsRead(Long noticeId,HttpServletRequest request);

    /**
     * 将所有公告标记为已读
     * @param request
     * @return
     */
    ResponseResult<?> markAllUnReadNoticeAsRead(HttpServletRequest request);

    /**
     * 删除公告
     * @param noticeId 公告ID
     * @param request
     * @return
     */
    ResponseResult<?> deleteNotice(Long noticeId,HttpServletRequest request);

    /**
     * 修改公告
     * @param updateNoticeDTO 修改公告实体类
     * @param request
     * @return
     */
    ResponseResult<?> updateNotice(UpdateNoticeDTO updateNoticeDTO, HttpServletRequest request);

    /**
     * 获取未读公告数量
     * @param request
     * @return
     */
    ResponseResult<?> getNotReadNoticeCount(HttpServletRequest request);

    /**
     * 获取所有公告数量
     * @param request
     * @return
     */
    public ResponseResult<?> getAllNoticeCount(HttpServletRequest request);

    /**
     * 根据公告ID获取公告
     * @param noticeId 公告ID
     * @param request
     * @return
     */
    ResponseResult<?> getNoticeById(Long noticeId,HttpServletRequest request);

    /**
     * 批量删除公告
     * @param noticeIds 公告ID
     * @param request
     * @return
     */
    ResponseResult<?> batchDeleteNotices(List<Long> noticeIds, HttpServletRequest request);
}
