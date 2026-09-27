package com.enerpulse.service;

import com.enerpulse.common.exception.BusinessException;
import com.enerpulse.dto.request.MenuRequest;
import com.enerpulse.entity.Menu;
import com.enerpulse.repository.MenuRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class MenuService {
    private final MenuRepository menuRepository;
    private static final Long DEFAULT_TENANT_ID = 1L;

    public List<Menu> list() {
        return menuRepository.findByTenantIdOrderBySortNoAsc(DEFAULT_TENANT_ID);
    }

    public Menu create(MenuRequest req) {
        Menu menu = new Menu();
        menu.setTenantId(DEFAULT_TENANT_ID);
        menu.setParentId(req.getParentId());
        menu.setName(req.getName());
        menu.setPath(req.getPath());
        menu.setIcon(req.getIcon());
        menu.setSortNo(req.getSortNo() != null ? req.getSortNo() : 0);
        menu.setStatus(req.getStatus() != null ? req.getStatus() : "ACTIVE");
        menu.setPermissionCode(req.getPermissionCode());
        menu.setPublicAccess(req.getPublicAccess() != null ? req.getPublicAccess() : false);
        if (Boolean.TRUE.equals(menu.getPublicAccess())) {
            menu.setPublicToken(UUID.randomUUID().toString().replace("-", ""));
        }
        return menuRepository.save(menu);
    }

    public Menu update(Long id, MenuRequest req) {
        Menu menu = get(id);
        menu.setParentId(req.getParentId());
        menu.setName(req.getName());
        menu.setPath(req.getPath());
        menu.setIcon(req.getIcon());
        menu.setSortNo(req.getSortNo() != null ? req.getSortNo() : menu.getSortNo());
        menu.setStatus(req.getStatus() != null ? req.getStatus() : menu.getStatus());
        menu.setPermissionCode(req.getPermissionCode());
        if (req.getPublicAccess() != null) {
            menu.setPublicAccess(req.getPublicAccess());
            if (Boolean.TRUE.equals(req.getPublicAccess()) && menu.getPublicToken() == null) {
                menu.setPublicToken(UUID.randomUUID().toString().replace("-", ""));
            }
        }
        return menuRepository.save(menu);
    }

    public void delete(Long id) {
        menuRepository.deleteById(id);
    }

    public Menu get(Long id) {
        return menuRepository.findById(id)
                .orElseThrow(() -> new BusinessException(40401, "菜单不存在"));
    }

    public String generatePublicLink(Long id) {
        Menu menu = get(id);
        if (!Boolean.TRUE.equals(menu.getPublicAccess())) {
            menu.setPublicAccess(true);
            menu.setPublicToken(UUID.randomUUID().toString().replace("-", ""));
            menuRepository.save(menu);
        }
        return "/public/menu/" + menu.getPublicToken();
    }
}
