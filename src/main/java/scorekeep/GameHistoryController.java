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

    @Autowired
    private GameHistoryModel model;
    private UserFactory userFactory = new UserFactory();

    @RequestMapping(method = RequestMethod.GET)
    public List<GameHistory> get() {
        return model.get();
    }

    @RequestMapping(method = RequestMethod.POST)
    public GameHistory create() throws IOException {
        String winner = userFactory.randomName();
        String loser = userFactory.randomName();

        return model.create(winner, loser);
    }
}
