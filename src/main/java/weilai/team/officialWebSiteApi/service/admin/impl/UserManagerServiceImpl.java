package weilai.team.officialWebSiteApi.service.admin.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import weilai.team.officialWebSiteApi.entity.admin.DO.User;
import weilai.team.officialWebSiteApi.entity.admin.DO.UserGroupLeader;
import weilai.team.officialWebSiteApi.entity.admin.DO.UserPermission;
import weilai.team.officialWebSiteApi.entity.admin.DTO.*;
import weilai.team.officialWebSiteApi.entity.admin.VO.*;
import weilai.team.officialWebSiteApi.mapper.admin.UserCourseMapper;
import weilai.team.officialWebSiteApi.mapper.admin.UserGroupLeaderMapper;
import weilai.team.officialWebSiteApi.mapper.admin.UserMapper;
import weilai.team.officialWebSiteApi.mapper.admin.UserPermissionMapper;
import weilai.team.officialWebSiteApi.mapper.post.PostMapper;
import weilai.team.officialWebSiteApi.mapper.post.UserCollectMapper;
import weilai.team.officialWebSiteApi.service.admin.UserManagerService;
import weilai.team.officialWebSiteApi.service.recruit.FileService;
import weilai.team.officialWebSiteApi.util.*;
import static weilai.team.officialWebSiteApi.util.Values.*;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.validation.constraints.NotNull;
import java.util.*;
import java.util.concurrent.TimeUnit;


/**
 * ClassName:UserManagerServiceImpl
 * Description:
 *
 * @Author:独酌
 * @Create:2024/12/2 9:51
 */
@Service
public class UserManagerServiceImpl implements UserManagerService {

    @Resource
    private UserMapper userMapper;

    @Resource
    private UserPermissionMapper userPermissionMapper;

    @Resource
    private PostMapper postMapper;

    @Resource
    private UserCourseMapper userCourseMapper;

    @Resource
    private UserCollectMapper userCollectMapper;

    @Resource
    private UserGroupLeaderMapper userGroupLeaderMapper;

    @Resource
    private RedisCacheUtil redisCacheUtil;

    @Resource
    private FileService fileService;

    @Resource
    private UserUtil userUtil;

    @Resource
    private RedisUtil redisUtil;

    /*
        查看社区，个人资料，消息 -- 不需要权限，登录即可
        通讯录、概况权限
        社区管理权限
        招新管理权限
        权限管理的权限
     */
    private static final Set<String> authoritySet = new HashSet<>(Arrays.asList("notice_admin","team_admin","community_admin","recruit_admin","admin_plus","attendance_admin","teacher"));

    @Override
    @Transactional
    public ResponseResult<?> modifyUserAuthorities(ModifyUserAuthDTO modifyUserAuthDTO, HttpServletRequest request) {
        //获取用户的id
        Long userId = modifyUserAuthDTO.getUserId();

        //获取当前用户的信息
        User currentUser = userUtil.getUserInfo(request);

        //添加用户的权限
        String[] authority = modifyUserAuthDTO.getAuthority();

        //判断权限是否存在，是否符合预期
        if(authority == null || authority.length > authoritySet.size())
            return ResponseResult.PARAM_ERROR;

        Set<String> set = new HashSet<>(Arrays.asList(authority));
        // 如果我要修改自己的权限，那末新增的权限里面必须要有 admin_plus 权限
        if(userId.equals(currentUser.getId()) && !set.contains("admin_plus")) {
            return ResponseResult.PARAM_ERROR;
        }

        //删除用户以前的权限
        userPermissionMapper.deleteByUserIdIn(Collections.singletonList(userId));

        // 判断是否有面试管的权限
        boolean isInterview = false;

        for(String s : authority){
            boolean contains = authoritySet.contains(s);
            if(!contains)
                continue;
            if("admin_plus".equals(s) && !userId.equals(currentUser.getId())) {
                // 超级管理员权限转移
                List<String> auth = currentUser.getAuth();
                auth.remove("admin_plus");
                currentUser.setAuth(auth);
                currentUser.setPassword(null);
                // 修改将数据库中的我的这个权限删除掉
                userPermissionMapper.deleteByUserIdAndAuthority(currentUser.getId(),"admin_plus");
                //更新Redis中的权限
                redisUtil.updateUserAuth(currentUser);
            }
            //添加用户的权限
            UserPermission userPermission = new UserPermission();
            userPermission.setUserId(userId);
            userPermission.setAuthority(s);

            //添加用户的权限
            userPermissionMapper.insert(userPermission);

            if("recruit_admin".equals(s)) {
                isInterview = true;
            }
        }

        if(isInterview) {
            userMapper.updateIsInterview(userId,1);
        } else {
            userMapper.updateIsInterview(userId,0);
        }

        //删除redis中的缓存
        redisCacheUtil.deleteRedis(USER_AUTHORITY_PREFIX);

        //更新用户的权限
        User user = userMapper.selectById(userId);
        user.setPassword(null);
        user.setAuth(Arrays.asList(authority));
        redisUtil.updateUserAuth(user);

        return ResponseResult.OK;
    }

    @Override
    public ResponseResult<?> reachUser(String content, String[] auth, Integer pageNumber, Integer pageSize) {
        //判断权限是否存在，是否符合预期
        if(auth != null && auth.length > 5) return ResponseResult.PARAM_ERROR;

        //添加缓存
        Map<?,?> reMap = redisCacheUtil.queryWithPassThroughByHash(
                USER_AUTHORITY_PREFIX,
                "",
                content + "." + Arrays.toString(auth) + "." + pageNumber + "." + pageSize,
                Map.class,
                () -> {
                    //由于在函数中，不能改变变量的值，只能创建一个临时变量来储存查询的内容
                    String countTemp = content;

                    //创建权限列表用于查询
                    List<String> list = null;
                    if (auth != null && auth.length != 0) list = Arrays.asList(auth);

                    //判断搜索框是否为空，为空查询全部，否则，查询
                    if (countTemp == null || countTemp.trim().isEmpty()) countTemp = "%";
                    else countTemp = "%" + countTemp + "%";

                    //查询
                    Page<UserListVO> pageInfo = new Page<>(pageNumber, pageSize);
                    List<UserListVO> userListVOS = userMapper.reachUserLike(pageInfo, countTemp, list);

                    //返回数据
                    Map<String, Object> map = new HashMap<>();
                    map.put("pageInfo", pageInfo);
                    map.put("userList", userListVOS);
                    return map;
                },
                USER_AUTHORITY_OUT_TIME,
                TimeUnit.HOURS
        );

        return ResponseResult.OK.put(reMap);
    }

    @Override
    public ResponseResult<?> getUserListByGroup(String grade, String group, Integer pageNumber, Integer pageSize) {
        Map<?,?> reMap = redisCacheUtil.queryWithPassThroughByHash(
                TEAM_GROUP_PREFIX,
                "teamListByGroup",
                grade + "." + group + "." + pageNumber + "." + pageSize,
                Map.class,
                () -> {
                    //查询
                    Page<User> pageInfo = new Page<>(pageNumber, pageSize);
                    List<User> users = userMapper.selectAllByGradeAndGroup(pageInfo, grade, group);
                    List<TeamUserListVO> teamUserList = new ArrayList<>();
                    users.forEach((u) -> teamUserList.add(new TeamUserListVO(u,false)));

                    //查询组长
                    Long groupLeader = userGroupLeaderMapper.selectGroupLeaderIdByGroupPostiton(grade + "$" + group);
                    if (groupLeader != null) {
                        User user = getUser(groupLeader);
                        TeamUserListVO userGroupLeader = new TeamUserListVO(user,true);
                        teamUserList.add(0, userGroupLeader);
                    }

                    //返回数据
                    Map<String, Object> map = new HashMap<>();
                    map.put("teamUserList", teamUserList);
                    map.put("pageInfo", pageInfo);
                    return map;
                }, TEAM_GROUP_OUT_TIME, TimeUnit.HOURS
        );

        return ResponseResult.OK.put(reMap);
    }

    @Override
    public ResponseResult<?> getTeamUserList(Integer pageNumber, Integer pageSize) {
        Map<?,?> reMap = redisCacheUtil.queryWithPassThroughByHash(
                TEAM_GROUP_PREFIX,
                "teamList",
                pageNumber + "." + pageSize,
                Map.class,
                () -> {
                    //查询数量
                    Long userCount = userMapper.getUserCount();

                    //查询团队的概况
                    List<TeamAbleVO> teamAble = userMapper.getTeamAble();
                    teamAble.forEach(t -> {
                        List<String> group = t.getGroup();
                        group.sort(Comparator.comparingInt(o -> Integer.parseInt(o.split("\\$")[0])));
                    });

                    //查询团队的用户
                    Page<User> pageInfo = new Page<>(pageNumber, pageSize);
                    List<User> users = userMapper.selectAll(pageInfo);
                    List<TeamUserListVO> teamUserList = new ArrayList<>();
                    users.forEach((u) -> teamUserList.add(new TeamUserListVO(u,false)));

                    //返回数据
                    Map<String, Object> map = new HashMap<>();
                    map.put("userCount", userCount);
                    map.put("teamAble", teamAble);
                    map.put("teamUserList", teamUserList);
                    map.put("pageInfo", pageInfo);
                    return map;
                }, TEAM_GROUP_OUT_TIME, TimeUnit.HOURS
        );

        return ResponseResult.OK.put(reMap);
    }

    @Override
    public ResponseResult<?> modifyTeamUserInfo(UpdateTeamUserInfoDTO updateTeamUserInfoDTO) {
        Long id = updateTeamUserInfoDTO.getId();
        User user = getUser(id);
        if(user == null) return ResponseResult.USER_NOT_FOUND;

        String name = updateTeamUserInfoDTO.getName();
        String group = updateTeamUserInfoDTO.getGroup();
        String grade = updateTeamUserInfoDTO.getGrade();
        String clazz = updateTeamUserInfoDTO.getClazz();
        String studyId = updateTeamUserInfoDTO.getStudyId();

        if(name != null && !name.isEmpty()) {
            user.setName(name);
            //删除redis中的缓存
            redisCacheUtil.deleteRedis(TEAM_SEARCH_PREFIX);
        }
        if(group!= null &&!group.isEmpty()) user.setGroup(group);
        if(grade!= null &&!grade.isEmpty()) user.setGrade(grade);
        if(clazz!= null &&!clazz.isEmpty()) user.setClazz(clazz);
        if(studyId!= null &&!studyId.isEmpty()) user.setStudyId(studyId);

        int i = userMapper.updateById(user);

        //删除redis中的缓存
        //删除第一次请求的缓存
        redisCacheUtil.deleteRedis(TEAM_GROUP_PREFIX + "teamList");
        redisCacheUtil.deleteRedis(TEAM_GROUP_PREFIX + "teamListByGroup");
        redisCacheUtil.deleteRedis(TEAM_GROUP_PREFIX + "teamListByGrade");
        redisCacheUtil.deleteRedis(TEAM_SEARCH_PREFIX);
        return i <= 0 ? ResponseResult.SERVICE_ERROR : ResponseResult.OK;
    }

    @Override
    public ResponseResult<?> deleteTeamUserInfo(List<Long> id) {
        if(id == null || id.isEmpty()) return ResponseResult.USER_NOT_FOUND;
        //删除用户
        int i = userMapper.deleteBatchIds(id);
        //删除用户贴子
        postMapper.deleteByUserIdIn(id);
        //删除用户的收藏
        userCollectMapper.deleteByUserIdIn(id);
        //删除用户的权限
        userPermissionMapper.deleteByUserIdIn(id);
        //删除用户的课程表
        userCourseMapper.deleteByUseIdIn(id);
        //删除组长
        userGroupLeaderMapper.delByGroupLeaderIdIn(id);

        //删除redis中的缓存
        //删除第一次请求的缓存
        redisCacheUtil.deleteRedis(TEAM_GROUP_PREFIX + "teamList");
        redisCacheUtil.deleteRedis(TEAM_GROUP_PREFIX + "teamListByGroup");
        redisCacheUtil.deleteRedis(TEAM_GROUP_PREFIX + "teamListByGrade");
        redisCacheUtil.deleteRedis(TEAM_SEARCH_PREFIX);
        redisCacheUtil.deleteRedis(USER_AUTHORITY_PREFIX);
        return i <= 0 ? ResponseResult.SERVICE_ERROR : ResponseResult.OK;
    }

    @Override
    public ResponseResult<?> searchTeamUser(String content) {
        //查询用户，只有用户姓名被修改时，才需要更新数据
        List<?> teamSearchUser =
                redisCacheUtil.queryWithPassThroughByHash(
                TEAM_SEARCH_PREFIX,
                "",
                content,
                List.class,
                ()->userMapper.searchUserByNameLike("%" + content + "%"),
                TEAM_SEARCH_OUT_TIME,
                TimeUnit.HOURS
        );
        return ResponseResult.OK.put(teamSearchUser);
    }

    @Override
    public ResponseResult<?> getTeamUser(Long userId) {
        // 这里没有做对这个的更新缓存的操作，因此过期时间设置的短一点，5 分钟
        TeamUserVO teamUser = redisCacheUtil.queryWithPassThroughByString(
                TEAM_USER_PREFIX,
                userId,
                TeamUserVO.class,
                () -> {
                    User user = userMapper.selectById(userId);
                    if (user == null) return null;
                    Long l = userGroupLeaderMapper.selectGroupLeaderIdByGroupPostiton(user.getGrade() + "$" + user.getGroup());
                    User ladleUser = null;
                    if (l != null) {
                        ladleUser = userMapper.selectById(l);
                    }
                    List<Integer> groupByGrade = userMapper.getGroupByGrade(user.getGrade());
                    return new TeamUserVO(user, ladleUser, groupByGrade);
                },
                TEAM_USER_OUT_TIME,
                TimeUnit.MINUTES
        );
        if(teamUser == null) return ResponseResult.USER_NOT_FOUND;
        
        return ResponseResult.OK.put(teamUser);
                
    }

    @Override
    public ResponseResult<?> setGroupLabel(SetUserGroupLadle setUserGroupLadle) {
        //查询用户是否存在
        User user = getUser(setUserGroupLadle.getUserId());
        if(user == null) return ResponseResult.USER_NOT_FOUND;

        //判断是否已经存在组长了
        Long l = userGroupLeaderMapper.selectGroupLeaderIdByGroupPostiton(setUserGroupLadle.getGroup());
        if(l != null) return ResponseResult.GROUP_LADLE_EXIT;

        UserGroupLeader userGroupLeader = new UserGroupLeader();
        userGroupLeader.setGroupLeaderId(setUserGroupLadle.getUserId());
        userGroupLeader.setGroupPostiton(setUserGroupLadle.getGroup());
        userGroupLeaderMapper.insert(userGroupLeader);
        //删除redis中的缓存
        //删除第一次请求的缓存
        redisCacheUtil.deleteRedis(TEAM_GROUP_PREFIX + "teamList");
        redisCacheUtil.deleteRedis(TEAM_GROUP_PREFIX + "teamListByGroup");
        return ResponseResult.OK;
    }

    @Override
    public ResponseResult<?> addUsers(MultipartFile file) {
        //删除redis中的缓存
        //删除第一次请求的缓存
        redisCacheUtil.deleteRedis(TEAM_GROUP_PREFIX + "teamList");
        redisCacheUtil.deleteRedis(TEAM_GROUP_PREFIX + "teamListByGroup");
        redisCacheUtil.deleteRedis(TEAM_GROUP_PREFIX + "teamListByGrade");
        redisCacheUtil.deleteRedis(USER_AUTHORITY_PREFIX);
        redisCacheUtil.deleteRedis(TEAM_SEARCH_PREFIX);

        return fileService.uploadExcelFile(file);
    }

    @Override
    public ResponseResult<?> addUser(AddUserDTO addUserDTO) {
        User user1 = userMapper.selectAllByUsernameOrEmail(addUserDTO.getStudyId());

        User user2 = userMapper.selectAllByUsernameOrEmail(addUserDTO.getEmail());

        if(user1 != null || user2!= null) return ResponseResult.GROUP_USER_EXIT;

        User user = new User();
        String name = addUserDTO.getName();
        String studyId = addUserDTO.getStudyId();
        String grade = addUserDTO.getGrade();
        String clazz = addUserDTO.getClazz();
        String group = addUserDTO.getGroup();
        String sex = addUserDTO.getSex();
        String qq = addUserDTO.getQQ();
        String email = addUserDTO.getEmail();
        String phone = addUserDTO.getPhone();

        if(name == null || studyId == null ||
                grade == null || clazz == null ||
                group == null || sex == null ||
                qq == null || email == null || phone == null)
            return ResponseResult.PARAM_ERROR;

        user.setName(name);
        user.setUsername(studyId);
        user.setPassword(DEFAULT_PASSWORD);
        user.setStudyId(studyId);
        user.setGrade(grade);
        user.setClazz(clazz);
        user.setGroup(group);
        user.setSex(sex);
        user.setQq(qq);
        user.setEmail(email);
        user.setPhone(phone);
        int insert = userMapper.insert(user);

        //删除redis中的缓存
        //删除第一次请求的缓存
        redisCacheUtil.deleteRedis(TEAM_GROUP_PREFIX + "teamList");
        redisCacheUtil.deleteRedis(TEAM_GROUP_PREFIX + "teamListByGroup");
        redisCacheUtil.deleteRedis(TEAM_GROUP_PREFIX + "teamListByGrade");
        redisCacheUtil.deleteRedis(USER_AUTHORITY_PREFIX);
        redisCacheUtil.deleteRedis(TEAM_SEARCH_PREFIX);

        return insert <= 0? ResponseResult.SERVICE_ERROR : ResponseResult.OK;
    }

    @Override
    public ResponseResult<?> getUsersByGrade(String grade) {
        List<?> usersByGrade = redisCacheUtil.queryWithPassThroughByHash(
                TEAM_GROUP_PREFIX,
                "teamListByGrade",
                grade,
                List.class,
                ()-> userMapper.getUsersByGrade(grade),
                TEAM_GROUP_OUT_TIME,
                TimeUnit.HOURS
        );
        return ResponseResult.OK.put(usersByGrade);
    }

    @Override
    public ResponseResult<?> modifyManyUser(ModifyManyUserDTO modifyManyUserDTO) {
        int i = userMapper.modifyManyUser(Arrays.asList(modifyManyUserDTO.getIds()),
                modifyManyUserDTO.getGroup(), modifyManyUserDTO.getGrade(),
                modifyManyUserDTO.getClazz());

        redisCacheUtil.deleteRedis(TEAM_GROUP_PREFIX + "teamList");
        redisCacheUtil.deleteRedis(TEAM_GROUP_PREFIX + "teamListByGroup");
        redisCacheUtil.deleteRedis(TEAM_GROUP_PREFIX + "teamListByGrade");
        return i <= 0 ? ResponseResult.SERVICE_ERROR : ResponseResult.OK;
    }

    @Override
    public ResponseResult<?> cancelGroupLarder(Long userId, String group) {
        int i = userGroupLeaderMapper.delByGroupLeaderIdAndGroupPostiton(userId,group);
        //删除redis中的缓存
        //删除第一次请求的缓存
        redisCacheUtil.deleteRedis(TEAM_GROUP_PREFIX + "teamList");
        redisCacheUtil.deleteRedis(TEAM_GROUP_PREFIX + "teamListByGroup");
        redisCacheUtil.deleteRedis(TEAM_GROUP_PREFIX + "teamListByGrade");
        return ResponseResult.OK;
    }

    private User getUser(Long userId){
        //利用Redis进行缓存
        return redisCacheUtil.queryWithPassThroughByString(
                USER_INFO_PREFIX,
                userId,
                User.class,()-> userMapper.selectById(userId),
                USER_INFO_OUT_TIME,
                TimeUnit.HOURS
        );
    }
}
