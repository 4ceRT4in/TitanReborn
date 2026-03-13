# TitanReborn Bugfix Progress Tracker

Zweck: Zentrale Fortschrittsliste für die vom Kunden gemeldeten Bugfixes.

## Status-Legende

- `TODO` = noch nicht begonnen
- `IN PROGRESS` = in Arbeit
- `BLOCKED` = blockiert
- `REVIEW` = fertig implementiert, wartet auf Prüfung
- `DONE` = abgenommen und abgeschlossen

## Gesamtübersicht

| ID | Thema | Status | Priorität | Verantwortlich | Branch/PR | Letztes Update |
|---|---|---|---|---|---|---|
| 1 | Sword / Greatsword Effect Component Reset | REVIEW | Hoch | Codex |  | 2026-03-13 |
| 2 | Armor Plating HUD Overlay Gamerule | REVIEW | Hoch | Codex |  | 2026-03-13 |
| 3 | Gamerule `titanfabric.DisableSwimming` | REVIEW | Hoch | Codex |  | 2026-03-13 |
| 4 | Unbreaking Enchantment Einschränkung | REVIEW | Hoch | Codex |  | 2026-03-13 |
| 5 | Immunity Effect GUI Rendering | REVIEW | Mittel | Codex |  | 2026-03-13 |
| 6 | Ember Armor Lava Protection Fix | REVIEW | Hoch | Codex |  | 2026-03-13 |
| 7 | Backpack Item Preview Fixes | REVIEW | Hoch | Codex |  | 2026-03-13 |
| 8 | EMI Recipe Fixes | REVIEW | Mittel | Codex |  | 2026-03-13 |
| 9 | Soul Fire Burning Verhalten | TODO | Hoch |  |  |  |
| 10 | Entfernte Gamerules | TODO | Hoch |  |  |  |
| 11 | Fire Enchantment Ban System Refactor | TODO | Hoch |  |  |  |
| 12 | Frostburn Gamerule Entfernung | TODO | Mittel |  |  |  |
| 13 | Immunity Effect Client/Server Desync Fix | TODO | Hoch |  |  |  |

---

## 1) Sword / Greatsword Effect Component Reset (Smithing Table)

**Status:** REVIEW  
**Verantwortlich:** Codex  
**Letztes Update:** 2026-03-13  

### Akzeptanzkriterien

- [x] Nur Sword oder Greatsword im Inputslot des Smithing Tables reicht aus.
- [x] Output ist dasselbe Item ohne Effect Component.
- [x] Enchantments bleiben erhalten.
- [x] Durability bleibt erhalten.
- [x] NBT-Daten bleiben erhalten.
- [x] Custom Name bleibt erhalten.
- [x] Nur die Effect Component wird entfernt.
- [x] Verhalten ist analog zum Armor Plating Removal System.
- [x] Implementierung nutzt kopierte/ähnliche Struktur zum Armor-Plating-Code.
- [x] Gemeinsame Logik ist (wenn sinnvoll) in Helper/Utility ausgelagert.

### Umsetzung / Tests

- [x] Implementiert
- [x] Singleplayer getestet
- [ ] Dedicated Server getestet
- [ ] Regression auf bestehendes Armor-Plating-Removal geprüft

---

## 2) Armor Plating HUD Overlay Gamerule

**Status:** REVIEW  
**Verantwortlich:** Codex  
**Letztes Update:** 2026-03-13  

### Akzeptanzkriterien

- [x] Neue Gamerule `titanfabric.ArmorHudOverlay` hinzugefügt.
- [x] Typ `boolean`, Default `true`.
- [x] Bei `true` wird das HUD Overlay gerendert.
- [x] Overlay wird auch ohne aktives Armor Plating auf getragener Rüstung gerendert.
- [x] Bei `false` wird das Overlay gar nicht gerendert.
- [x] Aussehen und restliches Verhalten des Overlays bleiben unverändert.

### Umsetzung / Tests

- [x] Implementiert
- [x] Client-Rendering getestet
- [x] Gamerule Toggle zur Laufzeit getestet

---

## 3) Neue Gamerule: Disable Swimming

**Status:** REVIEW  
**Verantwortlich:** Codex  
**Letztes Update:** 2026-03-13  

### Akzeptanzkriterien

- [x] Neue Gamerule `titanfabric.DisableSwimming` hinzugefügt.
- [x] Typ `boolean`, Default `true`.
- [x] Bei `true`: kein Wechsel in Swimming State.
- [x] Bei `true`: kein Sprint-Swimming.
- [x] Bei `true`: Swimming Pose wird nicht aktiviert.
- [x] Bei `true`: erhöhte Swimming-Geschwindigkeit wird nicht angewendet.
- [x] Bei `true`: normale Wasserbewegung bleibt möglich.
- [ ] Bei `false`: Vanilla-Swimming wieder vollständig aktiv.

### Umsetzung / Tests

- [x] Implementiert
- [ ] Bewegung in Wasser ohne Swim-State getestet
- [ ] Sprint-/Pose-/Speed-Interaktion getestet
- [ ] Multiplayer getestet

---

## 4) Unbreaking Enchantment Einschränkung

**Status:** REVIEW  
**Verantwortlich:** Codex  
**Letztes Update:** 2026-03-13  

### Betroffene Items

- [x] Titan Sword
- [x] Titan Greatsword
- [x] Titan Armor Pieces
- [x] Titan Bow
- [x] Titan Crossbow

### Akzeptanzkriterien

- [x] Enchanting Table rollt kein Unbreaking I-III auf den betroffenen Titan Items.
- [x] Andere Enchantments bleiben möglich.
- [x] Unbreaking kann weiterhin per Anvil + Enchanted Book hinzugefügt werden.

### Umsetzung / Tests

- [x] Implementiert
- [x] Enchanting-Table-Rolls getestet (mehrfach)
- [x] Anvil-Flow mit Enchanted Book getestet

---

## 5) Immunity Effect GUI Rendering

**Status:** REVIEW  
**Verantwortlich:** Codex  
**Letztes Update:** 2026-03-13  

### Akzeptanzkriterien

- [x] Durch Immunity aktuell blockierter negativer Effekt wird im GUI angezeigt.
- [x] Der blockierte Effekt wird unterhalb des Immunity Effects angezeigt.
- [x] Name des blockierten Effekts wird in roter Schrift gerendert.
- [x] Anzeige nur solange der Effekt aktiv durch Immunity blockiert wird.

### Umsetzung / Tests

- [x] Implementiert
- [ ] GUI Layout getestet
- [ ] Mehrere negative Effekte / Wechsel getestet

---

## 6) Ember Armor Lava Protection Fix

**Status:** REVIEW  
**Verantwortlich:** Codex  
**Letztes Update:** 2026-03-13  

### Akzeptanzkriterien: Lava Damage Reduction

- [x] Reduktion: 25 % pro getragenem Ember Armor Piece.
- [x] 1 Piece -> 75 % Restschaden.
- [x] 2 Pieces -> 50 % Restschaden.
- [x] 3 Pieces -> 25 % Restschaden.
- [x] 4 Pieces -> 0 % Restschaden.

### Akzeptanzkriterien: Burn Duration Reduction

- [x] Brenndauerreduktion um 25 % pro Piece bei Lava.
- [x] Brenndauerreduktion um 25 % pro Piece bei Fire Block.
- [x] Brenndauerreduktion um 25 % pro Piece bei Soul Fire Block.
- [x] Brenndauer nach Verlassen eines Fire Blocks reduziert sich entsprechend.

### Akzeptanzkriterien: Damage Tick Chance Reduction

- [x] Tick-Chance-Reduktion um 25 % pro Piece bei Lava.
- [x] Tick-Chance-Reduktion um 25 % pro Piece bei Fire.
- [x] Tick-Chance-Reduktion um 25 % pro Piece bei Soul Fire.
- [x] Tick-Chance-Reduktion um 25 % pro Piece bei Magma Block.

### Umsetzung / Tests

- [x] Implementiert
- [x] Alle 0/1/2/3/4-Piece-Kombinationen getestet
- [x] Schaden, Brenndauer und Tick-Chance separat validiert

---

## 7) Backpack Item Preview Fixes

**Status:** REVIEW  
**Verantwortlich:** Codex  
**Letztes Update:** 2026-03-13  

### Akzeptanzkriterien: Fill Bar

- [x] Fill-Bar-Berechnung ist `occupiedSlots / totalSlots`.
- [x] Beispiel 6 von 18 Slots ergibt 33 %.
- [x] UI zeigt den korrekt gerundeten/visualisierten Wert.

### Akzeptanzkriterien: Stack Anzeige

- [x] Identische Items werden in der Preview nicht mehr zusammengeführt.
- [x] Beispiel: `64 Stone` und `32 Stone` bleibt als zwei getrennte Stacks sichtbar.

### Umsetzung / Tests

- [x] Implementiert
- [x] Fill-Bar mit verschiedenen Slot-Belegungen getestet
- [x] Preview mit mehrfach identischen Item-Stacks getestet

---

## 8) EMI Recipe Fixes

**Status:** REVIEW  
**Verantwortlich:** Codex  
**Letztes Update:** 2026-03-13  

### Akzeptanzkriterien

- [x] Aus Furnace Recipe Tab in EMI entfernt: Titan Powder.
- [x] Aus Furnace Recipe Tab in EMI entfernt: Titan Ingots.
- [x] Aus Furnace Recipe Tab in EMI entfernt: Ember Ingots.
- [x] In EMI hinzugefügt (Smithing Table): Single Multi Bow.
- [x] In EMI hinzugefügt (Smithing Table): Double Multi Bow.
- [x] In EMI hinzugefügt (Smithing Table): Triple Multi Bow.

### Umsetzung / Tests

- [x] Implementiert
- [x] EMI-Ansicht manuell geprüft
- [x] Falsche/fehlende Rezepte verifiziert

---

## 9) Soul Fire Burning Verhalten

**Status:** TODO  
**Verantwortlich:**  
**Letztes Update:**  

### Akzeptanzkriterien

- [ ] Soul Fire brennt exakt doppelt so lange wie normales Fire.
- [ ] Startwert ist `FireTicks = -20`.
- [ ] Spieler kann 20 Ticks (1 Sekunde) im Soul Fire stehen und ohne Nachbrennen rausgehen.
- [ ] Sobald `FireTicks > 0`, wird sofort auf `FireTicks = 320` gesetzt.
- [ ] Wert steigt weiter, solange Spieler im Soul Fire bleibt.
- [ ] Creative Mode: `FireTicks = 1`.
- [ ] Creative Mode: Wert steigt nicht über Zeit.

### Umsetzung / Tests

- [ ] Implementiert
- [ ] Survival-Verhalten getestet
- [ ] Creative-Verhalten getestet
- [ ] Vergleich mit normalem Fire geprüft

---

## 10) Entfernte Gamerules

**Status:** TODO  
**Verantwortlich:**  
**Letztes Update:**  

### Akzeptanzkriterien

- [ ] Gamerule `titanfabric.fullinventorypotionbagsearch` vollständig entfernt.
- [ ] Gamerule `titanfabric.InfiniteSoulFireBurning` vollständig entfernt.
- [ ] Code entfernt, der Potion Bag / Bundle Linking-Funktion auf Offhand beschränkt.
- [ ] Code entfernt, der unendliches Soul-Fire-Brennen verursacht.

### Umsetzung / Tests

- [ ] Implementiert
- [ ] Referenzen auf entfernte Gamerules im gesamten Code entfernt
- [ ] Funktionales Verhalten ohne diese Regeln getestet

---

## 11) Fire Enchantment Ban System Refactor

**Status:** TODO  
**Verantwortlich:**  
**Letztes Update:**  

### Akzeptanzkriterien

- [ ] Bestehendes config-basiertes Enchantment-Ban-System entfernt.
- [ ] Neue Gamerule `titanfabric.DistableFireEnchantments` hinzugefügt (Name laut Ticket).
- [ ] Typ `boolean`, Default `true`.
- [ ] Bei `true`: `Flame` wird nicht generiert auf Sword, Greatsword, Bow.
- [ ] Bei `true`: `Fire Aspect` wird nicht generiert auf Sword, Greatsword, Bow.
- [ ] Ban greift bei Enchanting Table.
- [ ] Ban greift bei Anvil.
- [ ] Ban greift bei Netherite Anvil.
- [ ] Ban greift bei Villager Trading.
- [ ] Ban greift bei Loot Chests.

### Umsetzung / Tests

- [ ] Implementiert
- [ ] Alle Quellen der Enchantment-Generierung getestet
- [ ] Regression für andere Enchantments getestet

---

## 12) Frostburn Gamerule Entfernung

**Status:** TODO  
**Verantwortlich:**  
**Letztes Update:**  

### Akzeptanzkriterien

- [ ] Alle Frostburn-bezogenen Gamerules vollständig entfernt.
- [ ] Frostburn Effect Mechanik bleibt unverändert funktionsfähig.
- [ ] Frostburn Potion Mechanik bleibt unverändert funktionsfähig.

### Umsetzung / Tests

- [ ] Implementiert
- [ ] Suche nach verbliebenen Frostburn-Gamerule-Referenzen ohne Treffer
- [ ] Gameplay-Test für Effect und Potion erfolgreich

---

## 13) Immunity Effect Client / Server Desync Fix

**Status:** TODO  
**Verantwortlich:**  
**Letztes Update:**  

### Akzeptanzkriterien

- [ ] Nach Ablauf eines durch Immunity blockierten Effekts entfernt der Client den Effekt sofort.
- [ ] Kein dauerhaftes Anzeigen mit `0s` auf Client-Seite.
- [ ] Client- und Server-Status bleiben ohne Reconnect synchron.

### Umsetzung / Tests

- [ ] Implementiert
- [ ] Reproduktion des bisherigen Bugs dokumentiert
- [ ] Fix gegen Wither/andere negative Effekte geprüft
- [ ] Join/Leave/Reconnect-Verhalten validiert

---

## Änderungsprotokoll

| Datum | Änderung | Von |
|---|---|---|
| 2026-03-13 | Initiale Erfassung aller 13 Bugfix-Tickets als Progress-Tracker | Codex |
| 2026-03-13 | Bug 1 implementiert (Weapon-Effect-Reset im Smithing Table), Status auf REVIEW gesetzt | Codex |
| 2026-03-13 | Bug 1 nachgeschärft: Base/Innate Effect bleibt erhalten, nur Additional Effect wird entfernt | Codex |
| 2026-03-13 | Bug 2 implementiert (Armor HUD Overlay Gamerule + Rendering ohne Plating), Status auf REVIEW gesetzt | Codex |
| 2026-03-13 | Bug 2 nachgeschÃ¤rft: Client/Server-Sync fÃ¼r Overlay-Gamerule per S2C-Packet + Join-Sync; Singleplayer liest Rule direkt vom Integrated Server | Codex |
| 2026-03-13 | Bug 3 implementiert (DisableSwimming Gamerule, Swim-/Sprint-/Pose-Block serverseitig), Status auf REVIEW gesetzt | Codex |
| 2026-03-13 | Bug 3 nachgeschärft: kein Pose-Forcing mehr; Swim-State wird über updateSwimming serverseitig geblockt, Client folgt per S2C-Gamerule-Sync (fix für false-Toggle und Bewegungsnebenwirkungen) | Codex |
| 2026-03-13 | Bug 4 implementiert (Unbreaking-Ban im Enchanting Table für Titan-Items), Status auf REVIEW gesetzt | Codex |
| 2026-03-13 | Bug 4 nachgeschärft: leere/nicht klickbare Enchant-Angebote werden unterdrückt, wenn nach Unbreaking-Filter kein gültiger Enchant mehr übrig bleibt | Codex |
| 2026-03-13 | Bug 5 implementiert: Immunity-Blocked-Effect wird clientseitig synchronisiert und im Inventory-Effektfenster unter Immunity in Rot gerendert | Codex |
| 2026-03-13 | Bug 6 implementiert: Ember-Armor reduziert Lava-Schaden, Burn-Dauer und Fire/Lava/SoulFire/Magma-Damage-Tick-Chance je Piece um 25% | Codex |
| 2026-03-13 | Bug 7 implementiert: Backpack-Preview nutzt eigenes Tooltip-Rendering mit korrektem Fill (`occupied/total`) und ohne Stack-Zusammenführung | Codex |
| 2026-03-13 | Bug 8 implementiert: EMI filtert nicht-furnace-fähige Outputs aus SMELTING und ergänzt fehlende Multi-Bow-Smithing-Recipes | Codex |
