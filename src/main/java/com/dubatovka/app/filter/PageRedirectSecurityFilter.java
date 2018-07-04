package com.dubatovka.app.filter;


import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import static com.dubatovka.app.config.ConfigConstant.FILTER_PARAM_INDEX_PATH;

// TODO заменить PageRedirectSecurityFilter с помощью SpringSecurity

/**
 * The class provides security filter for servlet container which prevents application users custom
 * site navigation.
 */
/*@WebFilter(
    filterName = "PageRedirectFilter",
    urlPatterns = {"/pages/*"},
    initParams = {@WebInitParam(name = FILTER_PARAM_INDEX_PATH, value = ConfigConstant.PAGE_INDEX)}
)*/
public class PageRedirectSecurityFilter implements Filter {
    /**
     * Path to index page.
     */
    private String indexPath;
    
    /**
     * Initializes {@link #indexPath} field using init parameters.
     *
     * @param filterConfig a filter configuration object used by a servlet container to pass
     *                     information to a filter during initialization
     */
    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        indexPath = filterConfig.getInitParameter(FILTER_PARAM_INDEX_PATH);
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
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;
        httpResponse.sendRedirect(httpRequest.getContextPath() + indexPath);
        chain.doFilter(request, response);
    }
    
    /**
     * Called by the web container to indicate to pressedKey filter that it is being
     * taken out of service.
     * <p>This method is only called once all threads within the filter's
     * doFilter method have exited or after pressedKey timeout period has passed.
     * After the web container calls this method, it will not call the
     * doFilter method again on this instance of the filter.
     * <p>This method gives the filter an opportunity to clean up any
     * resources that are being held (for example, memory, file handles,
     * threads) and make sure that any persistent state is synchronized
     * with the filter's current state in memory.
     */
    @Override
    public void destroy() {
        indexPath = null;
    }
}