package com.gkiss01.meetdebwebapi.exception;

import com.gkiss01.meetdebwebapi.utils.ErrorCodes;
import com.gkiss01.meetdebwebapi.utils.Utils;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
public class UnAuthorizedHandler implements AuthenticationEntryPoint {

    @Override
    public void commence(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, AuthenticationException e) throws IOException, ServletException {
        Utils.errorResponse(httpServletRequest, httpServletResponse, ErrorCodes.USER_DISABLED_NOT_VALID);
    }
}
