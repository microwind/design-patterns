package com.github.microwind.webdemo.service;

import com.github.microwind.springwind.annotation.Service;
import com.github.microwind.springwind.annotation.Autowired;
import com.github.microwind.webdemo.dao.ArticleDao;
import com.github.microwind.webdemo.model.Article;
import java.util.List;

/**
 * 文章服务层
 */
@Service
public class ArticleService {

    @Autowired
    private ArticleDao articleDao;

    public List<Article> getAllArticles() {
        return articleDao.findAll();
    }

    public Article getArticleById(Long id) {
        return articleDao.findById(id);
    }

    public List<Article> getArticlesByColumnId(Long columnId) {
        return articleDao.findByColumnId(columnId);
    }

    public void saveArticle(Article article) {
        articleDao.save(article);
    }

    public boolean updateArticle(Article article) {
        return articleDao.update(article);
    }

    public boolean deleteArticle(Long id) {
        return articleDao.delete(id);
    }
}
