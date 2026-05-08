package weilai.team.officialWebSiteApi.mapper.admin;
import java.util.Collection;
import java.util.List;
import org.apache.ibatis.annotations.Param;

import org.apache.ibatis.annotations.Mapper;
import weilai.team.officialWebSiteApi.entity.admin.DO.UserPermission;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

/**
* @author 王科林
* @description 针对表【user_permission(角色表)】的数据库操作Mapper
* @createDate 2024-11-11 10:14:01
* @Entity weilai.team.officialWebSiteApi.entity.admin.DO.UserPermission
*/
@Mapper
public interface UserPermissionMapper extends BaseMapper<UserPermission> {

    /**
     * 根据用户id获取权限
     * @param userId 用户id
     * @return 权限列表
     */
    List<String> getAuthorityByUserId(@Param("userId") Long userId);


    /**
     * 根据用户id和权限名删除权限
     * @param userId 用户id
     * @param authority 权限名
     * @return 影响行数
     */
    int deleteByUserIdAndAuthority(@Param("userId") Long userId, @Param("authority") String authority);

    /**
     * 根据用户的id删除权限
     * @param userIdList 用户id列表
     * @return 影响行数
     */
    int deleteByUserIdIn(@Param("userIdList") Collection<Long> userIdList);
}




