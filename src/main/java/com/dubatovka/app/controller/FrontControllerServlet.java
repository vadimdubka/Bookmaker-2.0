package com.dubatovka.app.controller;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

import static com.dubatovka.app.config.ConfigConstant.ATTR_PREV_QUERY;
import static com.dubatovka.app.config.ConfigConstant.FORWARD;
import static com.dubatovka.app.config.ConfigConstant.FRONT_CONTROLLER;
import static com.dubatovka.app.config.ConfigConstant.PAGE_INDEX;
import static com.dubatovka.app.config.ConfigConstant.PREV_QUERY;
import static com.dubatovka.app.config.ConfigConstant.REDIRECT;

/**
 * The class provides realization of controller for client requests in accordance with MVC pattern.
 *
 * @author Dubatovka Vadim
 */
@WebServlet(name = FRONT_CONTROLLER, urlPatterns = {"/controller"})
public class FrontControllerServlet extends HttpServlet {
    /**
     * Processes request sent by GET method.
     *
     * @param req  {@link HttpServletRequest} from client with parameters for processing
     * @param resp {@link HttpServletResponse} to client with parameters to work with on client
     *             side
     * @throws ServletException if the request could not be handled
     * @throws IOException      if an input or output error is detected when the servlet handles the
     *                          GET request
     */
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
        throws ServletException, IOException {
        processRequest(req, resp);
    }
    
    /**
     * Processes request sent by POST method.
     *
     * @param req  {@link HttpServletRequest} from client with parameters for processing
     * @param resp {@link HttpServletResponse} to client with parameters to work with on client
     *             side
     * @throws ServletException if the request could not be handled
     * @throws IOException      if an input or output error is detected when the servlet handles the
     *                          GET request
     */
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
        throws ServletException, IOException {
        processRequest(req, resp);
    }
    
    /**
     * Processes request. <p>Defines {@link Command} from {@link HttpServletRequest} and executes
     * it. Navigates to definite query corresponding to {@link PageNavigator} returned from
     * {@link Command#execute} method. If command execution didn't return valid {@link
     * PageNavigator} processes request using default scenario.</p>
     *
     * @param req  {@link HttpServletRequest} from client with parameters for processing
     * @param resp {@link HttpServletResponse} to client with parameters to work with on client
     *             side
     * @throws ServletException if the request could not be handled
     * @throws IOException      if an input or output error is detected when the servlet handles the
     *                          request
     */
    private void processRequest(HttpServletRequest req, HttpServletResponse resp)
        throws ServletException, IOException {
        Command       command   = CommandFactory.defineCommand(req);
        PageNavigator navigator = command.execute(req);
        if (navigator != null) {
            processNavigator(req, resp, navigator);
        } else {
            defaultProcessRequest(req, resp);
        }
    }
    
    /**
     * Navigates client to definite resource using definite response method based on 'query' and
     * 'response type' parameters from {@link PageNavigator}.
     *
     * @param req       {@link HttpServletRequest} from client
     * @param resp      {@link HttpServletResponse} to client
     * @param navigator {@link PageNavigator} with response parameters (contains 'query' and
     *                  'response type' data for navigation
     * @throws ServletException when
     * @throws IOException      when
     */
    private void processNavigator(HttpServletRequest req, HttpServletResponse resp,
                                  PageNavigator navigator)
        throws ServletException, IOException {
        String query = navigator.getQuery();
        if (PREV_QUERY.equals(query)) {
            query = takePreviousQuery(req);
        }
        String responseType = navigator.getResponseType();
        switch (responseType) {
            case FORWARD:
                ServletContext servletContext = getServletContext();
                RequestDispatcher dispatcher = servletContext.getRequestDispatcher(query);
                dispatcher.forward(req, resp);
                break;
            case REDIRECT:
                String contextPath = req.getContextPath();
                resp.sendRedirect(contextPath + query);
                break;
            default:
                defaultProcessRequest(req, resp);
        }
    }
    
    /**
     * Processes request using default scenario, that redirects user to index page.
     *
     * @param req  {@link HttpServletRequest} from client
     * @param resp {@link HttpServletResponse} to client
     * @throws IOException if an input or output error is detected when the servlet handles the
     *                     request
     */
    private static void defaultProcessRequest(HttpServletRequest req, HttpServletResponse resp)
        throws IOException {
        String contextPath = req.getContextPath();
        resp.sendRedirect(contextPath + PAGE_INDEX);
    }
    
    /**
     * Takes saved to {@link HttpSession} previous query.
     */
    private static String takePreviousQuery(HttpServletRequest req) {
        String prevQuery = (String) req.getSession().getAttribute(ATTR_PREV_QUERY);
        if (prevQuery == null) {
            prevQuery = PAGE_INDEX;
        }
        return prevQuery;
    }
}