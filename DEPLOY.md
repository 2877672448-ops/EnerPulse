# EnerPulse 云服务器部署指南

## 第一步：购买云服务器

| 平台 | 推荐 | 最低配置 |
|------|------|---------|
| 阿里云 | ECS 轻量服务器 | 2核4G 40G SSD |
| 腾讯云 | 轻量应用服务器 | 2核4G 50G SSD |
| 华为云 | Flexus云服务器 | 2核4G 40G |

系统选择：Ubuntu 22.04 或 CentOS 8+

## 第二步：安装 Docker

SSH 登录服务器后执行：

```bash
# 一键安装 Docker
curl -fsSL https://get.docker.com | sh
systemctl start docker
systemctl enable docker

# 验证
docker --version
```

## 第三步：上传项目

在本地电脑执行：

```bash
# 方法1: scp 上传
scp -r D:\EnerFlow root@你的服务器IP:/opt/enerpulse

# 方法2: 如果用 Git
# 在服务器上:
git clone 你的仓库地址 /opt/enerpulse
```

## 第四步：一键部署

```bash
cd /opt/enerpulse
bash deploy.sh
```

按提示输入：
- 数据库密码（建议改掉默认密码）
- 端口（默认80，直接回车即可）

等待 5-10 分钟自动构建完成。

## 第五步：访问

浏览器打开：`http://你的服务器IP`
- 账号：admin
- 密码：123456

## （可选）配置域名 + HTTPS

```bash
# 安装 Caddy（自动 HTTPS）
apt install caddy -y

# 配置反向代理
cat > /etc/caddy/Caddyfile << EOF
你的域名.com {
    reverse_proxy localhost:80
}
EOF

systemctl restart caddy
```

访问 `https://你的域名.com` 即可，自动 HTTPS。

## 常用运维命令

| 操作 | 命令 |
|------|------|
| 查看所有服务 | docker compose ps |
| 查看后端日志 | docker compose logs -f backend |
| 重启所有服务 | docker compose restart |
| 停止所有服务 | docker compose down |
| 重新构建 | docker compose up -d --build |
| 查看数据卷 | docker volume ls |