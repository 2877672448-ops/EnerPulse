#!/bin/bash
set -e

GREEN='\033[0;32m'
YELLOW='\033[0;33m'
RED='\033[0;31m'
NC='\033[0m'

echo -e "${GREEN}===== EnerPulse 能耗管理平台 一键部署 =====${NC}"

# ===== 检查 Docker =====
if ! command -v docker &> /dev/null; then
    echo -e "${RED}错误: 未安装 Docker${NC}"
    echo "请先安装 Docker:"
    echo "  curl -fsSL https://get.docker.com | sh"
    echo "  systemctl start docker && systemctl enable docker"
    exit 1
fi

if ! docker compose version &> /dev/null; then
    echo -e "${RED}错误: 未安装 docker-compose${NC}"
    echo "请先安装 docker-compose:"
    echo "  curl -L https://github.com/docker/compose/releases/latest/download/docker-compose-linux-x86_64 -o /usr/local/bin/docker-compose"
    echo "  chmod +x /usr/local/bin/docker-compose"
    exit 1
fi

echo -e "${GREEN}✓ Docker 已安装${NC}"

# ===== 配置 =====
echo ""
read -p "请输入数据库密码 (默认 enerpulse2024): " DB_PASS
DB_PASS=${DB_PASS:-enerpulse2024}

read -p "请输入对外端口 (默认 80): " PORT
PORT=${PORT:-80}

JWT_SECRET=$(openssl rand -hex 32)

# 写入 .env
cat > .env << EOF
POSTGRES_PASSWORD=$DB_PASS
JWT_SECRET=$JWT_SECRET
PORT=$PORT
EOF

echo ""
echo -e "${YELLOW}配置确认:${NC}"
echo "  数据库密码: ******"
echo "  对外端口: $PORT"
echo "  JWT密钥: 已自动生成"
echo ""
read -p "确认开始部署? (y/n): " confirm
if [ "$confirm" != "y" ]; then
    echo "已取消"
    exit 0
fi

# ===== 构建+启动 =====
echo ""
echo -e "${GREEN}===== 开始构建并启动 (首次约5-10分钟) =====${NC}"
docker compose up -d --build

# ===== 等待启动 =====
echo ""
echo -e "${YELLOW}等待服务启动...${NC}"
sleep 15

# 检查状态
ALL_OK=true
for svc in postgres redis backend frontend; do
    if docker ps | grep -q "enerpulse-$svc"; then
        echo -e "  ${GREEN}✓ $svc 已启动${NC}"
    else
        echo -e "  ${RED}✗ $svc 启动失败${NC}"
        docker logs enerpulse-$svc --tail 20 2>&1
        ALL_OK=false
    fi
done

if [ "$ALL_OK" = false ]; then
    echo ""
    echo -e "${RED}部分服务启动失败，请检查日志${NC}"
    echo "  docker compose logs -f"
    exit 1
fi

# ===== 获取服务器IP =====
SERVER_IP=$(curl -s ifconfig.me 2>/dev/null || hostname -I | awk '{print $1}')

echo ""
echo -e "${GREEN}===== 部署成功! =====${NC}"
echo ""
echo "  访问地址: http://$SERVER_IP:$PORT"
echo "  登录账号: admin"
echo "  登录密码: 123456"
echo ""
echo -e "${YELLOW}  ⚠ 首次登录后请立即修改默认密码!${NC}"
echo ""
echo "常用命令:"
echo "  查看日志:   docker compose logs -f backend"
echo "  重启服务:   docker compose restart"
echo "  停止服务:   docker compose down"
echo "  查看状态:   docker compose ps"
echo ""