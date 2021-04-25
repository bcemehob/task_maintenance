package net.erply.demo.base.dao;

import java.util.List;
import java.util.Optional;

public interface Dao<T> {
    T save(T t);

    void deleteById(Integer id);

    T update(T task);

    List<T> getAll();

    Optional<T> findById(Integer id);
}
