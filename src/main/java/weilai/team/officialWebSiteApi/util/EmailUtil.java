package weilai.team.officialWebSiteApi.util;

import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.UUID;

/**
 * ClassName:EmailUtil
 * Description:
 *
 * @Author:独酌
 * @Create:2024/8/24 20:46
 */
public class EmailUtil {

    private static final String logo = "项目logo图片";

    /**
     * 获取验证码
     * @param email 邮箱
     * @param javaMailSender 从springBoot中注入的邮件配置
     * @return 验证码
     */
    public static String getCode(String email,JavaMailSender javaMailSender){
        //发送验证码，返回验证码
        String code = UUID.randomUUID().toString().substring(0,6);
        MimeMessage mimeMessage = javaMailSender.createMimeMessage();
        try {
            //设置内容时 HTML 格式
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage,true);
            helper.setTo(email);
            helper.setSubject("未来软件工作室");
            //设置是 HTML 格式
            helper.setText("<!DOCTYPE html>\n" +
                    "<html lang=\"zh\">\n" +
                    "  <head>\n" +
                    "    <meta charset=\"UTF-8\" />\n" +
                    "    <meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\" />\n" +
                    "    <title>邮箱验证码</title>\n" +
                    "    <style>\n" +
                    "      * {\n" +
                    "        margin: 0;\n" +
                    "        padding: 0;\n" +
                    "        box-sizing: border-box;\n" +
                    "      }\n" +
                    "\n" +
                    "      body {\n" +
                    "        font-family: -apple-system, BlinkMacSystemFont, \"Segoe UI\", Roboto,\n" +
                    "          sans-serif;\n" +
                    "        min-height: 100vh;\n" +
                    "        max-width: 100vw;\n" +
                    "        overflow: hidden;\n" +
                    "        position: relative;\n" +
                    "        display: flex;\n" +
                    "        align-items: center;\n" +
                    "        justify-content: center;\n" +
                    "      }\n" +
                    "      .container {\n" +
                    "        /* 绝对定位 */\n" +
                    "        position: absolute;\n" +
                    "        top: 0;\n" +
                    "        left: 0;\n" +
                    "        width: 100%;\n" +
                    "        height: 100%;\n" +
                    "        overflow: hidden;\n" +
                    "      }\n" +
                    "      .meteor {\n" +
                    "        position: absolute;\n" +
                    "        top: 50%;\n" +
                    "        left: 50%;\n" +
                    "        width: 4px;\n" +
                    "        height: 4px;\n" +
                    "        background-color: #fff;\n" +
                    "        border-radius: 50%;\n" +
                    "        /* 发光效果 */\n" +
                    "        box-shadow: 0 0 0 4px rgba(255, 255, 255, 0.1),\n" +
                    "          0 0 0 8px rgba(255, 255, 255, 0.1), 0 0 20px rgba(255, 255, 255, 1);\n" +
                    "        /* 执行动画 */\n" +
                    "        animation: animate 3s linear infinite;\n" +
                    "        z-index: 99;\n" +
                    "      }\n" +
                    "\n" +
                    "      /* 拖尾效果 */\n" +
                    "      .meteor::before {\n" +
                    "        content: \"\";\n" +
                    "        position: absolute;\n" +
                    "        top: 50%;\n" +
                    "        transform: translateY(-50%);\n" +
                    "        width: 300px;\n" +
                    "        height: 3px;\n" +
                    "        background: linear-gradient(90deg, #fff, transparent);\n" +
                    "      }\n" +
                    "      /* 接下来分别为每一个流星设置位置、动画延迟时间、动画时长 */\n" +
                    "      .meteor:nth-child(1) {\n" +
                    "        top: 0;\n" +
                    "        right: 0;\n" +
                    "        /* initial关键字用于设置CSS属性为它的默认值 */\n" +
                    "        left: initial;\n" +
                    "        /* 动画延迟时间 */\n" +
                    "        animation-delay: 0s;\n" +
                    "        /* 动画时长 */\n" +
                    "        animation-duration: 1s;\n" +
                    "      }\n" +
                    "      .meteor:nth-child(2) {\n" +
                    "        top: 0;\n" +
                    "        right: 80px;\n" +
                    "        left: initial;\n" +
                    "        animation-delay: 0.2s;\n" +
                    "        animation-duration: 3s;\n" +
                    "      }\n" +
                    "      .meteor:nth-child(3) {\n" +
                    "        top: 80px;\n" +
                    "        right: 0;\n" +
                    "        left: initial;\n" +
                    "        animation-delay: 0.4s;\n" +
                    "        animation-duration: 2s;\n" +
                    "      }\n" +
                    "      .meteor:nth-child(4) {\n" +
                    "        top: 0;\n" +
                    "        right: 180px;\n" +
                    "        left: initial;\n" +
                    "        animation-delay: 0.6s;\n" +
                    "        animation-duration: 1.5s;\n" +
                    "      }\n" +
                    "      .meteor:nth-child(5) {\n" +
                    "        top: 0;\n" +
                    "        right: 400px;\n" +
                    "        left: initial;\n" +
                    "        animation-delay: 0.8s;\n" +
                    "        animation-duration: 2.5s;\n" +
                    "      }\n" +
                    "      .meteor:nth-child(6) {\n" +
                    "        top: 0;\n" +
                    "        right: 600px;\n" +
                    "        left: initial;\n" +
                    "        animation-delay: 1s;\n" +
                    "        animation-duration: 3s;\n" +
                    "      }\n" +
                    "      .meteor:nth-child(7) {\n" +
                    "        top: 300px;\n" +
                    "        right: 0;\n" +
                    "        left: initial;\n" +
                    "        animation-delay: 1.2s;\n" +
                    "        animation-duration: 1.75s;\n" +
                    "      }\n" +
                    "      .meteor:nth-child(8) {\n" +
                    "        top: 0;\n" +
                    "        right: 700px;\n" +
                    "        left: initial;\n" +
                    "        animation-delay: 1.4s;\n" +
                    "        animation-duration: 1.25s;\n" +
                    "      }\n" +
                    "      .meteor:nth-child(9) {\n" +
                    "        top: 0;\n" +
                    "        right: 1000px;\n" +
                    "        left: initial;\n" +
                    "        animation-delay: 0.75s;\n" +
                    "        animation-duration: 2.25s;\n" +
                    "      }\n" +
                    "      .meteor:nth-child(10) {\n" +
                    "        top: 0;\n" +
                    "        right: 450px;\n" +
                    "        left: initial;\n" +
                    "        animation-delay: 2.75s;\n" +
                    "        animation-duration: 2.25s;\n" +
                    "      }\n" +
                    "\n" +
                    "      /* 定义动画 */\n" +
                    "      /* 流星划过动画 */\n" +
                    "      @keyframes animate {\n" +
                    "        0% {\n" +
                    "          transform: rotate(315deg) translateX(0);\n" +
                    "          opacity: 1;\n" +
                    "        }\n" +
                    "        90% {\n" +
                    "          opacity: 1;\n" +
                    "        }\n" +
                    "        100% {\n" +
                    "          transform: rotate(315deg) translateX(-1000px);\n" +
                    "          opacity: 0;\n" +
                    "        }\n" +
                    "      }\n" +
                    "\n" +
                    "      /* 主要内容卡片 */\n" +
                    "      .verification-card {\n" +
                    "        background: rgba(30, 41, 59, 0.9);\n" +
                    "        backdrop-filter: blur(20px);\n" +
                    "        border: 1px solid rgba(255, 255, 255, 0.1);\n" +
                    "        border-radius: 20px;\n" +
                    "        padding: 40px;\n" +
                    "        width: 90%;\n" +
                    "        max-width: 500px;\n" +
                    "        text-align: center;\n" +
                    "        color: white;\n" +
                    "        position: relative;\n" +
                    "        z-index: 10;\n" +
                    "        box-shadow: 0 20px 40px rgba(0, 0, 0, 0.3);\n" +
                    "      }\n" +
                    "\n" +
                    "      .title {\n" +
                    "        font-size: 36px;\n" +
                    "        font-weight: bold;\n" +
                    "        margin-bottom: 20px;\n" +
                    "        background: linear-gradient(135deg, #fff, #a0aec0);\n" +
                    "        -webkit-background-clip: text;\n" +
                    "        -webkit-text-fill-color: transparent;\n" +
                    "        background-clip: text;\n" +
                    "      }\n" +
                    "\n" +
                    "      .description {\n" +
                    "        color: rgba(255, 255, 255, 0.7);\n" +
                    "        font-size: 16px;\n" +
                    "        line-height: 1.6;\n" +
                    "        margin-bottom: 30px;\n" +
                    "      }\n" +
                    "\n" +
                    "      .email-info {\n" +
                    "        background: rgba(0, 0, 0, 0.2);\n" +
                    "        border-radius: 10px;\n" +
                    "        padding: 20px;\n" +
                    "        margin-bottom: 30px;\n" +
                    "        border: 1px solid rgba(255, 255, 255, 0.1);\n" +
                    "      }\n" +
                    "\n" +
                    "      .email-label {\n" +
                    "        color: rgba(255, 255, 255, 0.6);\n" +
                    "        font-size: 14px;\n" +
                    "        margin-bottom: 5px;\n" +
                    "      }\n" +
                    "\n" +
                    "      .email-address {\n" +
                    "        color: #60a5fa;\n" +
                    "        font-size: 18px;\n" +
                    "        font-weight: 500;\n" +
                    "      }\n" +
                    "\n" +
                    "      .verification-code {\n" +
                    "        background: rgba(96, 165, 250, 0.1);\n" +
                    "        border: 2px solid #60a5fa;\n" +
                    "        border-radius: 12px;\n" +
                    "        padding: 20px;\n" +
                    "        margin-bottom: 30px;\n" +
                    "        position: relative;\n" +
                    "      }\n" +
                    "\n" +
                    "      .code-label {\n" +
                    "        color: rgba(255, 255, 255, 0.6);\n" +
                    "        font-size: 14px;\n" +
                    "        margin-bottom: 10px;\n" +
                    "      }\n" +
                    "\n" +
                    "      .code-value {\n" +
                    "        font-size: 28px;\n" +
                    "        font-weight: bold;\n" +
                    "        letter-spacing: 3px;\n" +
                    "        color: #60a5fa;\n" +
                    "        font-family: \"Courier New\", monospace;\n" +
                    "      }\n" +
                    "\n" +
                    "      .copy-button {\n" +
                    "        position: absolute;\n" +
                    "        top: 50%;\n" +
                    "        right: 15px;\n" +
                    "        transform: translateY(-50%);\n" +
                    "        background: rgba(96, 165, 250, 0.2);\n" +
                    "        border: 1px solid #60a5fa;\n" +
                    "        color: #60a5fa;\n" +
                    "        padding: 8px 12px;\n" +
                    "        border-radius: 6px;\n" +
                    "        cursor: pointer;\n" +
                    "        font-size: 12px;\n" +
                    "        transition: all 0.3s ease;\n" +
                    "      }\n" +
                    "\n" +
                    "      .time-info {\n" +
                    "        color: rgba(255, 255, 255, 0.5);\n" +
                    "        font-size: 14px;\n" +
                    "        margin-bottom: 30px;\n" +
                    "      }\n" +
                    "\n" +
                    "/*========================*/\n" +
                    "      /* 卡片内部的星空背景 - 更随机的效果 */\n" +
                    "      .card-stars {\n" +
                    "        position: absolute;\n" +
                    "        top: 0;\n" +
                    "        left: 0;\n" +
                    "        width: 100%;\n" +
                    "        height: 100%;\n" +
                    "        z-index: -1;\n" +
                    "        /* 增加更多层次的星星，使用不同大小和透明度 */\n" +
                    "        background-image:\n" +
                    "                radial-gradient(circle, rgba(255,255,255,0.9) 1px, transparent 1.5px),\n" +
                    "                radial-gradient(circle, rgba(255,255,255,0.7) 0.5px, transparent 2px),\n" +
                    "                radial-gradient(circle, rgba(220,240,255,0.8) 1px, transparent 3px),\n" +
                    "                radial-gradient(circle, rgba(255,255,255,0.6) 0.8px, transparent 2.5px),\n" +
                    "                radial-gradient(circle, rgba(200,230,255,0.5) 0.7px, transparent 2px);\n" +
                    "\n" +
                    "        /* 不同的背景尺寸，创造随机性 */\n" +
                    "        background-size: 450px 450px, 300px 300px, 250px 250px, 180px 180px, 120px 120px;\n" +
                    "\n" +
                    "        /* 错开的背景位置，避免规律性排列 */\n" +
                    "        background-position:\n" +
                    "                calc(50% + 123px) calc(50% + 45px),\n" +
                    "                calc(50% - 87px) calc(50% - 124px),\n" +
                    "                calc(50% + 65px) calc(50% - 176px),\n" +
                    "                calc(50% - 132px) calc(50% + 98px),\n" +
                    "                calc(50% + 145px) calc(50% - 67px);\n" +
                    "\n" +
                    "        /* 更复杂的闪烁动画，每个层次有不同延迟 */\n" +
                    "        animation: starTwinkle 8s ease-in-out infinite;\n" +
                    "      }\n" +
                    "\n" +
                    "      /* 改进的星星闪烁动画 */\n" +
                    "      @keyframes starTwinkle {\n" +
                    "        0%, 100% {\n" +
                    "          opacity: 0;\n" +
                    "          background-position:\n" +
                    "                  calc(50% + 123px) calc(50% + 45px),\n" +
                    "                  calc(50% - 87px) calc(50% - 124px),\n" +
                    "                  calc(50% + 65px) calc(50% - 176px),\n" +
                    "                  calc(50% - 132px) calc(50% + 98px),\n" +
                    "                  calc(50% + 145px) calc(50% - 67px);\n" +
                    "        }\n" +
                    "        25% {\n" +
                    "          opacity: 1;\n" +
                    "          background-position:\n" +
                    "                  calc(50% + 133px) calc(50% + 50px),\n" +
                    "                  calc(50% - 77px) calc(50% - 114px),\n" +
                    "                  calc(50% + 75px) calc(50% - 186px),\n" +
                    "                  calc(50% - 122px) calc(50% + 108px),\n" +
                    "                  calc(50% + 155px) calc(50% - 57px);\n" +
                    "        }\n" +
                    "        50% {\n" +
                    "          opacity: 0.3;\n" +
                    "          background-position:\n" +
                    "                  calc(50% + 113px) calc(50% + 40px),\n" +
                    "                  calc(50% - 97px) calc(50% - 134px),\n" +
                    "                  calc(50% + 55px) calc(50% - 166px),\n" +
                    "                  calc(50% - 142px) calc(50% + 88px),\n" +
                    "                  calc(50% + 135px) calc(50% - 77px);\n" +
                    "        }\n" +
                    "        75% {\n" +
                    "          opacity: 0.6;\n" +
                    "          background-position:\n" +
                    "                  calc(50% + 143px) calc(50% + 60px),\n" +
                    "                  calc(50% - 67px) calc(50% - 104px),\n" +
                    "                  calc(50% + 85px) calc(50% - 196px),\n" +
                    "                  calc(50% - 112px) calc(50% + 118px),\n" +
                    "                  calc(50% + 165px) calc(50% - 47px);\n" +
                    "        }\n" +
                    "      }\n" +
                    "/*=========================*/\n" +
                    "\n" +
                    "      @keyframes pulse {\n" +
                    "        0% {\n" +
                    "          opacity: 1;\n" +
                    "          transform: scale(1);\n" +
                    "        }\n" +
                    "        50% {\n" +
                    "          opacity: 0.5;\n" +
                    "          transform: scale(1.2);\n" +
                    "        }\n" +
                    "        100% {\n" +
                    "          opacity: 1;\n" +
                    "          transform: scale(1);\n" +
                    "        }\n" +
                    "      }\n" +
                    "\n" +
                    "      /* 响应式设计 */\n" +
                    "      @media (max-width: 768px) {\n" +
                    "        .verification-card {\n" +
                    "          padding: 30px 20px;\n" +
                    "          margin: 20px;\n" +
                    "        }\n" +
                    "\n" +
                    "        .title {\n" +
                    "          font-size: 28px;\n" +
                    "        }\n" +
                    "\n" +
                    "        .code-value {\n" +
                    "          font-size: 24px;\n" +
                    "        }\n" +
                    "      }\n" +
                    "    </style>\n" +
                    "  </head>\n" +
                    "  <body>\n" +
                    "    <!-- 星空背景 -->\n" +
                    "    <div class=\"container\">\n" +
                    "      <!-- 10个span -->\n" +
                    "      <span class=\"meteor\"></span>\n" +
                    "      <span class=\"meteor\"></span>\n" +
                    "      <span class=\"meteor\"></span>\n" +
                    "      <span class=\"meteor\"></span>\n" +
                    "      <span class=\"meteor\"></span>\n" +
                    "      <span class=\"meteor\"></span>\n" +
                    "      <span class=\"meteor\"></span>\n" +
                    "      <span class=\"meteor\"></span>\n" +
                    "      <span class=\"meteor\"></span>\n" +
                    "      <span class=\"meteor\"></span>\n" +
                    "    </div>\n" +
                    "\n" +
                    "    <!-- 主要内容 -->\n" +
                    "    <div class=\"verification-card\">\n" +
                    "      <!-- 卡片内部的星空背景 -->\n" +
                    "      <div class=\"card-stars\"></div>\n" +
                    "\n" +
                    "      <h1 class=\"title\">未来软件工作室</h1>\n" +
                    "      <p class=\"description\">\n" +
                    "        为了验证您的电子邮件地址，请使用下面的验证码完成此过程。注意：验证码有效期为<em style=\"color: darkorange\">5分钟</em>，请及时使用。\n" +
                    "      </p>\n" +
                    "\n" +
                    "      <div class=\"verification-code\">\n" +
                    "        <div class=\"code-label\">验证码</div>\n" +
                    "        <div class=\"code-value\" id=\"verificationCode\">"+ code +"</div>\n" +
                    "      </div>\n" +
                    "\n" +
                    "      <div class=\"time-info\">\n" +
                    "        如果您没有进行此操作，请忽略此电子邮件。出于安全考虑，请勿泄露验证码\n" +
                    "      </div>\n" +
                    "    </div>\n" +
                    "  </body>\n" +
                    "</html>\n",true);
            javaMailSender.send(mimeMessage);
        } catch (MessagingException e) {
            LogUtil.Error("创建 MimeMessageHelper 类时，出现异常",e);
        }
        return code;
    }
}
