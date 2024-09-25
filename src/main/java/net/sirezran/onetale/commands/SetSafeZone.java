package net.sirezran.onetale.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

public class SetSafeZone {

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("setSafeZone")
            .then(Commands.argument("radius", IntegerArgumentType.integer(10))
                .then(Commands.argument("start y level", IntegerArgumentType.integer(-60,300))
                    .then(Commands.argument("points", IntegerArgumentType.integer(3,50))
                        .executes(context -> {

                            int radius = IntegerArgumentType.getInteger(context, "radius");
                            int startYLevel = IntegerArgumentType.getInteger(context, "start y level");
                            int points = IntegerArgumentType.getInteger(context, "points");
                            ServerPlayer player = context.getSource().getPlayerOrException();
                            ServerLevel world = (ServerLevel) player.getCommandSenderWorld(); // Get the world the player is in

                            double playerX = player.getX();
                            double playerZ = player.getZ();

                            // Calculate and set blocks at 5 points around the circle
                            for (int i = 0; i < points; i++) {
                                double angle = Math.toRadians((int)(360 / points) * i);
                                double x = playerX + radius * Math.cos(angle);
                                double z = playerZ + radius * Math.sin(angle);


                                // starts at given Y level and drops to a non air block
                                int currentY = startYLevel;
                                BlockPos pos = new BlockPos((int)x, currentY, (int)z);
                                BlockState block = world.getBlockState(pos);


                                while (
                                        block.isAir() ||
                                        block.getBlock() == Blocks.WATER ||
                                        block.getBlock() == Blocks.KELP ||
                                        block.getBlock() == Blocks.SEAGRASS ||
                                        block.getBlock() == Blocks.GRASS ||
                                        block.getBlock() == Blocks.TALL_GRASS ||
                                        block.is(BlockTags.LOGS) ||
                                        block.is(BlockTags.LEAVES) ||
                                        block.is(BlockTags.FLOWERS)
                                        ) {

                                    currentY -= 1;
                                    pos = new BlockPos((int)x, currentY, (int)z);
                                    block = world.getBlockState(pos);
                                }
                                //once ground is found, ensure that the entire beacon base is in the ground
                                currentY -= 5;


                                // clear 3x3x15 area above the beacons
                                for (int yClear = currentY+1; yClear <= currentY+15; yClear++) {
                                    for (int dx = -1; dx <= 1; dx++) {
                                        for (int dz = -1; dz <= 1; dz++) {
                                            pos = new BlockPos((int) x + dx, yClear, (int) z + dz);
                                            world.setBlockAndUpdate(pos, Blocks.AIR.defaultBlockState());
                                        }
                                    }
                                }
                                // Places the 3x3 beacon base
                                for (int dx = -1; dx <= 1; dx++) {
                                    for (int dz = -1; dz <= 1; dz++) {
                                        pos = new BlockPos((int)x + dx, currentY, (int)z + dz);
                                        world.setBlockAndUpdate(pos, Blocks.BEACON.defaultBlockState());
                                    }
                                }
                                // Places the 4x4 iron block base
                                for (int dx = -2; dx <= 2; dx++) {
                                    for (int dz = -2; dz <= 2; dz++) {
                                        pos = new BlockPos((int)x + dx, currentY - 1, (int)z + dz);
                                        world.setBlockAndUpdate(pos, Blocks.IRON_BLOCK.defaultBlockState());
                                    }
                                }
                            }
                            return 1; // Success
                        })))));
    }
}