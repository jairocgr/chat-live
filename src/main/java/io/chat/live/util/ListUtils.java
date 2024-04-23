package io.chat.live.util;

import java.util.List;

public class ListUtils {

    public static <T> T lastItemFrom(List<T> list) {
        return list.get(list.size() - 1);
    }

    public static <T> T firstItemFrom(List<T> list) {
        return list.get(0);
    }

    public static <T> T secondItemFrom(List<T> list) {
        return list.get(1);
    }

}
