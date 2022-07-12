package com.jiaruiblog.service;

import java.io.IOException;

public interface ElasticService {

    String search(String keyword) throws IOException;
}
