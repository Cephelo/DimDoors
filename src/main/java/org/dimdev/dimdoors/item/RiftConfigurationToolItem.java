package org.dimdev.dimdoors.item;

import net.fabricmc.api.Environment;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.world.World;
import org.dimdev.dimdoors.ModConfig;

import java.util.List;

import static net.fabricmc.api.EnvType.*;

public class RiftConfigurationToolItem extends Item {
    RiftConfigurationToolItem() {
        super(new Item.Settings().group(ModItemGroups.DIMENSIONAL_DOORS).maxCount(1).maxDamage(16));
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity player, Hand hand) {
        ItemStack stack = player.getStackInHand(hand);
        HitResult hit = player.rayTrace(RayTraceHelper.REACH_DISTANCE, 0, false);

        if (world.isClient) {
            if (!RayTraceHelper.hitsRift(hit, world)) {
                player.sendMessage(new TranslatableText("tools.rift_miss"));
            }
            return new TypedActionResult<>(ActionResult.FAIL, stack);
        }

        if (RayTraceHelper.hitsRift(hit, world)) {
//            RiftBlockEntity rift = (RiftBlockEntity) world.getBlockEntity(new BlockPos(hit.getPos()));

            return new TypedActionResult<>(ActionResult.SUCCESS, stack);
        }
        return new TypedActionResult<>(ActionResult.FAIL, stack);
    }

    @Override
    @Environment(CLIENT)
    public void appendTooltip(ItemStack itemStack, World world, List<Text> list, TooltipContext tooltipContext) {
        if (I18n.hasTranslation(this.getTranslationKey() + ".info")) {
            list.add(new TranslatableText(this.getTranslationKey() + ".info"));
        }
    }
}
