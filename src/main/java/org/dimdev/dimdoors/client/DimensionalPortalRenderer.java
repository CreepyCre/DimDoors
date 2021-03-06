package org.dimdev.dimdoors.client;

import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import com.google.common.collect.ImmutableList;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public final class DimensionalPortalRenderer {
	private static final Random RANDOM = new Random(31100L);
	private static final ModelPart MODEL;
	private static final ModelPart TALL_MODEL;
	private static final EntityRenderDispatcher ENTITY_RENDER_DISPATCHER;
	private static final List<RenderLayer> RENDER_LAYERS = ImmutableList.copyOf(IntStream.range(0, 16).mapToObj(MyRenderLayer::getPortal).collect(Collectors.toList()));

	public static void renderWithTransforms(MatrixStack matrices, BlockPos pos, Transformer transformer, VertexConsumerProvider vertexConsumers, int light, int overlay, boolean tall) {
		matrices.push();
		double squaredDistance = pos.getSquaredDistance(ENTITY_RENDER_DISPATCHER.camera.getPos(), true);
		int offset = getOffset(squaredDistance);
		transformer.transform(matrices);
		renderModels(vertexConsumers, transformer, matrices, light, overlay, tall, offset);
		matrices.pop();
	}

	private static void renderModels(VertexConsumerProvider vertexConsumers, Transformer transformer, MatrixStack matrices, int light, int overlay, boolean tall, int offset) {
		renderSingleModel(vertexConsumers.getBuffer(RENDER_LAYERS.get(0)), transformer,  matrices, light, overlay, 0.15F, tall);

		for (int i = 1; i < offset; ++i) {
			renderSingleModel(vertexConsumers.getBuffer(RENDER_LAYERS.get(i)), transformer,  matrices, light, overlay,  2.0F / (float) (18 - i), tall);
		}
	}

	private static void renderSingleModel(VertexConsumer vertexConsumer, Transformer transformer, MatrixStack matrices, int light, int overlay, float v, boolean tall) {
		float r = MathHelper.clamp((RANDOM.nextFloat() * 0.3F + 0.1F) * v, 0, 1);
		float g = MathHelper.clamp((RANDOM.nextFloat() * 0.4F + 0.1F) * v, 0, 1);
		float b = MathHelper.clamp((RANDOM.nextFloat() * 0.5F + 0.6F) * v, 0, 1);

		ModelPart model = tall ? TALL_MODEL : MODEL;
		model.render(matrices, vertexConsumer, light, overlay, r, g, b, 1);
	}

	private static int getOffset(double d) {
		if (d > 36864.0D) {
			return 1;
		} else if (d > 25600.0D) {
			return 3;
		} else if (d > 16384.0D) {
			return 5;
		} else if (d > 9216.0D) {
			return 7;
		} else if (d > 4096.0D) {
			return 9;
		} else if (d > 1024.0D) {
			return 11;
		} else if (d > 576.0D) {
			return 13;
		} else {
			return d > 256.0D ? 14 : 15;
		}
	}

	static {
		MODEL = new ModelPart(1024, 1024, 0, 0);
		TALL_MODEL = new ModelPart(1024, 1024, 0, 0);
		MODEL.addCuboid(0, 0, 0, 16, 16, 0);
		TALL_MODEL.addCuboid(0, 0, 0, 16, 32, 0);
		ENTITY_RENDER_DISPATCHER = MinecraftClient.getInstance().getEntityRenderDispatcher();
	}
}
