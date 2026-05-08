package weilai.team.officialWebSiteApi.config.securityComponent;

import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import weilai.team.officialWebSiteApi.entity.admin.DO.User;
import weilai.team.officialWebSiteApi.mapper.admin.UserMapper;
import weilai.team.officialWebSiteApi.mapper.admin.UserPermissionMapper;

import javax.annotation.Resource;
import java.util.Objects;

@Service
public class DIYUserDetailsService implements UserDetailsService {

    @Resource
    private UserMapper userMapper;

    @Resource
    private UserPermissionMapper userPermissionMapper;

    @Override
    public UserDetails loadUserByUsername(String account) throws UsernameNotFoundException {
        //这里要保证数据库中的email和username是不能重复，否则会出现问题
        User user = userMapper.selectAllByUsernameOrEmail(account);
        if(Objects.isNull(user)){
            throw new UsernameNotFoundException(account + " 不存在!");
        }
        user.setAuth(userPermissionMapper.getAuthorityByUserId(user.getId()));
        return user;
    }
}
