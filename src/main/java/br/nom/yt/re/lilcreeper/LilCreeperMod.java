package br.nom.yt.re.lilcreeper;

import br.nom.yt.re.lilcreeper.entity.LilCreeper;
import br.nom.yt.re.lilcreeper.entity.LilCreeperRenderer;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.client.rendereregistry.v1.EntityRendererRegistry;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricEntityTypeBuilder;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.SpawnEggItem;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class LilCreeperMod implements ModInitializer {
	public static final EntityType<LilCreeper> LIL_CREEPER = Registry.register(
			Registry.ENTITY_TYPE,
			new Identifier("lilcreeper", "lil_creeper"),
			FabricEntityTypeBuilder
					.create(SpawnGroup.MONSTER, LilCreeper::new)
					.dimensions(EntityDimensions.fixed(0.6F, 1.7F - 0.6f))
					.trackRangeBlocks(8).build());
	public static final Item LIL_CREEPER_SPAWN_EGG =
			new SpawnEggItem(LIL_CREEPER, 0xbfff00, 0x00000, new FabricItemSettings().group(ItemGroup.MISC));

	@Override
	public void onInitialize() {
		FabricDefaultAttributeRegistry.register(LIL_CREEPER, LilCreeper.createLilCreeperAttributes());
		EntityRendererRegistry.INSTANCE.register(LIL_CREEPER, (dispatcher, context) -> new LilCreeperRenderer(dispatcher));
		Registry.register(Registry.ITEM, new Identifier("lilcreeper", "spawn_lil_creeper"), LIL_CREEPER_SPAWN_EGG);
	}
}
