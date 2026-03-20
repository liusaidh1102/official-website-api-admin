package weilai.team.officialWebSiteApi.entity.admin.VO;

import lombok.Data;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * ClassName:UserListVO
 * Description:
 *
 * @Author:独酌
 * @Create:2024/12/2 10:03
 */
@Data
public class UserListVO {
        private Long id;

        private String username;

        private String name;

        private String sex;

        private List<String> authority;

        public void setAuthority(String authority){
                if(authority != null) {
                        List<String> list = Arrays.asList(authority.split(","));
                        list.sort(String::compareTo);
                        this.authority = list;
                }
                else this.authority = new ArrayList<>();
        }
}
