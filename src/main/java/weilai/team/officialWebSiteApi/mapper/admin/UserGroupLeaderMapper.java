package weilai.team.officialWebSiteApi.mapper.admin;
import java.util.Collection;
import java.util.List;
import org.apache.ibatis.annotations.Param;

import org.apache.ibatis.annotations.Mapper;
import weilai.team.officialWebSiteApi.entity.admin.DO.UserGroupLeader;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

/**
* @author 王科林
* @description 针对表【user_group_leader】的数据库操作Mapper
* @createDate 2024-12-03 21:26:25
* @Entity weilai.team.officialWebSiteApi.entity.admin.DO.UserGroupLeader
*/
@Mapper
public interface UserGroupLeaderMapper extends BaseMapper<UserGroupLeader> {

    /**
     * 根据组别查询组长id
     * @param groupPostiton 组别
     * @return 组长id
     */
    Long selectGroupLeaderIdByGroupPostiton(@Param("groupPostiton") String groupPostiton);

    /**
     * 根据组长id删除组长
     * @param groupLeaderIdList 组长id列表
     * @return 删除的行数
     */
    int delByGroupLeaderIdIn(@Param("groupLeaderIdList") Collection<Long> groupLeaderIdList);

    /**
     * 根据组长id和组取消组长
     * @param groupLeaderId 组长id
     * @param groupPostiton 组
     * @return 删除的行数
     */
    int delByGroupLeaderIdAndGroupPostiton(@Param("groupLeaderId") Long groupLeaderId, @Param("groupPostiton") String groupPostiton);
}




