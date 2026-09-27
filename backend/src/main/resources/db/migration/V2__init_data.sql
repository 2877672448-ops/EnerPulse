-- EnerPulse Initial Data V2

-- Default tenant
insert into tenants (id, name, code, status) values (1, '默认租户', 'T001', 'ACTIVE');

-- Admin user: username=admin, password=123456
insert into users (id, tenant_id, username, password_hash, nickname, status)
values (1, 1, 'admin', '$2a$10$G0B6YmQ.MttZjyyHt3NaROJdVvWKFEA1yateBbv9Uh0HPjuudOiq2', '系统管理员', 'ACTIVE');

-- Roles
insert into roles (id, tenant_id, name, code, description, status) values
(1, 1, '超级管理员', 'SUPER_ADMIN', '拥有所有权限', 'ACTIVE'),
(2, 1, '运维人员', 'OPERATOR', '设备与数据运维', 'ACTIVE');

-- Permissions
insert into permissions (code, name, type, resource, action) values
('user:view', '用户查看', 'API', 'user', 'view'),
('user:create', '用户新增', 'API', 'user', 'create'),
('user:edit', '用户编辑', 'API', 'user', 'edit'),
('user:delete', '用户删除', 'API', 'user', 'delete'),
('role:view', '角色查看', 'API', 'role', 'view'),
('role:edit', '角色编辑', 'API', 'role', 'edit'),
('area:view', '区域查看', 'API', 'area', 'view'),
('area:edit', '区域编辑', 'API', 'area', 'edit'),
('gateway:view', '网关查看', 'API', 'gateway', 'view'),
('gateway:edit', '网关编辑', 'API', 'gateway', 'edit'),
('device:view', '设备查看', 'API', 'device', 'view'),
('device:edit', '设备编辑', 'API', 'device', 'edit'),
('point:view', '测点查看', 'API', 'point', 'view'),
('point:edit', '测点编辑', 'API', 'point', 'edit'),
('energy:view', '能源查看', 'API', 'energy', 'view'),
('alarm:view', '告警查看', 'API', 'alarm', 'view'),
('alarm:edit', '告警处理', 'API', 'alarm', 'edit'),
('dashboard:view', '仪表板查看', 'API', 'dashboard', 'view'),
('dashboard:edit', '仪表板编辑', 'API', 'dashboard', 'edit'),
('system:view', '系统查看', 'API', 'system', 'view');

-- Admin gets all permissions
insert into role_permissions (role_id, permission_id)
select 1, id from permissions;

-- Operator gets view + device/energy/alarm edit
insert into role_permissions (role_id, permission_id)
select 2, id from permissions where code like '%:view' or code in ('device:edit','point:edit','alarm:edit');

-- Assign admin to super admin role
insert into user_roles (user_id, role_id) values (1, 1);

-- Dictionary types
insert into dictionary_types (id, tenant_id, code, name, status) values
(1, 1, 'ENERGY_TYPE', '能源类型', 'ACTIVE'),
(2, 1, 'ENERGY_ITEM', '能源子项', 'ACTIVE'),
(3, 1, 'UNIT', '单位', 'ACTIVE'),
(4, 1, 'AREA_TYPE', '区域类型', 'ACTIVE');

-- Energy types
insert into dictionary_items (tenant_id, type_id, code, name, unit, sort_no, status) values
(1, 1, 'ELECTRICITY', '电', 'kWh', 1, 'ACTIVE'),
(1, 1, 'WATER', '水', 'm3', 2, 'ACTIVE'),
(1, 1, 'GAS', '气', 'm3', 3, 'ACTIVE'),
(1, 1, 'HEAT', '热', 'GJ', 4, 'ACTIVE'),
(1, 1, 'COLD', '冷', 'GJ', 5, 'ACTIVE');

-- Units
insert into dictionary_items (tenant_id, type_id, code, name, sort_no, status) values
(1, 3, 'KWH', 'kWh', 1, 'ACTIVE'),
(1, 3, 'M3', 'm3', 2, 'ACTIVE'),
(1, 3, 'GJ', 'GJ', 3, 'ACTIVE'),
(1, 3, 'KW', 'kW', 4, 'ACTIVE');

-- Area types
insert into dictionary_items (tenant_id, type_id, code, name, sort_no, status) values
(1, 4, 'BUILDING', '建筑', 1, 'ACTIVE'),
(1, 4, 'FLOOR', '楼层', 2, 'ACTIVE'),
(1, 4, 'AREA', '区域', 3, 'ACTIVE');
