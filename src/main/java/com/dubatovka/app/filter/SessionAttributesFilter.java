package com.dubatovka.app.filter;

import com.dubatovka.app.config.ConfigConstant;
import com.dubatovka.app.entity.User;

import javax.servlet.DispatcherType;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.annotation.WebInitParam;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.io.IOException;

import static com.dubatovka.app.config.ConfigConstant.ATTR_LOCALE;
import static com.dubatovka.app.config.ConfigConstant.ATTR_ROLE;
import static com.dubatovka.app.config.ConfigConstant.FILTER_PARAM_LOCALE;
import static com.dubatovka.app.config.ConfigConstant.FILTER_PARAM_ROLE;
import static com.dubatovka.app.config.ConfigConstant.FRONT_CONTROLLER;

/**
 * The class provides security filter for servlet container which monitors each session to have
 * locale and role attributes set.
 */
@WebFilter(
    filterName = "SecurityFilter",
    servletNames = {FRONT_CONTROLLER},
    initParams = {@WebInitParam(name = FILTER_PARAM_ROLE, value = ATTR_ROLE),
                     @WebInitParam(name = FILTER_PARAM_LOCALE, value = ATTR_LOCALE)},
    dispatcherTypes = {DispatcherType.REQUEST, DispatcherType.FORWARD}
)
public class SessionAttributesFilter implements Filter {
    /**
     * Role attribute name.
     */
    private String role;
    /**
     * Locale attribute name.
     */
    private String locale;
    
    /**
     * Initializes {@link #role} and {@link #locale} fields using init parameters.
     *
     * @param filterConfig a filter configuration object used by a servlet container to pass
     *                     information to a filter during initialization
     */
    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        role = filterConfig.getInitParameter(FILTER_PARAM_ROLE);
        locale = filterConfig.getInitParameter(FILTER_PARAM_LOCALE);
    }
    
    /**
     * The <code>doFilter</code> method of the Filter is called by the
     * container each time a request/response pair is passed through the
     * chain due to a client request for a resource at the end of the chain.
     * The FilterChain passed in to this method allows the Filter to pass
     * on the request and response to the next entity in the chain.
     * <p>
     * <p>A typical implementation of this method would follow the following
     * pattern:
     * <ol>
     * <li>Examine the request
     * <li>Optionally wrap the request object with a custom implementation to
     * filter content or headers for input filtering
     * <li>Optionally wrap the response object with a custom implementation to
     * filter content or headers for output filtering
     * <li>
     * <ul>
     * <li><strong>Either</strong> invoke the next entity in the chain
     * using the FilterChain object
     * (<code>chain.doFilter()</code>),
     * <li><strong>or</strong> not pass on the request/response pair to
     * the next entity in the filter chain to
     * block the request processing
     * </ul>
     * <li>Directly set headers on the response after invocation of the
     * next entity in the filter chain.
     * </ol>
     *
     * @param request  an object which provides client request information to a servlet
     * @param response an object which assists a servlet in sending a response to the client
     * @param chain    an object provided by the servlet container to the developer giving a view
     *                 into the invocation chain of a filtered request for a resource. Filters use
     *                 the FilterChain to invoke the next filter in the chain, or if the calling
     *                 filter is the last filter in the chain, to invoke the resource at the end of
     *                 the chain
     */
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
        throws IOException, ServletException {
        HttpServletRequest req        = (HttpServletRequest) request;
        HttpSession        session    = req.getSession();
        Object             userRole   = session.getAttribute(role);
        Object             userLocale = session.getAttribute(locale);
        if (userRole == null) {
            userRole = User.UserRole.GUEST;
            session.setAttribute(role, userRole);
        }
        if (userLocale == null) {
            userLocale = ConfigConstant.LOCALE_DEFAULT;
            session.setAttribute(locale, userLocale);
        }
        chain.doFilter(request, response);
    }
    
    /**
     * Called by the web container to indicate to a filter that it is being
     * taken out of service.
     * <p>
     * <p>This method is only called once all threads within the filter's
     * doFilter method have exited or after a timeout period has passed.
     * After the web container calls this method, it will not call the
     * doFilter method again on this instance of the filter.
     * <p>
     * <p>This method gives the filter an opportunity to clean up any
     * resources that are being held (for example, memory, file handles,
     * threads) and make sure that any persistent state is synchronized
     * with the filter's current state in memory.
     */
    @Override
    public void destroy() {
        role = null;
        locale = null;
    }
}
