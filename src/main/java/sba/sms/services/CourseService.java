package sba.sms.services;


import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;
import sba.sms.dao.CourseI;
import sba.sms.models.Course;
import sba.sms.utils.HibernateUtil;

import java.util.List;

public class CourseService implements CourseI {

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

    @Override
    public Course getCourseById(int courseId) {
        Session s = HibernateUtil.getSessionFactory().openSession();
        Query<Course> q = s.createQuery("from course where id = :id", Course.class);
        q.setParameter(courseId, "id");
        Course ret = q.getSingleResult();

        s.close();
        return ret;
    }

    @Override
    public List<Course> getAllCourses() {
        Session s = HibernateUtil.getSessionFactory().openSession();
        Query<Course> q = s.createQuery("from course", Course.class);
        List<Course> ret = q.getResultList();

        s.close();
        return ret;
    }
}
