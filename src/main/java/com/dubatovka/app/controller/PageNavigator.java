package com.dubatovka.app.controller;

import static com.dubatovka.app.config.ConfigConstant.FORWARD;
import static com.dubatovka.app.config.ConfigConstant.GOTO_INDEX;
import static com.dubatovka.app.config.ConfigConstant.GOTO_MAIN;
import static com.dubatovka.app.config.ConfigConstant.GOTO_MAKE_BET;
import static com.dubatovka.app.config.ConfigConstant.GOTO_MANAGE_PLAYERS;
import static com.dubatovka.app.config.ConfigConstant.GOTO_PLAYER_STATE;
import static com.dubatovka.app.config.ConfigConstant.GOTO_REGISTER;
import static com.dubatovka.app.config.ConfigConstant.PAGE_INDEX;
import static com.dubatovka.app.config.ConfigConstant.PAGE_MAIN;
import static com.dubatovka.app.config.ConfigConstant.PAGE_MAKE_BET;
import static com.dubatovka.app.config.ConfigConstant.PAGE_MANAGE_PLAYERS;
import static com.dubatovka.app.config.ConfigConstant.PAGE_PLAYER_STATE;
import static com.dubatovka.app.config.ConfigConstant.PAGE_REGISTER;
import static com.dubatovka.app.config.ConfigConstant.PREV_QUERY;
import static com.dubatovka.app.config.ConfigConstant.REDIRECT;

/**
 * Enumeration of objects used for navigation purposes in {@link FrontControllerServlet#processNavigator}.
 * Each contain 'query' and 'responseType' {@link String} objects. <p>There id 3 ways of processing
 * navigation:
 * <ol>
 *     <li>Forwarding to jsp-page</li>
 *     <li>Forwarding to command-query, which processes navigation to definite page</li>
 *     <li>Redirecting to command-query, which processes navigation to definite page</li>
 * </ol>
 *
 * @author Dubatovka Vadim
 */
public enum PageNavigator {
    FORWARD_PAGE_INDEX(PAGE_INDEX, FORWARD),
    FORWARD_GOTO_INDEX(GOTO_INDEX, FORWARD),
    REDIRECT_GOTO_INDEX(GOTO_INDEX, REDIRECT),
    
    FORWARD_PAGE_MAIN(PAGE_MAIN, FORWARD),
    FORWARD_GOTO_MAIN(GOTO_MAIN, FORWARD),
    REDIRECT_GOTO_MAIN(GOTO_MAIN, REDIRECT),
    
    FORWARD_PAGE_MAKE_BET(PAGE_MAKE_BET, FORWARD),
    FORWARD_GOTO_MAKE_BET(GOTO_MAKE_BET, FORWARD),
    REDIRECT_GOTO_MAKE_BET(GOTO_MAKE_BET, REDIRECT),
    
    FORWARD_PAGE_MANAGE_PLAYER(PAGE_MANAGE_PLAYERS, FORWARD),
    FORWARD_GOTO_MANAGE_PLAYER(GOTO_MANAGE_PLAYERS, FORWARD),
    REDIRECT_GOTO_MANAGE_PLAYER(GOTO_MANAGE_PLAYERS, REDIRECT),
    
    FORWARD_PAGE_PLAYER_STATE(PAGE_PLAYER_STATE, FORWARD),
    FORWARD_GOTO_PLAYER_STATE(GOTO_PLAYER_STATE, FORWARD),
    REDIRECT_GOTO_PLAYER_STATE(GOTO_PLAYER_STATE, REDIRECT),
    
    FORWARD_PAGE_REGISTER(PAGE_REGISTER, FORWARD),
    FORWARD_GOTO_REGISTER(GOTO_REGISTER, FORWARD),
    REDIRECT_GOTO_REGISTER(GOTO_REGISTER, REDIRECT),
    
    /**
     * Used to order {@link FrontControllerServlet} to take previous query.
     */
    FORWARD_PREV_QUERY(PREV_QUERY, FORWARD),
    REDIRECT_PREV_QUERY(PREV_QUERY, REDIRECT);
    
    /**
     * Query to process
     */
    private final String query;
    /**
     * Response type of query processing
     */
    private final String responseType;
    
    /**
     * Constructs enumeration objects with definite query and response type.
     *
     * @param query {@link String} to process
     * @param responseType {@link String} response type of query processing
     */
    PageNavigator(String query, String responseType) {
        this.query = query;
        this.responseType = responseType;
    }
    
    /**
     * Getter of query
     *
     * @return {@link String} query to process
     */
    public String getQuery() {
        return query;
    }
    
    /**
     * Getter of response type
     *
     * @return {@link String} response type of query processing
     */
    public String getResponseType() {
        return responseType;
    }
}