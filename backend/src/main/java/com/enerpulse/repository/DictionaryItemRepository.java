package com.enerpulse.repository;

import com.enerpulse.entity.DictionaryItem;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface DictionaryItemRepository extends JpaRepository<DictionaryItem, Long> {
    List<DictionaryItem> findByTypeId(Long typeId);
    List<DictionaryItem> findByTypeIdOrderBySortNo(Long typeId);
}
