package weilai.team.officialWebSiteApi.service.post;


import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;

import weilai.team.officialWebSiteApi.entity.post.DO.Post;

import weilai.team.officialWebSiteApi.entity.post.DTO.PageDto;
import weilai.team.officialWebSiteApi.entity.post.DTO.PageQueryDto;
import weilai.team.officialWebSiteApi.entity.post.DTO.PostDto;
import weilai.team.officialWebSiteApi.entity.post.DTO.UpdatePostDto;
import weilai.team.officialWebSiteApi.entity.post.VO.PagePostVo;
import weilai.team.officialWebSiteApi.util.ResponseResult;
import javax.servlet.http.HttpServletRequest;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
* @author 杜昱徵
* @description 针对表【post(用户发的贴子内容)】的数据库操作Service
* @createDate 2024-11-11 17:01:02
*/
public interface PostService extends IService<Post> {

    /**
     * 发布帖子
     * @param postDto
     */
    ResponseResult<?> put(PostDto postDto, HttpServletRequest request);

    /**
     * 删除多个帖子
     * @param id
     */
    ResponseResult<?> deletePostByIds(List<Long> id,HttpServletRequest request);

    /**
     * 查看帖子的详细信息
     * @param id
     * @return
     */
    ResponseResult<?> getOnePost(Long id,HttpServletRequest request);

    /**
     * 根据id修改帖子
     * @param postDto
     */
    ResponseResult<?> updatePostById(UpdatePostDto postDto,HttpServletRequest request);

    /**
     * 分页条件查询帖子
     * @param pageQueryDto
     */
    ResponseResult<?> pageQuery(PageQueryDto pageQueryDto,HttpServletRequest request);
    /**
     * 对帖子进行点赞
     * @param id
     */
    ResponseResult<?> like(Long id,HttpServletRequest request);

  /**
   * 删除单个帖子
   * @param id
   * @param request
   * @return
   */
  ResponseResult<?> deletePostById(Long id, HttpServletRequest request);

  /**
   * 根据id集合查询帖子
   * @param pageDto
   * @param request
   */
  Page<PagePostVo> selectPostByIds(PageDto pageDto, HttpServletRequest request);

    /**
     * 后台查询所有的帖子
     * @param pageQueryDto
     * @param request
     * @return
     */
    ResponseResult<?> AdminPageQuery(PageQueryDto pageQueryDto, HttpServletRequest request);
}
