package org.zj.mychat.common.user.dao;

import org.zj.mychat.common.user.domain.entity.Black;
import org.zj.mychat.common.user.mapper.BlackMapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 黑名单 服务实现类
 * </p>
 *
 * @author <a href="https://github.com/1720400789">zj</a>
 * @since 2024-02-19
 */
@Service
public class BlackDao extends ServiceImpl<BlackMapper, Black> {

}
