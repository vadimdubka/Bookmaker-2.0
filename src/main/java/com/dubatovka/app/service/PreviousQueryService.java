package com.dubatovka.app.service;

import javax.servlet.http.HttpServletRequest;

public interface PreviousQueryService {
    
    void saveQueryToSession(HttpServletRequest request);
    
    String takePreviousQuery(HttpServletRequest req);
}
