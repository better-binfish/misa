package xyz.binfish.misa;

import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.List;

import xyz.binfish.misa.cli.CommandLine;
import xyz.binfish.misa.util.NumberUtil;
import xyz.binfish.logger.Logger;

public class Settings {

	private final int shardCount;
	private final int[] shards;
	private final boolean useColors;
	private final boolean useDebugging;

	private final List<String> jarArgs;

	Settings(CommandLine cmd, String[] args) {
		this.shardCount = NumberUtil.parseInt(cmd.getOptionValue("shard-count", "0"));
		this.shards = parseShardIds(cmd);
		this.useColors = !cmd.hasOption("no-colors");
		this.useDebugging = cmd.hasOption("debug");
		this.jarArgs = Arrays.asList(args);
	}

	public int getShardCount() {
		return shardCount < 1 ? -1 : shardCount;
	}

	@Nullable
	public int[] getShards() {
		return shards;
	}

	public boolean useColors() {
		return useColors;
	}

	public boolean useDebugging() {
		return useDebugging;
	}

	public List<String> getJarArgs() {
		return jarArgs;
	}

	private int[] parseShardIds(CommandLine cmd) {
		if(getShardCount() == -1 || !cmd.hasOption("shards")) {
			return null;
		}

		try {
			String[] parts = cmd.getOptionValue("shards").split("-");
			if(parts.length == 1) {
				return new int[]{
					NumberUtil.getBetween(
							Integer.parseInt(parts[0]), 0, getShardCount()
					)
				};
			}

			if(parts.length != 2) {
				return null;
			}

			int min = NumberUtil.getBetween(Integer.parseInt(parts[0]), 0, getShardCount());
			int max = NumberUtil.getBetween(Integer.parseInt(parts[1]), 0, getShardCount());

			if(min == max) {
				return new int[]{ min };
			}

			if(min > max) {
				max = max + min;
				min = max - min;
				max = max - min;
			}

			int range = max - min + 1;
			int[] shards = new int[range];
			for(int i = 0; i < range; i++) {
				shards[i] = min++;
			}

			return shards;
		} catch(NumberFormatException e) {
			Logger.getLogger().error("Failed to parse shard range for the \"--shards\" flag.", e);
			return null;
		}
	}
}
