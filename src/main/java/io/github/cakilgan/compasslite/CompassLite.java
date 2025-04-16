package io.github.cakilgan.compasslite;

import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.client.event.RenderGuiOverlayEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.config.ModConfig;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraftforge.fml.common.Mod;
import net.minecraft.client.Minecraft;
import net.minecraft.core.Direction;
import com.mojang.logging.LogUtils;
import org.slf4j.Logger;

import java.util.ArrayList;
import java.util.List;

// The value here should match an entry in the META-INF/mods.toml file
@Mod(CompassLite.MODID)
public class CompassLite {
    public static final String MODID = "compasslite";
    public static final String MOD_NAME = "CompassLite";
    public static final String VERSION = "0.1";
    private static final Logger LOGGER = LogUtils.getLogger();

    public CompassLite(FMLJavaModLoadingContext context){
        MinecraftForge.EVENT_BUS.register(new Hud());
        context.registerConfig(ModConfig.Type.CLIENT,Config.SPEC);
    }

    public static final List<ResourceLocation> _COMPASS;
    static {
        _COMPASS = new ArrayList<>();
        for (int i = 0; i<=31; i++){
            if (i<10){
            _COMPASS.add(ResourceLocation.fromNamespaceAndPath("minecraft","textures/item/compass_0"+i+".png"));
            }else{
                _COMPASS.add(ResourceLocation.fromNamespaceAndPath("minecraft","textures/item/compass_"+i+".png"));
            }
        }
    }
    public class Hud{
        @SubscribeEvent
        public void render(RenderGuiOverlayEvent event){
            Minecraft mc = Minecraft.getInstance();
            if (mc.options.renderDebug) return;
            if (mc.player == null || !mc.player.level().isClientSide()) return;

            float yaw = mc.player.getYHeadRot();

            yaw = (yaw % 360 + 360) % 360;

            int frameIndex = (int)((yaw / 360f) * 32);
            frameIndex = Math.max(0, Math.min(31, frameIndex));

            ResourceLocation currentCompassFrame = _COMPASS.get(frameIndex);

            PoseStack stack = event.getGuiGraphics().pose();
            stack.pushPose();
            stack.translate(Config.X_POS_MODIFIER.get(),Config.Y_POS_MODIFIER.get(),1f);
            stack.scale(Config.X_SCALE_MODIFIER.get().floatValue(),Config.Y_SCALE_MODIFIER.get().floatValue(),1f);
            event.getGuiGraphics().blit(currentCompassFrame, 10, 10, 0, 0, 16, 16, 16, 16);

            if (Config.SHOW_DIRECTION_TEXTS.get()){
                stack.pushPose();
                stack.scale(0.6f,0.6f,1f);

                int c1=0xFFFFFF,c2=0xFFFFFF,c3=0xFFFFFF,c4=0xFFFFFF;
                Direction direction =mc.player.getDirection();
                if (direction==Direction.NORTH){
                    c1 = Config.DIRECTION_TEXT_COLOR.get();
                }
                if (direction==Direction.SOUTH){
                    c2 = Config.DIRECTION_TEXT_COLOR.get();
                }
                if (direction==Direction.WEST){
                    c3 = Config.DIRECTION_TEXT_COLOR.get();
                }
                if (direction==Direction.EAST){
                    c4 = Config.DIRECTION_TEXT_COLOR.get();
                }

                event.getGuiGraphics().drawString(mc.font, "N", 27, 7, c1, true);
                event.getGuiGraphics().drawString(mc.font, "S", 27, 13+32, c2, true);
                event.getGuiGraphics().drawString(mc.font, "W", 10, 10+16, c3, true);
                event.getGuiGraphics().drawString(mc.font, "E", 12+32, 10+16, c4, true);

                stack.popPose();
            }
            stack.popPose();

        }
    }
    public class Config{
        public static final ForgeConfigSpec SPEC;
        public static final ForgeConfigSpec.BooleanValue SHOW_DIRECTION_TEXTS;
        public static final ForgeConfigSpec.DoubleValue X_SCALE_MODIFIER;
        public static final ForgeConfigSpec.DoubleValue Y_SCALE_MODIFIER;
        public static final ForgeConfigSpec.DoubleValue X_POS_MODIFIER;
        public static final ForgeConfigSpec.DoubleValue Y_POS_MODIFIER;

        public static final ForgeConfigSpec.IntValue DIRECTION_TEXT_COLOR;
        static {
            ForgeConfigSpec.Builder builder = new ForgeConfigSpec.Builder();
            builder.push("Core Config");
            SHOW_DIRECTION_TEXTS =builder
                    .comment("changes should render the direction texts on the compass")
                    .define("show_direction_texts",true);
            X_SCALE_MODIFIER = builder
                    .comment("changes the x scale of compass hud and texts")
                    .defineInRange("x_scale_modifier",1.0,0,Double.MAX_VALUE);
            Y_SCALE_MODIFIER = builder
                    .comment("changes the y scale of compass hud and texts")
                    .defineInRange("y_scale_modifier",1.0,0,Double.MAX_VALUE);
            X_POS_MODIFIER = builder
                    .comment("changes the x pos of compass hud and texts")
                    .defineInRange("x_pos_modifier",1.0,0,Double.MAX_VALUE);
            Y_POS_MODIFIER = builder
                    .comment("changes the y pos of compass hud and texts")
                    .defineInRange("y_pos_modifier",1.0,0,Double.MAX_VALUE);
            builder.pop();
            builder.push("Directions");
            DIRECTION_TEXT_COLOR = builder
                    .comment("direction text color when compass directs it")
                    .defineInRange("direction_text_color",16711680,0,Integer.MAX_VALUE);
            builder.pop();
            SPEC = builder.build();
        }
    }
}
