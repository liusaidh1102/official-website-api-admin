package weilai.team.officialWebSiteApi.mapper.admin;
import java.util.Collection;
import java.util.List;

import com.baomidou.mybatisplus.core.injector.methods.UpdateById;
import org.apache.ibatis.annotations.Param;

import org.apache.ibatis.annotations.Mapper;
import weilai.team.officialWebSiteApi.entity.admin.DO.UserCourse;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import weilai.team.officialWebSiteApi.entity.admin.DTO.UserCourseMinDTO;

/**
* @author 王科林
* @description 针对表【user_course(用户的课程表)】的数据库操作Mapper
* @createDate 2024-11-11 10:14:01
* @Entity weilai.team.officialWebSiteApi.entity.admin.DO.UserCourse
*/
@Mapper
public interface UserCourseMapper extends BaseMapper<UserCourse> {

    /**
     * 根据用户id查询所有课程
     * @param useId 用户id
     * @return 用户课程列表
     */
    UserCourse selectAllByUseId(@Param("useId") Long useId);

    /**
     * 删除单独的课程
     * @param userCourse 课程信息
     * @return 影响数据库的行数
     */
    int updateAllByCourseId(@Param("userCourse") UserCourse userCourse);

    /**
     * 根据用户id删除用户所有的课程
     * @param useIdList 用户id集合
     * @return 影响数据库的行数
     */
    int deleteByUseIdIn(@Param("useIdList") Collection<Long> useIdList);

    /**
     * 针对软删除的清理
     * @return 影响数据库的行数
     */
    int softDeleteClear();

    /**
     * 获取前 4 届的课表信息
     * @return 课表信息
     */
    List<UserCourseMinDTO> getUserCourseHtml();

    /**
     * 清理课表
     */
    int clearUserCourse();

    int clearUserCourseById(Long userId);
}




