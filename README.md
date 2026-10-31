**English** | [Русский](README.ru.md)

[![Build Status](https://github.com/nesquikpro/monkey/actions/workflows/gradle.yml/badge.svg)](https://github.com/nnsquik/essentials/actions/workflows/gradle.yml)

---

### Mods for Minecraft 1.8.9, written in Forge 2.x + Mixin

---

## Mods

|                                       |                                                                      |
|---------------------------------------|----------------------------------------------------------------------|
| **AutoSprint**                        | Automatic sprint without having to hold down a key                   |
| **Clear Chat Messages**               | Deleting messages from a chat                                        |
| **Copy Chat Message**                 | The ability to copy a message directly from the chat                 |
| **Chat Translator**                   | Translate chat messages to Russian with a click, and RU → EN with `/tr` |
| **Chat Text Border**                  | Draws a thin outline around chat text for better readability         |
| **Remove Footer & Header (Tab List)** | Removes the top and bottom banners from the TAB player list          |
| **Custom GUI**                        | For the Hypixel server (QuickMod)                                    |
| **Chat ❤ Replacer**                   | Automatically replaces <3 with ❤️ as you type                        |
| **Remove Crosshair Blend**            | Removes blurring/color overlap in the crosshairs                     |
| **Player Ping in Tab List**           | Displays players' ping in the TAB list                               |
| **Always Night Mode**                 | Turns the world into a never-ending night                            |
| **No Foliage Render**                 | Removes leaves, grass, and vegetation to improve FPS and visibility  |
| **Remove red numbers in Scoreboard**  | Removes red numbers (points) from the scoreboard                     |
| **FullBright**                        | Maximum gamma, no dark areas                                         |
| **Hide Boss Bar**                     | Hides the boss health bar                                            |
| **Zoom Smooth Camera Fix**            | Disables smooth camera while the zoom key is held                    |

## Commands

All commands are client-side: nothing is sent to the server.

| Command             | Description                                                                 |
|---------------------|-----------------------------------------------------------------------------|
| `/ts`               | Toggle AutoSprint                                                           |
| `/cc`               | Clear chat                                                                  |
| `/copy <text>`      | Copy text to the clipboard (used by Copy Chat Message)                      |
| `/fov <10-110>`     | Set the field of view                                                       |
| `/tr <message>`     | Translate a message from Russian to English (shown only to you)             |
| `/monkey`           | Toggle the Murder Mystery detector (Hypixel only)                           |

## Keybinds

| Key | Description                               |
|-----|-------------------------------------------|
| `M` | Join Murder Mystery Classic (Hypixel only) |
| `G` | Open the quick-join GUI (Hypixel only)    |

Keys can be changed in the Minecraft controls menu under **murder mystery**.

## Chat Translator

### Translate incoming messages

Every chat message gets a clickable **[T]** button. Click it and the message is
translated to Russian (the source language is detected automatically). The result
is shown only in your own chat.

### `/tr` — translate your own message

```
/tr Привет, это не моё
[tr] hi, this isnt mine
```

The result is formatted the way people actually type in chat:

- starts with a lowercase letter
- apostrophes are removed (`it's` → `its`, `don't` → `dont`)
- no period at the end

Click the result to copy it to the clipboard, then paste it into the real chat.

### How it works

Requests run in a background thread, so the game never freezes. Services are tried
in order until one responds:

1. Google Cloud Translation API (optional, set `OFFICIAL_API_KEY` in `Translator.java`)
2. Google Translate (`translate.googleapis.com`, `translate.google.com`)
3. Google `clients5` endpoint
4. MyMemory (no key, ~5000 characters per day)

If every service fails, the chat shows the error of each one, e.g.
`googleapis: HTTP 429; clients5: HTTP 429; mymemory: HTTP 429`.

> The free Google endpoints are unofficial and may return `HTTP 429` (too many
> requests). In that case the translation falls back to MyMemory automatically.

## Murder Mystery Mod

### Features

- **Highlight in the tab list** — the killer's nickname turns dark red
- **Chat notification** — the mod posts the killer's name in the chat
- Support for **Classic** and **Double** modes

### How it works

Every frame the mod checks the players around you to see whether they are holding
an item with a knife skin. The player list is refreshed once per second.
If a knife is detected, the player is marked as a killer, highlighted in the tab
list, and a message is posted in the chat.
