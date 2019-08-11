package com.company.transfer.dao.impl;

import com.company.transfer.dao.TransfersDAO;
import com.company.transfer.model.Transfer;
import org.sql2o.Sql2o;

import java.util.List;

public class TransfersDAOImpl implements TransfersDAO {
    private Sql2o sql2o;

    public TransfersDAOImpl(Sql2o sql2o) {
        this.sql2o = sql2o;
    }

    @Override
    public List<Transfer> getAll() {
        return null;
    }

    @Override
    public void deleteAll() {

    }
}
