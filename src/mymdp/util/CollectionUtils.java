package mymdp.util;

import static java.util.Collections.emptyMap;

import java.util.Map;

public class CollectionUtils
{
	public static <K, V> Map<K,V> nullToEmpty(final Map<K,V> map) {
		return map == null ? emptyMap() : map;
	}
}
