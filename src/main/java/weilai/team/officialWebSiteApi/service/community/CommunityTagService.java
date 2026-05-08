package weilai.team.officialWebSiteApi.service.community;

import org.springframework.stereotype.Service;
import weilai.team.officialWebSiteApi.entity.community.DO.CommunityTag;
import com.baomidou.mybatisplus.extension.service.IService;
import weilai.team.officialWebSiteApi.util.ResponseResult;

/**
* @author Administrator
* @description 针对表【community_tag】的数据库操作Service
* @createDate 2024-11-10 09:36:47
*/
@Service
public interface CommunityTagService extends IService<CommunityTag> {
    /**
     * 热门标签展示
     * @param type
     */
    ResponseResult<?> findHotTagsByTagUses(Integer type);


    /**
     * 标签的云展示
     */
    ResponseResult<?> getAllTagNames();


}
