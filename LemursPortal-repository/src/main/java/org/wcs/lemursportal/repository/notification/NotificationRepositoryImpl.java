package org.wcs.lemursportal.repository.notification;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaDelete;
import javax.persistence.criteria.Root;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import org.wcs.lemursportal.model.notification.Notification;

/**
 * @author Mikajy <mikajy401@gmail.com>
 *
 */
@Repository
@Transactional(readOnly=true)
public class NotificationRepositoryImpl implements NotificationRepository {
	
	@PersistenceContext
	protected EntityManager em;

	/* (non-Javadoc)
	 * @see org.wcs.lemursportal.repository.notification.NotificationRepository#save(org.wcs.lemursportal.model.notification.Notification)
	 */
	@Transactional(readOnly=false)
	@Override
	public void save(Notification notification) {
		em.persist(notification);
		return;
	}

	/* (non-Javadoc)
	 * @see org.wcs.lemursportal.repository.notification.NotificationRepository#findAllByUser(java.lang.Integer)
	 */
	@Override
	public List<Notification> findAllByUser(Integer userId) {
		/*CriteriaBuilder builder = em.getCriteriaBuilder();
		CriteriaQuery<Notification> select = builder.createQuery(Notification.class);
		Root<Notification> from = select.from(Notification.class);
		select.select(from);
		select.where(builder.equal(from.get("userId"), userId));
		select.orderBy(builder.desc(from.get("date")));
		TypedQuery<Notification> typedQuery = em.createQuery(select);
		List<Notification> notifications = typedQuery.getResultList();*/
		StringBuilder sb = new StringBuilder("select n from Notification n ")
			.append("left join fetch n.thematique left join fetch n.question ")
			.append("where n.userId=:userId and (n.hasBeenRead is null or n.hasBeenRead =:notRead) order by n.date desc ");
		TypedQuery<Notification> query = em.createQuery(sb.toString(), Notification.class);
		query.setParameter("userId", userId);
		query.setParameter("notRead", false);
		List<Notification> notifications = query.getResultList();
		return notifications;
	}

	/* (non-Javadoc)
	 * @see org.wcs.lemursportal.repository.notification.NotificationRepository#countByUser(java.lang.Integer)
	 */
	@Override
	public Long countByUser(Integer userId) {
		TypedQuery<Long> query = em.createQuery("select count(n.id) from Notification n where n.userId=:userId and (n.hasBeenRead is null or n.hasBeenRead =:notRead)", Long.class);
		query.setParameter("userId", userId);
		query.setParameter("notRead", false);
		Long count = query.getSingleResult();
		return count;
	}

	/* (non-Javadoc)
	 * @see org.wcs.lemursportal.repository.notification.NotificationRepository#deleteByUser(java.lang.Integer)
	 */
	@Transactional(readOnly=false)
	@Override
	public int deleteByUser(Integer userId) {
		CriteriaBuilder builder = em.getCriteriaBuilder();
		CriteriaDelete<Notification> delete = builder.createCriteriaDelete(Notification.class);
		Root<Notification> from = delete.from(Notification.class);
		delete.where(builder.equal(from.get("userId"), userId));
		Query query = em.createQuery(delete);
		int nbRowsDeleted = query.executeUpdate();
		return nbRowsDeleted;
	}

}
