package com.sagarius.goddess.server.util;

import java.util.Collection;
import java.util.Date;

import javax.jdo.PersistenceManager;
import javax.jdo.Transaction;

import org.sagarius.radix.server.model.BaseEntity;
import org.sagarius.radix.server.model.Metadata;

import com.sagarius.goddess.server.Utils;

public class ModelUtils {
	private static final PersistenceManager MANAGER = PMFactory.getManager();

	public static final <T> T persistEntity(T entity) {
		return persistEntity(entity, false);
	}

	public static final <T> T persistEntity(T entity, boolean updateMeta) {
		if (updateMeta) {
			if (entity instanceof BaseEntity) {
				BaseEntity baseEntity = (BaseEntity) entity;
				Metadata meta = baseEntity.getMeta();
				if (meta == null) {
					meta = Utils.getMetadata();
				}
				meta.setTimestamp(new Date());
				meta.setEditor(Utils.getCurrentMember());
				baseEntity.setMeta(meta);
			}
		}
		Transaction transaction = MANAGER.currentTransaction();
		try {
			transaction.begin();
			entity = MANAGER.makePersistent(entity);
			transaction.commit();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (transaction.isActive()) {
				transaction.rollback();
				return null;
			}
		}
		return entity;
	}

	public static final <T> Collection<T> persistEntities(Collection<T> entities) {
		Transaction transaction = MANAGER.currentTransaction();
		try {
			transaction.begin();
			entities = MANAGER.makePersistentAll(entities);
			transaction.commit();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (transaction.isActive()) {
				transaction.rollback();
				return null;
			}
		}
		return entities;
	}

	public static final <T> T[] persistEntities(T... entities) {
		Transaction transaction = MANAGER.currentTransaction();
		try {
			transaction.begin();
			entities = MANAGER.makePersistentAll(entities);
			transaction.commit();
		} finally {
			if (transaction.isActive()) {
				transaction.rollback();
				return null;
			}
		}
		return entities;
	}
}
