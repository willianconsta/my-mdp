package mymdp.util;

import java.util.Collections;
import java.util.Map;

public class CollectionUtils
{
	public static <K, V> Map<K,V> nullToEmpty(final Map<K,V> map) {
		return map == null ? Collections.<K,V> emptyMap() : map;
	}
}
