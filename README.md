# Mob & Entity ESP Mod — Minecraft 1.21 (Fabric)

Mod Client cho **Minecraft 1.21 (Java 21, Fabric Loader 0.16.9, Fabric API 0.102.0+1.21, Yarn `1.21+build.9`)** hiển thị vị trí sinh vật trong game với 3 lớp trực quan:
1. **3D Bounding Box (`WorldRenderer.drawBox`):** Khung hộp 3D nội suy mượt theo `RenderTickCounter.getTickDelta(false)`.
2. **Viền Phát Sáng Xuyên Tường (`MinecraftClientMixin` + `EntityMixin`):** Bật shader outline gốc của Minecraft 1.21 xuyên địa hình với màu sắc riêng theo từng phân nhóm sinh vật (tương thích hoàn toàn với OpenGL 3.2 Core Profile).
3. **Đường Chỉ Hướng (`Tracer Line`) & HUD Thống Kê (`HudRenderCallback`):** Đường nối từ tâm nhìn tới sinh vật và bảng đếm số lượng sinh vật quanh người chơi ở góc trái trên màn hình.

---

## Phím Tắt Mặc Định Trong Game

| Phím | Chức năng |
|---|---|
| `O` | Bật / Tắt toàn bộ ESP Sinh Vật |
| `P` | Chuyển chế độ lọc: **Tất cả** $\rightarrow$ **Chỉ Quái thù địch** $\rightarrow$ **Chỉ Thú hiền** $\rightarrow$ **Chỉ Người chơi** |
| `I` | Bật / Tắt Viền Phát Sáng Xuyên Tường (Vanilla Shader Outline) |
| `U` | Bật / Tắt Đường Chỉ Hướng (Tracer Lines) |

---

## Phân Loại Màu Sắc Sinh Vật

| Nhóm sinh vật | Lớp Minecraft 1.21 (Yarn) | Màu hiển thị | Mã màu Hex |
|---|---|---|---|
| **Quái thù địch (Hostile)** | `Monster` | Đỏ | `#FF3333` |
| **Người chơi (Player)** | `PlayerEntity` | Vàng kim | `#FFD91A` |
| **Thú hiền (Passive)** | `PassiveEntity` | Xanh lá | `#33F259` |
| **Sinh vật nước / Ambient** | `WaterCreatureEntity`, `AmbientEntity` | Xanh lam (Cyan) | `#33CCFF` |
| **Sinh vật khác (Golem...)** | `LivingEntity` khác | Tím | `#CC59FF` |

---

## Môi Trường Build Từ Xa Trên GitHub Codespaces (Tuân Thủ `SKILL.md`)

Theo **§1.1 của `SKILL.md` (`github-codespaces-remote-agent`)**, máy điều phối (orchestrator) **không chạy `gradle`/`javac` cục bộ** mà thực thi toàn bộ quá trình biên dịch trên GitHub Codespaces:

1. **Cấu hình Devcontainer chuẩn §10 (`.devcontainer/devcontainer.json` & `.devcontainer/setup-agent.sh`):**
   - Cài đặt sẵn **OpenJDK 21 (Temurin)**, **Gradle**, **GitHub CLI (`gh`)** và **OpenSSH Server (`sshd`)**.
   - Script `.devcontainer/setup-agent.sh` tự động kiểm tra JDK 21, khởi tạo Gradle Wrapper `8.10.2` và chỉ ghi marker `.git/agent-ready` sau khi mọi bước hoàn tất.
2. **Chạy Build tự động từ Orchestrator sang Codespace (`remote-build-codespace.sh`):**
   ```bash
   # Nạp token hợp lệ có quyền codespace từ secret store (không dán vào chat)
   export CS_REPO="jsjsjwieje658-spec/<TEN_REPO_MOD>"
   export CS_PROFILE="fabric-build"
   bash /home/user/staged-fabric-entity-esp-1.21/remote-build-codespace.sh
   ```
   Script sẽ chạy tuần tự: `doctor` $\rightarrow$ `ensure --dry-run` $\rightarrow$ `ensure` $\rightarrow$ `put` mã nguồn lên Codespace $\rightarrow$ `exec -- ./gradlew build --no-daemon` trên Codespace $\rightarrow$ `get` file `build/libs/mobesp-1.0.0.jar` về `/home/user/artifacts/mobesp-1.0.0.jar` $\rightarrow$ tự động `stop` Codespace trong `trap EXIT`.
