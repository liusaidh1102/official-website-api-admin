package weilai.team.officialWebSiteApi.entity.recruit.VO;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

/**
 * @author lzw
 * @date 2024/12/1 8:27
 * @description 作用：
 */
@Data
@AllArgsConstructor
public class PageVO<T> {


    /*
    * 当前页码
     */
    private Long pageNo;

    /*
    * 每页数量
     */
    private Long pageSize;

    /*
    * 总数
     */
    private Long total;

    /*
    * 数据
     */
    private List<T> data;

}
