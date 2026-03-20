package weilai.team.officialWebSiteApi.entity.message.DTO;

import lombok.Data;
import weilai.team.officialWebSiteApi.entity.message.DO.Message;
import weilai.team.officialWebSiteApi.entity.message.VO.MessageVO;

import java.io.Serializable;
import java.util.Date;

@Data
public class MessageDTO implements Serializable {

    MessageVO messageVO;

    /**
     * 接收者ID
     */

    private Long receiverId;


    private static final long serialVersionUID = -793474297692L;

    public MessageDTO(MessageVO messageVO, Long receiverId){
       this.messageVO=messageVO;
        this.receiverId=receiverId;
    }


}
