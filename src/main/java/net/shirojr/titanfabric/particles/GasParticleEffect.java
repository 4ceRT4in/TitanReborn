package net.shirojr.titanfabric.particles;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleType;
import net.minecraft.util.registry.Registry;
import net.shirojr.titanfabric.TitanFabric;

import java.util.Locale;

public class GasParticleEffect implements ParticleEffect {
    public static final ParticleEffect.Factory<GasParticleEffect> PARAMETERS_FACTORY = new ParticleEffect.Factory<GasParticleEffect>() {
        @Override
        public GasParticleEffect read(ParticleType<GasParticleEffect> type, StringReader reader) throws CommandSyntaxException {
            reader.expect(' ');
            float red = reader.readFloat();
            reader.expect(' ');
            float green = reader.readFloat();
            reader.expect(' ');
            float blue = reader.readFloat();
            reader.expect(' ');
            float scale = reader.readFloat();
            reader.expect(' ');
            float alpha = reader.readFloat();
            return new GasParticleEffect(red, green, blue, scale, alpha);
        }

        @Override
        public GasParticleEffect read(ParticleType<GasParticleEffect> type, PacketByteBuf buf) {
            return new GasParticleEffect(buf.readFloat(), buf.readFloat(), buf.readFloat(), buf.readFloat(), buf.readFloat());
        }
    };

    private final float red;
    private final float green;
    private final float blue;
    private final float scale;
    private final float alpha;

    public GasParticleEffect(float red, float green, float blue, float scale, float alpha) {
        this.red = red;
        this.green = green;
        this.blue = blue;
        this.scale = scale;
        this.alpha = alpha;
    }

    @Override
    public ParticleType<?> getType() {
        return TitanFabric.GAS_PARTICLE;
    }

    public float getRed() { return red; }
    public float getGreen() { return green; }
    public float getBlue() { return blue; }
    public float getScale() { return scale; }

    public float getAlpha() {
        return alpha;
    }

    @Override
    public void write(PacketByteBuf buf) {
        buf.writeFloat(red);
        buf.writeFloat(green);
        buf.writeFloat(blue);
        buf.writeFloat(scale);
        buf.writeFloat(alpha);
    }

    @Override
    public String asString() {
        return String.format(Locale.ROOT, "%s %.2f %.2f %.2f %.2f %.2f", Registry.PARTICLE_TYPE.getId(this.getType()), red, green, blue, scale, alpha);
    }
}

