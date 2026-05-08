import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Servlet Filter implementation class LoginFilter
 */
@WebFilter(filterName = "LoginFilter", urlPatterns = "/*")
public class LoginFilter implements Filter {
    private final List<String> allowedURIs = new ArrayList<>();

    /**
     * @see Filter#doFilter(ServletRequest, ServletResponse, FilterChain)
     */
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        String requestURI = httpRequest.getRequestURI();
        String contextPath = httpRequest.getContextPath();
        System.out.println("LoginFilter: " + requestURI);

        // Check if this URL is allowed to access without logging in
        if (this.isUrlAllowedWithoutLogin(requestURI)) {
            // Keep default action: pass along the filter chain
            chain.doFilter(request, response);
            return;
        }

        if(isDashboardUrl(requestURI)){
            if (httpRequest.getSession().getAttribute("employeeEmail") == null) {
                httpResponse.sendRedirect(contextPath + "/_dashboard-login.html");
            } else {
                chain.doFilter(request, response);
            }
            return;
        }
        // Redirect to login page if the "user" attribute doesn't exist in session
        if (httpRequest.getSession().getAttribute("user") == null) {
            httpResponse.sendRedirect(contextPath + "/login.html");
        } else {
            chain.doFilter(request, response);
        }
    }

    private boolean isUrlAllowedWithoutLogin(String requestURI) {
        /*
         Setup your own rules here to allow accessing some resources without logging in
         Always allow your own login related requests(html, js, servlet, etc..)
         You might also want to allow some CSS files, etc..
         */
        return allowedURIs.stream().anyMatch(requestURI.toLowerCase()::endsWith);
    }

    private boolean isDashboardUrl(String requestURI){
        return requestURI.endsWith("/_dashboard") || requestURI.endsWith("/_dashboard.html")
                || requestURI.endsWith("/_dashboard.js") || requestURI.endsWith("/api/add-star") ||
                requestURI.endsWith("/api/metadata") || requestURI.endsWith("/api/add-movie");
    }

    public void init(FilterConfig fConfig) {
        allowedURIs.add("index.html");
        allowedURIs.add("login.html");
        allowedURIs.add("login.js");
        allowedURIs.add("api/login");
        allowedURIs.add("cs122b_spring_cruisers_war/");
        allowedURIs.add("_dashboard-login.html");
        allowedURIs.add("_dashboard-login.js");
        allowedURIs.add("api/dashboard-login");
    }
}