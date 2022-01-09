/*******************************************************************************
 * Copyright 2021, the Glitchfiend Team.
 * Creative Commons Attribution-NonCommercial-NoDerivatives 4.0.
 ******************************************************************************/
package terrablender.mixin;

import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.levelgen.*;
import net.minecraft.world.level.levelgen.blending.Blender;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import terrablender.worldgen.IExtendedNoiseBasedChunkGenerator;

import java.util.function.Supplier;

@Mixin(NoiseBasedChunkGenerator.class)
public class MixinNoiseBasedChunkGenerator implements IExtendedNoiseBasedChunkGenerator
{
    @Redirect(method = "iterateNoiseColumn", at=@At(value="INVOKE", target = "net/minecraft/world/level/levelgen/NoiseChunk.forColumn(IIIILnet/minecraft/world/level/levelgen/NoiseSampler;Lnet/minecraft/world/level/levelgen/NoiseGeneratorSettings;Lnet/minecraft/world/level/levelgen/Aquifer$FluidPicker;)Lnet/minecraft/world/level/levelgen/NoiseChunk;"))
    public NoiseChunk redirectForColumn(int x, int z, int cellNoiseMinY, int cellCountY, NoiseSampler sampler, NoiseGeneratorSettings noiseGenSettings, Aquifer.FluidPicker fluidPicker)
    {
        return this.forColumn(x, z, cellNoiseMinY, cellCountY, sampler, noiseGenSettings, fluidPicker);
    }

    @Redirect(method = {"doCreateBiomes", "buildSurface", "applyCarvers", "doFill"}, at=@At(value="INVOKE", target="net/minecraft/world/level/chunk/ChunkAccess.getOrCreateNoiseChunk(Lnet/minecraft/world/level/levelgen/NoiseSampler;Ljava/util/function/Supplier;Lnet/minecraft/world/level/levelgen/NoiseGeneratorSettings;Lnet/minecraft/world/level/levelgen/Aquifer$FluidPicker;Lnet/minecraft/world/level/levelgen/blending/Blender;)Lnet/minecraft/world/level/levelgen/NoiseChunk;"))
    public NoiseChunk redirectGetOrCreateNoiseChunk(ChunkAccess chunkAccess, NoiseSampler sampler, Supplier<NoiseChunk.NoiseFiller> noiseFiller, NoiseGeneratorSettings settings, Aquifer.FluidPicker fluidPicker, Blender blender)
    {
        return this.getOrCreateNoiseChunk(chunkAccess, sampler, noiseFiller, settings, fluidPicker, blender);
    }

    @Override
    public NoiseChunk forColumn(int x, int z, int cellNoiseMinY, int cellCountY, NoiseSampler sampler, NoiseGeneratorSettings noiseGenSettings, Aquifer.FluidPicker fluidPicker)
    {
        return NoiseChunk.forColumn(x, z, cellNoiseMinY, cellCountY, sampler, noiseGenSettings, fluidPicker);
    }

    @Override
    public NoiseChunk getOrCreateNoiseChunk(ChunkAccess chunkAccess, NoiseSampler sampler, Supplier<NoiseChunk.NoiseFiller> noiseFiller, NoiseGeneratorSettings settings, Aquifer.FluidPicker fluidPicker, Blender blender)
    {
        if (chunkAccess.noiseChunk == null)
            chunkAccess.noiseChunk = NoiseChunk.forChunk(chunkAccess, sampler, noiseFiller, settings, fluidPicker, blender);

        return chunkAccess.noiseChunk;
    }
}