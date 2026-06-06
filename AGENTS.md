# Database Projects

Database-related backend projects in this workspace.

## Projects

| Project | Path | Stack | Description |
|---|---|---|---|
| hmdp | `Database/hmdp` | Java 8, Spring Boot 2.7, Redis | 黑马点评 — 秒杀、Feed 流、优惠券 |
| hmdp-plus | `Database/hmdp-plus` | Java 8, Spring Boot | hmdp 模块化重构版（多模块 Maven） |
| HMDP-Redis | `Database/HMDP-Redis` | Java, Redis | Redis 独立实现 |
| deer-flow | `Database/deer-flow` | — | 工作流项目 |
| airi | `Database/airi` | TypeScript, pnpm | AI 助手子集 |

## Common Setup

Most projects use MySQL + Redis locally. Default credentials in `application.yaml`:
- MySQL: `root / 123456` on `127.0.0.1:3306`
- Redis: `127.0.0.1:6739` (no password)

## Boundaries

### ⚠️ Ask First
- Modifying database connection or Redis config
- Switching auth provider (Session / JWT / Redis-token)
- Adding Flyway migrations that affect other projects

### 🚫 Never
- Commit real database credentials or secrets
- Remove or rename shared entity/DTO fields without updating all consumers
