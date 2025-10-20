package org.example.service;

import org.example.model.Room;
import org.example.util.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.Transaction;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

public class RoomService {

    public List<Room> findAvailableRooms(LocalDate checkIn, LocalDate checkOut) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            String hql = "SELECT r FROM Room r WHERE r.id NOT IN (" +
                         "  SELECT b.room.id FROM Booking b WHERE " +
                         "  b.checkInDate < :checkOut AND b.checkOutDate > :checkIn" +
                         ")";
            return session.createQuery(hql, Room.class)
                    .setParameter("checkIn", checkIn)
                    .setParameter("checkOut", checkOut)
                    .list();
        } catch (Exception e) {
            e.printStackTrace();
            return Collections.emptyList();
        }
    }

    public List<Room> getAllRooms() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery("FROM Room", Room.class).list();
        } catch (Exception e) {
            e.printStackTrace();
            return Collections.emptyList();
        }
    }

    public void saveRoom(Room room) {
        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            session.persist(room); // persist is the standard JPA method for saving a new entity
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            e.printStackTrace();
        }
    }

    public void deleteRoom(Room room) {
        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            // CORRECTED LINE: Use remove() instead of delete() for Hibernate 6
            session.remove(room); 
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            e.printStackTrace();
        }
    }
}