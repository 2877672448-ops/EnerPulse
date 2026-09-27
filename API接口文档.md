# EnerPulse API 接口文档（可开发版）

> 版本：v1.0.0  
> Base URL：`/api/v1`  
> 说明：REST 路径、状态码、JSON 返回结构属于本项目开发规范，不是原厂接口原文。

---

# 1. API 总规范

## 1.1 请求头

```http
Authorization: Bearer <access_token>
Content-Type: application/json
X-Request-Id: req_xxx
```

## 1.2 成功响应

```json
{
  "code": 0,
  "message": "success",
  "data": {},
  "requestId": "req_001"
}
```

## 1.3 失败响应

```json
{
  "code": 40001,
  "message": "参数错误",
  "data": null,
  "requestId": "req_001"
}
```

## 1.4 分页

请求：

```http
GET /api/v1/devices?page=1&pageSize=20
```

响应：

```json
{
  "code": 0,
  "message": "success",
  "data": {
    "items": [],
    "page": 1,
    "pageSize": 20,
    "total": 0
  },
  "requestId": "req_001"
}
```

---

# 2. 认证

## POST /auth/login

请求：

```json
{
  "username": "admin",
  "password": "123456"
}
```

响应：

```json
{
  "code": 0,
  "message": "success",
  "data": {
    "accessToken": "xxx",
    "expiresIn": 7200,
    "user": {
      "id": 1,
      "username": "admin",
      "nickname": "管理员"
    }
  },
  "requestId": "req_001"
}
```

## POST /auth/logout

```http
POST /api/v1/auth/logout
```

## GET /auth/me

```http
GET /api/v1/auth/me
```

---

# 3. 用户

## GET /users

参数：

```text
page
pageSize
keyword
status
roleId
```

## POST /users

```json
{
  "username": "operator",
  "nickname": "运行人员",
  "mobile": "13800000000",
  "roleIds": [2],
  "status": "ACTIVE"
}
```

## PUT /users/{id}

## DELETE /users/{id}

## POST /users/{id}/reset-password

用户新增、修改、删除、重置密码属于原系统能力。fileciteturn0file0L151-L175

---

# 4. 角色与权限

```http
GET  /roles
POST /roles
PUT  /roles/{id}
DELETE /roles/{id}

GET /permissions
PUT /roles/{id}/permissions
```

角色权限配置属于原系统功能。fileciteturn0file0L195-L223

---

# 5. 区域

## GET /areas/tree

```json
{
  "code": 0,
  "message": "success",
  "data": [
    {
      "id": 1,
      "code": "B001",
      "name": "A栋",
      "areaType": "BUILDING",
      "children": [
        {
          "id": 2,
          "parentId": 1,
          "code": "F01",
          "name": "1层",
          "areaType": "FLOOR",
          "children": []
        }
      ]
    }
  ]
}
```

## POST /areas

```json
{
  "parentId": 1,
  "areaType": "FLOOR",
  "code": "F01",
  "name": "1层",
  "sortNo": 1
}
```

## PUT /areas/{id}

## DELETE /areas/{id}

---

# 6. 网关

## GET /gateways

筛选：

```text
keyword
status
```

## POST /gateways

```json
{
  "gatewayCode": "GW001",
  "name": "一号楼网关",
  "topic": "enerpulse/T001/GW001",
  "serverHost": "192.168.1.10",
  "serverPort": 1883,
  "reportInterval": 60
}
```

## PUT /gateways/{id}

## DELETE /gateways/{id}

## GET /gateways/{id}/status

## POST /gateways/{id}/sync-points

网关配置包括 Topic、网关 ID、平台地址、MQTT 端口、报告周期。fileciteturn0file0L329-L344

---

# 7. 设备

```http
GET    /devices
POST   /devices
GET    /devices/{id}
PUT    /devices/{id}
DELETE /devices/{id}
```

POST：

```json
{
  "deviceCode": "D001",
  "name": "一号配电箱",
  "gatewayId": 1,
  "areaId": 2,
  "energyTypeId": 10,
  "installLocation": "1层配电室"
}
```

---

# 8. 测点

```http
GET    /points
POST   /points
GET    /points/{id}
PUT    /points/{id}
DELETE /points/{id}
```

POST：

```json
{
  "gatewayId": 1,
  "deviceId": 1,
  "pointCode": "P001",
  "name": "总电表累计电量",
  "energyTypeId": 1,
  "energyItemId": 1001,
  "valueType": "CUMULATIVE",
  "unit": "kWh",
  "totalFlag": true,
  "multiplier": 1
}
```

---

# 9. 字典

```http
GET  /dictionaries/types
POST /dictionaries/types
GET  /dictionaries/items
POST /dictionaries/items
PUT  /dictionaries/items/{id}
DELETE /dictionaries/items/{id}
```

---

# 10. 峰平谷

```http
GET  /tariff-periods
POST /tariff-periods
PUT  /tariff-periods/{id}
DELETE /tariff-periods/{id}
POST /tariff-periods/validate
```

POST：

```json
{
  "name": "高峰",
  "periodType": "PEAK",
  "startTime": "08:00:00",
  "endTime": "12:00:00",
  "sortNo": 1
}
```

校验接口返回：

```json
{
  "valid": true,
  "coveredMinutes": 1440,
  "overlap": false,
  "gap": false
}
```

原系统要求峰平谷覆盖完整 24 小时。fileciteturn0file0L288-L301

---

# 11. 费率

```http
GET  /tariffs
POST /tariffs
PUT  /tariffs/{id}
DELETE /tariffs/{id}
```

---

# 12. 历史数据

```http
GET /energy/history
```

参数：

```text
gatewayId
energyTypeId
energyItemId
deviceId
pointId
startTime
endTime
quality
```

返回：

```json
{
  "items": [
    {
      "pointId": 1001,
      "timestamp": "2026-09-27T10:00:00+08:00",
      "value": 123.45,
      "unit": "kWh",
      "quality": "GOOD"
    }
  ]
}
```

原系统支持按网关、能源类型/子项、设备、测点和时间条件查询历史数据。fileciteturn0file0L366-L381

---

# 13. 设备数据

```http
GET /energy/device-data
```

可导出：

```http
POST /energy/device-data/export
```

---

# 14. 能耗分析

```http
GET /energy/analysis
```

参数：

```text
objectType
objectId
energyTypeId
category
period
startDate
endDate
```

示例：

```http
GET /api/v1/energy/analysis?objectType=AREA&objectId=2&energyTypeId=1&period=DAY&startDate=2026-09-01&endDate=2026-09-27
```

响应：

```json
{
  "series": [
    {
      "date": "2026-09-27",
      "consumption": 1234.56,
      "cost": 876.55
    }
  ],
  "totalConsumption": 1234.56,
  "totalCost": 876.55
}
```

原系统包含能耗分析、柱状图、PDF/Excel 下载等功能。fileciteturn0file0L406-L413

---

# 15. 能源占比

```http
GET /energy/ratio
```

响应：

```json
{
  "items": [
    {
      "energyTypeId": 1,
      "name": "电",
      "consumption": 1000,
      "ratio": 0.65
    }
  ]
}
```

---

# 16. 同比

```http
GET /energy/yoy
```

响应：

```json
{
  "current": 1200,
  "previous": 1000,
  "difference": 200,
  "rate": 0.2
}
```

---

# 17. 报表导出

```http
POST /reports/export
GET  /reports/exports/{id}
GET  /reports/exports/{id}/download
```

请求：

```json
{
  "reportType": "ENERGY_ANALYSIS",
  "format": "XLSX",
  "filters": {
    "startDate": "2026-09-01",
    "endDate": "2026-09-27"
  }
}
```

---

# 18. Dashboard

```http
GET    /dashboards
POST   /dashboards
GET    /dashboards/{id}
PUT    /dashboards/{id}
DELETE /dashboards/{id}

POST   /dashboards/{id}/widgets
PUT    /dashboards/{id}/widgets/{widgetId}
DELETE /dashboards/{id}/widgets/{widgetId}
PUT    /dashboards/{id}/widgets/order
```

---

# 19. 告警

```http
GET /alarms
GET /alarms/{id}
POST /alarms/{id}/ack
POST /alarms/{id}/recover
POST /alarms/{id}/close
```

查询参数：

```text
gatewayId
deviceId
pointId
level
status
startTime
endTime
```

---

# 20. 通知

```http
GET /notifications
POST /notifications/{id}/retry
```

---

# 21. 云端可视化

```http
GET    /visualizations
POST   /visualizations
PUT    /visualizations/{id}
DELETE /visualizations/{id}
POST   /visualizations/{id}/upload
GET    /visualizations/{id}/download
```

上传使用：

```http
Content-Type: multipart/form-data
```

字段：

```text
projectName
entryFile
file
accessMode
```

---

# 22. 操作日志

```http
GET /operation-logs
```

查询：

```text
userId
module
action
startTime
endTime
keyword
```

原系统包含操作日志。fileciteturn0file0L302-L322

---

# 23. HTTP 错误码

```text
0       成功
40001   参数错误
40101   未登录
40301   无权限
40401   资源不存在
40901   唯一键冲突
42201   业务校验失败
50001   系统错误
```

---

# 24. 接口开发要求

- [ ] 所有接口需要认证
- [ ] 所有写接口需要权限
- [ ] 所有写接口记录操作日志
- [ ] 所有错误带 requestId
- [ ] 不返回堆栈
- [ ] 时间字段统一 ISO-8601
- [ ] 分页统一结构
- [ ] 导出统一任务化
