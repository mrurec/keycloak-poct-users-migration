package com.mrurec.keycloakusersmigration.batch;

import com.mrurec.keycloakusersmigration.poct.model.PoctUser;
import com.mrurec.keycloakusersmigration.poct.repository.PoctUserRepository;
import org.springframework.batch.item.ItemWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class BatchWriter implements ItemWriter<List<PoctUser>> {
    @Autowired
    private PoctUserRepository repository;
    @Override
    public void write(List<? extends List<PoctUser>> list) throws Exception {
        list.iterator().forEachRemaining(poctUsers -> poctUsers.forEach(poctUser -> repository.save(poctUser)));
    }
}
