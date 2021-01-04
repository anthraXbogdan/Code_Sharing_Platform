package com.bogdanenache.Code_Sharing_Platform.services;

import com.bogdanenache.Code_Sharing_Platform.entities.Data;

public interface DataService {

    void saveData(Data data);

    void deleteData(Data data);

    Data findByCodeID(String codeID);

    Data findByUuid(String uuid);
}
