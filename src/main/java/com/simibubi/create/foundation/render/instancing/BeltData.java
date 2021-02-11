package com.simibubi.create.foundation.render.instancing;

import com.simibubi.create.foundation.block.render.SpriteShiftEntry;
import com.simibubi.create.foundation.render.gl.GlPrimitiveType;
import com.simibubi.create.foundation.render.gl.attrib.CommonAttributes;
import com.simibubi.create.foundation.render.gl.attrib.VertexAttribSpec;
import com.simibubi.create.foundation.render.gl.attrib.VertexFormat;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;

import java.nio.ByteBuffer;

import static com.simibubi.create.foundation.render.gl.attrib.VertexAttribSpec.*;

public class BeltData extends KineticData<BeltData> {
    public static final VertexAttribSpec TARGET_UV = copy(CommonAttributes.VEC4);
    public static final VertexAttribSpec SCROLL_MULT = new VertexAttribSpec(GlPrimitiveType.BYTE, 1, true);

    public static VertexFormat FORMAT = new VertexFormat(KineticData.FORMAT, CommonAttributes.ROTATION, CommonAttributes.UV, TARGET_UV, SCROLL_MULT);

    private float rotX;
    private float rotY;
    private float rotZ;
    private float sourceU;
    private float sourceV;
    private float minU;
    private float minV;
    private float maxU;
    private float maxV;
    private byte scrollMult;

    public BeltData setRotation(float rotX, float rotY, float rotZ) {
        this.rotX = rotX;
        this.rotY = rotY;
        this.rotZ = rotZ;
        return this;
    }


    public BeltData setScrollTexture(SpriteShiftEntry spriteShift) {
        TextureAtlasSprite source = spriteShift.getOriginal();
        TextureAtlasSprite target = spriteShift.getTarget();

        this.sourceU = source.getMinU();
        this.sourceV = source.getMinV();
        this.minU = target.getMinU();
        this.minV = target.getMinV();
        this.maxU = target.getMaxU();
        this.maxV = target.getMaxV();

        return this;
    }

    public BeltData setScrollMult(float scrollMult) {
        this.scrollMult = (byte) (scrollMult * 127);
        return this;
    }

    @Override
    public void write(ByteBuffer buf) {
        super.write(buf);

        putVec3(buf, rotX, rotY, rotZ);

        putVec2(buf, sourceU, sourceV);
        putVec4(buf, minU, minV, maxU, maxV);

        put(buf, scrollMult);
    }
}
