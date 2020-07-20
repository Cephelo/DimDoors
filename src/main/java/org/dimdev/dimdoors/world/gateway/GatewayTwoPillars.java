package org.dimdev.dimdoors.world.gateway;

import net.minecraft.block.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class GatewayTwoPillars extends BaseSchematicGateway {
    private static final int GATEWAY_RADIUS = 4;

    public GatewayTwoPillars() {
        super("two_pillars");
    }

    @Override
    protected void generateRandomBits(World world, int x, int y, int z) {
        //Replace some of the ground around the gateway with bricks
        for (int xc = -GATEWAY_RADIUS; xc <= GATEWAY_RADIUS; xc++) {
            for (int zc = -GATEWAY_RADIUS; zc <= GATEWAY_RADIUS; zc++) {
                //Check that the block is supported by an opaque block.
                //This prevents us from building over a cliff, on the peak of a mountain,
                //or the surface of the ocean or a frozen lake.
                if (world.getBlockState(new BlockPos(x + xc, y - 1, z + zc)).getMaterial().isSolid()) {
                    //Randomly choose whether to place bricks or not. The math is designed so that the
                    //chances of placing a block decrease as we get farther from the gateway's center.
                    if (Math.abs(xc) + Math.abs(zc) < world.random.nextInt(2) + 3) {
                        //Place Stone Bricks
                        world.setBlockState(new BlockPos(x + xc, y, z + zc), Blocks.STONE_BRICKS.getDefaultState());
                    } else if (Math.abs(xc) + Math.abs(zc) < world.random.nextInt(3) + 3) {
                        //Place Cracked Stone Bricks
                        world.setBlockState(new BlockPos(x + xc, y, z + zc), Blocks.CRACKED_STONE_BRICKS.getDefaultState());
                    }
                }
            }
        }
    }
}
