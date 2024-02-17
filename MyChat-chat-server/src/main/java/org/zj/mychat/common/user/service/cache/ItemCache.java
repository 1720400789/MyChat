package org.zj.mychat.common.user.service.cache;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;
import org.zj.mychat.common.user.dao.ItemConfigDao;
import org.zj.mychat.common.user.domain.entity.ItemConfig;

import java.util.List;

/**
 *
 */
@Component
public class ItemCache {

    @Autowired
    private ItemConfigDao itemConfigDao;

    /**
     * Spring 缓存三注解：
     * - @Cacheable 存入缓存，如果缓存存在则直接走缓存
     * - @CachePut 更新缓存，不管缓存存在与否都覆盖缓存
     * - @CacheEvict 清空缓存
     * @param itemType 物品种类
     * @return
     */
    @Cacheable(cacheNames = "item", key = "'itemsByType:' + #itemType")
    public List<ItemConfig> getByType(Integer itemType) {
        return itemConfigDao.getByType(itemType);
    }

    /**
     * 清空缓存
     */
    @CacheEvict(cacheNames = "item", key = "'itemsByType:' + #itemType")
    public void evictByType(Integer itemType) {
    }
}
