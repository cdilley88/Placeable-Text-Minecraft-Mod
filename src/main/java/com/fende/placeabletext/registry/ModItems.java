package com.fende.placeabletext.registry;

import com.fende.placeabletext.PlaceableTextMod;
import com.fende.placeabletext.item.PlacerItem;
import net.minecraft.world.item.Item;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class ModItems {
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(PlaceableTextMod.MOD_ID);
    public static final DeferredItem<PlacerItem> LETTER_PLACER = ITEMS.registerItem(
            "letter_placer", p -> new PlacerItem(PlacerItem.Category.LETTER, p), new Item.Properties().stacksTo(1));
    public static final DeferredItem<PlacerItem> NUMBER_PLACER = ITEMS.registerItem(
            "number_placer", p -> new PlacerItem(PlacerItem.Category.NUMBER, p), new Item.Properties().stacksTo(1));
    public static final DeferredItem<PlacerItem> SPECIAL_PLACER = ITEMS.registerItem(
            "special_placer", p -> new PlacerItem(PlacerItem.Category.SPECIAL, p), new Item.Properties().stacksTo(1));
    private ModItems() {}
}

