package net.shirojr.titanfabric.item;

import net.fabricmc.fabric.api.client.itemgroup.FabricItemGroupBuilder;
import net.minecraft.block.Blocks;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.Identifier;
import net.shirojr.titanfabric.TitanFabric;
import net.shirojr.titanfabric.color.TitanFabricColorProviders;
import net.shirojr.titanfabric.util.ModelPredicateProviders;

public class TitanFabricItemGroups {

    public static final ItemGroup TITAN = FabricItemGroupBuilder.build(new Identifier(TitanFabric.MODID,"titan"),
            () -> new ItemStack(TitanFabricItems.LEGEND_CRYSTAL));
   /* public static final ItemGroup BACKPACK = FabricItemGroupBuilder.create(
                    new Identifier(TitanFabric.MODID, "backpack"))
            .icon(() -> new ItemStack(TitanFabricItems.BACKPACK_BIG))
            .appendItems(stacks -> {
                for (String color : TitanFabricColorProviders.COLOR_KEYS) {
                    ItemStack stack = new ItemStack(TitanFabricItems.BACKPACK_BIG);
                    ItemStack stack2 = new ItemStack(TitanFabricItems.BACKPACK_MEDIUM);
                    ItemStack stack3 = new ItemStack(TitanFabricItems.BACKPACK_SMALL);
                    NbtCompound nbt = new NbtCompound();
                    nbt.putBoolean(color, true);
                    stack.setNbt(nbt);
                    stack2.setNbt(nbt);
                    stack3.setNbt(nbt);
                    stacks.add(stack);
                    stacks.add(stack2);
                    stacks.add(stack3);
                }
            })
            .build();
    public static final ItemGroup PARACHUTE = FabricItemGroupBuilder.create(
                    new Identifier(TitanFabric.MODID, "parachute"))
            .icon(() -> new ItemStack(TitanFabricItems.PARACHUTE))
            .appendItems(stacks -> {
                for (String color : TitanFabricColorProviders.COLOR_KEYS) {
                    ItemStack stack = new ItemStack(TitanFabricItems.PARACHUTE);
                    NbtCompound nbt = new NbtCompound();
                    nbt.putBoolean(color, true);
                    stack.setNbt(nbt);
                    stacks.add(stack);
                }
            })
            .build();

    */
}
