package org.stonexthree.web.utils;

import com.fasterxml.jackson.databind.ObjectMapper;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class ResponseWriter {
    public static void writeObjectAsJsonToResponse(HttpServletResponse response, Object object) throws IOException {
        response.setContentType("application/json;charset=utf-8");
        response.getWriter().println(new ObjectMapper().writeValueAsString(object));
    }
}
