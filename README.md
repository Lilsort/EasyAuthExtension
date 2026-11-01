# EasyAuthExtension (Fabric 1.20.1)

A server-side security extension for the popular [EasyAuth](https://github.com/NikitaCartes/EasyAuth) Fabric authentication mod, designed to protect Minecraft servers against password brute-force and dictionary attacks.

---

## 🛡️ Features

- **Brute-Force Detection:** Tracks failed login attempts per IP address in real-time (`FailedLoginIpManager`).
- **Automated IP Bans:** Automatically bans attacker IP addresses for a configurable duration when max attempt limits are reached.
- **Reset on Success:** Successful logins automatically reset the failure counter for that IP.
- **Configurable:** Persistent JSON configuration for ban duration and max failed attempts.

---

## ⌨️ In-Game Commands (Admin / Level 3+)

- `/eaext bantime <minutes>` — Set the automatic IP ban duration (in minutes).
- `/eaext maxattempts <number>` — Set the maximum allowed failed attempts before banning.
- `/eaext unban <ip>` — Unban a specific IP address (with auto-completion of currently banned IPs).

---

## 🛠️ Building

```bash
git clone https://github.com/Lilsort/EasyAuthExtension.git
cd EasyAuthExtension
./gradlew build
```
