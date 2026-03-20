package weilai.team.officialWebSiteApi.service.post;

import weilai.team.officialWebSiteApi.entity.post.DO.UserCollect;
import com.baomidou.mybatisplus.extension.service.IService;
import weilai.team.officialWebSiteApi.util.ResponseResult;

import javax.servlet.http.HttpServletRequest;

/**
* @author 杜昱徵
* @description 针对表【user_collect(用户的收藏[贴子])】的数据库操作Service
* @createDate 2024-11-13 16:30:33
*/
public interface UserCollectService extends IService<UserCollect> {
    /**
     * 收藏帖子
     * @param id
     * @return
     */
    ResponseResult<?> collect(Long id, HttpServletRequest request);


}
