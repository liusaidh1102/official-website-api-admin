package weilai.team.officialWebSiteApi.service.community.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import weilai.team.officialWebSiteApi.entity.community.DO.CommunityTag;
import weilai.team.officialWebSiteApi.mapper.community.CommunityTagPostRelationsMapper;
import weilai.team.officialWebSiteApi.service.community.CommunityTagService;
import weilai.team.officialWebSiteApi.mapper.community.CommunityTagMapper;
import org.springframework.stereotype.Service;
import weilai.team.officialWebSiteApi.util.ResponseResult;

import javax.annotation.Resource;
import java.util.*;

/**
 * @author Administrator
 * @description 针对表【community_tag】的数据库操作Service实现
 * @createDate 2024-11-10 09:36:47
 */
@Service
public class CommunityTagServiceImpl extends ServiceImpl<CommunityTagMapper, CommunityTag>
        implements CommunityTagService{
    private static final Logger logger = LoggerFactory.getLogger(CommunityTagServiceImpl.class);

    @Resource
    CommunityTagPostRelationsMapper communityTagPostRelationsMapper;
    @Resource
    CommunityTagMapper communityTagMapper;

    /**
     * 热门标签展示
     * @return 热门标签
     * @param type
     */
    @Override
    public ResponseResult<?> findHotTagsByTagUses(Integer type) {
        List<String>HotTags=communityTagMapper.findHotTagsByTagUses(type);
        return ResponseResult.HOT_SUCCESS.put(HotTags);
    }

    /**
     * 标签的云展示
     */
    @Override
    public ResponseResult<?> getAllTagNames() {
        List<String> allTagNames = new ArrayList<>();
        try {
            allTagNames = communityTagMapper.getAllTagNames();

            // 检查返回的列表是否为null（虽然这通常应该在Mapper层处理）
            if (allTagNames == null) {
                logger.error("getAllTagNames() returned null, which is unexpected. Defaulting to empty list.");
                allTagNames = new ArrayList<>();
            }

            if (allTagNames.isEmpty()) {
                logger.info("No tags found, adding default tag.");
                allTagNames.add("暂无可展示的标签");
            } else {
                logger.info("Found {} tags.", allTagNames.size());
            }

            return ResponseResult.GET_ALL_TAG_NAMES.put(allTagNames);
        } catch (Exception e) {
            // 捕获并处理可能发生的异常（例如数据库连接问题）
            logger.error("Error occurred while fetching tag names: ", e);
            // 根据需要返回错误响应或进行其他错误处理
            return ResponseResult.ERROR.put(e.getMessage());
        }
    }


}





