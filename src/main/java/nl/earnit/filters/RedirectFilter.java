package nl.earnit.filters;

import jakarta.servlet.*;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.Arrays;

import nl.earnit.Constants;

public class RedirectFilter implements Filter {
    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        Filter.super.init(filterConfig);
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse,
                         FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) servletRequest;

        System.out.println(req.getServletPath());

        // Do not redirect static content or api
        if (req.getServletPath().startsWith("/static") || req.getServletPath().startsWith("/api")) {
            filterChain.doFilter(servletRequest, servletResponse);
            return;
        }

        // Do not redirect login and signup page
        if (req.getServletPath().startsWith("/login") || req.getServletPath().startsWith("/signup")) {
            filterChain.doFilter(servletRequest, servletResponse);
            return;
        }

        // If no session token redirect to login
        Cookie[] cookies = req.getCookies();
        if (cookies == null || Arrays.stream(cookies).noneMatch(x -> x.getName().equals("earnit-token"))) {
            ((HttpServletResponse) servletResponse).sendRedirect(Constants.ABSOLUTE_URL + "/login");
            return;
        }

        // TODO: get user and then decide to go to '/student', '/company' or '/staff'

        if (!req.getServletPath().startsWith("/student")) {
            RequestDispatcher dispatcher = servletRequest.getServletContext()
                .getRequestDispatcher("/student" + req.getServletPath());
            dispatcher.forward(servletRequest, servletResponse);
            return;
        }

        filterChain.doFilter(servletRequest, servletResponse);
    }

    @Override
    public void destroy() {
        Filter.super.destroy();
    }
}
