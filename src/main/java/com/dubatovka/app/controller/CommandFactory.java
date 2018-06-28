package com.dubatovka.app.controller;

import com.dubatovka.app.config.ConfigConstant;
import com.dubatovka.app.controller.impl.ChangeLocaleCommand;
import com.dubatovka.app.controller.impl.EventCreateCommand;
import com.dubatovka.app.controller.impl.EventDeleteCommand;
import com.dubatovka.app.controller.impl.EventInfoUpdateCommand;
import com.dubatovka.app.controller.impl.EventResultUpdateCommand;
import com.dubatovka.app.controller.impl.MakeBetCommand;
import com.dubatovka.app.controller.impl.OutcomeCreateCommand;
import com.dubatovka.app.controller.impl.PayWinBetCommand;
import com.dubatovka.app.controller.impl.authorization.LoginCommand;
import com.dubatovka.app.controller.impl.authorization.LogoutCommand;
import com.dubatovka.app.controller.impl.authorization.RegisterCommand;
import com.dubatovka.app.controller.impl.navigation.GotoIndexCommand;
import com.dubatovka.app.controller.impl.navigation.GotoMainCommand;
import com.dubatovka.app.controller.impl.navigation.GotoMakeBetCommand;
import com.dubatovka.app.controller.impl.navigation.GotoManagePlayersCommand;
import com.dubatovka.app.controller.impl.navigation.GotoPlayerStateCommand;
import com.dubatovka.app.controller.impl.navigation.GotoRegisterCommand;
import com.dubatovka.app.controller.impl.navigation.events.GotoEventCorrectOutcomeCommand;
import com.dubatovka.app.controller.impl.navigation.events.GotoEventCorrectResultCommand;
import com.dubatovka.app.controller.impl.navigation.events.GotoEventManageCommand;
import com.dubatovka.app.controller.impl.navigation.events.GotoEventManageFailedCommand;
import com.dubatovka.app.controller.impl.navigation.events.GotoEventSetOutcomeCommand;
import com.dubatovka.app.controller.impl.navigation.events.GotoEventSetResultCommand;
import com.dubatovka.app.controller.impl.navigation.events.GotoEventShowActualCommand;
import com.dubatovka.app.controller.impl.navigation.events.GotoEventShowResultCommand;
import com.dubatovka.app.controller.impl.navigation.events.GotoEventToPayCommand;
import com.dubatovka.app.entity.User;
import com.dubatovka.app.service.PreviousQueryService;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.EnumMap;
import java.util.Map;

import static com.dubatovka.app.config.ConfigConstant.ATTR_ROLE;

/**
 * The factory of Commands for using with {@link FrontControllerServlet}.
 *
 * @author Dubatovka Vadim
 */
final class CommandFactory {
    private static final Logger logger = LogManager.getLogger(CommandFactory.class);
    
    private static final String ERR_COMMAND_TYPE_PARAM =
        "Request doesn't have command_type parameter or defined command_type parameter is invalid: %s.";
    private static final String ERR_COMMAND_IMPL       =
        "Command implementation is not defined for command type: %s.";
    
    //TODO избавиться от PreviousQueryService previousQueryService в этом классе
    @Autowired
    private static PreviousQueryService previousQueryService;
    
    /**
     * {@link EnumMap} collection of common commands for oll user roles.
     */
    private static final Map<CommandType, Command> commonCommands  = new EnumMap<>(CommandType.class);
    /**
     * {@link EnumMap} collection of commands available to {@link User.UserRole#GUEST}.
     */
    private static final Map<CommandType, Command> guestCommands   = new EnumMap<>(CommandType.class);
    /**
     * {@link EnumMap} collection of commands available to {@link User.UserRole#PLAYER}.
     */
    private static final Map<CommandType, Command> playerCommands  = new EnumMap<>(CommandType.class);
    /**
     * {@link EnumMap} collection of commands available to {@link User.UserRole#ADMIN}.
     */
    private static final Map<CommandType, Command> adminCommands   = new EnumMap<>(CommandType.class);
    /**
     * {@link EnumMap} collection of commands available to {@link User.UserRole#ANALYST}.
     */
    private static final Map<CommandType, Command> analystCommands = new EnumMap<>(CommandType.class);
    
    static {
        commonCommands.put(CommandType.GOTO_INDEX, new GotoIndexCommand());
        commonCommands.put(CommandType.GOTO_MAIN, new GotoMainCommand());
        commonCommands.put(CommandType.GOTO_REGISTER, new GotoRegisterCommand());
        commonCommands.put(CommandType.CHANGE_LOCALE, new ChangeLocaleCommand());
        commonCommands.put(CommandType.REGISTER, new RegisterCommand());
        commonCommands.put(CommandType.LOGIN, new LoginCommand());
        commonCommands.put(CommandType.LOGOUT, new LogoutCommand());
        commonCommands.put(CommandType.GOTO_MAKE_BET, new GotoMakeBetCommand());
        commonCommands.put(CommandType.MAKE_BET, new MakeBetCommand());
        commonCommands.put(CommandType.GOTO_EVENT_SHOW_ACTUAL, new GotoEventShowActualCommand());
        commonCommands.put(CommandType.GOTO_EVENT_SHOW_RESULT, new GotoEventShowResultCommand());
        
        guestCommands.putAll(commonCommands);
        
        playerCommands.putAll(commonCommands);
        playerCommands.put(CommandType.GOTO_PLAYER_STATE, new GotoPlayerStateCommand());
        
        adminCommands.putAll(commonCommands);
        adminCommands.put(CommandType.GOTO_MANAGE_PLAYERS, new GotoManagePlayersCommand());
        adminCommands.put(CommandType.GOTO_EVENT_MANAGE, new GotoEventManageCommand());
        adminCommands.put(CommandType.GOTO_EVENT_SET_RESULT, new GotoEventSetResultCommand());
        adminCommands.put(CommandType.GOTO_EVENT_CORRECT_RESULT, new GotoEventCorrectResultCommand());
        adminCommands.put(CommandType.GOTO_EVENT_MANAGE_FAILED, new GotoEventManageFailedCommand());
        adminCommands.put(CommandType.GOTO_EVENTS_TO_PAY, new GotoEventToPayCommand());
        adminCommands.put(CommandType.EVENT_DELETE, new EventDeleteCommand());
        adminCommands.put(CommandType.EVENT_CREATE, new EventCreateCommand());
        adminCommands.put(CommandType.EVENT_INFO_UPDATE, new EventInfoUpdateCommand());
        adminCommands.put(CommandType.EVENT_RESULT_UPDATE, new EventResultUpdateCommand());
        adminCommands.put(CommandType.PAY_WIN_BET, new PayWinBetCommand());
        
        analystCommands.putAll(commonCommands);
        analystCommands.put(CommandType.GOTO_EVENT_SET_OUTCOME, new GotoEventSetOutcomeCommand());
        analystCommands.put(CommandType.GOTO_EVENT_CORRECT_OUTCOME, new GotoEventCorrectOutcomeCommand());
        analystCommands.put(CommandType.OUTCOME_CREATE, new OutcomeCreateCommand());
    }
    
    /**
     * Private constructor to forbid creation of {@link CommandFactory} instance.
     */
    private CommandFactory() {
    }
    
    /**
     * Defines {@link Command} based on {@link ConfigConstant#PARAM_COMMAND_TYPE} parameter from
     * {@link HttpServletRequest} and {@link ConfigConstant#ATTR_ROLE} attribute from {@link
     * HttpSession}.
     *
     * @param request {@link HttpServletRequest}
     * @return {@link Command}
     */
    static Command defineCommand(HttpServletRequest request) {
        String        commandTypeName        = request.getParameter(ConfigConstant.PARAM_COMMAND_TYPE);
        User.UserRole role                   = (User.UserRole) request.getSession().getAttribute(ATTR_ROLE);
        boolean       isCommandTypeNameValid = isCommandTypeNameValid(commandTypeName);
        Command       command;
        
        if (isCommandTypeNameValid) {
            commandTypeName = commandTypeName.trim().toUpperCase();
            CommandType commandType = CommandType.valueOf(commandTypeName);
            switch (role) {
                case PLAYER:
                    command = playerCommands.get(commandType);
                    break;
                case ADMIN:
                    command = adminCommands.get(commandType);
                    break;
                case ANALYST:
                    command = analystCommands.get(commandType);
                    break;
                case GUEST:
                    command = guestCommands.get(commandType);
                    break;
                default:
                    command = guestCommands.get(commandType);
            }
            if (command == null) {
                logger.log(Level.ERROR, String.format(ERR_COMMAND_IMPL, commandType));
                command = new GotoIndexCommand();
            }
        } else {
            logger.log(Level.ERROR, String.format(ERR_COMMAND_TYPE_PARAM, commandTypeName));
            command = new GotoIndexCommand();
        }
        
        return command;
    }
    
    /**
     * Validates command type name.
     *
     * @param commandTypeName {@link String}
     * @return boolean result of validation
     */
    private static boolean isCommandTypeNameValid(String commandTypeName) {
        boolean isExist = !((commandTypeName == null) || commandTypeName.trim().isEmpty());
        if (isExist) {
            for (CommandType type : CommandType.values()) {
                if (type.toString().equalsIgnoreCase(commandTypeName)) {
                    return true;
                }
            }
        }
        return false;
    }
    
    /**
     * Enumeration of command types.
     */
    private enum CommandType {
        CHANGE_LOCALE,
        REGISTER,
        LOGIN,
        LOGOUT,
        MAKE_BET,
        PAY_WIN_BET,
        EVENT_DELETE,
        EVENT_CREATE,
        EVENT_INFO_UPDATE,
        EVENT_RESULT_UPDATE,
        OUTCOME_CREATE,
        GOTO_MAIN,
        GOTO_INDEX,
        GOTO_REGISTER,
        GOTO_MANAGE_PLAYERS,
        GOTO_MAKE_BET,
        GOTO_PLAYER_STATE,
        GOTO_EVENT_SHOW_ACTUAL,
        GOTO_EVENT_MANAGE,
        GOTO_EVENT_SET_OUTCOME,
        GOTO_EVENT_CORRECT_OUTCOME,
        GOTO_EVENT_SET_RESULT,
        GOTO_EVENT_CORRECT_RESULT,
        GOTO_EVENT_SHOW_RESULT,
        GOTO_EVENT_MANAGE_FAILED,
        GOTO_EVENTS_TO_PAY
    }
}