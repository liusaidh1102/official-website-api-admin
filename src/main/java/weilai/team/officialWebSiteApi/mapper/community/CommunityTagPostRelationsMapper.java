package weilai.team.officialWebSiteApi.mapper.community;

import org.apache.ibatis.annotations.Param;
import weilai.team.officialWebSiteApi.entity.community.DO.CommunityTagPostRelations;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.TreeMap;

/**
* @author Administrator
* @description 针对表【community_tag_post_relations】的数据库操作Mapper
* @createDate 2024-11-10 09:36:47
* @Entity weilai.team.officialWebSiteApi.entity.community.DO.CommunityTagPostRelations
*/
public interface CommunityTagPostRelationsMapper extends BaseMapper<CommunityTagPostRelations> {
    //@Mapper
    void addPostTag(@Param("postId") Long postId, @Param("tid") Long tid, int type);

    /**
     * 根据传入的帖子id和标签id删除对应的标签关系
     * @param tid 标签id
     * @param postId 帖子id
     */
    void deleteByPostIdAndTid(@Param("tid")Long tid, @Param("postId")Long postId);

    /**
     * 获取当前帖子ID对应的标签ID列表
     * @param postId 帖子ID
     * @return 标签ID
     */
    List<Long> findTagIds(@Param("postId")Long postId);

    /**
     * 根据tid获取到对应的postid
     * @param tid 标签ID
     * @param type
     * @return 对应的postid集合
     */
    List<Long> findPostIdByTid(@Param("tid") Long tid, @Param("type")Integer type);
    /**
     * 查询这篇文章的标签数目
     */
    int selectCountByPostId(@Param("postId")Long postId);

    void deleteByTid(@Param("tid")Long tid);



    int selectCountByTagId(@Param("tagId") Long tagId);


    List<Long> selectTagsIdByType(@Param("type") Integer type);


    List<Long> selectTagIdByType(Integer type);
}




