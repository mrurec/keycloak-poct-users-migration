package com.mrurec.keycloakusersmigration.batch;

import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.JpaItemWriter;

import java.util.List;

public class JpaListItemWriter<T> implements ItemWriter<List<T>> {

    private final JpaItemWriter<T> jpaItemWriter;

    public JpaListItemWriter(JpaItemWriter<T> jpaItemWriter) {
        this.jpaItemWriter = jpaItemWriter;
    }

    @Override
    public void write(List<? extends List<T>> items) throws Exception {
        for (List<T> poctUsers : items) {
            this.jpaItemWriter.write(poctUsers);
        }
    }
}
