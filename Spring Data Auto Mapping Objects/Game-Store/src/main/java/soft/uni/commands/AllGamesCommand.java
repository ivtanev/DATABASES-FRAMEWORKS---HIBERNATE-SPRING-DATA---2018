package soft.uni.commands;

import soft.uni.models.bindingModels.user.LoggedInUser;
import soft.uni.models.viewModels.game.GameView;
import soft.uni.services.api.GameService;
import soft.uni.services.api.UserService;
import soft.uni.utils.Session;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by Venelin on 27.7.2017 г..
 */
public class AllGamesCommand extends Command {

    public AllGamesCommand(UserService userService, GameService gameService) {
        super(userService, gameService);
    }

    @Override
    public String execute(String... params) {

        LoggedInUser loggedInUser = Session.getLoggedInUser();

        if (loggedInUser == null) {
            return "Not logged in";
        }

        List<GameView> games = super.getGameService().getAll();

        return games.stream().map(Object::toString).collect(Collectors.joining("\n"));
    }
}
