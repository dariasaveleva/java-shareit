package ru.practicum.shareit.item.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

@Repository
public interface ItemRepository extends JpaRepository<Item, Long> {
    List<Item> findAllByOwnerIdOrderByIdAsc(long userId, Pageable page);

    @Query("select item from Item item " +
            "where item.available = true " +
            "and (lower(item.name) like %?1% " +
            "or lower(item.description) like %?1%)")
    List<Item> searchByText(String text, Pageable page);

    @Query("select item from Item item " +
            "where item.request.id in :ids ")
    List<Item> searchByRequestId(@Param("ids") List<Long> ids);

    @Query("select item from Item item " +
            "where item.request.id = ?1")
    List<Item> searchByItemRequestId(Long requestId);

}
