package weilai.team.officialWebSiteApi.service.recruit.impl;
import com.alibaba.excel.EasyExcel;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import weilai.team.officialWebSiteApi.entity.admin.DO.User;
import weilai.team.officialWebSiteApi.entity.recruit.DO.RecruitClass;
import weilai.team.officialWebSiteApi.entity.recruit.DO.RecruitGrade;
import weilai.team.officialWebSiteApi.entity.recruit.DO.RecruitInterview;
import weilai.team.officialWebSiteApi.entity.recruit.DO.RecruitUser;
import weilai.team.officialWebSiteApi.entity.recruit.DTO.ExportDTO;
import weilai.team.officialWebSiteApi.entity.recruit.DTO.PageDTO;
import weilai.team.officialWebSiteApi.entity.recruit.DTO.RecruitQueryDTO;
import weilai.team.officialWebSiteApi.entity.recruit.DTO.UpdateInfoDTO;
import weilai.team.officialWebSiteApi.entity.recruit.VO.*;
import weilai.team.officialWebSiteApi.mapper.recruit.RecruitClassMapper;
import weilai.team.officialWebSiteApi.mapper.recruit.RecruitGradeMapper;
import weilai.team.officialWebSiteApi.mapper.recruit.RecruitInterviewMapper;
import weilai.team.officialWebSiteApi.mapper.recruit.RecruitMapper;
import weilai.team.officialWebSiteApi.service.recruit.RecruitManageService;
import weilai.team.officialWebSiteApi.util.LogUtil;
import weilai.team.officialWebSiteApi.util.ResponseResult;
import weilai.team.officialWebSiteApi.util.UserUtil;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.net.URLEncoder;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import static weilai.team.officialWebSiteApi.entity.recruit.enums.MysqlConstant.*;

/**
 * @author lzw
 * @date 2024/11/11 15:07
 * @description 作用：招新的管理相关业务实现
 */
@Service
public class RecruitManageServiceImpl implements RecruitManageService{

    @Resource
    private RecruitMapper recruitMapper;

    @Resource
    private RecruitGradeMapper recruitGradeMapper;

    @Resource
    private RecruitClassMapper recruitClassMapper;

    @Resource
    private RecruitInterviewMapper recruitInterviewMapper;

    @Resource
    private UserUtil userUtil;


    @Override
    public ResponseResult<?> listAllRecruitUser(RecruitQueryDTO recruitQueryDTO) {
        LambdaQueryWrapper<RecruitUser> queryWrapper = new QueryWrapper<RecruitUser>().lambda();
        //未被删除的
        queryWrapper.eq(RecruitUser::getIsDeleted, IS_NOT_DELETED)
                //状态筛选
                .eq(recruitQueryDTO.getStatus() != null, RecruitUser::getStatus, recruitQueryDTO.getStatus())
                //年级筛选
                .eq(StringUtils.isNotBlank(recruitQueryDTO.getGrade()), RecruitUser::getGrade, recruitQueryDTO.getGrade())
                //性别筛选
                .eq(StringUtils.isNotBlank(recruitQueryDTO.getSex()), RecruitUser::getSex, recruitQueryDTO.getSex())
                //班级筛选
                .eq(StringUtils.isNotBlank(recruitQueryDTO.getClazz()), RecruitUser::getClazz, recruitQueryDTO.getClazz());
        //按时间查询
        queryWrapper.between(recruitQueryDTO.getStartTime() != null && recruitQueryDTO.getEndTime() != null
                        ,RecruitUser::getCreateTime, recruitQueryDTO.getStartTime(), recruitQueryDTO.getEndTime());

        if (StringUtils.isNotBlank(recruitQueryDTO.getCondition())) {
            queryWrapper.and(wrapper ->
                    wrapper.like(RecruitUser::getName, recruitQueryDTO.getCondition())
                            .or()
                            .like(RecruitUser::getQqNumber, recruitQueryDTO.getCondition())
                            .or()
                            .like(RecruitUser::getEmail, recruitQueryDTO.getCondition())
                            .or()
                            .like(RecruitUser::getStudentId, recruitQueryDTO.getCondition())
            );
        }
        queryWrapper.select();


        //创建分页的page对象，设置分页的参数,在set方法中指定，当参数为null时，默认是第一页和10条数据
        Page<RecruitUser> page = new Page<>(recruitQueryDTO.getPageDTO().getPageNo(), recruitQueryDTO.getPageDTO().getPageSize());
        //使用mybatis的BaseMapper进行查询
        Page<RecruitUser> recruitUserPage = recruitMapper.selectPage(page, queryWrapper);
        List<RecruitUser> records = recruitUserPage.getRecords();
        //将结果转换为vo对象返回
        List<RecruitUserVO> collect = records.stream().map(recruitUser -> {
            RecruitUserVO recordVO = new RecruitUserVO();
            BeanUtils.copyProperties(recruitUser, recordVO);
            return recordVO;
        }).collect(Collectors.toList());
        PageVO<RecruitUserVO> pageVO = new PageVO<>(recruitUserPage.getCurrent(), recruitUserPage.getSize(), recruitUserPage.getTotal(), collect);
        return ResponseResult.OK.put(pageVO);
    }

    @Transactional
    @Override
    public ResponseResult<?> updateRecruitUserStatus(Integer[] ids, Integer interviewStatus) {
        //不是0-3的状态，直接返回错误
        if (interviewStatus < 0 || interviewStatus > 3) {
            return ResponseResult.INTERVIEW_STATUS_ERROR;
        }
        //如果是修改为待面试，要将数据插入到面试表中
        LambdaQueryWrapper<RecruitUser> queryWrapper = new QueryWrapper<RecruitUser>().lambda()
                .eq(RecruitUser::getIsDeleted, IS_NOT_DELETED)
                .in(RecruitUser::getId, ids);
        if (interviewStatus.equals(STATUS_WAIT_INTERVIEW)) {
            List<RecruitInterview> insertList = recruitMapper.selectIdAndNameList(queryWrapper);
            for (RecruitInterview recruitInterview : insertList) {
                recruitInterviewMapper.insertInterviewUser(recruitInterview.getId(), recruitInterview.getName(), recruitInterview.getGrade());
            }
        }
        //如果不是待面试，直接修改状态
        LambdaUpdateWrapper<RecruitUser> updateWrapper = new UpdateWrapper<RecruitUser>()
                .lambda()
                .in(ids != null, RecruitUser::getId, ids)
                .eq(RecruitUser::getIsDeleted, IS_NOT_DELETED)
                .set(RecruitUser::getStatus, interviewStatus)
                .set(RecruitUser::getUpdateTime, LocalDateTime.now());
        recruitMapper.update(null, updateWrapper);
        return ResponseResult.OK;
    }

    @Override
    public ResponseResult<?> resultExport(HttpServletResponse response, ExportDTO exportDTO) {
        LogUtil.info("------------------导出录取结果--------------");
        response.setCharacterEncoding("utf-8");
        // 这里URLEncoder.encode可以防止中文乱码 和EasyExcel没有关系
        String fileName = "录取结果.xlsx";
        try {
            //查询数据
            LambdaQueryWrapper<RecruitUser> lambdaQueryWrapper = new QueryWrapper<RecruitUser>()
                    .lambda()
                    .select(RecruitUser::getName, RecruitUser::getClazz, RecruitUser::getSex, RecruitUser::getGrade, RecruitUser::getClazz, RecruitUser::getUpdateTime, RecruitUser::getStatus)
                    .between(exportDTO.getStartTime() != null && exportDTO.getEndTime() != null
                            , RecruitUser::getUpdateTime, exportDTO.getStartTime(), exportDTO.getEndTime())
                    .eq(StringUtils.isNotBlank(exportDTO.getGrade()), RecruitUser::getGrade, exportDTO.getGrade())
                    .eq(StringUtils.isNotBlank(exportDTO.getSex()), RecruitUser::getSex, exportDTO.getSex())
                    .eq(StringUtils.isNotBlank(exportDTO.getClazz()), RecruitUser::getClazz, exportDTO.getClazz())
                    .eq(exportDTO.getStatus() != null, RecruitUser::getStatus, exportDTO.getStatus())
                    .orderByDesc(RecruitUser::getUpdateTime);
            List<RecruitUser> resultList = recruitMapper.selectList(lambdaQueryWrapper);
            if (resultList.isEmpty()){
                return ResponseResult.RESULT_IS_NULL;
            }
            // 设置响应头，使浏览器弹出下载框
            response.setHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode(fileName, "UTF-8"));
            //将数据转换为vo对象
            List<RecruitResultExcelVO> collect = resultList.stream().map(recruitUser -> {
                return new RecruitResultExcelVO(recruitUser.getName(), recruitUser.getGrade(), recruitUser.getClazz(), recruitUser.getSex(), recruitUser.getUpdateTime(), recruitUser.getStatus());
            }).collect(Collectors.toList());
            // 使用EasyExcel写入到输出流
            EasyExcel.write(response.getOutputStream(), RecruitResultExcelVO.class).sheet("模板").doWrite(collect);
            return ResponseResult.OK;
        } catch (Exception e) {
            LogUtil.Error("导出失败！", e);
            return ResponseResult.ERROR_RESULT_EXPORT;
        }
    }

    @Override
    public ResponseResult<?> getResume(Integer id) {
        // 查询照片
        LambdaQueryWrapper<RecruitUser> queryWrapper = new QueryWrapper<RecruitUser>()
                .lambda()
                .eq(RecruitUser::getId, id)
                .select(RecruitUser::getFileUrl);
        RecruitUser recruitUser = recruitMapper.selectOne(queryWrapper);
        if (recruitUser == null) {
            return ResponseResult.NOT_FOUND_RESOURCE;
        }
        return ResponseResult.OK.put(recruitUser.getFileUrl());
    }

    @Override
    public ResponseResult<?> listAllGrade(PageDTO pageDTO) {
        // 设置默认分页参数
        long pageNo = pageDTO.getPageNo() == null ? 1L : pageDTO.getPageNo();
        long pageSize = pageDTO.getPageSize() == null ? 10L : pageDTO.getPageSize();

        // 创建分页对象
        Page<RecruitGrade> recruitGradePage = new Page<>(pageNo, pageSize);

        // 创建查询条件并排序
        LambdaQueryWrapper<RecruitGrade> queryWrapper = new LambdaQueryWrapper<RecruitGrade>()
                .orderByDesc(RecruitGrade::getCreateTime);

        // 执行分页查询
        Page<RecruitGrade> recruitGradeList = recruitGradeMapper.selectPage(recruitGradePage, queryWrapper);

        // 构建分页响应对象
        PageVO<RecruitGrade> recruitGradePageVO = new PageVO<>(
                recruitGradeList.getCurrent(),
                recruitGradeList.getPages(),
                recruitGradeList.getTotal(),
                recruitGradeList.getRecords()
        );

        // 返回成功响应
        return ResponseResult.OK.put(recruitGradePageVO);
    }

    @Transactional
    @Override
    public ResponseResult<?> updateInfo(UpdateInfoDTO updateInfoDTO) {
        if (ObjectUtils.isEmpty(updateInfoDTO) || updateInfoDTO.getId() == null) {
            return ResponseResult.INTERVIEW_ID_NOT_BE_NULL;
        }
        String studentId = updateInfoDTO.getStudentId();
        String grade = updateInfoDTO.getGrade();
        String clazz = updateInfoDTO.getClazz();
        String sex = updateInfoDTO.getSex();
        String qq = updateInfoDTO.getQq();
        String email = updateInfoDTO.getEmail();
        String name = updateInfoDTO.getName();
        //1. Recruit_User表中的信息修改
        LambdaUpdateWrapper<RecruitUser> updateWrapper = new UpdateWrapper<RecruitUser>()
                .lambda()
                .eq(RecruitUser::getId, updateInfoDTO.getId())
                .set(StringUtils.isNotBlank(studentId), RecruitUser::getStudentId, studentId)
                .set(StringUtils.isNotBlank(email), RecruitUser::getEmail, email)
                .set(StringUtils.isNotBlank(grade), RecruitUser::getGrade, grade)
                .set(StringUtils.isNotBlank(clazz), RecruitUser::getClazz, clazz)
                .set(StringUtils.isNotBlank(sex), RecruitUser::getSex, sex)
                .set(StringUtils.isNotBlank(qq), RecruitUser::getQqNumber, qq)
                .set(StringUtils.isNotBlank(name), RecruitUser::getName, name)
                .set(RecruitUser::getUpdateTime, LocalDateTime.now());
        recruitMapper.update(null, updateWrapper);

        //2. recruit_interview 表中的信息修改(修改name和grade)
        LambdaUpdateWrapper<RecruitInterview> updateInterviewWrapper = new UpdateWrapper<RecruitInterview>()
                .lambda()
                .eq(RecruitInterview::getUserId, updateInfoDTO.getId())
                .set(StringUtils.isNotBlank(grade), RecruitInterview::getGrade, grade)
                .set(StringUtils.isNotBlank(name), RecruitInterview::getName, name);
        return ResponseResult.OK;
    }

    @Transactional
    @Override
    public ResponseResult<?> deleteRecruitUser(Integer id) {
        LambdaUpdateWrapper<RecruitUser> updateWrapper = new UpdateWrapper<RecruitUser>()
                .lambda()
                .eq(RecruitUser::getId, id)
                .set(RecruitUser::getIsDeleted, IS_DELETED)
                .set(RecruitUser::getUpdateTime, LocalDateTime.now());
        recruitMapper.update(null, updateWrapper);
        //如果有面试的记录，删除对应的面试记录，硬删除
        LambdaUpdateWrapper<RecruitInterview> deleteWrapper = new UpdateWrapper<RecruitInterview>()
                .lambda()
                .eq(RecruitInterview::getUserId, id);
        recruitInterviewMapper.delete(deleteWrapper);
        return ResponseResult.OK;
    }

    @Override
    public ResponseResult<?> getCount(Integer status) {
        LambdaQueryWrapper<RecruitUser> queryWrapper = new QueryWrapper<RecruitUser>()
                .lambda()
                .eq(RecruitUser::getIsDeleted, IS_NOT_DELETED)
                .eq(RecruitUser::getStatus, status);
        Long count = recruitMapper.selectCount(queryWrapper);
        return ResponseResult.OK.put(count);
    }

    @Override
    public ResponseResult<?> getAboutMe(Integer status, HttpServletRequest request, PageDTO pageDTO) {
        if (!status.equals(WAIT_FEEDBACK) && !status.equals(ADMIT) && !status.equals(NOT_ADMIT)) {
            return ResponseResult.PARAM_ILLEGAL;
        }
        Page<RecruitInterview> page = new Page<>(pageDTO.getPageNo(), pageDTO.getPageSize());
        User userInfo = userUtil.getUserInfo(request);
        Long id = userInfo.getId();
        LambdaQueryWrapper<RecruitInterview> queryWrapper = new QueryWrapper<RecruitInterview>()
                .lambda()
                .eq(RecruitInterview::getInterviewStatus, status)
                .and(wrapper ->
                        wrapper.eq(RecruitInterview::getFirstHr, id)
                                .or()
                                .eq(RecruitInterview::getSecondHr, id)
                                .or()
                                .eq(RecruitInterview::getThirdHr, id)
                );
        IPage<RecruitInterviewVO> pageRes = recruitInterviewMapper.selectMyPage(page, queryWrapper);
        PageVO<RecruitInterviewVO> pageVO = new PageVO<>(pageRes.getCurrent(), pageRes.getSize(), pageRes.getTotal(), pageRes.getRecords());
        return ResponseResult.OK.put(pageVO);
    }

    @Override
    public ResponseResult<?> getClazz(String grade) {
        if (grade == null) {
            return ResponseResult.PARAM_ILLEGAL;
        }

        LambdaQueryWrapper<RecruitClass> queryWrapper = new LambdaQueryWrapper<RecruitClass>()
                .eq(RecruitClass::getGrade, grade)
                .eq(RecruitClass::getIsDeleted, IS_NOT_DELETED);

        List<RecruitClass> recruitClasses = recruitClassMapper.selectList(queryWrapper);

        return ResponseResult.OK.put(recruitClasses);
    }

    @Override
    public ResponseResult<?> getName() {
        LambdaQueryWrapper<RecruitUser> queryWrapper = new QueryWrapper<RecruitUser>()
                .lambda()
                //一次性选取多个字段，因为select()方法会覆盖之前的选择
                .select(RecruitUser::getId, RecruitUser::getName)
                .eq(RecruitUser::getIsDeleted, IS_NOT_DELETED)
                .eq(RecruitUser::getStatus, STATUS_WAIT_ARRANGE);
        List<RecruitUser> recruitUsers = recruitMapper.selectList(queryWrapper);
        List<RecruitNameVO> result = new ArrayList<>(recruitUsers.size());
        //对象转换
        for (RecruitUser recruitUser : recruitUsers){
            RecruitNameVO recruitNameVO = new RecruitNameVO();
            BeanUtils.copyProperties(recruitUser,recruitNameVO);
            result.add(recruitNameVO);
        }
        return ResponseResult.OK.put(result);
    }
}
