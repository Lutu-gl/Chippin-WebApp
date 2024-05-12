package at.ac.tuwien.sepr.groupphase.backend.repository;

import at.ac.tuwien.sepr.groupphase.backend.entity.ApplicationUser;
import at.ac.tuwien.sepr.groupphase.backend.entity.Friendship;
import at.ac.tuwien.sepr.groupphase.backend.entity.FriendshipStatus;
import jakarta.persistence.EntityManager;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaDelete;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.CriteriaUpdate;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

@Repository
public class FriendshipRepository {

    private final EntityManager entityManager;

    public FriendshipRepository(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    public List<ApplicationUser> findFriendsOfUser(ApplicationUser user) {
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<ApplicationUser> criteriaQuery1 = criteriaBuilder.createQuery(ApplicationUser.class);
        CriteriaQuery<ApplicationUser> criteriaQuery2 = criteriaBuilder.createQuery(ApplicationUser.class);
        Root<Friendship> root1 = criteriaQuery1.from(Friendship.class);
        Root<Friendship> root2 = criteriaQuery2.from(Friendship.class);

        Predicate predicate1 = criteriaBuilder.and(
            criteriaBuilder.equal(root1.get("sender"), user),
            criteriaBuilder.equal(root1.get("friendshipStatus"), FriendshipStatus.ACCEPTED)
        );
        Predicate predicate2 = criteriaBuilder.and(
            criteriaBuilder.equal(root2.get("receiver"), user),
            criteriaBuilder.equal(root2.get("friendshipStatus"), FriendshipStatus.ACCEPTED)
        );

        criteriaQuery1.select(root1.get("receiver")).where(predicate1);
        criteriaQuery2.select(root2.get("sender")).where(predicate2);

        List<ApplicationUser> resultList = new ArrayList<>();
        resultList.addAll(entityManager.createQuery(criteriaQuery1).getResultList());
        resultList.addAll(entityManager.createQuery(criteriaQuery2).getResultList());

        return resultList;
    }

    public List<ApplicationUser> findIncomingFriendRequestsOfUser(ApplicationUser user) {
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<ApplicationUser> criteriaQuery = criteriaBuilder.createQuery(ApplicationUser.class);
        Root<Friendship> root = criteriaQuery.from(Friendship.class);

        Predicate receiverEqualsUserPredicate = criteriaBuilder.equal(root.get("receiver"), user);
        Predicate friendshipStatusIsPendingPredicate = criteriaBuilder.equal(root.get("friendshipStatus"), FriendshipStatus.PENDING);

        criteriaQuery.select(root.get("sender"))
            .where(criteriaBuilder.and(receiverEqualsUserPredicate, friendshipStatusIsPendingPredicate));

        return entityManager.createQuery(criteriaQuery).getResultList();
    }

    public boolean pendingFriendRequestExists(ApplicationUser sender, ApplicationUser receiver) {
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<Friendship> criteriaQuery = criteriaBuilder.createQuery(Friendship.class);
        Root<Friendship> root = criteriaQuery.from(Friendship.class);

        Predicate whereClause = criteriaBuilder.and(
            criteriaBuilder.equal(root.get("sender"), sender),
            criteriaBuilder.equal(root.get("receiver"), receiver),
            criteriaBuilder.equal(root.get("friendshipStatus"), FriendshipStatus.PENDING)
        );

        criteriaQuery.where(whereClause);

        return entityManager.createQuery(criteriaQuery).getResultList().size() > 0;
    }

    public boolean anyFriendshipRelationBetweenUsersExists(ApplicationUser user1, ApplicationUser user2) {
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<Friendship> criteriaQuery = criteriaBuilder.createQuery(Friendship.class);
        Root<Friendship> root = criteriaQuery.from(Friendship.class);

        Predicate predicate1 = criteriaBuilder.and(
            criteriaBuilder.equal(root.get("sender"), user1),
            criteriaBuilder.equal(root.get("receiver"), user2)
        );

        Predicate predicate2 = criteriaBuilder.and(
            criteriaBuilder.equal(root.get("sender"), user2),
            criteriaBuilder.equal(root.get("receiver"), user1)
        );

        Predicate whereClause = criteriaBuilder.or(predicate1, predicate2);
        criteriaQuery.where(whereClause);

        return entityManager.createQuery(criteriaQuery).getResultList().size() > 0;
    }

    @Transactional
    public Friendship save(Friendship friendship) {
        entityManager.persist(friendship);
        return friendship;
    }

    @Transactional
    public boolean acceptFriendRequest(ApplicationUser sender, ApplicationUser receiver) {
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaUpdate<Friendship> criteriaUpdate = criteriaBuilder.createCriteriaUpdate(Friendship.class);
        Root<Friendship> root = criteriaUpdate.from(Friendship.class);

        Predicate whereClause = criteriaBuilder.and(
            criteriaBuilder.equal(root.get("sender"), sender),
            criteriaBuilder.equal(root.get("receiver"), receiver),
            criteriaBuilder.equal(root.get("friendshipStatus"), FriendshipStatus.PENDING)
        );

        criteriaUpdate.set(root.get("friendshipStatus"), FriendshipStatus.ACCEPTED)
            .where(whereClause);

        int updatedCount = entityManager.createQuery(criteriaUpdate).executeUpdate();

        return updatedCount > 0;
    }

    @Transactional
    public boolean rejectFriendRequest(ApplicationUser sender, ApplicationUser receiver) {
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaDelete<Friendship> criteriaDelete = criteriaBuilder.createCriteriaDelete(Friendship.class);
        Root<Friendship> root = criteriaDelete.from(Friendship.class);

        Predicate predicate = criteriaBuilder.and(
            criteriaBuilder.equal(root.get("sender"), sender),
            criteriaBuilder.equal(root.get("receiver"), receiver),
            criteriaBuilder.equal(root.get("friendshipStatus"), FriendshipStatus.PENDING)
        );

        criteriaDelete.where(predicate);

        int deletedCount = entityManager.createQuery(criteriaDelete).executeUpdate();

        return deletedCount > 0;
    }

    @Transactional
    public void deleteAll() {
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaDelete<Friendship> criteriaDelete = criteriaBuilder.createCriteriaDelete(Friendship.class);
        Root<Friendship> root = criteriaDelete.from(Friendship.class);
        entityManager.createQuery(criteriaDelete).executeUpdate();
    }

}
