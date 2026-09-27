package com.enerpulse.controller;

import com.enerpulse.common.api.ApiResponse;
import com.enerpulse.entity.DictionaryItem;
import com.enerpulse.entity.DictionaryType;
import com.enerpulse.operationlog.OperationLog;
import com.enerpulse.service.DictionaryService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/dictionaries")
@RequiredArgsConstructor
public class DictionaryController {
    private final DictionaryService dictionaryService;

    @GetMapping("/types")
    public ApiResponse<List<DictionaryType>> listTypes() {
        return ApiResponse.success(dictionaryService.listTypes());
    }

    @PostMapping("/types")
    @OperationLog(module = "DICTIONARY", action = "CREATE_TYPE")
    public ApiResponse<DictionaryType> createType(@RequestBody DictionaryType type) {
        return ApiResponse.success(dictionaryService.createType(type));
    }

    @GetMapping("/items")
    public ApiResponse<List<DictionaryItem>> listItems(@RequestParam Long typeId) {
        return ApiResponse.success(dictionaryService.listItems(typeId));
    }

    @PostMapping("/items")
    @OperationLog(module = "DICTIONARY", action = "CREATE_ITEM")
    public ApiResponse<DictionaryItem> createItem(@RequestBody DictionaryItem item) {
        return ApiResponse.success(dictionaryService.createItem(item));
    }

    @PutMapping("/items/{id}")
    @OperationLog(module = "DICTIONARY", action = "UPDATE_ITEM")
    public ApiResponse<DictionaryItem> updateItem(@PathVariable Long id, @RequestBody DictionaryItem item) {
        return ApiResponse.success(dictionaryService.updateItem(id, item));
    }

    @DeleteMapping("/items/{id}")
    @OperationLog(module = "DICTIONARY", action = "DELETE_ITEM")
    public ApiResponse<Void> deleteItem(@PathVariable Long id) {
        dictionaryService.deleteItem(id);
        return ApiResponse.success();
    }
}
