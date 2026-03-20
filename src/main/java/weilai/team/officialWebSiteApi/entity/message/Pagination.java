package weilai.team.officialWebSiteApi.entity.message;

import lombok.Data;

@Data
public class Pagination {
    private int currentPage;  // 当前页
    private int pageSize;     // 每页大小
    private int totalCount;   // 总记录数
    private int totalPage;    // 总页数

    public Pagination(int currentPage, int pageSize, int totalCount, int totalPage) {
        this.currentPage = currentPage;
        this.pageSize = pageSize;
        this.totalCount = totalCount;
        this.totalPage = totalPage;
    }
}
