package weilai.team.officialWebSiteApi.mapper.admin;
import java.util.Date;
import java.util.List;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.models.auth.In;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import weilai.team.officialWebSiteApi.entity.admin.DO.User;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import weilai.team.officialWebSiteApi.entity.admin.DTO.DingIDDTO;
import weilai.team.officialWebSiteApi.entity.admin.VO.*;

/**
* @author 王科林
* @description 针对表【user(用户表)】的数据库操作Mapper
* @createDate 2024-11-09 22:11:08
* @Entity weilai.team.officialWebSiteApi.entity.admin.DO.User
*/
@Mapper
public interface UserMapper extends BaseMapper<User> {
    /**
     * 根据用户名(账号)查询用户信息
     * @param account 用户名(账号)
     * @return 单个用户
     */
    User selectAllByUsernameOrEmail(@Param("account") String account);

    /**
     * 根据用户id获取用户的生活照片
     * @param id 用户id
     * @return 生活照片列表
     */
    UserLifePhoto selectLifePhotoById(@Param("id") Long id);

    /**
     * 根据邮箱修改密码
     * @param password 密码
     * @param email 邮箱
     * @return 影响行数
     */
    int updatePasswordByEmail(@Param("password") String password, @Param("email") String email);

    /**
     * 根据用户的账号或邮箱修改最后登录时间
     * @param lastLoginTime 最后的登录时间
     * @param account 账号或邮箱
     * @return 影响行数
     */
    int updateLastLoginTimeByUsernameOrEmail(@Param("lastLoginTime") Date lastLoginTime,@Param("currentLoginTime") Date currentLoginTime, @Param("account") String account);

    /**
     * 根据用户id 获取用户的帖子
     *
     * @param pageInfo  分页信息
     * @param userId   用户id
     * @return 贴子列表
     */
    List<UserPostVO> getPostByUserId(Page<UserPostVO> pageInfo, @Param("userId") Long userId);

    /**
     * 根据用户id获取用户的贴子的”所有“信息（所有贴子的数量，所有的点赞数，所有的评论数，所有的收藏数）
     * @param userId 用户id
     */
    UserPostInfoVO getUserPostAllInfo(@Param("userId") Long userId);

    /**
     * 根据用户id 获取用户收藏的贴子
     *
     * @param pageInfo  分页信息
     * @param userId   用户id
     * @return 贴子列表
     */
    List<UserPostVO> geUserCollect(Page<UserPostVO> pageInfo, @Param("userId") Long userId);

    /**
     * 模糊查询用户，根据name、user_destination
     *
     * @param pageInfo  分页信息
     * @param content  搜索内容
     * @return 用户列表
     */
    List<SearchUserVO> searchUserLike(Page<SearchUserVO> pageInfo, @Param("content") String content);

    /**
     * 分页查询用户 + 权限
     * @param pageInfo  分页信息
     * @return 用户列表
     */
    List<UserListVO> getUserList(Page<UserListVO> pageInfo);

    /**
     * 查询小组的总体情况
     * @return 小组的总体情况
     */
    List<TeamAbleVO> getTeamAble();

    /**
     * 获取用户数量
     * @return 用户数量
     */
    Long getUserCount();

    /**
     * 查询所有用户
     * @param pageInfo  分页信息
     * @return 用户列表
     */
    List<User> selectAll(Page<User> pageInfo);

    /**
     * 根据年级和组别查询用户
     *
     * @param pageInfo 分页信息
     * @param grade 年级
     * @param group 组别
     * @return 用户列表
     */
    List<User> selectAllByGradeAndGroup(Page<User> pageInfo, @Param("grade") String grade, @Param("group") String group);

    /**
     * 根据权限和姓名模糊查询用户
     * @param pageInfo  分页信息
     * @param content  搜索内容
     * @param authorities 权限
     * @return 用户列表
     */
    List<UserListVO> reachUserLike(Page<UserListVO> pageInfo, @Param("content") String content, @Param("authorities") List<String> authorities);

    /**
     * 根据姓名模糊查询用户
     * @param content  搜索内容
     * @return 用户列表
     */
    List<TeamSearchUserVO> searchUserByNameLike(@Param("content") String content);

    /**
     * 定时清理软删除
     * @return 影响的行数
     */
    int softDeleteClear();

    /**
     * 根据年级获取组别
     * @param grade 年级
     * @return 组别
     */
    List<Integer> getGroupByGrade(@Param("grade") String grade);

    /**
     * 根据年级获取用户
     * @param grade 年级
     * @return 用户
     */
    List<TeamSearchUserVO> getUsersByGrade(@Param("grade") String grade);

    /**
     * 修改用户信息
     * @param ids   用户id
     * @param group  组别
     * @param grade  年级
     * @param clazz  班级
     * @return 影响的行数
     */
    int modifyManyUser(@Param("ids") List<Long> ids, @Param("group")String group,@Param("grade") String grade, @Param("clazz")String clazz);

    /**
     * 获取概况的信息
     * @return 概况的信息
     */
    SummarizeVO selectSummarize();

    /**
     * 获取前后端每届的信息
     * @return 前后端每届的信息
     */
    List<SummarizeVO.Per> selectSummarizePer();

    /**
     * 获取每个地区的人数
     * @return 每个地区的人数
     */
    List<SummarizeVO.Area> selectSummarizeArea();

    /**
     * 获取每组的用户id、姓名、钉钉id
     * @return 每组的用户id、姓名、钉钉id
     */
    List<DingIDDTO> getDingIDByGroup(@Param("group") List<String> group);

    /**
     * 获取每组的用户id、姓名、钉钉id
     * @return 每组的用户id、姓名、钉钉id
     */
    List<DingIDDTO> getDingIDAll();

    /**
     *  根据组别查询用户数量
     * @param group 组别
     * @return 用户数量
     */
    Long getUserCountByGroup(@Param("group") String group);

    /**
     * 根据用户姓名修改钉钉id
     * @param name 用户姓名
     * @param DingId 钉钉id
     * @return 影响的行数
     */
    int updateUserByDingIdByUserName(@Param("name") String name,@Param("DingId") String DingId);

    /**
     * 修改用户是否面试官
     * @param userId 用户id
     * @param i 是否面试官 0 不是 1 是
     */
    void updateIsInterview(@Param("userId") Long userId, @Param("i") int i);
}




