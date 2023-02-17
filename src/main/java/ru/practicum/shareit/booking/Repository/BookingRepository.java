package ru.practicum.shareit.booking.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.booking.model.Booking;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {
    List<Booking> findAllByBookerIdOrderByStartTimeDesc(long userId);

    @Query("select booking from Booking booking " +
            "where booking.startTime <?2 " +
            "and booking.finishTime > ?2 " +
            "and booking.booker.id = ?1 " +
            "order by booking.startTime")
    List<Booking> findByBookerCurrent(long userId, LocalDateTime now);

    @Query("select booking from Booking booking " +
            "where booking.finishTime < ?2 " +
            "and booking.booker.id = ?1 " +
            "order by booking.startTime desc")
    List<Booking> findByBookerPast(long userId, LocalDateTime finishTime);

    @Query("select booking from Booking booking " +
            "where booking.startTime > ?2 " +
            "and booking.booker.id = ?1 " +
            "order by booking.startTime desc")
    List<Booking> findByBookerFuture(long userId, LocalDateTime startTime);

    @Query("select booking from Booking booking " +
            "where booking.status = ?2 " +
            "and booking.booker.id = ?1 " +
            "order by booking.status desc")
    List<Booking> findByBookerAndStatus(long userId, BookingStatus status);

    List<Booking> findByItemOwnerIdOrderByStartTimeDesc(long userId);

    @Query("select booking from Booking booking " +
            "where booking.startTime < ?2 " +
            "and booking.finishTime > ?2 " +
            "and booking.item.owner.id = ?1 " +
            "order by booking.startTime")
    List<Booking> findByItemOwnerCurrent(long userId, LocalDateTime now);

    @Query("select booking from Booking booking " +
            "where booking.finishTime < ?2 " +
            "and booking.item.owner.id = ?1 " +
            "order by booking.startTime")
    List<Booking> findByItemOwnerPast(long userId, LocalDateTime finishTime);


    @Query("select booking from Booking booking " +
            "where booking.startTime > ?2 " +
            "and booking.item.owner.id = ?1 " +
            "order by booking.startTime desc")
    List<Booking> findByItemOwnerFuture(long userId, LocalDateTime startTime);

    @Query("select booking from Booking booking " +
            "where booking.status = ?2 " +
            "and booking.item.owner.id = ?1 " +
            "order by booking.status desc")
    List<Booking> findByItemOwnerAndStatus(long userId, BookingStatus status);

    Optional<Booking> findByBookerIdAndItemIdAndFinishTimeBefore(long bookerId, long itemId, LocalDateTime finishTime);

    @Query("select distinct booking from Booking booking " +
            "where booking.finishTime < :now " +
            "and booking.item.id in :ids " +
            "and booking.item.owner.id = :userId " +
            "order by booking.startTime asc")
    List<Booking> findBookingsLast(@Param("ids") List<Long> ids,
                                   @Param("now") LocalDateTime now,
                                   @Param("userId") long userId);

    @Query("select distinct booking from Booking booking " +
            "where booking.startTime > :now " +
            "and booking.item.id in :ids " +
            "and booking.item.owner.id = :userId " +
            "order by booking.startTime asc")
    List<Booking> findBookingsNext(@Param("ids") List<Long> ids,
                                   @Param("now") LocalDateTime now,
                                   @Param("userId") long userId);
}
