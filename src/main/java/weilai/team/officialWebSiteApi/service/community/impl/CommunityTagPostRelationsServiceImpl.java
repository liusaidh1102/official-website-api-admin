package weilai.team.officialWebSiteApi.service.community.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import weilai.team.officialWebSiteApi.entity.community.DO.CommunityTag;
import weilai.team.officialWebSiteApi.entity.community.DO.CommunityTagPostRelations;
import weilai.team.officialWebSiteApi.entity.community.DTO.TagPageDto;
import weilai.team.officialWebSiteApi.entity.community.DTO.tagUsesDto;
import weilai.team.officialWebSiteApi.entity.post.DTO.PageDto;
import weilai.team.officialWebSiteApi.entity.post.VO.PagePostVo;
import weilai.team.officialWebSiteApi.mapper.community.CommunityTagMapper;
import weilai.team.officialWebSiteApi.mapper.post.PostMapper;
import weilai.team.officialWebSiteApi.service.community.CommunityTagPostRelationsService;
import weilai.team.officialWebSiteApi.mapper.community.CommunityTagPostRelationsMapper;
import org.springframework.stereotype.Service;
import weilai.team.officialWebSiteApi.service.post.PostService;
import weilai.team.officialWebSiteApi.util.ResponseResult;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author Administrator
 * @description 针对表【community_tag_post_relations】的数据库操作Service实现
 * @createDate 2024-11-10 09:36:47
 */
@Service
public class CommunityTagPostRelationsServiceImpl extends ServiceImpl<CommunityTagPostRelationsMapper, CommunityTagPostRelations>
        implements CommunityTagPostRelationsService {
    @Resource
    CommunityTagPostRelationsMapper communityTagPostRelationsMapper;
    @Resource
    CommunityTagMapper communityTagMapper;
    @Resource
    PostMapper postMapper;
    @Resource
    private PostService postService;


    private static final Logger logger = LoggerFactory.getLogger(CommunityTagPostRelationsServiceImpl.class);

    /**
     * 根据帖子的id给帖子添加标签
     *
     * @param postId   帖子id
     * @param tagNames 标签名称
     * @return 添加标签成功的信息
     */
    @Override
    public ResponseResult<?> addTagById(Long postId, String[] tagNames) {
        int type = postMapper.selectTypeByPostId(postId);
        List<CommunityTag> communityTags = new ArrayList<>();
        try {
            // 记录输入参数
            logger.info("addTagById called with postId: {} and tagNames: {}", postId, Arrays.toString(tagNames));
            //首先，通过postId查询这篇文章的标签数目，如果标签数目大于5，则不被允许继续添加标签
            int count = communityTagPostRelationsMapper.selectCountByPostId(postId);
            if (count >= 7 || count + tagNames.length >= 7) {
                return ResponseResult.ADD_FAIL;
            } else {
                // 查找或创建标签
                for (String tagName : tagNames) {
                    CommunityTag communityTag = communityTagMapper.findByName(tagName);
                    if (communityTag == null) {
                        communityTag = new CommunityTag();
                        communityTag.setTagName(tagName);
                        communityTag.setTagUses(1);
                        communityTagMapper.insert(communityTag);

                        // 打印出communityTag的ID
                        logger.info("New tag created with name: {}, ID: {}", tagName, communityTag.getTid()); // 确保getId()方法存在
                    } else {
                        //communityTag.setTagUses(communityTag.getTagUses()+1);
                        communityTagMapper.updateByName(tagName);
                        logger.info("Existing tag found with name: {}", tagName);
                    }
                    communityTags.add(communityTag);
                }

                // 然后，将标签与帖子关联起来
                for (CommunityTag communityTag : communityTags) {
                    communityTagPostRelationsMapper.addPostTag(postId, communityTag.getTid(), type);
                    logger.info("Tag with ID: {} associated with post ID: {}", communityTag.getTid(), postId);
                }

                // 如果一切正常，记录成功信息
                logger.info("Tags successfully added to post ID: {}", postId);
                return ResponseResult.ADD_SUCCESS;
            }
        } catch (Exception e) {
            // 记录异常信息
            logger.error("Error adding tags to post ID: {}", postId, e);
            return ResponseResult.ADD_FAILURE;
        }
    }

    /**
     * 根据帖子的id和要删除的标签的名称来删除文章的标签
     *
     * @param postId  帖子id
     * @param tagName 标签名称
     * @return 删除成功的信息
     */
    @Override
    public ResponseResult<?> deleteByIdAndTagName(Long postId, String tagName) {
        // 参数验证
        if (postId == null || tagName == null || tagName.isEmpty()) {
            logger.error("Invalid parameters: postId={}, tagName={}", postId, tagName);
            return ResponseResult.PARAM_ERROR;
        }
        try {
            // 根据标签名称查找标签id
            CommunityTag communityTag = communityTagMapper.findByName(tagName);
            if (communityTag == null) {
                logger.error("Tag not found: tagName={}", tagName);
                return ResponseResult.TAG_NOT_FOUND;
            }
            Long tid = communityTag.getTid();
            Integer tagUses = communityTag.getTagUses();
            // 根据传入的帖子id删除对应的标签关系
            communityTagPostRelationsMapper.deleteByPostIdAndTid(tid, postId);
            // 更新标签的使用次数（subByTid是减少标签使用次数的意思）
            if (tagUses > 0) {
                tagUses = tagUses - 1;
            } else {
                tagUses = 0;
            }
            communityTagMapper.subByTid(tid, tagUses);

            logger.info("Successfully deleted tag relation for postId={} and tagName={}", postId, tagName);
            return ResponseResult.DELETE_SUCCESS;
        } catch (Exception e) {
            logger.error("Error deleting tag relation for postId={} and tagName={}", postId, tagName, e);
            return ResponseResult.DELETE_FAILURE;
        }
    }

    /**
     * 根据用户使用过的标签进行推荐
     *
     * @param type 类型
     * @param uid  当前登录的用户id
     * @return 推荐的标签
     */
    @Override
    public ResponseResult<List<String>> recommendByUserId(Integer type, Long uid) {
        List<Long> collectedTagIds = new ArrayList<>();
        Set<String> tagNames = new LinkedHashSet<>();
        List<String> hotTagNames = communityTagMapper.findRecommend(type); // 假设这里不会返回null

        List<Long> postIds = postMapper.selectPostIdsByUid(type, uid);
        if (postIds != null) {
            for (Long postId : postIds) {
                logger.info("Start looking for the tag ID corresponding to the post ID {}", postId);
                List<Long> tempTagIds = communityTagPostRelationsMapper.findTagIds(postId);
                if (tempTagIds != null) {
                    logger.info("Post ID {} found {} Tag IDs", postId, tempTagIds.size());
                    collectedTagIds.addAll(tempTagIds);
                } else {
                    logger.warn("No tag IDs found for post ID {}", postId);
                }
            }
        } else {
            logger.warn("No post IDs found for user ID {}", uid);
        }
        for (Long tagId : collectedTagIds) {
            String tagName = communityTagMapper.findById(tagId);
            if (tagName != null) { // 假设findById不会返回null，或者应该处理null情况
                tagNames.add(tagName);
            }
        }
        tagNames.addAll(hotTagNames);
        logger.info("A total of {} unique tag names were found for recommendations", tagNames.size());
        List<String> finalTagNames = new ArrayList<>(tagNames);
        return ResponseResult.RECOMMEND_SUCCESS.put(finalTagNames); // 假设这里泛型已经正确指定为List<String>
    }

    /**
     * 关联帖子列表
     *
     * @param
     * @param type
     * @return 使用该标签名称的所有帖子
     */
    @Override
    public ResponseResult<?> findPostByTagName(TagPageDto pageDto, Integer type, HttpServletRequest request) {
        String tagName = pageDto.getTagName();
        logger.info("Starting to find posts by tag name: {}", tagName);
        // 根据tagName获取对应的id
        Long tid = communityTagMapper.findTidByName(tagName);

        // 检查tid是否为null
        if (tid == null) {
            logger.warn("No TID found for tag name: {}", tagName);
            return ResponseResult.FAIL_FIND_POST.put("No tag found with name: " + tagName);
        }
        // 根据tid获取到对应的postid
        List<Long> postIds = communityTagPostRelationsMapper.findPostIdByTid(tid, type);

        // 检查postIds是否为空
        if (postIds == null || postIds.isEmpty()) {
            logger.warn("No posts found for TID: {}", tid);
            return ResponseResult.FAIL_FIND_POST.put("No posts found for tag with TID: " + tid);
        }
        // 根据postId获取所有的与该标签相关的帖子
        PageDto page = new PageDto();
        page.setIds(postIds);
        page.setPage(pageDto.getPage());
        page.setPageSize(pageDto.getPageSize());
        Page<PagePostVo> pagePostVoPage = postService.selectPostByIds(page, request);
        // 记录成功找到的文章ID
        logger.info("Found {} posts for tag name: {}", postIds.size(), tagName);
        // 返回响应结果，包含找到的文章ID
        return ResponseResult.FIND_POST.put(pagePostVoPage);
    }

    /**
     * 标签筛选
     */
    @Override
    public ResponseResult<List<String>> labelFiltering(String str) {
        try {
            logger.info("Filtering labels with input: {}", str);
            List<String> tagNames = communityTagMapper.getTagNamesFuzzyMatching(str);
            if (tagNames == null) {
                // 处理tagNames为null的情况，例如返回一个包含错误信息的ResponseResult对象
                logger.error("Tag names list is null for input: {}", str);
                tagNames.add("Failed to retrieve tag names");
                return ResponseResult.LABILE_FILE.put(tagNames);
            }
            logger.info("Filtered tag names: {}", tagNames);
            return ResponseResult.LABILE_FILTER.put(tagNames); // 假设LABEL_FILTER和put方法是正确的
        } catch (Exception e) {
            logger.error("Error filtering labels for input: {}", str, e);
            // 处理异常，例如返回一个包含错误信息的ResponseResult对象
            List<String> error = new ArrayList<>();
            error.add("Error occurred during label filtering");
            return ResponseResult.LABILE_FILE.put(error);
        }
    }

        /**
     * 根据帖子id删除对应的标签
     */
public ResponseResult<?> deleteByPostId(Long postId) {
  List<Long>tids= communityTagPostRelationsMapper.findTagIds(postId);
    for (Long tid : tids) {
        communityTagPostRelationsMapper.deleteByTid(tid);
        //更新使用次数
        communityTagMapper.updateUsesByTid(tid);
    }
    return ResponseResult.DELETE_SUCCESS;
    }


    public ResponseResult<?> getTagUsageStatistics(Integer type) {
    List<tagUsesDto> tagUsesDtoList=new ArrayList<>();
        List<Long> tagIds = communityTagPostRelationsMapper.selectTagsIdByType(type);
        if (tagIds.isEmpty()) {
            return ResponseResult.UseNotFound.put(new tagUsesDto()); // 假设这是处理未找到的逻辑
        }
        for (Long tagId : tagIds) {
            String tagName = communityTagMapper.findTagNameById(tagId);
            if (tagName!= null) {
                int count = communityTagPostRelationsMapper.selectCountByTagId(tagId);
                tagUsesDto tagUsesDto=new tagUsesDto();
                tagUsesDto.setTagName(tagName);
                tagUsesDto.setCount(count);
                tagUsesDtoList.add(tagUsesDto);
        }
        }
        return ResponseResult.USAGE_STATISTICS.put(tagUsesDtoList);
    }
}




