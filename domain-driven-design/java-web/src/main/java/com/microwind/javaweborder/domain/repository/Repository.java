package com.microwind.javaweborder.domain.repository;

import java.util.List;
import java.util.Optional;

/**
 * 通用仓储接口。
 *
 * <p>DDD 战术构件：<b>仓储（Repository）</b>。把"聚合根的集合"抽象成
 * 像内存集合一样的对象，对外屏蔽底层存储细节。
 *
 * <h3>关键约定</h3>
 * <ul>
 *   <li>仓储以<b>聚合根</b>为单位，不存储孤立的子实体</li>
 *   <li>接口归属<b>领域层</b>，具体实现归属<b>基础设施层</b>（依赖倒置原则）</li>
 *   <li>{@code findById} 未命中返回 {@link Optional#empty()}，不抛异常；
 *       抛不抛异常由应用层决定</li>
 * </ul>
 *
 * <p>第二个类型参数 {@code ID} 让不同聚合可以使用各自专属的 ID 值对象
 * （如 OrderId / UserId），避免方法签名上互相混淆。
 *
 * @param <T>  聚合根类型
 * @param <ID> 聚合根 ID 值对象类型
 */
public interface Repository<T, ID> {

    /**
     * 保存聚合根（新增或更新）。
     *
     * @param entity 聚合根
     */
    void save(T entity);

    /**
     * 按 ID 查找聚合根。
     *
     * @param id 聚合根 ID
     * @return Optional 包装；不存在时为空
     */
    Optional<T> findById(ID id);

    /**
     * 查询全部聚合根（演示用，真实场景应分页）。
     *
     * @return 聚合根列表
     */
    List<T> findAll();

    /**
     * 按 ID 删除聚合根。
     *
     * @param id 聚合根 ID
     */
    void delete(ID id);
}
