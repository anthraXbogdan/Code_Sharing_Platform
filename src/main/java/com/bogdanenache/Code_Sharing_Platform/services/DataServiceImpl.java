package com.bogdanenache.Code_Sharing_Platform.services;

import com.bogdanenache.Code_Sharing_Platform.entities.Data;
import com.bogdanenache.Code_Sharing_Platform.repositories.DataRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class DataServiceImpl implements DataService {

    @Autowired
    private DataRepository dataRepository;

    @Override
    public void saveData(Data data) {
        dataRepository.save(data);
    }

    @Override
    public void deleteData(Data data) {
        dataRepository.delete(data);
    }

    @Override
    public Data findByCodeID(String codeID) {
        return dataRepository.findByCodeID(codeID);
    }

    @Override
    public Data findByUuid(String uuid) {
        return dataRepository.findByUuid(uuid);
    }


}

