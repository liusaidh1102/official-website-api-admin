package weilai.team.officialWebSiteApi.util;

import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Slf4j
public class LogUtil {
    public static void Error(String message,Throwable e) {
        log.error(message,e);
    }

    public static void info(String message) {
        log.info(message);
    }
}
