package weilai.team.officialWebSiteApi.service.recruit.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import weilai.team.officialWebSiteApi.entity.admin.DO.User;
import weilai.team.officialWebSiteApi.entity.recruit.DO.RecruitInterview;
import weilai.team.officialWebSiteApi.entity.recruit.DO.RecruitUser;
import weilai.team.officialWebSiteApi.entity.recruit.DTO.PageDTO;
import weilai.team.officialWebSiteApi.entity.recruit.DTO.RecruitCommentDTO;
import weilai.team.officialWebSiteApi.entity.recruit.DTO.RecruitInterviewDTO;
import weilai.team.officialWebSiteApi.entity.recruit.DTO.ScheduleInterviewDTO;
import weilai.team.officialWebSiteApi.entity.recruit.VO.HrVO;
import weilai.team.officialWebSiteApi.entity.recruit.VO.PageVO;
import weilai.team.officialWebSiteApi.entity.recruit.VO.RecruitInterviewVO;
import weilai.team.officialWebSiteApi.mapper.admin.UserMapper;
import weilai.team.officialWebSiteApi.mapper.recruit.RecruitInterviewMapper;
import weilai.team.officialWebSiteApi.mapper.recruit.RecruitMapper;
import weilai.team.officialWebSiteApi.service.recruit.RecruitInterviewService;
import weilai.team.officialWebSiteApi.util.DateUtil;
import weilai.team.officialWebSiteApi.util.LogUtil;
import weilai.team.officialWebSiteApi.util.ResponseResult;
import weilai.team.officialWebSiteApi.util.UserUtil;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static weilai.team.officialWebSiteApi.entity.recruit.enums.InterviewStatueEnum.WAIT_ARRANGE;
import static weilai.team.officialWebSiteApi.entity.recruit.enums.MysqlConstant.*;

/**
 * @author lzw
 * @date 2024/11/11 15:08
 * @description 作用：招新的面试相关业务实现
 */
@Service
public class RecruitInterviewServiceImpl implements RecruitInterviewService {


    @Resource
    private RecruitInterviewMapper recruitInterviewMapper;

    @Resource
    private UserMapper userMapper;

    @Resource
    private UserUtil userUtil;

    @Resource
    private RecruitMapper recruitMapper;


    @Transactional
    @Override
    public ResponseResult<?> scheduleInterviewer(ScheduleInterviewDTO scheduleInterviewDTO) {
        Long userId = scheduleInterviewDTO.getUserId();
        RecruitUser recruitUser = recruitMapper.selectOne(new QueryWrapper<RecruitUser>().lambda().eq(RecruitUser::getId, userId));
        if (recruitUser == null){
            return ResponseResult.ID_IS_NOT_EXIST;
        }
        //dto对象拷贝
        RecruitInterview recruitInterview = new RecruitInterview();
        recruitInterview.setName(recruitUser.getName());
        recruitInterview.setGrade(recruitUser.getGrade());
        BeanUtils.copyProperties(scheduleInterviewDTO, recruitInterview);
        //1.修改候选人状态为待面试
        UpdateWrapper<RecruitUser> updateWrapper = new UpdateWrapper<>();
        updateWrapper.lambda()
                .eq(RecruitUser::getId, recruitInterview.getUserId())
                .set(RecruitUser::getStatus, STATUS_WAIT_INTERVIEW)
                .set(RecruitUser::getUpdateTime, LocalDateTime.now());
        recruitMapper.update(null, updateWrapper);
        //2.增加记录
        // 判断是不是第二次面试
        LambdaQueryWrapper<RecruitInterview> queryWrapper = new QueryWrapper<RecruitInterview>()
                .lambda()
                .eq(RecruitInterview::getUserId, userId);
        // 设置为二面
        if (recruitInterviewMapper.exists(queryWrapper)) {
            recruitInterview.setRound(2);
        }
        recruitInterviewMapper.insert(recruitInterview);
        return ResponseResult.OK;
    }

    @Transactional
    @Override
    public ResponseResult<?> insertComment(RecruitCommentDTO recordDTO) {
        Long id = recordDTO.getId();
        Long userId = recordDTO.getUserId();
        Integer status = recordDTO.getStatus();
        String comment = recordDTO.getComment();
        Integer isSecond = recordDTO.getIsSecond();
        //先判断状态参数是否正确
        if (!Objects.equals(status, NOT_ADMIT) && !Objects.equals(status, ADMIT)) {
            return ResponseResult.STATUS_NOT_FOUND;
        }

        //构建RecruitInterview表中的修改条件，先修改面评和状态
        LambdaUpdateWrapper<RecruitInterview> updateWrapper = new UpdateWrapper<RecruitInterview>()
                .lambda()
                .eq(RecruitInterview::getId, id)
                .set(RecruitInterview::getComment, comment);

        //构建recruit_user 表中的修改条件
        LambdaUpdateWrapper<RecruitUser> recruitWrapper = new UpdateWrapper<RecruitUser>().lambda();
        recruitWrapper.eq(RecruitUser::getId,userId).set(RecruitUser::getUpdateTime, LocalDateTime.now());
        //待二面，相当于是一面未录取，将面试记录状态设置成未录取，recruit_user表中用户的状态还是待安排,只需要更新时间
        if (isSecond.equals(1)){
            updateWrapper.set(RecruitInterview::getInterviewStatus, NOT_ADMIT);
            // 将用户状态设置为待安排
            recruitWrapper.set(RecruitUser::getStatus, WAIT_ARRANGE.getCode());
        }else if (isSecond.equals(0)){
            //未录取和录取，直接修改面试状态，recruit_user表中用户的状态也要修改为已录取或淘汰
            updateWrapper.set(RecruitInterview::getInterviewStatus, status);
            //更新recruitUser表中的时间,同时更新用户的面试状态
            recruitWrapper.set(RecruitUser::getStatus, status);
        }else{
            return ResponseResult.PARAM_ILLEGAL_INTERVIEW;
        }
        recruitInterviewMapper.update(null, updateWrapper);
        recruitMapper.update(null, recruitWrapper);
        return ResponseResult.OK;
    }

    @Override
    public ResponseResult<?> listAllInterview(RecruitInterviewDTO interviewDTO, HttpServletRequest request) {
        // 校验面试状态
        Integer interviewStatus = interviewDTO.getStatus();
        if (interviewStatus < ALL_INTERVIEW || interviewStatus > NOT_ADMIT) {
            return ResponseResult.INTERVIEW_PARAM_ILLEGAL;
        }

        // 构建查询条件
        LambdaQueryWrapper<RecruitInterview> wrapper = buildQueryWrapper(interviewDTO, request);

        // 联表查询
        List<RecruitInterviewVO> list = recruitInterviewMapper.selectMyList(wrapper);
        return ResponseResult.OK.put(list);
    }

    /**
     * 构建查询条件
     */
    private LambdaQueryWrapper<RecruitInterview> buildQueryWrapper(RecruitInterviewDTO interviewDTO, HttpServletRequest request) {
        LambdaQueryWrapper<RecruitInterview> wrapper = new LambdaQueryWrapper<>();

        // 根据面试状态设置查询条件
        Integer interviewStatus = interviewDTO.getStatus();
        if (WAIT_MY_INTERVIEW.equals(interviewStatus)) {
            // 查询待我面试的数据
            User userInfo = userUtil.getUserInfo(request);
            Long userId = userInfo.getId();
            wrapper.and(w -> w
                    .eq(RecruitInterview::getFirstHr, userId)
                    .or()
                    .eq(RecruitInterview::getSecondHr, userId)
                    .or()
                    .eq(RecruitInterview::getThirdHr, userId)
            );
        } else if (!ALL_INTERVIEW.equals(interviewStatus)) {
            // 查询待反馈、已录取、未录取的数据
            wrapper.eq(RecruitInterview::getInterviewStatus, interviewStatus);
        }

        // 设置姓名查询
        wrapper.like(StringUtils.isNotBlank(interviewDTO.getName()), RecruitInterview::getName, interviewDTO.getName());

        // 设置面试轮次
        wrapper.eq(interviewDTO.getRound() != null, RecruitInterview::getRound, interviewDTO.getRound());

        // 设置时间范围
        wrapper.between(interviewDTO.getStartTime() != null && interviewDTO.getEndTime() != null,
                RecruitInterview::getStartTime, interviewDTO.getStartTime(), interviewDTO.getEndTime());

        // 设置年级
        wrapper.eq(StringUtils.isNotBlank(interviewDTO.getGrade()), RecruitInterview::getGrade, interviewDTO.getGrade());

        // 设置面试官 ID
        if (interviewDTO.getIds() != null && !interviewDTO.getIds().isEmpty()) {
            wrapper.and(w -> buildInterviewerCondition(w, interviewDTO.getIds()));
        }

        return wrapper;
    }

    /**
     * 构建面试官 ID 查询条件
     */
    private void buildInterviewerCondition(LambdaQueryWrapper<RecruitInterview> wrapper, List<Long> ids) {
        wrapper.and(w -> w
                .in(RecruitInterview::getFirstHr, ids)
                .or()
                .in(RecruitInterview::getSecondHr, ids)
                .or()
                .in(RecruitInterview::getThirdHr, ids)
        );
    }


    @Override
    public ResponseResult<?> listAllHr(PageDTO pageDTO, String name) {
        Page<User> page = new Page<>(pageDTO.getPageNo(), pageDTO.getPageSize());
        //查询面试官的id、姓名、头像
        LambdaQueryWrapper<User> queryWrapper = new QueryWrapper<User>()
                .lambda()
                .eq(User::getIsInterview, IS_HR)
                .like(name != null, User::getName, name)
                .select(User::getId, User::getName, User::getHeadPortrait);

        Page<User> userPage = userMapper.selectPage(page, queryWrapper);
        List<User> userList = userPage.getRecords();
        //将userList集合转换为HrVO集合
        List<HrVO> hrVOList = userList.stream().map(user -> {
            HrVO vo = new HrVO();
            BeanUtils.copyProperties(user, vo);
            return vo;
        }).collect(Collectors.toList());
        //返回分页的对象
        PageVO<HrVO> hrVOPageVO = new PageVO<>(userPage.getCurrent(), userPage.getSize(), userPage.getTotal(), hrVOList);
        return ResponseResult.OK.put(hrVOPageVO);
    }

    @Override
    public ResponseResult<?> getComment(Integer id) {
        LambdaQueryWrapper<RecruitInterview> wrapper = new QueryWrapper<RecruitInterview>()
                .lambda()
                .eq(RecruitInterview::getId, id)
                .select(RecruitInterview::getComment);
        RecruitInterview recruitInterview = recruitInterviewMapper.selectOne(wrapper);
        if (recruitInterview == null) {
            return ResponseResult.INTERVIEW_NOT_FOUND;
        }
        return ResponseResult.OK.put(recruitInterview.getComment());
    }
}
