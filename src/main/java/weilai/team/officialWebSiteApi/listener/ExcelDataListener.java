package weilai.team.officialWebSiteApi.listener;
import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.event.AnalysisEventListener;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.lf5.viewer.LogFactor5ErrorDialog;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import weilai.team.officialWebSiteApi.entity.recruit.DTO.UserInfoExcel;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * listener监听器，判断excel的表头是不是正常的，数据是不是为空
 */
@Component
@Data
public class ExcelDataListener extends AnalysisEventListener<UserInfoExcel> {

    private static final Logger log = LoggerFactory.getLogger(ExcelDataListener.class);
    public List<UserInfoExcel> data = new CopyOnWriteArrayList<UserInfoExcel>();


    // 校验表头标志
    public boolean flag = true;


    /**
     * 重写invokeHeadMap方法，校验表头
     *headMap的key是表头下标，value是内容
     */
    @Override
    public void invokeHeadMap(Map<Integer, String> headMap,AnalysisContext context) {
        log.info("开始进行表头校验，表头内容为: {}", headMap);

        // 定义期望的表头
        String[] expectedHeaders = {"姓名", "学号", "邮箱", "性别", "班级", "年级", "组别", "电话", "QQ"};

        // 检查表头是否包含所有必要的列
        for (int i = 0; i < expectedHeaders.length; i++) {
            if (!headMap.containsKey(i)) {
                log.error("表头校验失败，缺少索引为 {} 的列", i);
                flag = false;
                return;
            }
        }

        // 检查表头内容是否与期望的一致
        for (int i = 0; i < expectedHeaders.length; i++) {
            String head = headMap.get(i);
            if (StringUtils.isNotEmpty(head) && !head.equals(expectedHeaders[i])) {
                log.error("表头校验失败，索引为 {} 的列期望是 '{}'，但实际是 '{}'", i, expectedHeaders[i], headMap.get(i));
                flag = false;
                return;
            }
        }

        log.info("表头校验通过");
    }

    @Override
    public void invoke(UserInfoExcel userInfoExcel, AnalysisContext analysisContext) {
        // 这里放具体数据校验方法，校验通过往data里面放数据，否则直接return
        if (!checkValidateBlank(userInfoExcel)) {
            //有一个数据不符合要求，直接返回
            flag = false;
            return;
        }
        data.add(userInfoExcel);
    }


    protected boolean checkValidateBlank(UserInfoExcel userInfoExcel) {
        //判断某一列的数据是不是为空
        return StringUtils.isNotEmpty(userInfoExcel.getName()) && StringUtils.isNotEmpty(userInfoExcel.getStudyId()) && StringUtils.isNotEmpty(userInfoExcel.getEmail())
                && StringUtils.isNotEmpty(userInfoExcel.getSex()) && StringUtils.isNotEmpty(userInfoExcel.getClazz()) && StringUtils.isNotEmpty(userInfoExcel.getGrade())
                && StringUtils.isNotEmpty(userInfoExcel.getGroup()) && StringUtils.isNotEmpty(userInfoExcel.getPhone()) && StringUtils.isNotEmpty(userInfoExcel.getQq());
    }



    @Override
    public void doAfterAllAnalysed(AnalysisContext context) {
        //校验后的方法
    }
}
