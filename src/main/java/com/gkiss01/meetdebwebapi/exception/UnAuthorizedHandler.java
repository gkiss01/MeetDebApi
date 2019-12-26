package com.gkiss01.meetdebwebapi.exception;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.gkiss01.meetdebwebapi.model.GenericResponse;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;

@Component
public class UnAuthorizedHandler implements AuthenticationEntryPoint {

    @Override
    public void commence(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, AuthenticationException e) throws IOException, ServletException {
        GenericResponse response = new GenericResponse(true, null);
        response.addError("Unauthorized!");
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
