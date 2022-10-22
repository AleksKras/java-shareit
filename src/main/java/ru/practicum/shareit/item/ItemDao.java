package ru.practicum.shareit.item;

import javax.validation.Valid;
import java.util.List;

public interface ItemDao {

    Item get(long id);

    Item create(@Valid Item item);

    void delete(long id);

    Item update(@Valid Item item);

    List<Item> getAll(long userid);

    List<Item> search(String query);

}
