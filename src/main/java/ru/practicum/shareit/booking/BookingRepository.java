package ru.practicum.shareit.booking;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.user.User;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface BookingRepository extends JpaRepository<Booking, Long> {

    List<Booking> findByBookerEquals(User user);

    Booking findFirstByItemEqualsAndEndBeforeOrderByEndDesc(Item item, LocalDateTime endDateTime);

    Booking findFirstByItemEqualsAndStartAfterOrderByStartAsc(Item item, LocalDateTime startDateTime);

    Optional<Booking> findFirstByItemEqualsAndAndBookerEqualsAndEndBefore(Item item, User user, LocalDateTime endDateTime);

    @Query(" select b from Booking b " +
            "where  b.item.owner.id = ?1")
    List<Booking> findAllOwner(long userId);

    @Query(" select b from Booking b " +
            "where  (b.start > ?1 and " +
            "       b.end < ?2)" +
            "       or (b.end > ?1 and" +
            "      b.end < ?2 and" +
            "       b.start < ?1)")
    List<Booking> findAllByDate(LocalDateTime startDate, LocalDateTime endDate);

}
