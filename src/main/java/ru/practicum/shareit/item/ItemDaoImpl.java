package ru.practicum.shareit.item;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;
import ru.practicum.shareit.exception.NotFoundException;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Data
@Component
@Validated
public class ItemDaoImpl implements ItemDao {
    private Map<Long, Item> itemMap = new HashMap<>();
    private long id;

    public Item get(long id) {
        if (itemMap.containsKey(id)) {
            return itemMap.get(id);
        } else {
            log.info("Пользователь с идентификатором {} не найден.", id);
            throw new NotFoundException("Пользователь с ID=" + id + " не найден");
        }
    }

    public Item create(@Valid Item item) {
        id++;
        item.setId(id);
        itemMap.put(id, item);
        return item;
    }

    public void delete(long itemId) {
        Item item = get(itemId);
        itemMap.remove(itemId);
    }

    public Item update(@Valid Item item) {
        itemMap.put(item.getId(), item);
        return item;
    }

    public List<Item> getAll(long userId) {
        List<Item> items = new ArrayList<Item>(itemMap.values());
        return items.stream().filter(item -> item.getOwner().getId() == userId).collect(Collectors.toList());
    }

    public List<Item> search(String query) {
        List<Item> searchResultList = new ArrayList<>();
        if (!query.isEmpty()) {
            List<Item> items = new ArrayList<Item>(itemMap.values());
            searchResultList = items.stream().filter(item ->
                    ((item.getName().toLowerCase().contains(query.toLowerCase()) ||
                            item.getDescription().toLowerCase().contains(query.toLowerCase())) &&
                            item.getAvailable())).collect(Collectors.toList());
        }
        return searchResultList;
    }
}
