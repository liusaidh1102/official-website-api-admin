package weilai.team.officialWebSiteApi.entity.admin.VO;

import com.alibaba.fastjson2.JSON;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * ClassName:UserLifePhoto
 * Description:
 *
 * @Author:独酌
 * @Create:2025/3/5 20:44
 */
@Data
public class UserLifePhoto {
    private List<String> lifePhoto;

    public void setLifePhoto(String lifePhoto) {
        this.lifePhoto = JSON.parseArray(lifePhoto, String.class);
    }

    public List<String> getLifePhoto() {
        return lifePhoto;
    }
}
