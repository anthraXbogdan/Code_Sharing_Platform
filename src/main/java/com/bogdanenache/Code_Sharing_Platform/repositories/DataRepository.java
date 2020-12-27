package com.bogdanenache.Code_Sharing_Platform.repositories;

import com.bogdanenache.Code_Sharing_Platform.entities.Data;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DataRepository extends JpaRepository<Data, Long> {

    Data findByCodeID(String codeID);
}
