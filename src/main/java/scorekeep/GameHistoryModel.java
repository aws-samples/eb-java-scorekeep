package scorekeep;

import org.springframework.beans.factory.annotation.Autowired;
import scorekeep.dao.gamehistory.GameHistory;
import scorekeep.dao.gamehistory.GameHistoryRepository;

import java.util.ArrayList;
import java.util.List;

public class GameHistoryModel {

    @Autowired(required = false)
    private GameHistoryRepository repository;

    public List<GameHistory> get() {
        List<GameHistory> result = new ArrayList<>();
        repository.findAll().forEach(result::add);
        return result;
    }

    public GameHistory create(String winnerId, String loserId) {
        GameHistory gameHistory = new GameHistory();
        gameHistory.setWinningPlayer(winnerId);
        gameHistory.setLosingPlayer(loserId);
        return repository.save(gameHistory);
    }
}
