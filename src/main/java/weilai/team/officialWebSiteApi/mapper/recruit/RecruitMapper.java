package weilai.team.officialWebSiteApi.mapper.recruit;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import weilai.team.officialWebSiteApi.entity.recruit.DO.RecruitInterview;
import weilai.team.officialWebSiteApi.entity.recruit.DO.RecruitUser;
import java.util.List;
/**
 * @author lzw
 * @date 2024/11/12 16:33
 * @description 作用：招新管理和报名的mapper
 */
@Mapper
public interface RecruitMapper extends BaseMapper<RecruitUser> {


    /**
     * 根据ids查询用户的id和姓名
     * @param queryWrapper 查询条件
     * @return 返回结果
     */
    List<RecruitInterview> selectIdAndNameList(@Param("ew")LambdaQueryWrapper<RecruitUser> queryWrapper);


}
