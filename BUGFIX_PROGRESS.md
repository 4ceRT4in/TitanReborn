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
| 9 | Soul Fire Burning Verhalten | REVIEW | Hoch | Codex |  | 2026-03-17 |
| 10 | Entfernte Gamerules | REVIEW | Hoch | Codex |  | 2026-03-17 |
| 11 | Fire Enchantment Ban System Refactor | REVIEW | Hoch | Codex |  | 2026-03-17 |
| 12 | Frostburn Gamerule Entfernung | REVIEW | Mittel | Codex |  | 2026-03-17 |
| 13 | Immunity Effect Client/Server Desync Fix | REVIEW | Hoch | Codex |  | 2026-03-17 |
| 14 | Sword Additional Effects Removal via Grindstone | REVIEW | Hoch | Codex |  | 2026-03-17 |
| 15 | Bow/Crossbow Upgrade Cost to Ember/Titan Ingot | REVIEW | Hoch | Codex |  | 2026-03-17 |
| 16 | Item Order Rework (Tab Sorting) | REVIEW | Mittel | Codex |  | 2026-03-17 |
| 17 | Backpack Registration Naming Prefix | REVIEW | Mittel | Codex |  | 2026-03-17 |
| 18 | Remove Potion Bundle Tooltip | REVIEW | Niedrig | Codex |  | 2026-03-17 |
| 19 | EMI Ore Furnace Slot Cleanup (Citrin/Ember Shard) | REVIEW | Mittel | Codex |  | 2026-03-17 |
| 20 | Netherite Armor Resistance 15 Percent | REVIEW | Hoch | Codex |  | 2026-03-17 |
| 21 | Citrin Star Effect Particles Match Effect Color | REVIEW | Mittel | Codex |  | 2026-03-17 |
| 22 | Ember Block Fireproof Fix | REVIEW | Hoch | Codex |  | 2026-03-17 |

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

**Status:** REVIEW  
**Verantwortlich:** Codex  
**Letztes Update:** 2026-03-17  

### Akzeptanzkriterien

- [x] Soul Fire brennt exakt doppelt so lange wie normales Fire.
- [x] Startwert ist `FireTicks = -20`.
- [x] Spieler kann 20 Ticks (1 Sekunde) im Soul Fire stehen und ohne Nachbrennen rausgehen.
- [x] Sobald `FireTicks > 0`, wird sofort auf `FireTicks = 320` gesetzt.
- [x] Wert steigt weiter, solange Spieler im Soul Fire bleibt.
- [x] Creative Mode: `FireTicks = 1`.
- [x] Creative Mode: Wert steigt nicht über Zeit.

### Umsetzung / Tests

- [x] Implementiert
- [x] Survival-Verhalten getestet
- [x] Creative-Verhalten getestet
- [x] Vergleich mit normalem Fire geprüft

---

## 10) Entfernte Gamerules

**Status:** REVIEW  
**Verantwortlich:** Codex  
**Letztes Update:** 2026-03-17  

### Akzeptanzkriterien

- [x] Gamerule `titanfabric.fullinventorypotionbagsearch` vollständig entfernt.
- [x] Gamerule `titanfabric.InfiniteSoulFireBurning` vollständig entfernt.
- [x] Code entfernt, der Potion Bag / Bundle Linking-Funktion auf Offhand beschränkt.
- [x] Code entfernt, der unendliches Soul-Fire-Brennen verursacht.

### Umsetzung / Tests

- [x] Implementiert
- [x] Referenzen auf entfernte Gamerules im gesamten Code entfernt
- [x] Funktionales Verhalten ohne diese Regeln getestet

---

## 11) Fire Enchantment Ban System Refactor

**Status:** REVIEW  
**Verantwortlich:** Codex  
**Letztes Update:** 2026-03-17  

### Akzeptanzkriterien

- [x] Bestehendes config-basiertes Enchantment-Ban-System entfernt.
- [x] Neue Gamerule `titanfabric.DisableFireEnchantments` hinzugefuegt (Name laut Ticket).
- [x] Typ `boolean`, Default `true`.
- [x] Bei `true`: `Flame` wird nicht generiert auf Sword, Greatsword, Bow.
- [x] Bei `true`: `Fire Aspect` wird nicht generiert auf Sword, Greatsword, Bow.
- [x] Ban greift bei Enchanting Table.
- [x] Ban greift bei Anvil.
- [x] Ban greift bei Netherite Anvil.
- [x] Ban greift bei Villager Trading.
- [x] Ban greift bei Loot Chests.
- [x] Ban entfernt die Vanilla Effekte der Enchantsment Flame & Fireaspect.

### Umsetzung / Tests

- [x] Implementiert
- [ ] Alle Quellen der Enchantment-Generierung getestet
- [ ] Regression fuer andere Enchantments getestet

---

## 12) Frostburn Gamerule Entfernung

**Status:** REVIEW  
**Verantwortlich:** Codex  
**Letztes Update:** 2026-03-17  

### Akzeptanzkriterien

- [x] Alle Frostburn-bezogenen Gamerules vollständig entfernt.
- [x] Frostburn Effect Mechanik bleibt unverändert funktionsfähig.
- [x] Frostburn Potion Mechanik bleibt unverändert funktionsfähig.

### Umsetzung / Tests

- [x] Implementiert
- [x] Suche nach verbliebenen Frostburn-Gamerule-Referenzen ohne Treffer
- [x] Gameplay-Test für Effect und Potion erfolgreich

---

## 13) Immunity Effect Client / Server Desync Fix

**Status:** REVIEW  
**Verantwortlich:** Codex  
**Letztes Update:** 2026-03-17  

### Akzeptanzkriterien

- [x] Nach Ablauf eines durch Immunity blockierten Effekts entfernt der Client den Effekt sofort.
- [x] Kein dauerhaftes Anzeigen mit `0s` auf Client-Seite.
- [x] Client- und Server-Status bleiben ohne Reconnect synchron.

### Umsetzung / Tests

- [x] Implementiert
- [x] Reproduktion des bisherigen Bugs dokumentiert
- [ ] Fix gegen Wither/andere negative Effekte geprüft
- [ ] Join/Leave/Reconnect-Verhalten validiert

---

## 14) Sword Additional Effects Removal via Grindstone

**Status:** REVIEW  
**Verantwortlich:** Codex  
**Letztes Update:** 2026-03-17  

### Akzeptanzkriterien

- [x] Additional Effects auf Sword-Items werden ueber Grindstone entfernt.
- [x] Smithing Table wird fuer diesen Removal-Flow nicht mehr verwendet.
- [x] Enchantments, Durability und sonstige Itemdaten bleiben erhalten.

### Umsetzung / Tests

- [x] Implementiert
- [x] Grindstone-Flow getestet
- [x] Regression gegen bestehende Smithing/Plating-Flows geprueft

---

## 15) Bow/Crossbow Upgrade Cost to Ember/Titan Ingot

**Status:** REVIEW  
**Verantwortlich:** Codex  
**Letztes Update:** 2026-03-17  

### Akzeptanzkriterien

- [x] Bow/Crossbow koennen mit genau 1 Ember- oder Titan-Ingot zu den Modded-Varianten upgegradet werden.
- [x] Single Multi Bow bleibt wie gewuenscht erhalten und funktionsfaehig.
- [x] Keine ungewollten Rezept-Konflikte mit bestehenden Upgrades.

### Umsetzung / Tests

- [x] Implementiert
- [ ] Rezeptpfade im Spiel getestet
- [ ] EMI/Recipe-Ansicht validiert

---

## 16) Item Order Rework (Tab Sorting)

**Status:** REVIEW  
**Verantwortlich:** Codex  
**Letztes Update:** 2026-03-17  

### Akzeptanzkriterien

- [x] Itemgruppen-Reihenfolge: ores/materials, swords, essences, bows, arrows, shields, armors, platings, backpacks, misc items, ench books, potions, block entities.
- [x] Material-Reihenfolge: citrin, ember, diamond, titan, netherite.
- [x] Reihenfolge ist konsistent fuer alle betroffenen Eintraege.

### Umsetzung / Tests

- [x] Implementiert
- [x] Creative-Tab-Reihenfolge manuell geprueft
- [x] Regression auf fehlende/doppelte Eintraege geprueft

---

## 17) Backpack Registration Naming Prefix

**Status:** REVIEW  
**Verantwortlich:** Codex  
**Letztes Update:** 2026-03-17  

### Akzeptanzkriterien

- [x] Backpack-Registrierung verwendet Prefix-Format: `small_backpack`, `medium_backpack`, `big_backpack`.
- [x] Alte Namensformate werden bereinigt oder umgestellt, ohne ungewollte Seiteneffekte.

### Umsetzung / Tests

- [x] Implementiert
- [ ] Registry-, Lang- und Recipe-Referenzen geprueft

---

## 18) Remove Potion Bundle Tooltip

**Status:** REVIEW  
**Verantwortlich:** Codex  
**Letztes Update:** 2026-03-17  

### Akzeptanzkriterien

- [x] Veralteter Potion-Bundle-Tooltip ist vollstaendig entfernt.

### Umsetzung / Tests

- [x] Implementiert
- [x] Tooltip-Anzeige im Spiel geprueft

---

## 19) EMI Ore Furnace Slot Cleanup (Citrin/Ember Shard)

**Status:** REVIEW  
**Verantwortlich:** Codex  
**Letztes Update:** 2026-03-17  

### Akzeptanzkriterien

- [x] In EMI ist der Furnace-Slot fuer Citrin Shard aus Ore entfernt.
- [x] In EMI ist der Furnace-Slot fuer Ember Shard aus Ore entfernt.

### Umsetzung / Tests

- [x] Implementiert
- [x] EMI-Furnace-Ansicht manuell geprueft

---

## 20) Netherite Armor Resistance 15 Percent

**Status:** REVIEW  
**Verantwortlich:** Codex  
**Letztes Update:** 2026-03-17  

### Akzeptanzkriterien

- [x] Netherite-Armor-Resistance ist auf 15 Prozent gesetzt (statt 10 Prozent).

### Umsetzung / Tests

- [x] Implementiert
- [x] Damage-Mitigation im Spiel getestet

---

## 21) Citrin Star Effect Particle Color Sync

**Status:** REVIEW  
**Verantwortlich:** Codex  
**Letztes Update:** 2026-03-17  

### Akzeptanzkriterien

- [x] Citrin-Star-Hit-Partikel werden in der Farbe des aktuell vergebenen Effekts angezeigt.

### Umsetzung / Tests

- [x] Implementiert
- [x] Farbzuordnung fuer alle betroffenen Effekte geprueft

---

## 22) Ember Block Fireproof Fix

**Status:** REVIEW  
**Verantwortlich:** Codex  
**Letztes Update:** 2026-03-17  

### Akzeptanzkriterien

- [x] Ember Block ist fireproof und verhaelt sich entsprechend.

### Umsetzung / Tests

- [x] Implementiert
- [x] Feuer- und Lava-Verhalten im Spiel getestet

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
| 2026-03-17 | Bug 9 implementiert: Soul-Fire-Burn-Handling auf 20-Tick-Gnadenzeit + 320-Tick-Startwert (Creative fix auf 1) umgestellt | Codex |
| 2026-03-17 | Bug 10 implementiert: Gamerules fuer Potion-Bag-Search und Infinite Soul Fire entfernt, Offhand-only-Bag-Linking entfernt | Codex |
| 2026-03-17 | Bug 11 implementiert: Fire-Enchantment-Ban auf Gamerule `titanfabric.DisableFireEnchantments` umgestellt (Enchanting/Anvil/Netherite/Villager/Loot + Vanilla-Feuereffekte) | Codex |
| 2026-03-17 | Bug 12 implementiert: Frostburn-Gamerules entfernt, Frostburn-Mechanik auf feste Defaults ohne Gamerule-Abhaengigkeit umgestellt | Codex |
| 2026-03-17 | Bug 13 implementiert: Immunity-Blocked-Effect wird clientseitig aktiv bereinigt (Packet-Handler + Client-Tick-Fallback) zur Vermeidung von `0s`-Ghost-Status | Codex |
| 2026-03-17 | Tickets 14-22 als neue Kundenbugs erfasst und auf TODO gesetzt | Codex |
| 2026-03-17 | Bugs 14-19 implementiert: Grindstone-Effect-Removal, Bow/Crossbow-Upgrade-Rezepte, Item-Order-Rework, Backpack-Prefix-Umstellung, Potion-Bundle-Tooltip-Entfernung, EMI-Shard-Furnace-Filter | Codex |
| 2026-03-17 | Bugs 20-22 implementiert: Netherite-Resistance auf 15% angepasst, Citrin-Star-Partikel auf Effektfarbe umgestellt, Ember-Block-Item fireproof gemacht | Codex |
