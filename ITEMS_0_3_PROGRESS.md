# Titan Reborn 0.3 Progress

Stand: 2026-03-26
Status: REVIEW
Version-Ziel: 0.3

## Status-Legende
- TODO: Noch nicht begonnen
- IN_PROGRESS: Aktive Umsetzung
- REVIEW: Implementiert, technische Validierung erledigt, Gameplay-Test noch offen
- DONE: Implementiert und gameplay-seitig bestaetigt
- BLOCKED: Externe Rueckfrage oder Blocker offen

## Ticket-Uebersicht
| ID | Ticket | Status | Prioritaet | Kurzbeschreibung |
| --- | --- | --- | --- | --- |
| 32 | Enchanted Diamond Apple Item Core | REVIEW | Hoch | Neues 3-Use Food-Item mit eigener Consume-Logik, Durability-Bar und Crafting |
| 33 | Diamond Absorption Effect + HUD | REVIEW | Hoch | Neuer custom Absorption-Typ mit blauen Herzen, Conversion zu gelber Absorption und Client-Sync |
| 34 | Diamond Apple Rework | REVIEW | Mittel | Diamond Apple auf Diamond Absorption I umstellen |
| 35 | Recovery Potion System | REVIEW | Hoch | Neuer Recovery-Effect mit Buffer, Threshold-Healing, Refill, Brewing und Splash/Lingering-Kompatibilitaet |
| 36 | Recovery Potion Client Visuals | REVIEW | Mittel | Mob-Effect-Icon, optionale Buffer-UI, Tooltip-/Particle-Polish |
| 37 | Assets / Lang / Datagen / Validation | REVIEW | Hoch | Modelle, Texturen, Uebersetzungen, Item Group, Datagen, Compile-/Datagen-Checks |

## Zieldefinition
Die Modversion 0.3 fuehrt zwei neue Spielsysteme ein:
- `Enchanted Diamond Apple` als dreifach verwendbares Upgrade des `Diamond Apple`
- `Recovery Potion` als neuer Healing-Buffer-Effekt mit automatischer Heilung unter einem Health-Threshold

Die Umsetzung muss server- und clientseitig konsistent funktionieren, inklusive:
- Persistenz bei Save/Load
- Sync fuer HUD und Client-Anzeige
- korrekte Dauer-/Amplifier-Logik
- korrektes Verhalten fuer Drink-, Splash- und Lingering-Potions
- saubere Registrierungen fuer Items, Effects, Potions, Brewing und Creative Tab

## Tickets

### 32) Enchanted Diamond Apple Item Core
Status: REVIEW
Prioritaet: Hoch

Umfang:
- Neues Item `enchanted_diamond_apple`
- Max Stack Size `1`
- `3` Nutzungen ueber Durability-Bar
- Jeder Consume verbraucht genau `1` Use
- Nach der dritten Nutzung verschwindet das Item
- Laengere Eat-Animation als normales Food
- Rezept mit `1x Diamond Apple` im Zentrum und `4x Diamond Block` auf den vier kardinalen Slots

Akzeptanzkriterien:
- Item ist craftbar und im Creative Tab verfuegbar
- Erstes, zweites und drittes Essen funktionieren auf demselben Stack
- Nach der dritten Nutzung verschwindet das Item sauber
- Durability-Bar zeigt verbleibende Nutzungen an
- Consume funktioniert fuer Survival und Creative korrekt

Tests:
- [ ] Crafting Table Rezept pruefen
- [ ] Drei Consumptions auf demselben Stack pruefen
- [ ] Survival / Creative Verhalten pruefen
- [ ] Tooltip / Glint / Modell pruefen

### 33) Diamond Absorption Effect + HUD
Status: REVIEW
Prioritaet: Hoch

Umfang:
- Neuer Status Effect `diamond_absorption`
- Level I = `8 HP`, II = `12 HP`, III = `16 HP`
- Diamond-Herzen werden blau im HUD gerendert
- Wenn Diamond-Absorption Schaden absorbiert, sollen verbleibende Herzen zu normaler gelber Absorption werden
- Wenn der Effekt auslaeuft, soll verbleibende Absorption als normale gelbe Absorption bestehen bleiben
- Sync fuer Client und Persistenz bei Save/Load

Akzeptanzkriterien:
- HUD zeigt blaue Diamond-Herzen solange der Diamond-Absorption-State aktiv ist
- Schaden auf Diamond-Herzen fuehrt zu gelber Rest-Absorption statt Verlust der Anzeige-Integritaet
- Nach Ablauf des Effekts bleiben Rest-Herzen als normale Absorption erhalten
- Reapply / Upgrade von Level I -> II -> III verhaelt sich stabil

Tests:
- [ ] Level I/II/III einzeln pruefen
- [ ] Schaden auf aktive Diamond-Absorption pruefen
- [ ] Ablauf mit verbleibender Absorption pruefen
- [ ] Save/Load oder Rejoin pruefen

### 34) Diamond Apple Rework
Status: REVIEW
Prioritaet: Mittel

Umfang:
- `diamond_apple` entfernt Vanilla-Absorption und nutzt stattdessen `Diamond Absorption I`

Akzeptanzkriterien:
- Diamond Apple gibt exakt `Diamond Absorption I`
- Keine normale gelbe Absorption direkt beim Consume

Tests:
- [ ] Consume pruefen
- [ ] HUD-Farbe pruefen

### 35) Recovery Potion System
Status: REVIEW
Prioritaet: Hoch

Umfang:
- Neuer Status Effect `recovery`
- Basis-, Strong- und Long-Variante mit jeweils eigener Buffer-, Threshold- und Heal-Speed-Logik
- Buffer startet voll, refillt tickbasiert und heilt nur unter Threshold
- Heilung capped korrekt an aktueller/maximaler Gesundheit
- Brewing-Rezepte fuer Basis/Strong/Long
- Verhalten fuer normale, Splash- und Lingering-Potions

Akzeptanzkriterien:
- Base: `45s`, `6 HP Buffer`, `1 HP / 5s Refill`, `1 HP / 0.5s Heal`, Trigger bei `6 Herzen`
- Strong: `45s`, `12 HP Buffer`, `1 HP / 5s Refill`, `1 HP / 0.5s Heal`, Trigger bei `3 Herzen`
- Long: `90s`, `6 HP Buffer`, `1 HP / 5s Refill`, `1 HP / 1s Heal`, Trigger bei `6 Herzen`
- Buffer heilt nie ueber max HP
- Buffer heilt nur solange Threshold unterschritten ist und noch Reserve vorhanden ist

Tests:
- [ ] Base Potion trinken und Trigger pruefen
- [ ] Strong Potion trinken und Low-HP Trigger pruefen
- [ ] Long Potion trinken und langsamere Heal-Ticks pruefen
- [ ] Splash-/Lingering-Anwendung pruefen
- [ ] Refill nach leerem Buffer pruefen

### 36) Recovery Potion Client Visuals
Status: REVIEW
Prioritaet: Mittel

Umfang:
- Custom Mob-Effect-Icon
- Tooltip-/Potion-Text fuer Buffer und Threshold
- Optionale kleine Buffer-Anzeige nahe der Healthbar
- Leichte Healing-Particles waehrend Auto-Heal

Akzeptanzkriterien:
- Recovery Effekt ist im Inventar-/HUD-Overlay eindeutig erkennbar
- Buffer-UI ist clientseitig stabil und stoert Vanilla-HUD nicht
- Partikel sind sichtbar, aber nicht ueberladen

Tests:
- [ ] Inventar-/HUD-Icon pruefen
- [ ] Buffer-Anzeige pruefen
- [ ] Particle-Intensitaet ingame pruefen

### 37) Assets / Lang / Datagen / Validation
Status: REVIEW
Prioritaet: Hoch

Umfang:
- Item-/Potion-Modelle und Texturen
- Lang-Eintraege
- Item Group Eintraege
- Datagen fuer neue Rezepte
- Build- und Datagen-Validierung

Akzeptanzkriterien:
- Keine fehlenden Registry-/Asset-Fehler im Startlog
- `classes` kompiliert
- `runDatagen` generiert neue Daten ohne Fehler

Tests:
- [ ] Gradle classes
- [ ] Gradle runDatagen
- [ ] Startlog auf Missing Texture / Missing Model pruefen

## Aenderungsprotokoll
- 2026-03-26: Progress-Datei fuer Version 0.3 angelegt und Umsetzung gestartet.
- 2026-03-26: Enchanted Diamond Apple, Diamond Absorption, Diamond Apple Rework, Recovery Potion System, Client-Visuals und Datagen technisch umgesetzt und auf REVIEW gesetzt.
- 2026-03-26: Diamond-Absorption-Fix nachgezogen: max-absorption Clamp serverseitig korrigiert, gelbe Rest-Absorption nach Damage/Expiry/Rejoin abgesichert.
- 2026-03-26: Kunden-Texturen fuer Diamond Absorption und Recovery uebernommen; Recovery-Half-Heart-Reihenfolge und warmes Orange/Rot-Farbsetup nachgezogen.
- 2026-03-26: Diamond-Absorption-Damage-Conversion praezisiert und Regeneration fuer den normalen Diamond Apple wiederhergestellt.
- 2026-03-26: Recovery-Buffer-UI auf Vanilla-Herzen mit 1x3/2x3-Slotlayout rechts neben der Healthbar umgestellt; Enchanted Diamond Apple auf normale Diamond-Apple-Textur mit Glint umgestellt.
- 2026-03-26: Diamond-Absorption-Effekt entfernt sich jetzt sofort, sobald keine blauen Diamond-Herzen mehr uebrig sind.
- 2026-03-26: Diamond-/Enchanted-Diamond-Apple-Balancing nach Kundenentscheid angepasst: Diamond Apple auf 8 Diamanten, Enchanted auf 6/8/10 Diamond-Herzen und Diamond-Block-Reparatur ueber Anvil, Crafting Table und Grindstone.
- 2026-03-26: `recovery2.png` aus dem Kundenordner entfernt; `strong_recovery` nutzt jetzt wieder bewusst dieselbe Basis-Textur wie `recovery`.
- 2026-03-26: Enchanted-Diamond-Apple-Reparatur wieder auf Crafting Table reduziert; Anvil/Grindstone-Pfade entfernt und EMI-Crafting-Anzeige fuer den Repair-Flow ergaenzt.


## Implementierungsnotizen 2026-03-26
- `32`: `enchanted_diamond_apple` als eigenes 3-Use-Item mit Durability-Bar, laengerer Eat-Zeit (`2.4s`), Glint, Tooltip fuer Rest-Uses und Shaped-Rezept umgesetzt.
- `33`: `diamond_absorption` als eigener Status Effect plus synchronisierte Entity-Component umgesetzt. Blaue Herzen werden clientseitig ueber den Vanilla-Absorptionshearts gerendert.
- `33`: Verhalten fuer Damage/Expiry ist so umgesetzt, dass verbleibende Diamond-Absorption beim ersten Absorptionsschaden oder beim Effekt-Ende in normale gelbe Absorption uebergeht.
- `33`: Fehlerursache war das Vanilla-`GENERIC_MAX_ABSORPTION`-Clamp. Der Fix setzt das Cap jetzt explizit fuer Diamond Absorption und haelt Rest-Absorption nach Damage, Effekt-Ende und Rejoin serverseitig stabil.
- `33`: Damage auf Diamond Absorption konvertiert jetzt nur noch den wirklich getroffenen Blue-Absorption-Anteil zu gelber Absorption statt pauschal den kompletten Rest.
- `33`: Wenn der letzte Diamond-Absorption-Anteil verbraucht wurde, wird der Status Effect sofort entfernt; gelbe Rest-Absorption bleibt erhalten.
- `34`: `diamond_apple` nutzt jetzt `Diamond Absorption I` statt Vanilla-Absorption.
- `34`: Diamond Apple gibt wieder Regeneration beim Consume.
- `35`: Recovery-System ueber separate Recovery-Effects (`base`, `strong`, `long`) plus synchronisierte Buffer-Component umgesetzt. Buffer startet voll, refillt tickbasiert und heilt threshold-gesteuert mit pro Aktivierung gecapptem Budget.
- `35`: Brewing umgesetzt: `Awkward + Golden Apple -> Recovery`, `+ Glowstone -> Strong Recovery`, `+ Redstone -> Long Recovery`.
- `36`: Custom Potion-Models, Mob-Effect-Icons, Recovery-Tooltip und optionale Buffer-Heart-UI neben der Healthbar umgesetzt. Healing-Particles laufen serverseitig sparsam und farblich passend zum Recovery-Profil.
- `36`: Kunden-Texturen aus `titanreborn_update_textures` fuer Diamond Absorption und Recovery uebernommen. Recovery nutzt jetzt warmere Orange-/Rot-Toene fuer Effect-Farbe und Heal-Partikel; das Buffer-Half-Heart wurde auf Vanilla-Lesereihenfolge korrigiert.
- `36`: Recovery-Buffer rendert jetzt Vanilla-rote Herzen inklusive leerer Buffer-Slots im 1x3- bzw. 2x3-Layout direkt rechts neben der normalen Healthbar.
- `32`: Enchanted Diamond Apple nutzt jetzt bewusst dieselbe Basistextur wie der normale Diamond Apple; der enchanted Look kommt nur noch ueber den Glint.
- `32`: Diamond-Apple-Balancing nachgezogen: `diamond_apple` Rezept kostet jetzt 8 Diamanten; `enchanted_diamond_apple` nutzt jetzt 6/8/10 Diamond-Herzen ueber die drei Uses.
- `32`: `enchanted_diamond_apple` laesst sich mit `Diamond Block` um genau 1 Use reparieren. Der Pfad ist fuer Anvil, Crafting Table und Grindstone separat abgesichert.
- `32`: Repair-Umfang wieder reduziert: `enchanted_diamond_apple` repariert jetzt nur noch ueber das Special-Crafting-Rezept; EMI zeigt dafuer explizit zwei Repair-Stufen an.
- `37`: `classes` erfolgreich mit Java 21 validiert.
- `37`: `runDatagen` erfolgreich ausgefuehrt; Rezept `src/main/generated/data/titanfabric/recipe/enchanted_diamond_apple.json` erzeugt.

