package de.rawsoft.textgameeditor.config;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.io.Writer;
import java.util.HashMap;
import java.util.Map;

public abstract class ConfigurationProvider {

	private static final Map<Class<? extends ConfigurationProvider>, ConfigurationProvider> providers = new HashMap<>();

	static {
		providers.put(YamlConfiguration.class, new YamlConfiguration());
	}

	public static ConfigurationProvider getProvider(Class<? extends ConfigurationProvider> provider) {
		return providers.get(provider);
	}

	/*------------------------------------------------------------------------*/
	public abstract void save(ConfigurationSection config, File file) throws IOException;

	public abstract void save(ConfigurationSection config, Writer writer);

	public abstract ConfigurationSection load(File file) throws IOException;

	public abstract ConfigurationSection load(File file, ConfigurationSection defaults) throws IOException;

	public abstract ConfigurationSection load(Reader reader);

	public abstract ConfigurationSection load(Reader reader, ConfigurationSection defaults);

	public abstract ConfigurationSection load(InputStream is);

	public abstract ConfigurationSection load(InputStream is, ConfigurationSection defaults);

	public abstract ConfigurationSection load(String string);

	public abstract ConfigurationSection load(String string, ConfigurationSection defaults);
}
