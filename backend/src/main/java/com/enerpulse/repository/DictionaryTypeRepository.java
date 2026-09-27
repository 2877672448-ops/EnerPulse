package com.enerpulse.repository;

import com.enerpulse.entity.DictionaryType;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface DictionaryTypeRepository extends JpaRepository<DictionaryType, Long> {
    List<DictionaryType> findByTenantIdOrTenantIdIsNull(Long tenantId);
    Optional<DictionaryType> findByCode(String code);
}
