package me.wasteofti.constrcutor.modules;

import me.wasteofti.constrcutor.Constructor;
import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.settings.*;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.item.Items;
import net.minecraft.network.packet.c2s.play.ClientCommandC2SPacket;
import net.minecraft.util.math.*;

public class ConstructorSpiral extends Module {
    public ConstructorSpiral() {
        super(Constructor.CATEGORY, "SpiralMover", "Goes in spirals.");
    }

    final SettingGroup sgDefault = settings.getDefaultGroup();

    private final Setting<Integer> coverageDistance = sgDefault.add(new IntSetting.Builder()
        .name("Coverage Distance (Chunks)")
        .defaultValue(7)
        .build()
    );

    private final Setting<Integer> startIteration = sgDefault.add(new IntSetting.Builder()
        .name("Start Iteration")
        .defaultValue(0)
        .build()
    );

    private final Setting<Boolean> saveIteration = sgDefault.add(new BoolSetting.Builder()
        .name("Save Iteration on Disable")
        .defaultValue(true)
        .build()
    );

    private final Setting<BlockPos> centerPos = sgDefault.add(new BlockPosSetting.Builder()
        .name("Spiral Center Position")
        .defaultValue(BlockPos.ORIGIN)
        .build()
    );

    int iteration;
    ChunkPos pointOfTurn;
    Direction[] horizontals = { Direction.NORTH, Direction.EAST, Direction.SOUTH, Direction.WEST };

    @EventHandler
    public void onActivate() {
        if (mc.world == null) return;

        iteration = 0;
        pointOfTurn = mc.world.getChunk(centerPos.get()).getPos();

        for (int i = 0; i < startIteration.get(); i++) {
            pointOfTurn = nextTurnPoint();
            iteration++;
        }

        info("Next chunk at %s", pointOfTurn.toString());
    }

    @EventHandler
    public void onDeactivate() {
        if (saveIteration.get()) startIteration.set(iteration);
    }

    @EventHandler
    private void onTick(TickEvent.Post event) {
        if (mc.player == null) return;

        rotateTo(
            mc.player.getEyePos(),
            pointOfTurn.getCenterAtY(0).toCenterPos()
        );

        ChunkPos currentPos = mc.player.getChunkPos();
        if (currentPos.x == pointOfTurn.x && currentPos.z == pointOfTurn.z) {
            info("Reached chunk at %s with iteration %d", currentPos.toString(), iteration);
            pointOfTurn = nextTurnPoint();
            iteration++;
            info("Next chunk at %s", pointOfTurn.toString());
        }
    }

    private ChunkPos nextTurnPoint() {
        int index = iteration % horizontals.length;
        Vec3i directionVec = horizontals[index].getVector();
        int nextDistance = (iteration + 1) * coverageDistance.get();
        int nextX = pointOfTurn.x + directionVec.getX() * nextDistance;
        int nextZ = pointOfTurn.z + directionVec.getZ() * nextDistance;
        return new ChunkPos(nextX, nextZ);
    }

    private void rotateTo(Vec3d eyes, Vec3d to) {
        if (mc.player == null) return;

        double diffX = to.x - eyes.x;
        double diffZ = to.z - eyes.z;
        double yawRad = Math.atan2(diffZ, diffX);

        mc.player.setYaw((float) MathHelper.wrapDegrees(toDegree(yawRad) - 90.0));
    }

    private double toDegree(double rad) {
        return rad * 180 / Math.PI;
    }
}
