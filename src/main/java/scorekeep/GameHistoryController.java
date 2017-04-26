package scorekeep;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import scorekeep.dao.gamehistory.GameHistory;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping(value="/api/history")
public class GameHistoryController {

    @Autowired(required = false)
    private GameHistoryModel model;
    private UserFactory userFactory = new UserFactory();

    @RequestMapping(method = RequestMethod.GET)
    public List<GameHistory> get() throws RdsNotConfiguredException {
        if (! Application.isRdsEnabled()) {
            throw new RdsNotConfiguredException();
        }
        return model.get();
    }

    @RequestMapping(method = RequestMethod.POST)
    public GameHistory create() throws IOException, RdsNotConfiguredException {
        if (! Application.isRdsEnabled()) {
            throw new RdsNotConfiguredException();
        }

        String category = "American names";
        String winner = userFactory.randomNameLambda("1235ABCD", category);
        String loser = userFactory.randomNameLambda("ABCD1235", category);

        return model.create(winner, loser);
    }
}
