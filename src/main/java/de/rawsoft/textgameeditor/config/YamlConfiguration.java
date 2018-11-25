package de.rawsoft.textgameeditor.config;

import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;
import org.yaml.snakeyaml.representer.Representer;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.util.LinkedHashMap;
import java.util.Map;


public class YamlConfiguration extends ConfigurationProvider {

	private final ThreadLocal<Yaml> yaml = ThreadLocal.withInitial(() -> {
		Representer representer = new Representer() {
			{
				representers.put(ConfigurationSection.class, data -> represent(((ConfigurationSection) data).self));
			}
		};

		DumperOptions options = new DumperOptions();
		options.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);

		return new Yaml(new Constructor(), representer, options);
	});

	@Override
	public void save(ConfigurationSection config, File file) throws IOException {
		try (FileWriter writer = new FileWriter(file)) {
			save(config, writer);
		}
	}

	@Override
	public void save(ConfigurationSection config, Writer writer) {
		yaml.get().dump(config.self, writer);
	}

	@Override
	public ConfigurationSection load(File file) throws IOException {
		return load(file, null);
	}

	@Override
	public ConfigurationSection load(File file, ConfigurationSection defaults) throws IOException {
		try (InputStreamReader reader =  new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8)) {
			return load(reader, defaults);
		}
	}

	@Override
	public ConfigurationSection load(Reader reader) {
		return load(reader, null);
	}

	@Override
	@SuppressWarnings("unchecked")
	public ConfigurationSection load(Reader reader, ConfigurationSection defaults) {
		Map<String, Object> map = yaml.get().loadAs(reader, LinkedHashMap.class);
		if (map == null) {
			map = new LinkedHashMap<>();
		}
		return new ConfigurationSection(map, defaults);
	}

	@Override
	public ConfigurationSection load(InputStream is) {
		return load(is, null);
	}

	@Override
	@SuppressWarnings("unchecked")
	public ConfigurationSection load(InputStream is, ConfigurationSection defaults) {
		Map<String, Object> map = yaml.get().loadAs(is, LinkedHashMap.class);
		if (map == null) {
			map = new LinkedHashMap<>();
		}
		return new ConfigurationSection(map, defaults);
	}

	@Override
	public ConfigurationSection load(String string) {
		return load(string, null);
	}

	@Override
	@SuppressWarnings("unchecked")
	public ConfigurationSection load(String string, ConfigurationSection defaults) {
		Map<String, Object> map = yaml.get().loadAs(string, LinkedHashMap.class);
		if (map == null) {
			map = new LinkedHashMap<>();
		}
		return new ConfigurationSection(map, defaults);
	}
}
