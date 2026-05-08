package weilai.team.officialWebSiteApi.mapper.recruit;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import weilai.team.officialWebSiteApi.entity.recruit.DO.RecruitInterview;
import weilai.team.officialWebSiteApi.entity.recruit.DO.RecruitUser;
import weilai.team.officialWebSiteApi.entity.recruit.VO.HrVO;
import weilai.team.officialWebSiteApi.entity.recruit.VO.RecruitInterviewVO;

import java.util.List;

/**
 * @author lzw
 * @date 2024/11/25 11:14
 * @description 作用： 招新面试的mapper
 */
public interface RecruitInterviewMapper extends BaseMapper<RecruitInterview> {

    /**
     * 插入面试人员
     * @param userId 用户id
     * @param name 用户姓名
     */
    void insertInterviewUser(@Param("userId") Long userId, @Param("name") String name, @Param("grade") String grade);


    /**
     * 查询面试页面的信息
     * @param recruitInterviewPage  分页对象
     * @param wrapper 查询的Wrapper
     */
    IPage<RecruitInterviewVO> selectMyPage(@Param("recruitInterviewPage") Page<RecruitInterview> recruitInterviewPage, @Param("ew") LambdaQueryWrapper<RecruitInterview> wrapper);



    /**
     * 根据id获取面试官的信息
     * @param id 面试官的id
     * @return 返回信息
     */
    @Select("select id,name,head_portrait from user where id = #{id}")
    HrVO getHrById(@Param("id") Integer id);


    /**
     * 根据wrapper查询面试人员
     * @param wrapper 查询条件
     * @return
     */
    List<RecruitInterviewVO> selectMyList(@Param("ew") LambdaQueryWrapper<RecruitInterview> wrapper);


}
