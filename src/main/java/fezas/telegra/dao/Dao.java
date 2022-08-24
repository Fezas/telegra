package fezas.telegra.dao;

import java.util.List;
import java.util.Optional;

public interface Dao<K, E> {

    boolean delete(K id);

    E save(E object);

    void update(E object);

    Optional<E> findById(K id);

    List<E> findAll();
}
