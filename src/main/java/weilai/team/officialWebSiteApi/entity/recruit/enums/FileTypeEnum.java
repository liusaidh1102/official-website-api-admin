package weilai.team.officialWebSiteApi.entity.recruit.enums;

import java.util.regex.Pattern;

/**
 * @author lzw
 * @date 2024/11/11 11:22
 * @description 作用：文件类型的枚举类
 */
public enum FileTypeEnum {

        /*
            图片
         */
        IMAGE("image/.*"),
        /*
            pdf
         */
        PDF("application/pdf"),
        /*
            word  (就是.docx文件)
         */
        WORD("application/vnd.openxmlformats-officedocument.wordprocessingml.document"),
        /*
            excel(xlsx)
         */
        EXCEL("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");

        /*
            正则表达式
         */
        private final Pattern pattern;

        FileTypeEnum(String regex) {
            this.pattern = Pattern.compile(regex);
        }

        public Pattern getPattern() {
            return pattern;
        }
}
