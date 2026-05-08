package weilai.team.officialWebSiteApi.service.admin.impl;

import com.alibaba.fastjson2.JSON;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import weilai.team.officialWebSiteApi.config.MinioConfigProperties;
import weilai.team.officialWebSiteApi.entity.admin.DO.User;
import weilai.team.officialWebSiteApi.entity.admin.DO.UserCourse;
import weilai.team.officialWebSiteApi.entity.admin.DTO.*;
import weilai.team.officialWebSiteApi.entity.admin.VO.*;
import weilai.team.officialWebSiteApi.entity.recruit.enums.FileTypeEnum;
import weilai.team.officialWebSiteApi.mapper.admin.UserCourseMapper;
import weilai.team.officialWebSiteApi.mapper.admin.UserMapper;
import weilai.team.officialWebSiteApi.mapper.admin.UserPermissionMapper;
import weilai.team.officialWebSiteApi.mapper.community.CommunityTagMapper;
import weilai.team.officialWebSiteApi.mapper.community.CommunityTagPostRelationsMapper;
import weilai.team.officialWebSiteApi.service.admin.UserService;
import weilai.team.officialWebSiteApi.util.*;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.beans.Transient;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import static weilai.team.officialWebSiteApi.service.admin.impl.UserServiceImpl.Course.*;

/**
 * ClassName:UserServiceImpl
 * Description:
 *
 * @Author:独酌
 * @Create:2024/11/12 11:45
 */
@Slf4j
@Service
public class UserServiceImpl implements UserService {

    @Resource
    private UserUtil userUtil;

    @Resource
    private RedisUtil redisUtil;

    @Resource
    private UserMapper userMapper;

    @Resource
    private UserCourseMapper userCourseMapper;

    @Resource
    private RedisCacheUtil redisCacheUtil;

    @Resource
    private MinioUtil minioUtil;

    @Resource
    private MinioConfigProperties minioConfigProperties;

    @Resource
    private CommunityTagMapper communityTagMapper;

    @Resource
    private CommunityTagPostRelationsMapper communityTagPostRelationsMapper;

    @Resource
    private UserPermissionMapper userPermissionMapper;

    @Override
    public ResponseResult<?> getUserInfoByUserId(Long userId) {
        //利用Redis进行缓存
        User user = redisCacheUtil.queryWithPassThroughByString(
                Values.USER_INFO_PREFIX,
                userId,
                User.class,()-> userMapper.selectById(userId),
                Values.USER_INFO_OUT_TIME,
                TimeUnit.HOURS
        );
        if(user == null)
            return ResponseResult.USER_NOT_FOUND;
        List<String> authority = redisCacheUtil.queryWithPassThroughToList(
                Values.USER_PERMISSION,
                userId,
                String.class,
                () -> userPermissionMapper.getAuthorityByUserId(userId),
                Values.USER_PERMISSION_OUT_TIME,
                TimeUnit.HOURS
        );
        user.setAuth(authority);
        return ResponseResult.OK.put(new UserInfoVO(user));
    }

    @Override
    public ResponseResult<?> updateUserInfo(UpdateUserInfoDTO updateUserInfoDTO, HttpServletRequest request) {
        User user = userUtil.getUserInfo(request);
        //判断并更新其他信息
        if (!MyString.isNull(updateUserInfoDTO.getPhone()) && updateUserInfoDTO.getPhone().length() <= 11) user.setPhone(updateUserInfoDTO.getPhone());
        if (!MyString.isNull(updateUserInfoDTO.getQq()) && updateUserInfoDTO.getQq().length() <= 15) user.setQq(updateUserInfoDTO.getQq());
        if (!MyString.isNull(updateUserInfoDTO.getGraduationDestination()) && updateUserInfoDTO.getGraduationDestination().length() <= 20) user.setGraduationDestination(updateUserInfoDTO.getGraduationDestination());
        if (!MyString.isNull(updateUserInfoDTO.getUserDestination()) && updateUserInfoDTO.getUserDestination().length() <= 50) user.setUserDestination(updateUserInfoDTO.getUserDestination());
        if(updateUserInfoDTO.getLanQiaoCount() != null) user.setLanQiaoCount(updateUserInfoDTO.getLanQiaoCount());
        if(updateUserInfoDTO.getCopyrightCount() != null) user.setCopyrightCount(updateUserInfoDTO.getCopyrightCount());
        if(updateUserInfoDTO.getSalaryYear() != null) user.setSalaryYear(updateUserInfoDTO.getSalaryYear());
        if(!MyString.isNull(updateUserInfoDTO.getArea())) user.setArea(updateUserInfoDTO.getArea());

        //更新数据库
        int i = userMapper.updateById(user);
        if (i <= 0) {
            return ResponseResult.Bad_Request;
        }

        //更新redis
        redisUtil.setRedisObjectWithOutTime(Values.REDIS_TOKEN_PREFIX + user.getUsername(), user, Values.OUT_TIME);

        //删除Redis缓存
        redisCacheUtil.deleteRedis(Values.USER_INFO_PREFIX + user.getId());

        return ResponseResult.OK;
    }

    /**
     * 获取用户的生活照片
     * @param userId 用户id
     * @return 用户生活照片
     */
    @Override
    public ResponseResult<?> getUserLifePhotoByUserId(Long userId) {
        List<String> leftPhoto = redisCacheUtil.queryWithPassThroughToList(
                Values.USER_LEFT_PHOTO,
                userId,
                String.class,
                () -> {
                    UserLifePhoto r = userMapper.selectLifePhotoById(userId);
                    return r == null ? null : r.getLifePhoto();
                },
                Values.USER_LEFT_PHOTO_OUT_TIME,
                TimeUnit.HOURS
        );
        return ResponseResult.OK.put(leftPhoto);
    }

    @Override
    public ResponseResult<?> updateUserHeadPortrait(MultipartFile headPortrait, HttpServletRequest request) {
        User user = userUtil.getUserInfo(request);
        String originalFilename = user.getHeadPortrait();
        //更新头像
        if (headPortrait != null && FileUtil.isExpectedFileType(headPortrait, FileTypeEnum.IMAGE)) {
            String headPortraitPath = minioUtil.uploadImage(headPortrait, minioConfigProperties.getBucketName());
            if (headPortraitPath == null) {
                return ResponseResult.Bad_Request;
            }
            user.setHeadPortrait(headPortraitPath);
            //更新数据库
            int i = userMapper.updateById(user);
            if (i <= 0) {
                return ResponseResult.Bad_Request;
            }

            // 删除原文件
            minioUtil.deleteFile(originalFilename);

            //更新redis
            redisUtil.setRedisObjectWithOutTime(Values.REDIS_TOKEN_PREFIX + user.getUsername(), user, Values.OUT_TIME);

            //删除Redis缓存
            redisCacheUtil.deleteRedis(Values.USER_INFO_PREFIX + user.getId());

            return ResponseResult.OK;
        } else {
            return ResponseResult.Bad_Request;
        }

    }

    @Override
    public ResponseResult<?> addUserLifePhoto(List<MultipartFile> lifePhoto, HttpServletRequest request) {
        User user = userUtil.getUserInfo(request);
        List<String> ls = JSON.parseArray(user.getLifePhoto(), String.class);
        if(ls == null) ls = new ArrayList<>();
        //更新生活照片
        if (lifePhoto != null && !lifePhoto.isEmpty()) {
            for (MultipartFile l : lifePhoto) {
                String s = minioUtil.uploadImage(l, minioConfigProperties.getBucketName());
                if (s == null) return ResponseResult.SERVICE_ERROR;
                ls.add(s);
            }
            user.setLifePhoto(JSON.toJSONString(ls));
        } else {
            return ResponseResult.Bad_Request.put(lifePhoto);
        }

        //更新数据库
        int i = userMapper.updateById(user);
        if (i <= 0) {
            return ResponseResult.Bad_Request;
        }

        //更新redis
        redisUtil.setRedisObjectWithOutTime(Values.REDIS_TOKEN_PREFIX + user.getUsername(), user, Values.OUT_TIME);

        //删除Redis缓存
        redisCacheUtil.deleteRedis(Values.USER_INFO_PREFIX + user.getId());
        redisCacheUtil.deleteRedis(Values.USER_LEFT_PHOTO + user.getId());

        return ResponseResult.OK;
    }

    @Override
    public ResponseResult<?> deleteUserLifePhoto(String url, HttpServletRequest request) {
        if(url == null || url.isEmpty()) {
            return ResponseResult.Bad_Request.put("接收到的url：" + url);
        }
        User user = userUtil.getUserInfo(request);
        List<String> ls = JSON.parseArray(user.getLifePhoto(), String.class);
        if(ls != null)
            ls.remove(url);

        user.setLifePhoto(JSON.toJSONString(ls));
        //更新数据库
        int i = userMapper.updateById(user);
        if (i <= 0) {
            return ResponseResult.Bad_Request;
        }

        //删除文件
        minioUtil.deleteFile(url);

        //更新redis
        redisUtil.setRedisObjectWithOutTime(Values.REDIS_TOKEN_PREFIX + user.getUsername(), user, Values.OUT_TIME);

        //删除Redis缓存
        redisCacheUtil.deleteRedis(Values.USER_INFO_PREFIX + user.getId());
        redisCacheUtil.deleteRedis(Values.USER_LEFT_PHOTO + user.getId());

        return ResponseResult.OK;
    }

    @Override
    public ResponseResult<?> getUserCourse(Long userId) {
        //利用Redis进行缓存
        UserCourseVO userCourseVO = redisCacheUtil.queryWithPassThroughByString(
                Values.USER_COURSE_PREFIX,
                userId,
                UserCourseVO.class,
                ()-> new UserCourseVO(userCourseMapper.selectAllByUseId(userId)),
                Values.USER_COURSE_OUT_TIME,
                TimeUnit.HOURS
        );

        //判断是否有数据
        if (userCourseVO == null) {
            return ResponseResult.USER_NOT_FOUND_COURSE;
        }

        //返回数据
        return ResponseResult.OK.put(userCourseVO);
    }

    @Override
    public ResponseResult<?> getUserPost(Long userId, Integer pageNum, Integer pageSize) {
        String minorId = pageNum + "." + pageSize;
        Map<?,?> reMap = redisCacheUtil.queryWithPassThroughByHash(
                Values.USER_POST_PREFIX,
                userId,
                minorId,
                Map.class,
                () -> {
                        Page<UserPostVO> pageInfo = new Page<>(pageNum, pageSize);
                        //查询用户的贴子
                        List<UserPostVO> userPost = userMapper.getPostByUserId(pageInfo, userId);
                        userPost.forEach((p)->{
                            String cacheKey = Values.POST_TAGS_KEY + p.getPostId();
                            if (redisUtil.isExist(cacheKey)) {
                                p.setTags((List<String>)redisUtil.getRedisObject(cacheKey, List.class));
                            } else {
                                List<Long> tagIds = communityTagPostRelationsMapper.findTagIds(p.getPostId());
                                List<String> tagNames = new ArrayList<>();
                                for (Long tagId : tagIds) {
                                    String tagName = communityTagMapper.findById(tagId);
                                    tagNames.add(tagName);
                                }
                                redisUtil.setRedisObjectWithOutTime(cacheKey, tagNames, 3600000L); // 缓存1小时
                                p.setTags(tagNames);
                            }
                        });

                        //查询所有贴子的点赞量，所有贴子的数量，我的贴子被收藏的数量，所有的贴子的评论数量
                        UserPostInfoVO userPostAllInfo = userMapper.getUserPostAllInfo(userId);

                        //如果没有数据，返回null
                        if (userPost.isEmpty()) return null;

                        //封装数据
                        Map<String, Object> map = new HashMap<>();
                        map.put("userPost", userPost);
                        map.put("userPostAllInfo", userPostAllInfo);
                        map.put("pageInfo", pageInfo);
                        return map;
                },
                Values.USER_POST_OUT_TIME,
                TimeUnit.HOURS
        );

        //判断是否有数据
        if (reMap == null) {
            return ResponseResult.USER_NOT_FOUND_POST;
        }
        //返回数据
        return ResponseResult.OK.put(reMap);
    }

    @Override
    public ResponseResult<?> getUserCollect(Long userId, Integer pageNumber, Integer pageSize) {
        String minorId = pageNumber + "." + pageSize;
        Map<?,?> reMap = redisCacheUtil.queryWithPassThroughByHash(
                Values.USER_COLLECT_PREFIX,
                userId,
                minorId,
                Map.class,
                ()->{
                    Page<UserPostVO> pageInfo = new Page<>(pageNumber, pageSize);
                    List<UserPostVO> userCollect = userMapper.geUserCollect(pageInfo, userId);
                    userCollect.forEach((p)->{
                        String cacheKey = Values.POST_TAGS_KEY + p.getPostId();
                        if (redisUtil.isExist(cacheKey)) {
                            p.setTags((List<String>)redisUtil.getRedisObject(cacheKey, List.class));
                        } else {
                            List<Long> tagIds = communityTagPostRelationsMapper.findTagIds(p.getPostId());
                            List<String> tagNames = new ArrayList<>();
                            for (Long tagId : tagIds) {
                                String tagName = communityTagMapper.findById(tagId);
                                if(tagName != null) {
                                    tagNames.add(tagName);
                                }
                            }
                            redisUtil.setRedisObjectWithOutTime(cacheKey, tagNames, 3600000L); // 缓存1小时
                            p.setTags(tagNames);
                        }
                    });
                    if(userCollect.isEmpty()) return null;
                    Map<String,Object> map = new HashMap<>();
                    map.put("pageInfo",pageInfo);
                    map.put("userCollect",userCollect);
                    return map;
                },
                Values.USER_COLLECT_OUT_TIME,
                TimeUnit.HOURS
        );
        if(reMap == null)
            return ResponseResult.USER_NOT_FOUND_COLLECT;
        return ResponseResult.OK.put(reMap);
    }

    @Override
    public ResponseResult<?> setUserCourse(UserCourseDTO userCourseDTO, Long userId,HttpServletRequest request) {
        userId = userId == -1 ? userUtil.getUserInfo(request).getId() : userId;
        //通过用户id查询课程表
        UserCourse userCourse = userCourseMapper.selectAllByUseId(userId);

        //获取关键信息
        Integer weekTime = userCourseDTO.getWeekTime();

        if (userCourse == null) { //第一次添加课程表
            List<UserCourseBO> temp = new ArrayList<>();
            temp.add(new UserCourseBO(userCourseDTO));
            //排序：
            temp = temp.stream().sorted(Comparator.comparingInt(s -> s.getCourseTime().charAt(0))).collect(Collectors.toList());
            String jsonString = JSON.toJSONString(temp);
            userCourse = new UserCourse();
            switch (weekTime) {
                case 0: userCourse.setMonday(jsonString); break;
                case 1: userCourse.setTuesday(jsonString); break;
                case 2: userCourse.setWednesday(jsonString); break;
                case 3: userCourse.setThursday(jsonString); break;
                case 4: userCourse.setFriday(jsonString); break;
                case 5: userCourse.setSaturday(jsonString); break;
                case 6: userCourse.setSunday(jsonString); break;
            }
            userCourse.setUseId(userId);
            int i = userCourseMapper.insert(userCourse);
            return i <= 0 ? ResponseResult.USER_SET_COURSE_FILE : ResponseResult.OK;
        } else {  //已经添加过课程表
            /*
            每一个的基本实现为
            String monday = userCourse.getMonday();
            courseJson = getCourseJson(monday, userCourseDTO);
            userCourse.setMonday(courseJson);
            */
            switch (weekTime) {
                case 0: userCourse.setMonday(getCourseJson(userCourse.getMonday(), userCourseDTO)); break;
                case 1: userCourse.setTuesday(getCourseJson(userCourse.getTuesday(), userCourseDTO)); break;
                case 2: userCourse.setWednesday(getCourseJson(userCourse.getWednesday(), userCourseDTO)); break;
                case 3: userCourse.setThursday(getCourseJson(userCourse.getThursday(), userCourseDTO));break;
                case 4: userCourse.setFriday(getCourseJson(userCourse.getFriday(), userCourseDTO));break;
                case 5: userCourse.setSaturday(getCourseJson(userCourse.getSaturday(), userCourseDTO));break;
                case 6: userCourse.setSunday(getCourseJson(userCourse.getSunday(), userCourseDTO));break;
            }
            int i = userCourseMapper.updateById(userCourse);
            //删除缓存
            redisCacheUtil.deleteRedis(Values.USER_COURSE_PREFIX + userId);
            return i <= 0 ? ResponseResult.USER_SET_COURSE_FILE : ResponseResult.OK;
        }
    }

    private String getCourseJson(String course, UserCourseDTO userCourseDTO){
        String jsonString;
        if(MyString.isNull(course)){
            //星期还未添加
            List<UserCourseBO> temp = new ArrayList<>();
            temp.add(new UserCourseBO(userCourseDTO));
            //排序
            temp = temp.stream().sorted(Comparator.comparingInt(s -> s.getCourseTime().charAt(0))).collect(Collectors.toList());
            jsonString = JSON.toJSONString(temp);
        } else {
            //星期已经添加
            List<UserCourseBO> temp = JSON.parseArray(course, UserCourseBO.class);
            temp.add(new UserCourseBO(userCourseDTO));
            //排序
            temp = temp.stream().sorted(Comparator.comparingInt(s -> s.getCourseTime().charAt(0))).collect(Collectors.toList());
            jsonString = JSON.toJSONString(temp);
        }
        return jsonString;
    }

    @Override
    public ResponseResult<?> deleteUserCourse(Long oneCourseId, HttpServletRequest request) {
        //获取用户id
        Long userId = userUtil.getUserInfo(request).getId();

        //转化数据类型
        UserCourse userCourse = userCourseMapper.selectAllByUseId(userId);

        if(userCourse == null) return ResponseResult.USER_NOT_FOUND_COURSE;

        String monday = userCourse.getMonday();
        String tuesday = userCourse.getTuesday();
        String wednesday = userCourse.getWednesday();
        String thursday = userCourse.getThursday();
        String friday = userCourse.getFriday();
        String saturday = userCourse.getSaturday();
        String sunday = userCourse.getSunday();
        List<String> list = Arrays.asList(monday, tuesday, wednesday, thursday, friday, saturday, sunday);
        boolean flag = false;
        for(int i = 0;i < 7;i++){
            String s = list.get(i);
            if(s == null) continue;
            List<UserCourseBO> userCourseBOS = JSON.parseArray(s, UserCourseBO.class);
            int size = userCourseBOS.size();
            for(int j = 0;j < size;j++){
                UserCourseBO userCourseBO = userCourseBOS.get(j);
                if(Objects.equals(userCourseBO.getOneCourseId(), oneCourseId)){
                    userCourseBOS.remove(userCourseBOS.get(j));
                    flag = true;
                    break;
                }
            }
            if(flag) {
                String json = null;
                if(!userCourseBOS.isEmpty()){
                    json = JSON.toJSONString(userCourseBOS);
                }
                switch (i) {
                    case 0: userCourse.setMonday(json);break;
                    case 1: userCourse.setTuesday(json);break;
                    case 2: userCourse.setWednesday(json);break;
                    case 3: userCourse.setThursday(json);break;
                    case 4: userCourse.setFriday(json);break;
                    case 5: userCourse.setSaturday(json);break;
                    case 6: userCourse.setSunday(json);break;
                }
                break;
            }
        }
        int i = userCourseMapper.updateAllByCourseId(userCourse);
        //删除缓存
        redisCacheUtil.deleteRedis(Values.USER_COURSE_PREFIX + userId);
        return flag && i >= 1 ? ResponseResult.OK : ResponseResult.USER_DELETE_COURSE_FILE;
    }

    @Override
    public ResponseResult<?> updateUserCourse(UserCourseDTO userCourseDTO, HttpServletRequest request) {
        ResponseResult<?> responseResult = deleteUserCourse(userCourseDTO.getOneCourseId(), request);
        if(responseResult.getCode() != 200) return ResponseResult.USER_UPDATE_COURSE_FILE;
        ResponseResult<?> responseResult1 = setUserCourse(userCourseDTO, -1L,request);
        if(responseResult1.getCode()!= 200) return ResponseResult.USER_UPDATE_COURSE_FILE;
        return ResponseResult.OK;
    }

    @Override
    public ResponseResult<?> searchUser(String content, Integer pageNumber, Integer pageSize) {
        Page<SearchUserVO> pageInfo = new Page<>(pageNumber, pageSize);
        String c;
        if(content == null || content.isEmpty()) c = "%";
        else c = "%" + content + "%";
        List<SearchUserVO> searchUsers = userMapper.searchUserLike(pageInfo,c);
        HashMap<String, Object> map = new HashMap<>();
        map.put("pageInfo", pageInfo);
        map.put("searchUsers", searchUsers);
        return ResponseResult.OK.put(map);
    }

    @Override
    @Transactional
    public ResponseResult<?> addAutomaticUpdateUserCourse(AutomaticUserCourseDTO automaticUserCourseDTO, HttpServletRequest request) {
        User userInfo = userUtil.getUserInfo(request);
        Long userId = userInfo.getId();
        Course courseUtil = new Course();
        String course = courseUtil.readMultipartFileAsString(automaticUserCourseDTO.getCourseHtml());
        if(course == null) {
            return ResponseResult.Bad_Request;
        }
        Date startTime = automaticUserCourseDTO.getStartTime();
        UserCourse uc = userCourseMapper.selectAllByUseId(userId);

        int res;
        if(uc == null) {
            UserCourse userCourse = new UserCourse();
            userCourse.setUseId(userId);
            userCourse.setCourseHtml(course);
            userCourse.setStartTime(startTime);
            userCourse.setDeleteFlag(0);
            res = userCourseMapper.insert(userCourse);
        } else {
            uc.setCourseHtml(course);
            uc.setStartTime(startTime);
            res = userCourseMapper.updateById(uc);
        }
        if(res >= 1) {
            userCourseMapper.clearUserCourseById(userId);
            courseUtil.Auto(course,userId,startTime);
            return ResponseResult.OK;
        } else {
            return ResponseResult.USER_SET_COURSE_FILE;
        }
    }

    @Override
    public int AutomaticUpdateUserCourse() {
        List<UserCourseMinDTO> userCourseHtml = userCourseMapper.getUserCourseHtml();
        Course course = new Course();
        for(UserCourseMinDTO info : userCourseHtml) {
            course.Auto(info.getCourseHtml(),info.getUseId(),info.getStartTime());
        }
        return userCourseHtml.size();
    }


    @Data
    public class Course {
        private String name;       // 课程名称
        private String teacher;    // 教师
        private String time;       // 上课节次 [1-2]
        private String weeks;      // 上课周次 1-5,6-8,13
        private String location;   // 上课地点
        private Integer weekTime;    // 星期 [0-6] 一 - 日

        public Course() {}

        @Override
        public String toString() {
            return String.format("[%s] %s :: %s :: %s :: %s",
                    weekTime, name, teacher, time, location);
        }

        void Auto(String course,Long userId,Date startTime){
            // 1. 解析HTML获取课程数据
            List<Set<Course>> weeklyCourses = parseCourseTable(course);
            // 2.1 设置基础时间
            Date baseTime = startTime;
            // 2.2 设置当前时间
            Date nowTime = new Date();
            // 2.3 判断当前时间是基础时间的第几周，从 1 开始
            int weekDifference = (int) ((nowTime.getTime() - baseTime.getTime()) / (7 * 24 * 60 * 60 * 1000)) + 1;
            weeklyCourses.forEach(list -> {
                list.forEach(c -> {
                    String weeks = c.getWeeks();
                    List<Integer> willUpWeeks = parseNumbers(weeks);
                    if(willUpWeeks.contains(weekDifference)) {
                        String courseTime = extractParams(c.getTime());
                        UserCourseDTO userCourseDTO = new UserCourseDTO();
                        userCourseDTO.setCourseName(c.getName());
                        userCourseDTO.setCourseTime(courseTime);
                        userCourseDTO.setCoursePlace(c.getLocation());
                        userCourseDTO.setWeekTime(c.getWeekTime());
                        userCourseDTO.setOneCourseId(System.currentTimeMillis());
                        userCourseDTO.setWeeks(String.valueOf(weekDifference));
                        setUserCourse(userCourseDTO,userId,null);
                    }
                });
            });
        }


        /**
         * 从 HTML 表格中解析课程信息，生成每周课表
         * @param html 包含课程表格的 HTML 字符串
         * @return List<List<Course>>，索引 0~6 对应周一到周六
         */
        List<Set<Course>> parseCourseTable(String html) {
            // 初始化结果：7 个列表，对应周一到周日（0-6）
            List<Set<Course>> result = new ArrayList<>();
            for (int i = 0; i < 7; i++) {
                result.add(new HashSet<>());
            }

            // 星期映射
            Map<Character, Integer> dayMap = new HashMap<>();
            dayMap.put('一', 0);
            dayMap.put('二', 1);
            dayMap.put('三', 2);
            dayMap.put('四', 3);
            dayMap.put('五', 4);
            dayMap.put('六', 5);
            dayMap.put('日', 6);

            // 解析 HTML
            Document doc = Jsoup.parse(html);
            Elements rows = doc.select("table");
            String t = null;
            for (Element table : rows) {
                Elements trs = table.select("tbody").select("tr");
                for(Element tr : trs) {
                    Elements tds = tr.select("td");
                    if(tds.size() == 15) {
                        String courseName = getText(tds.get(2).text());
                        String teacherName = getText(tds.get(6).text());
                        List<String> strings = extractCourseInfo(tds.get(9).text());
                        for(String s : strings) {
                            // 1-15,17-18周 二[3-4] 弘毅楼A307(98)
                            String[] split = s.split(" ");

                            // 1-4,6-15,17,18周(单)
                            String weekTime;
                            if(split[0].contains("单") || split[0].contains("双")) {
                                weekTime = split[0].substring(0, split[0].length()-4) + split[0].charAt(split[0].length()-2);
                            } else {
                                weekTime = split[0].substring(0, split[0].length()-1);
                            }
                            String time = split[1].substring(1);
                            Character weekDay = split[1].charAt(0);
                            String location = split[2];

                            Course courseTemp = new Course();
                            courseTemp.setName(courseName);
                            courseTemp.setTeacher(teacherName);
                            courseTemp.setWeekTime(dayMap.get(weekDay));
                            courseTemp.setTime(time);
                            courseTemp.setLocation(location);
                            courseTemp.setWeeks(weekTime);

                            result.get(courseTemp.getWeekTime()).add(courseTemp);
                        }
                    } else if(tds.size() == 8) {
                        String text = tds.get(0).text();
                        Course course;
                        if(!text.isEmpty()) {
                            t = text;
                            String courseName = getText(tds.get(0).text()) + " " + getText(tds.get(3).text());
                            course = getCourse(tds, courseName, dayMap);
                        } else {
                            String courseName = getText(t) + " " + getText(tds.get(3).text());
                            course = getCourse(tds, courseName, dayMap);
                        }
                        result.get(course.getWeekTime()).add(course);
                    }
                }
            }

            return result;
        }

        String readMultipartFileAsString(MultipartFile courseHtml) {
            // 检查文件是否为空
            if (courseHtml.isEmpty()) {
                return null;
            }

            // 使用BufferedReader读取输入流，指定字符集为UTF-8
            try (BufferedReader reader = new BufferedReader(
                    new InputStreamReader(courseHtml.getInputStream(), StandardCharsets.UTF_8))) {

                StringBuilder content = new StringBuilder();
                String line;
                // 逐行读取内容并拼接
                while ((line = reader.readLine()) != null) {
                    content.append(line);
                    // 如果需要保留换行符，可以添加：content.append(System.lineSeparator());
                }
                return content.toString();
            } catch (IOException e) {
                // 3. 捕获IO异常，打印日志（关键：不抛出，仅记录问题）
                log.error("读取MultipartFile内容失败！文件名：{}", courseHtml.getOriginalFilename(), e);
                // 返回默认值（空字符串，也可根据业务返回null，但需注意后续判空）
                return null;
            }
        }

        private Course getCourse(Elements tds,String courseName,Map<Character, Integer> dayMap){
            String teacherName = getText(tds.get(5).text());
            // 四[1-2节](10周)
            String weekTimeAndTime = tds.get(6).text();
            Character c = weekTimeAndTime.charAt(0);
            String time = getTime(weekTimeAndTime);
            String weekTime = weekTimeAndTime.substring(weekTimeAndTime.length()-4, weekTimeAndTime.length()-2);
            String location = tds.get(7).text();
            Course courseTemp = new Course();
            courseTemp.setName(courseName);
            courseTemp.setTeacher(teacherName);
            courseTemp.setWeekTime(dayMap.get(c));
            courseTemp.setTime(time);
            courseTemp.setLocation(location);
            courseTemp.setWeeks(weekTime);
            return courseTemp;
        }

        private String getTime(String weekTimeAndTime) {
            String time = weekTimeAndTime.substring(1, 5) + "]";
            if("[9-1]".equals(time)) time = "[9-10]";
            else if(time.contains("节")) {
                if(time.contains("1") || time.contains("2")) {
                    time = "[1-2]";
                } else if(time.contains("3") || time.contains("4")) {
                    time = "[3-4]";
                } else if(time.contains("5") || time.contains("6")) {
                    time = "[5-6]";
                } else if(time.contains("7") || time.contains("8")) {
                    time = "[7-8]";
                } else if(time.contains("9") || time.contains("10")) {
                    time = "[9-10]";
                }
            }
            return time;
        }

        /**
         * 获取格式化后的文本
         * @param original eg：[xxxx...xxx] 哈哈
         * @return 哈哈
         */
        private String getText(String original) {
            Pattern pattern = Pattern.compile("^\\[.*?](.*)$");
            Matcher matcher = pattern.matcher(original);

            String result = null;
            if (matcher.find()) {
                result = matcher.group(1);
            }
            return result;
        }

        /**
         * 从课程安排字符串中提取所有信息块
         * @param input 包含课程安排的原始字符串
         * @return 包含各个课程信息块的List集合
         */
        private List<String> extractCourseInfo(String input) {
            List<String> courseInfos = new ArrayList<>();

            // 正则表达式模式：匹配完整的课程信息块
            // 格式为：周次 星期[节次] 地点(人数)
            String regex = "(\\d+-?\\d*,?)+周(?:\\(单\\)|\\(双\\))?\\s+[一二三四五六日]\\[\\d+-\\d+]\\s+.+?\\(\\d+\\)";

            // 编译正则表达式
            Pattern pattern = Pattern.compile(regex);
            Matcher matcher = pattern.matcher(input);

            // 查找所有匹配的信息块并添加到List中
            while (matcher.find()) {
                courseInfos.add(matcher.group());
            }

            return courseInfos;
        }


        /**
         * 解析格式字符串，提取所有数字，并根据可能存在的"单"或"双"字段进行筛选
         * @param input 输入的格式字符串
         * @return 包含所有提取数字的列表（已按单双筛选）
         */
        List<Integer> parseNumbers(String input) {
            // 先判断是否需要筛选单双数
            boolean filterOdd = input.contains("单");
            boolean filterEven = input.contains("双");

            // 移除"单"、"双"字样，避免干扰数字解析
            String processedInput = input.replace("单", "").replace("双", "");

            List<Integer> numbers = new ArrayList<>();

            // 分割逗号分隔的部分
            String[] parts = processedInput.split(",");

            for (String part : parts) {
                // 处理带参数的情况，支持范围+参数的格式，如6-9[5-6]
                String mainPart = part;
                Matcher paramMatcher = Pattern.compile("(\\d+-?\\d*)\\[.*?]").matcher(part);
                if (paramMatcher.matches()) {
                    mainPart = paramMatcher.group(1);
                }

                // 处理范围，如1-5
                if (mainPart.contains("-")) {
                    String[] range = mainPart.split("-");
                    try {
                        int start = Integer.parseInt(range[0].trim());
                        int end = Integer.parseInt(range[1].trim());

                        // 添加范围内的所有数字，并根据单双进行筛选
                        for (int i = start; i <= end; i++) {
                            if (isNumberAccepted(i, filterOdd, filterEven)) {
                                numbers.add(i);
                            }
                        }
                    } catch (NumberFormatException | ArrayIndexOutOfBoundsException e) {
                        System.err.println("无效的数字格式: " + mainPart);
                    }
                } else {
                    // 处理单个数字
                    try {
                        int num = Integer.parseInt(mainPart.trim());
                        if (isNumberAccepted(num, filterOdd, filterEven)) {
                            numbers.add(num);
                        }
                    } catch (NumberFormatException e) {
                        System.err.println("无效的数字格式: " + mainPart);
                    }
                }
            }

            return numbers;
        }

        /**
         * 判断数字是否符合单双筛选条件
         */
        private boolean isNumberAccepted(int num, boolean filterOdd, boolean filterEven) {
            // 如果没有筛选条件，全部接受
            if (!filterOdd && !filterEven) {
                return true;
            }
            // 筛选单数（奇数）
            if (filterOdd) {
                return num % 2 != 0;
            }
            // 筛选双数（偶数）
            return num % 2 == 0;
        }

        /**
         * 提取格式字符串中的所有参数（方括号中的内容）
         * @param input 输入的格式字符串
         * @return 提取到的所有参数列表
         */
        String extractParams(String input) {
            String params = null;
            Pattern pattern = Pattern.compile("\\[(.*?)]");
            Matcher matcher = pattern.matcher(input);

            while (matcher.find()) {
                params = matcher.group(1);
            }

            return params;
        }
    }
}
