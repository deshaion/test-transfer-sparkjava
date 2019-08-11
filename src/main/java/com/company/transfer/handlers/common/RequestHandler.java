package com.company.transfer.handlers.common;

import java.util.Map;

public interface RequestHandler<V> {
    Answer process(V value, Map<String, String> urlParams);
}
