package weilai.team.officialWebSiteApi.mapper.post;
import org.apache.ibatis.annotations.*;

import java.util.Collection;

import weilai.team.officialWebSiteApi.entity.post.DO.UserCollect;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import weilai.team.officialWebSiteApi.util.ResponseResult;

import java.util.ArrayList;
import java.util.List;

/**
* @author 35543
* @description 针对表【user_collect(用户的收藏[贴子])】的数据库操作Mapper
* @createDate 2024-11-13 16:30:33
* @Entity weilai.team.officialWebSiteApi.entity.post.DO.UserCollect
*/
@Mapper
public interface UserCollectMapper extends BaseMapper<UserCollect> {

    /**
     * 查询所有的收藏帖子的id
     * @return
     */
    List<Long> selectAllIds();

    /**
     * 软删除收藏
     * @param userId
     * @param id
     * @return
     */
    @Update("update user_collect set delete_flag=1 where collect_post=#{id} and user_id=#{userId}")
    boolean deleteByPostId(Long userId,Long id);

    /**
     * 修改删除状态达到收藏的目的
     * @param id
     *
     * @return
     */
    @Update("update user_collect set delete_flag=0 where collect_post=#{id} and user_id=#{userId}")
    int addCollect(Long userId, long id);

    /**
     * 批量添加收藏
     * @param userCollectsList
     * @return
     */
    int insertAll(List<UserCollect> userCollectsList);

    /**
     * 是否收藏
     * @param id
     * @param userId
     * @return
     */
    Integer getFlagByPostId(Long id, Long userId);

    /**
     * 查询收藏数
     * @param id
     */
    @Select("select count(*) from user_collect where collect_post=#{id} and delete_flag=0")
    Integer selectCountByPostId(Long id);

    /**
     * 根据用户id删除用户所有的收藏
     * @param userIdList 用户id列表
     * @return 影响行数
     */
    int deleteByUserIdIn(@Param("userIdList") Collection<Long> userIdList);

    /**
     * 根据帖子id删除帖子的所有收藏
     * @param id 帖子id
     */

    int deleteByPost(Long id);

    /**
     * 删除用户所有被软删除的收藏
     * @return
     */
    int clearUserCollectPost();
}




