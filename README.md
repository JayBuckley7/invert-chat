# Chat Down

A RuneLite plugin that puts chat stones above the native chatbox so opening it expands downwards. Split private chat follows beneath the open box, or beneath the stones when collapsed. If there is insufficient space beneath the box, private messages retain their native position.

Enable **Chat Down** in a resizable client. Use RuneLite's existing Alt-drag to move the chatbox to the top edge. Fixed mode keeps the normal layout. Disabling the plugin restores the positions it owns.

Development: Java 11-compatible sources, `./gradlew test jar`, or `./gradlew run` to launch the official development client. Hot reload tooling stays outside the plugin.

Manual checks: open/collapse each chat tab without moving the stones; type and scroll; receive split private messages with chat open and closed; test a dialogue and a resized window; disable the plugin and check the native layout returns.

