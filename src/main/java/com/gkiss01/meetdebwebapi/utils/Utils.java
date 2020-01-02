package com.gkiss01.meetdebwebapi.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.gkiss01.meetdebwebapi.model.GenericResponse;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Collections;

public class Utils {
    public static void errorResponse(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, String error) throws IOException {
        GenericResponse response = GenericResponse.builder().error(true).errors(Collections.singletonList(error)).build();
        OutputStream out = httpServletResponse.getOutputStream();

        ObjectMapper mapper;
        if (httpServletRequest.getHeader("Accept") != null && httpServletRequest.getHeader("Accept").equals("application/xml")) {
            httpServletResponse.setContentType("application/xml");
            mapper = new XmlMapper();
        }
        else {
            httpServletResponse.setContentType("application/json");
            mapper = new ObjectMapper();
        }

        mapper.writeValue(out, response);
        out.flush();
    }
}
