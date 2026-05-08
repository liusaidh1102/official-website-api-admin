package weilai.team.officialWebSiteApi.util;

public class MyString {

    /**
     * 全部是空的吗？
     *
     * @param str 可变参数列表
     * @return true:存在空元素，false:不存在空元素（全部不为空元素）
     */
    public static boolean isNull(String... str){
        for(String s : str){
            if(s == null || s.isEmpty() || s.trim().isEmpty()){
                return true;
            }
        }
        return false;
    }
}
