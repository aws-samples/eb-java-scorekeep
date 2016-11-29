package scorekeep.dao.gamehistory;

import org.springframework.data.repository.CrudRepository;

/**
 * Used by Spring to create a CRUD interface for GameHistory at runtime
 */
public interface GameHistoryRepository extends CrudRepository<GameHistory, Integer> {
}
