# TitanReborn

Fabric 1.16.5 Port of the Forgemod Titan

This mod uses the [Fabric Shield Lib](https://modrinth.com/mod/fabricshieldlib/version/1.7-1.18.2).
It has been included within the jar so no extra download has to be made.



---

# TODO:

### Custom Bows

- [ ] MultiBow
- [ ] LegendBow
- [ ] LegendArrows (Effect arrows?)
- [ ] FinalLegendBow
- [ ] EffectArrows?
- [ ] CitrinStarItem (+ Entity)

### Custom Shields

- DiaShield
- LegendShield

### effect armor

- [ ] CitrinArmor
- [ ] ExtendedArmor
- [ ] LegendArmor
- [ ] LegendBoots
- [ ] LegendChestplate
- [ ] LegendHelmet
- [ ] LegendLeggings
- [ ] NetherArmor?

### effect essence Items

- [ ] BlindnessEssence
- [ ] FireEssence
- [ ] PoisonEssence
- [ ] WeaknessEssence
- [ ] WitherEssence

### effect sword

- [ ] CitrinSword (+ effect versions) -> extend CitrinSwordItem
- [ ] ?Vanilla Dia? (+ effect versions) -> extend SwordItem
- [ ] LegendSword (+ effect versions) -> extend LegendSwordItem
- [ ] NetherSword (+ effect versions) -> extend NetherSwordItem

### effect entities

- [ ] BlindnessArrow
- [ ] PoisonArrow
- [ ] WeaknessArrow
- [ ] WitherArrow

### events

- GreatSwordFatigue -> GreatSwordItem interaction applies new EffectInstance(Effects.MINING_FATIGUE, 20, 2, false, false)
- HungerEventListener (warum überhaupt???)
- FullNetherite 
  - -> new EffectInstance(Effects.RESISTANCE, 40, 0, false, false, false)
  - -> cancel fire dmg 
- golden apple cancel effects then -> new EffectInstance(Effects.ABSORPTION, remainingDuration, 1) so just change intensity?
- CitrinStar thow -> rocket sound effect
- Boost AttributeModifiers when wearing Titan armor

### gui

### itemgroup

- LegendModItemGroup (icon -> Custom item: LEGEND_CRYSTAL)

### ores

- [ ] CitrinOre
- [ ] LegendOre
- [ ] NetherOre

### lingering potion brewing recipe
- extends BrewingRecipe
- check if input is splashpotion
- 

### blockentity

