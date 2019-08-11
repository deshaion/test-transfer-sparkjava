package com.company.transfer.dao;

import com.company.transfer.model.Transfer;

import java.util.List;

public interface TransfersDAO {
    List<Transfer> getAll();

    void deleteAll();
}
