package com.github.microwind.webdemo.dao;

import com.github.microwind.springwind.annotation.Repository;
import com.github.microwind.webdemo.model.Column;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

/**
 * 栏目数据访问层
 */
@Repository
public class ColumnDao {
    private List<Column> columns = new ArrayList<>();
    private AtomicLong idGenerator = new AtomicLong(4);

    public ColumnDao() {
        // 初始化测试数据
        initTestData();
    }

    private void initTestData() {
        Column c1 = new Column();
        c1.setId(1L);
        c1.setName("产品中心");
        c1.setDescription("产品介绍和展示");
        c1.setSort(1);
        c1.setCreateTime(LocalDateTime.now().minusDays(10));
        columns.add(c1);

        Column c2 = new Column();
        c2.setId(2L);
        c2.setName("新闻资讯");
        c2.setDescription("公司新闻和行业动态");
        c2.setSort(2);
        c2.setCreateTime(LocalDateTime.now().minusDays(9));
        columns.add(c2);

        Column c3 = new Column();
        c3.setId(3L);
        c3.setName("关于我们");
        c3.setDescription("公司介绍和联系方式");
        c3.setSort(3);
        c3.setCreateTime(LocalDateTime.now().minusDays(8));
        columns.add(c3);
    }

    public List<Column> findAll() {
        return new ArrayList<>(columns);
    }

    public Column findById(Long id) {
        return columns.stream()
            .filter(c -> c.getId().equals(id))
            .findFirst()
            .orElse(null);
    }

    public void save(Column column) {
        if (column.getId() == null) {
            column.setId(idGenerator.getAndIncrement());
            column.setCreateTime(LocalDateTime.now());
            columns.add(column);
        } else {
            // 更新
            for (int i = 0; i < columns.size(); i++) {
                if (columns.get(i).getId().equals(column.getId())) {
                    columns.set(i, column);
                    break;
                }
            }
        }
    }
}
