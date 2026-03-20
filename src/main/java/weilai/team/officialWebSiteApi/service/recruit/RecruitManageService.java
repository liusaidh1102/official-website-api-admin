package weilai.team.officialWebSiteApi.service.recruit;
import com.baomidou.mybatisplus.extension.service.IService;
import io.swagger.models.auth.In;
import weilai.team.officialWebSiteApi.entity.recruit.DO.RecruitUser;
import weilai.team.officialWebSiteApi.entity.recruit.DTO.ExportDTO;
import weilai.team.officialWebSiteApi.entity.recruit.DTO.PageDTO;
import weilai.team.officialWebSiteApi.entity.recruit.DTO.RecruitQueryDTO;
import weilai.team.officialWebSiteApi.entity.recruit.DTO.UpdateInfoDTO;
import weilai.team.officialWebSiteApi.util.ResponseResult;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author lzw
 * @date 2024/11/11 15:07
 * @description 作用：招新的管理相关业务
 */
public interface RecruitManageService {

    /**
     * 获取所有报名用户
     * @param recruitQueryDTO 查询参数
     * @return 返回所有报名用户的分页数据
     */
    ResponseResult<?> listAllRecruitUser(RecruitQueryDTO recruitQueryDTO);


    /**
     * 修改报名用户的状态
     * @param ids 报名用户的id数组
     * @return  返回结果
     */
    ResponseResult<?> updateRecruitUserStatus(Integer[] ids,Integer interviewStatus);

    /**
     * 结果导出
     * @param response  响应
     * @return 返回excel
     */
    ResponseResult<?> resultExport(HttpServletResponse response, ExportDTO exportDTO);

    /**
     * 获取简历
     * @param id 用户的id
     * @return 返回的是一个pdf文件的url地址
     */
    ResponseResult<?> getResume(Integer id);

    /**
     * 获取所有的年级
     * @return 返回年级
     */
    ResponseResult<?> listAllGrade(PageDTO pageDTO);

    /**
     * 修改报名用户的信息
     * @param updateInfoDTO 报名的表单
     * @return  返回信息
     */
    ResponseResult<?> updateInfo(UpdateInfoDTO updateInfoDTO);

    /**
     * 删除报名用户
     * @param id 报名用户的id
     * @return 返回信息
     */
    ResponseResult<?> deleteRecruitUser(Integer id);

    /**
     * 获取待安排面试的数量
     * @param status 待安排面试的状态
     * @return 返回数量
     */
    ResponseResult<?> getCount(Integer status);

    /**
     * 获取待我反馈/我录取/我淘汰的面试记录
     * @param status 状态
     * @param request 请求
     * @param pageDTO 分页参数
     * @return 返回结果
     */
    ResponseResult<?> getAboutMe(Integer status, HttpServletRequest request,PageDTO pageDTO);

    /**
     * 获取某年级下的班级
     * @param grade 年级id
     * @return
     */
    ResponseResult<?> getClazz(String grade);

    /**
     * 获取快捷操作的相关内容
     * @return 返回姓名和id
     */
    ResponseResult<?> getName();
}
