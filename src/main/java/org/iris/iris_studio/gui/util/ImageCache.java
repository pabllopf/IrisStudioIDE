package org.iris.iris_studio.gui.util;

import java.io.InputStream;
import java.util.Map;
import java.util.Objects;
import java.util.WeakHashMap;

import javafx.scene.image.Image;

public class ImageCache {

	private static final ImageCache INSTANCE = new ImageCache();

	public static Image getImage(String path) {
		return INSTANCE.get(path);
	}

	private final Map<String, Image> cache;

	private ImageCache() {
		cache = new WeakHashMap<>();
	}

	private Image get(String path) {
		if(cache.containsKey(path)) {
			return cache.get(path);
		}
		return load(path);
	}

	private Image load(String path) {
		InputStream stream = getClass().getResourceAsStream(path);
		Objects.requireNonNull(stream, path);
		Image image = new Image(stream);
		cache.put(path, image);
		return image;
	}

}
