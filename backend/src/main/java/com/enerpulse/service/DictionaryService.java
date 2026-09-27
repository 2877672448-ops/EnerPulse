package com.enerpulse.service;

import com.enerpulse.common.exception.BusinessException;
import com.enerpulse.entity.DictionaryItem;
import com.enerpulse.entity.DictionaryType;
import com.enerpulse.repository.DictionaryItemRepository;
import com.enerpulse.repository.DictionaryTypeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DictionaryService {
    private final DictionaryTypeRepository typeRepository;
    private final DictionaryItemRepository itemRepository;
    private static final Long DEFAULT_TENANT_ID = 1L;

    public List<DictionaryType> listTypes() {
        return typeRepository.findByTenantIdOrTenantIdIsNull(DEFAULT_TENANT_ID);
    }

    public DictionaryType createType(DictionaryType type) {
        type.setTenantId(DEFAULT_TENANT_ID);
        return typeRepository.save(type);
    }

    public List<DictionaryItem> listItems(Long typeId) {
        return itemRepository.findByTypeIdOrderBySortNo(typeId);
    }

    public DictionaryItem createItem(DictionaryItem item) {
        item.setTenantId(DEFAULT_TENANT_ID);
        return itemRepository.save(item);
    }

    public DictionaryItem updateItem(Long id, DictionaryItem item) {
        DictionaryItem existing = itemRepository.findById(id)
                .orElseThrow(() -> new BusinessException(40401, "字典项不存在"));
        existing.setCode(item.getCode());
        existing.setName(item.getName());
        existing.setUnit(item.getUnit());
        existing.setSortNo(item.getSortNo());
        existing.setStatus(item.getStatus());
        return itemRepository.save(existing);
    }

    public void deleteItem(Long id) {
        itemRepository.deleteById(id);
    }
}
