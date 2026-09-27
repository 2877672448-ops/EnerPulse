package com.enerpulse.controller;

import com.enerpulse.common.api.ApiResponse;
import com.enerpulse.common.api.PageResult;
import com.enerpulse.common.util.BeanCopyUtils;
import com.enerpulse.dto.request.UserRequest;
import com.enerpulse.dto.response.UserResponse;
import com.enerpulse.entity.User;
import com.enerpulse.operationlog.OperationLog;
import com.enerpulse.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @GetMapping
    public ApiResponse<PageResult<UserResponse>> list(@RequestParam(defaultValue = "1") int page,
                                                       @RequestParam(defaultValue = "20") int pageSize,
                                                       @RequestParam(required = false) String keyword,
                                                       @RequestParam(required = false) String status) {
        PageResult<User> pageResult = userService.list(page, pageSize, keyword, status);
        PageResult<UserResponse> resp = new PageResult<>();
        resp.setTotal(pageResult.getTotal());
        resp.setItems(BeanCopyUtils.copyList(pageResult.getItems(), UserResponse.class));
        return ApiResponse.success(resp);
    }

    @PostMapping
    @OperationLog(module = "USER", action = "CREATE")
    public ApiResponse<UserResponse> create(@Valid @RequestBody UserRequest req) {
        User u = BeanCopyUtils.copy(req, User.class);
        return ApiResponse.success(BeanCopyUtils.copy(userService.create(u, null), UserResponse.class));
    }

    @PutMapping("/{id}")
    @OperationLog(module = "USER", action = "UPDATE")
    public ApiResponse<UserResponse> update(@PathVariable Long id, @Valid @RequestBody UserRequest req) {
        User u = BeanCopyUtils.copy(req, User.class);
        return ApiResponse.success(BeanCopyUtils.copy(userService.update(id, u), UserResponse.class));
    }

    @DeleteMapping("/{id}")
    @OperationLog(module = "USER", action = "DELETE")
    public ApiResponse<Void> delete(@PathVariable Long id) {
        userService.delete(id);
        return ApiResponse.success();
    }

    @PostMapping("/{id}/reset-password")
    @OperationLog(module = "USER", action = "RESET_PASSWORD")
    public ApiResponse<Void> resetPassword(@PathVariable Long id, @RequestBody UserRequest req) {
        userService.resetPassword(id);
        return ApiResponse.success();
    }
}
