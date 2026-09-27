# EnerPulse MQTT 数据协议（可开发版）

> 版本：v1.0.0  
> 原始依据：迅饶能源管理平台使用说明书。  
> 原说明书明确 MQTT 接入、网关配置、Topic/ID 匹配、测点同步、数据存储和 MQTT 告警；以下 Topic 层级、JSON 字段及 QoS 为项目标准化协议建议。

---

# 1. 连接

```text
Protocol: MQTT
Default Port: 1883
```

网关必须提供：

```text
gatewayId
topic
serverHost
serverPort
reportInterval
```

原系统要求 Topic 和网关 ID 唯一，并在网关在线且匹配后同步点位。fileciteturn0file0L329-L344

---

# 2. Client ID

推荐：

```text
enerpulse-gateway-{gatewayId}
```

例如：

```text
enerpulse-gateway-GW001
```

---

# 3. Topic 标准

```text
enerpulse/{tenantId}/{gatewayId}/telemetry
enerpulse/{tenantId}/{gatewayId}/status
enerpulse/{tenantId}/{gatewayId}/alarm
enerpulse/{tenantId}/{gatewayId}/config
enerpulse/{tenantId}/{gatewayId}/ack
```

---

# 4. Topic 方向

```text
gateway -> platform
telemetry
status
alarm

platform -> gateway
config

gateway -> platform
ack
```

---

# 5. Telemetry

## Topic

```text
enerpulse/T001/GW001/telemetry
```

## Payload

```json
{
  "messageId": "MSG-20260927-000001",
  "gatewayId": "GW001",
  "timestamp": "2026-09-27T10:20:30+08:00",
  "points": [
    {
      "pointId": "P001",
      "value": 35.8,
      "quality": "GOOD"
    },
    {
      "pointId": "P002",
      "value": 10235.6,
      "quality": "GOOD"
    }
  ]
}
```

---

# 6. 字段

| 字段 | 必填 | 说明 |
|---|---|---|
| messageId | 是 | 报文唯一 ID |
| gatewayId | 是 | 网关 ID |
| timestamp | 是 | 采集时间 |
| points | 是 | 点位数组 |
| pointId | 是 | 测点编码 |
| value | 是 | 数值 |
| quality | 是 | 数据质量 |

---

# 7. Quality

标准枚举：

```text
GOOD
UNKNOWN
INVALID
STALE
MISSING
```

规则：

```text
GOOD      → 正常统计
UNKNOWN   → 保留并标记
INVALID   → 不进入正常统计
STALE     → 保留但标记过期
MISSING   → 不补 0
```

---

# 8. 瞬时值 / 累计值

原系统区分瞬时和累计，累计数据用于能耗分析。fileciteturn0file0L348-L363

测点自身定义：

```text
valueType = INSTANT
```

或：

```text
valueType = CUMULATIVE
```

累计数据：

```text
energy = currentTotal - previousTotal
```

---

# 9. 累计值异常

若：

```text
currentTotal < previousTotal
```

标记：

```text
METER_RESET
```

不得直接计算负能耗。

可选后续状态：

```text
METER_REPLACED
INVALID
```

---

# 10. Status

## Topic

```text
enerpulse/T001/GW001/status
```

Payload：

```json
{
  "messageId": "MSG-001",
  "gatewayId": "GW001",
  "timestamp": "2026-09-27T10:20:30+08:00",
  "status": "ONLINE",
  "firmwareVersion": "1.0.0"
}
```

状态：

```text
ONLINE
OFFLINE
```

---

# 11. Alarm

原系统支持通过 MQTT 接收设备告警。fileciteturn0file0L476-L486

## Topic

```text
enerpulse/T001/GW001/alarm
```

Payload：

```json
{
  "messageId": "ALM-001",
  "gatewayId": "GW001",
  "timestamp": "2026-09-27T10:20:30+08:00",
  "deviceId": "D001",
  "pointId": "P001",
  "alarmCode": "HIGH_POWER",
  "level": "HIGH",
  "message": "功率超过阈值",
  "value": 1200,
  "threshold": 1000,
  "status": "ACTIVE"
}
```

---

# 12. Config

## 平台下发

Topic：

```text
enerpulse/T001/GW001/config
```

Payload：

```json
{
  "requestId": "SYNC-001",
  "type": "POINT_SYNC",
  "gatewayId": "GW001",
  "timestamp": "2026-09-27T10:20:30+08:00",
  "points": [
    {
      "pointId": "P001",
      "name": "总电量",
      "valueType": "CUMULATIVE",
      "unit": "kWh",
      "multiplier": 1
    }
  ]
}
```

---

# 13. ACK

Topic：

```text
enerpulse/T001/GW001/ack
```

Payload：

```json
{
  "requestId": "SYNC-001",
  "gatewayId": "GW001",
  "timestamp": "2026-09-27T10:20:31+08:00",
  "success": true,
  "message": "sync success"
}
```

---

# 14. QoS

建议：

| Topic | QoS |
|---|---:|
| telemetry | 1 |
| status | 1 |
| alarm | 1 |
| config | 1 |
| ack | 1 |

QoS 为项目工程建议，原说明书未具体指定。

---

# 15. Retain

建议：

```text
status：retain=true
telemetry：retain=false
alarm：retain=false
config：retain=false
ack：retain=false
```

---

# 16. 幂等

服务端优先使用：

```text
messageId
```

作为报文幂等键。

没有 messageId 时：

```text
gatewayId + pointId + timestamp
```

作为业务幂等组合键。

---

# 17. 消息处理流程

```text
MQTT 收到消息
    ↓
解析 JSON
    ↓
校验 gatewayId
    ↓
校验 Topic
    ↓
校验 messageId
    ↓
校验 pointId
    ↓
校验 timestamp
    ↓
校验 quality/value
    ↓
幂等检查
    ↓
写 energy_raw_data
    ↓
发布内部数据事件
    ↓
聚合
```

---

# 18. 测点同步

原系统要求：

```text
网络正常
+
网关在线
+
Topic 一致
+
网关 ID 一致
```

后才能完成测点同步。fileciteturn0file0L534-L553

---

# 19. 接入验收

```text
[ ] MQTT 可连接
[ ] Gateway ID 正确
[ ] Topic 正确
[ ] telemetry 正常
[ ] status 正常
[ ] alarm 正常
[ ] config/ack 正常
[ ] 数据可入库
[ ] 重复报文不重复入库
[ ] 累计量可正常计算
[ ] 异常数据可识别
```

---

# 20. FAQ

### 测点同步失败

检查：

```text
网络
Broker
端口
Topic
Gateway ID
网关状态
```

原系统 FAQ 明确指出网络未连接或 Topic/ID 不匹配会导致同步失败。fileciteturn0file0L557-L581

### 数据不落历史库

检查：

```text
设备类型
值类型
测点配置
上报周期
时间戳
```

原系统快速开始说明要求配置设备类型/值类型等信息后进行历史数据存储。fileciteturn0file0L534-L553
