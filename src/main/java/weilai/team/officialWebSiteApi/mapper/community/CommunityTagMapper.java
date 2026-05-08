package weilai.team.officialWebSiteApi.mapper.community;

import org.apache.ibatis.annotations.MapKey;
import org.apache.ibatis.annotations.Param;
import weilai.team.officialWebSiteApi.entity.community.DO.CommunityTag;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import java.util.List;
import java.util.Map;

/**
 * @author Administrator
 * @description 针对表【community_tag】的数据库操作Mapper
 * @createDate 2024-11-10 09:36:47
 * @Entity weilai.team.officialWebSiteApi.entity.community.DO.CommunityTag
 */
public interface CommunityTagMapper extends BaseMapper<CommunityTag> {
    /**
     * 通过标签名称查找标签
     * @param tagName 标签名称
     * @return 标签对象
     */
    //@Mapper
    CommunityTag findByName(@Param("tagName") String tagName);

    void updateByName(@Param("tagName")String tagName);

    /**
     * 更新标签的使用次数（subByTid是减少标签使用次数的意思）
     * @param tid  标签id
     * @param tagUses 标签的使用次数
     */
    void subByTid(@Param("tid") Long tid,@Param("tagUses") Integer tagUses);

    /**
     * 根据总标签ID列表获取标签名
     * @param totalTagId 标签id
     * @return 标签名称
     */
    String findById(@Param("totalTagId")Long totalTagId);

    /**
     * 热门标签展示
     * @return 热门标签的一个集合
     * @param type
     */
    List<String> findHotTagsByTagUses(Integer type);

    /**
     * 查找推荐标签
     * @return 推荐标签的集合
     * @param type
     */
    List<String> findRecommend(Integer type);


    /**
     * 查询出所有的标签名称
     */
    List<String> getAllTagNames();

    /**
     * 根据tagName获取对应的id
     * @param tagName 标签名称
     * @return 对应的所有的标签id
     */
    Long findTidByName(@Param("tagName") String tagName);
    /**
     * 标签筛选
     * @param str 匹配的字符串
     * @return 相匹配的所有标签集合
     */
    List<String> getTagNamesFuzzyMatching(@Param("str")String str);

    void updateUsesByTid(@Param("tid")Long tid);

    String findTagNameById(@Param("tagId") Long tagId);
}




