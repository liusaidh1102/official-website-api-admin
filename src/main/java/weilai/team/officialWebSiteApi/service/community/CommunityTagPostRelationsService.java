package weilai.team.officialWebSiteApi.service.community;

import weilai.team.officialWebSiteApi.entity.community.DO.CommunityTagPostRelations;
import com.baomidou.mybatisplus.extension.service.IService;
import weilai.team.officialWebSiteApi.entity.community.DTO.TagPageDto;
import weilai.team.officialWebSiteApi.util.ResponseResult;

import javax.servlet.http.HttpServletRequest;

/**
* @author Administrator
* @description 针对表【community_tag_post_relations】的数据库操作Service
* @createDate 2024-11-10 09:36:47
*/

public interface CommunityTagPostRelationsService extends IService<CommunityTagPostRelations> {
    /**
     * 根据帖子的id给帖子添加标签
     * @param postId 帖子id
     * @param tagNames 标签名称
     * @return  添加标签成功的信息
     */
    ResponseResult<?> addTagById(Long postId, String[] tagNames);

    /**
     * 根据帖子的id和要删除的标签的名称来删除文章的标签
     * @param postId  帖子id
     * @param tagName  标签名称
     * @return 删除成功的信息
     */
    ResponseResult<?> deleteByIdAndTagName(Long postId, String tagName);

    /**
     * 根据用户使用过的标签进行推荐
     *
     * @param type
     * @param uid  当前登录的用户id
     * @return  推荐的标签
     */
    ResponseResult<?> recommendByUserId(Integer type, Long uid);
    /**
     * 关联帖子列表
     * @return 使用该标签名称的所有帖子
     */
    ResponseResult<?> findPostByTagName(TagPageDto pageDto, Integer type, HttpServletRequest request);
    /**
     * 标签筛选
     * @param str 匹配的字符串
     * @return
     */
    ResponseResult<?> labelFiltering(String str);

    ResponseResult<?> getTagUsageStatistics(Integer type);
}
