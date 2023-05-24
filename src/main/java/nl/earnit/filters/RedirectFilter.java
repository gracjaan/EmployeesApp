package nl.earnit.filters;

import jakarta.servlet.*;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.*;

import nl.earnit.Auth;
import nl.earnit.Constants;
import nl.earnit.models.db.User;

public class RedirectFilter implements Filter {
    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        Filter.super.init(filterConfig);
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse,
                         FilterChain filterChain) throws IOException, ServletException {
//        HttpServletRequest req = (HttpServletRequest) servletRequest;
//
//        // Do not redirect static content, error or api
//        if (req.getServletPath().startsWith("/static") || req.getServletPath().startsWith("/error") || req.getServletPath().startsWith("/api")) {
//            filterChain.doFilter(servletRequest, servletResponse);
//            return;
//        }
//
//        if (req.getServletPath().startsWith("/logout")) {
//            filterChain.doFilter(servletRequest, servletResponse);
//            return;
//        }
//
//        User user = validateJWT(req);
//
//        // Do not redirect login and signup page
//        if (req.getServletPath().startsWith("/login") || req.getServletPath().startsWith("/signup")) {
//            if (user == null) {
//                filterChain.doFilter(servletRequest, servletResponse);
//                return;
//            }
//
//            redirectHome(servletResponse);
//            return;
//        }
//
//
//        if (user == null) {
//            redirectLogin(servletResponse);
//            return;
//        }
//
//        // Redirect for user
//        String path = switch (user.getType()) {
//            case "COMPANY" -> "/company";
//            case "ADMINISTRATOR" -> "/staff";
//            default -> "/student";
//        };
//
//        if (!req.getServletPath().startsWith(path)) {
//            RequestDispatcher dispatcher = servletRequest.getServletContext()
//                .getRequestDispatcher(path + req.getServletPath());
//            dispatcher.forward(servletRequest, servletResponse);
//            return;
//        }

        filterChain.doFilter(servletRequest, servletResponse);
    }

    private User validateJWT(HttpServletRequest req) {
        // If no session token redirect to login
        if (req.getCookies() == null) {
            return null;
        }

        List<Cookie> cookies = new ArrayList<>(List.of(req.getCookies()));

        // Check if token cookie is available
        Optional<Cookie> tokenCookie = cookies.stream().filter(x -> x.getName().equals("earnit-token")).findFirst();
        if (tokenCookie.isEmpty()) {
            return null;
        }

        // Validate jwt
        String token = tokenCookie.get().getValue();
        return Auth.validateJWT(token);
    }

    private void redirectLogin(ServletResponse servletResponse) throws IOException {
        ((HttpServletResponse) servletResponse).sendRedirect(Constants.ABSOLUTE_URL + "/login");
    }

    private void redirectHome(ServletResponse servletResponse) throws IOException {
        ((HttpServletResponse) servletResponse).sendRedirect(Constants.ABSOLUTE_URL + "/");
    }

    @Override
    public void destroy() {
        Filter.super.destroy();
    }
}
