package com.dubatovka.app.controller.impl.navigation;

import com.dubatovka.app.controller.Command;
import com.dubatovka.app.controller.PageNavigator;
import com.dubatovka.app.entity.Player;
import com.dubatovka.app.service.PlayerService;
import com.dubatovka.app.service.PreviousQueryService;
import com.dubatovka.app.service.QueryService;
import com.dubatovka.app.service.impl.ServiceFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

import static com.dubatovka.app.config.ConfigConstant.ATTR_PLAYERS;

/**
 * The class provides navigating to page for players management.
 *
 * @author Dubatovka Vadim
 */
@Controller
public class GotoManagePlayersCommand implements Command {
    private final PreviousQueryService previousQueryService;
    
    @Autowired
    public GotoManagePlayersCommand(PreviousQueryService previousQueryService) {
        this.previousQueryService = previousQueryService;
    }
    
    @GetMapping("/manage_players_page")
    public String showManagePlayerPage(Model model, HttpServletRequest request) {
        List<Player> players;
        try (PlayerService playerService = ServiceFactory.getPlayerService()) {
            players = playerService.getAllPlayers();
        }
        request.setAttribute(ATTR_PLAYERS, players);
        previousQueryService.saveQueryToSession(request);
        return "manage_players";
    }
    
    /**
     * Method provides navigation process to page for players management.<p>
     *
     * @param request {@link HttpServletRequest} from client
     * @return {@link PageNavigator#FORWARD_PAGE_MANAGE_PLAYER}
     */
    @Deprecated
    @Override
    public PageNavigator execute(HttpServletRequest request) {
        List<Player> players;
        try (PlayerService playerService = ServiceFactory.getPlayerService()) {
            players = playerService.getAllPlayers();
        }
        request.setAttribute(ATTR_PLAYERS, players);
        QueryService.saveQueryToSession(request);
        return PageNavigator.FORWARD_PAGE_MANAGE_PLAYER;
    }
}
