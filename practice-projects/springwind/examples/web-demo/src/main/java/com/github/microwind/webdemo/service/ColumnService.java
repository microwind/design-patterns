package com.github.microwind.webdemo.service;

import com.github.microwind.springwind.annotation.Service;
import com.github.microwind.springwind.annotation.Autowired;
import com.github.microwind.webdemo.dao.ColumnDao;
import com.github.microwind.webdemo.model.Column;
import java.util.List;

/**
 * 栏目服务层
 */
@Service
public class ColumnService {

    @Autowired
    private ColumnDao columnDao;

    public List<Column> getAllColumns() {
        return columnDao.findAll();
    }

    public Column getColumnById(Long id) {
        return columnDao.findById(id);
    }

    public void saveColumn(Column column) {
        columnDao.save(column);
    }
}
