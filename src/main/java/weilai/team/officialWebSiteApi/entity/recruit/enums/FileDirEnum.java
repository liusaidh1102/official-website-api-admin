package weilai.team.officialWebSiteApi.entity.recruit.enums;

/**
 * @author lzw
 * @date 2024/11/11 14:33
 * @description 作用：上传的文件都在一个bucket桶中，但是在不同的文件夹中，文件夹的名称枚举类，不同模块有不同的文件夹
 */
public enum FileDirEnum {

    /*
     招新模块
     */
    RECRUIT("recruit"),

    /*
    帖子模块
     */
    POST("post"),

    /*
    个人信息文件夹
     */
    USER("user"),

    /*
    评论图片文件夹
     */
    COMMENT("comment");


    private final String dirName;

    FileDirEnum(String dirName) {
        this.dirName = dirName;
    }

    public String getDirName() {
        return dirName;
    }

}
