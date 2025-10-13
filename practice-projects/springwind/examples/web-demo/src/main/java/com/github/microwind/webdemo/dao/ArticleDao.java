package com.github.microwind.webdemo.dao;

import com.github.microwind.springwind.annotation.Repository;
import com.github.microwind.webdemo.model.Article;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

/**
 * 文章数据访问层
 */
@Repository
public class ArticleDao {
    private List<Article> articles = new ArrayList<>();
    private AtomicLong idGenerator = new AtomicLong(6);

    public ArticleDao() {
        // 初始化测试数据
        initTestData();
    }

    private void initTestData() {
        // 产品类文章
        Article a1 = new Article();
        a1.setId(1L);
        a1.setTitle("智能云服务平台");
        a1.setContent("春风公司推出的企业级云服务平台，提供稳定可靠的云计算服务。");
        a1.setColumnId(1L);
        a1.setAuthor("产品部");
        a1.setPublishTime(LocalDateTime.now().minusDays(7));
        articles.add(a1);

        Article a2 = new Article();
        a2.setId(2L);
        a2.setTitle("大数据分析系统");
        a2.setContent("帮助企业深度挖掘数据价值的专业分析系统。");
        a2.setColumnId(1L);
        a2.setAuthor("产品部");
        a2.setPublishTime(LocalDateTime.now().minusDays(6));
        articles.add(a2);

        // 新闻类文章
        Article a3 = new Article();
        a3.setId(3L);
        a3.setTitle("春风公司荣获年度创新企业奖");
        a3.setContent("在2024年度科技创新大会上，春风公司荣获年度创新企业奖。");
        a3.setColumnId(2L);
        a3.setAuthor("新闻中心");
        a3.setPublishTime(LocalDateTime.now().minusDays(2));
        articles.add(a3);

        Article a4 = new Article();
        a4.setId(4L);
        a4.setTitle("春风公司与高校达成战略合作");
        a4.setContent("春风公司与多所知名高校签署战略合作协议，共同推进产学研一体化。");
        a4.setColumnId(2L);
        a4.setAuthor("新闻中心");
        a4.setPublishTime(LocalDateTime.now().minusDays(1));
        articles.add(a4);

        Article a5 = new Article();
        a5.setId(5L);
        a5.setTitle("春风公司新产品发布会圆满举行");
        a5.setContent("春风公司2024年度新产品发布会在北京圆满举行，发布多款创新产品。");
        a5.setColumnId(2L);
        a5.setAuthor("新闻中心");
        a5.setPublishTime(LocalDateTime.now());
        articles.add(a5);
    }

    public List<Article> findAll() {
        return new ArrayList<>(articles);
    }

    public Article findById(Long id) {
        return articles.stream()
            .filter(a -> a.getId().equals(id))
            .findFirst()
            .orElse(null);
    }

    public List<Article> findByColumnId(Long columnId) {
        return articles.stream()
            .filter(a -> a.getColumnId().equals(columnId))
            .collect(Collectors.toList());
    }

    public void save(Article article) {
        if (article.getId() == null) {
            article.setId(idGenerator.getAndIncrement());
            article.setPublishTime(LocalDateTime.now());
            articles.add(article);
        } else {
            // 更新
            for (int i = 0; i < articles.size(); i++) {
                if (articles.get(i).getId().equals(article.getId())) {
                    articles.set(i, article);
                    break;
                }
            }
        }
    }
}
