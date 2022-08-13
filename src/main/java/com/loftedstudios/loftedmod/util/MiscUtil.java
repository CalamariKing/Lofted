package com.loftedstudios.loftedmod.util;

import com.google.gson.JsonElement;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.JsonOps;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

public class MiscUtil {

    @SuppressWarnings("unchecked")
    public static <T> T dummyObject() {
        return (T) new Object();
    }

    public static <T> T deserializeDataJson(Codec<T> codec, Identifier resource) throws IOException {
        return deserializeDataJson(JsonOps.INSTANCE, codec, resource);
    }

    public static <T> T deserializeDataJson(DynamicOps<JsonElement> ops, Codec<T> codec, Identifier resource) throws IOException {
        var resourcePath = "/data/" + resource.getNamespace() + '/' + resource.getPath() + ".json";
        var stream = MiscUtil.class.getResourceAsStream(resourcePath);
        if (stream == null) {
            throw new FileNotFoundException("Failed to locate data JSON: " + resource);
        }
        try (stream) {
            var reader = new InputStreamReader(stream, StandardCharsets.UTF_8);
            var decodeResult = codec.decode(ops, JsonHelper.deserialize(reader));

            var result = decodeResult.result();
            if (result.isPresent()) {
                return result.get().getFirst();
            } else {
                //noinspection OptionalGetWithoutIsPresent
                throw new IOException(decodeResult.error().get().message());
            }
        }
    }
}
