package sba.sms.services;


import jakarta.persistence.NoResultException;
import jakarta.persistence.NonUniqueResultException;
import lombok.extern.java.Log;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;
import sba.sms.dao.CourseI;
import sba.sms.models.Course;
import sba.sms.utils.HibernateUtil;

import java.util.List;
@Log
public class CourseService implements CourseI {

    // createCourse	void	Course	persist course to database, also handle commit,rollback, and exceptions
    /**
     * Saves a Course to the database.
     * @param course The course to save to the database. Expected to be fully instantiated and non-null.
     */
    @Override
    public void createCourse(Course course) {
        Transaction t = null;
        try (Session s = HibernateUtil.getSessionFactory().openSession()) {
            t = s.beginTransaction();
            s.persist(course);
            t.commit();
        } catch (HibernateException e) {
            if (t != null)
                t.rollback();
            e.printStackTrace();
        }
    }

    // getCourseById	Course	int courseId	return course if exists, also handle commit,rollback, and exceptions
    /**
     * Retrieves a course from the database by its unique ID.
     * @param courseId The id for the course to retrieve.
     * @throws HibernateException If courseId somehow matched multiple unique course IDs
     * @return A Course if a match was found, null otherwise.
     */
    @Override
    public Course getCourseById(int courseId) {
        try (Session s = HibernateUtil.getSessionFactory().openSession()) {
            Query<Course> q = s.createQuery("from Course where id = :id", Course.class);
            q.setParameter("id", courseId);
            return q.getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
        catch (NonUniqueResultException e)
        {
            log.info("Multiple courses with the supplied ID matched. Please report this error to IT.");
            e.printStackTrace();
            throw new HibernateException("Supplied ID " + courseId + " matches multiple unique course IDs in the database. How?");
        }
    }

    /**
     * Retrieves all courses from the database.
     * @return A List containing every course in the database.
     */
    @Override
    public List<Course> getAllCourses() {
        Session s = HibernateUtil.getSessionFactory().openSession();
        Query<Course> q = s.createQuery("from Course", Course.class);
        List<Course> ret = q.getResultList();

        s.close();
        return ret;
    }
}
