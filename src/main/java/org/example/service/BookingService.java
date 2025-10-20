package org.example.service;

import org.example.model.Booking;
import org.example.model.Guest;
import org.example.util.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.Transaction;

import java.util.Collections;
import java.util.List;

public class

BookingService {

    public void createBooking(Booking booking) {
        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            session.persist(booking);
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            e.printStackTrace();
        }
    }

    public List<Booking> getBookingsByGuest(Guest guest) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery("FROM Booking WHERE guest.id = :guestId", Booking.class)
                    .setParameter("guestId", guest.getId())
                    .list();
        } catch (Exception e) {
            e.printStackTrace();
            return Collections.emptyList();
        }
    }

    public List<Booking> getAllBookings() {
    try (Session session = HibernateUtil.getSessionFactory().openSession()) {
        // CORRECTED QUERY: "JOIN FETCH" tells Hibernate to load the related Guest and Room data immediately.
        return session.createQuery("FROM Booking b JOIN FETCH b.guest JOIN FETCH b.room", Booking.class).list();
    } catch (Exception e) {
        e.printStackTrace();
        return Collections.emptyList();
    }
}

    public void deleteBooking(Booking booking) {
        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            // CORRECTED LINE: Use remove() instead of delete() for Hibernate 6
            session.remove(booking);
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            e.printStackTrace();
        }
    }
}